import { describe, it, beforeEach, afterEach } from 'node:test';
import assert from 'node:assert';
import { io as ioClient, Socket as ClientSocket } from 'socket.io-client';
import { createServerApp } from '../src/server.js';
import { rooms, resetState } from '../src/state.js';
import http from 'http';

describe('Screen Time Roulette - Backend Lobby & Rules', () => {
  let server: http.Server;
  let port: number;
  let clientSockets: ClientSocket[] = [];

  beforeEach(async () => {
    resetState();
    const appData = createServerApp();
    server = appData.server;

    await new Promise<void>((resolve) => {
      server.listen(0, '127.0.0.1', () => {
        const address = server.address();
        if (address && typeof address !== 'string') {
          port = address.port;
        }
        resolve();
      });
    });
  });

  afterEach(async () => {
    for (const socket of clientSockets) {
      if (socket.connected) {
        socket.disconnect();
      }
    }
    clientSockets = [];

    await new Promise<void>((resolve) => {
      server.close(() => resolve());
    });
  });

  function createClient(): Promise<ClientSocket> {
    return new Promise((resolve) => {
      const socket = ioClient(`http://127.0.0.1:${port}`, {
        transports: ['websocket'],
        forceNew: true
      });
      clientSockets.push(socket);
      socket.on('connect', () => {
        resolve(socket);
      });
    });
  }

  it('1. createRoom - crée une room avec code à 6 caractères et assigne l\'hôte', async () => {
    const hostSocket = await createClient();

    const screenData = [
      { name: 'Instagram', timeInMinutes: 120, opens: 45 },
      { name: 'TikTok', timeInMinutes: 90, opens: 30 }
    ];

    const response = await new Promise<any>((resolve) => {
      hostSocket.emit(
        'createRoom',
        { pseudo: 'Alice', screenData },
        (res: any) => resolve(res)
      );
    });

    assert.strictEqual(response.success, true);
    assert.strictEqual(typeof response.roomCode, 'string');
    assert.strictEqual(response.roomCode.length, 6);
    assert.match(response.roomCode, /^[A-Z0-9]{6}$/);

    const room = rooms[response.roomCode];
    assert.ok(room);
    assert.strictEqual(room.hostId, hostSocket.id);
    assert.strictEqual(room.status, 'waiting');
    assert.ok(room.players[hostSocket.id!]);
    assert.strictEqual(room.players[hostSocket.id!].pseudo, 'Alice');
    assert.strictEqual(room.players[hostSocket.id!].score, 0);
    assert.strictEqual(room.players[hostSocket.id!].isDisconnected, false);
    assert.strictEqual(room.players[hostSocket.id!].screenData.length, 2);
  });

  it('2. joinRoom - permet à d\'autres joueurs de rejoindre et diffuse playerJoined', async () => {
    const hostSocket = await createClient();
    const player2Socket = await createClient();

    // 1. Host crée la room
    const createRes = await new Promise<any>((resolve) => {
      hostSocket.emit(
        'createRoom',
        { pseudo: 'Alice', screenData: [] },
        (res: any) => resolve(res)
      );
    });

    const roomCode = createRes.roomCode;

    // 2. Attente de l'événement playerJoined côté hôte
    const playerJoinedPromise = new Promise<any>((resolve) => {
      hostSocket.on('playerJoined', (data: any) => {
        if (data.joinedPlayer?.pseudo === 'Bob') {
          resolve(data);
        }
      });
    });

    // 3. Player 2 rejoint
    const joinRes = await new Promise<any>((resolve) => {
      player2Socket.emit(
        'joinRoom',
        {
          roomCode,
          pseudo: 'Bob',
          screenData: [{ name: 'YouTube', timeInMinutes: 60, opens: 10 }]
        },
        (res: any) => resolve(res)
      );
    });

    assert.strictEqual(joinRes.success, true);

    const eventData = await playerJoinedPromise;
    assert.strictEqual(eventData.roomId, roomCode);
    assert.strictEqual(eventData.players.length, 2);

    const bobInList = eventData.players.find((p: any) => p.pseudo === 'Bob');
    assert.ok(bobInList);
    assert.strictEqual(bobInList.isHost, false);
    assert.strictEqual(bobInList.isDisconnected, false);

    // Vérification de la room en mémoire
    assert.strictEqual(Object.keys(rooms[roomCode].players).length, 2);
  });

  it('3. joinRoom - refuse une salle inexistante ou un pseudo déjà utilisé', async () => {
    const playerSocket = await createClient();

    // Salle inexistante
    const resBadRoom = await new Promise<any>((resolve) => {
      playerSocket.emit(
        'joinRoom',
        { roomCode: 'FAKEXX', pseudo: 'Charlie', screenData: [] },
        (res: any) => resolve(res)
      );
    });
    assert.strictEqual(resBadRoom.success, false);

    // Création d'une room par Alice
    const hostSocket = await createClient();
    const createRes = await new Promise<any>((resolve) => {
      hostSocket.emit(
        'createRoom',
        { pseudo: 'Alice', screenData: [] },
        (res: any) => resolve(res)
      );
    });

    // Même pseudo Alice
    const resSamePseudo = await new Promise<any>((resolve) => {
      playerSocket.emit(
        'joinRoom',
        { roomCode: createRes.roomCode, pseudo: 'Alice', screenData: [] },
        (res: any) => resolve(res)
      );
    });
    assert.strictEqual(resSamePseudo.success, false);
    assert.match(resSamePseudo.message, /déjà utilisé/i);
  });

  it('4. Mode Fantôme & Déconnexion - conserve les données et diffuse playerLeft', async () => {
    const hostSocket = await createClient();
    const player2Socket = await createClient();

    const createRes = await new Promise<any>((resolve) => {
      hostSocket.emit(
        'createRoom',
        { pseudo: 'Alice', screenData: [] },
        (res: any) => resolve(res)
      );
    });
    const roomCode = createRes.roomCode;

    await new Promise<any>((resolve) => {
      player2Socket.emit(
        'joinRoom',
        {
          roomCode,
          pseudo: 'Bob',
          screenData: [{ name: 'Twitter', timeInMinutes: 40, opens: 15 }]
        },
        (res: any) => resolve(res)
      );
    });

    // Player 2 se déconnecte, l'hôte écoute playerLeft
    const playerLeftPromise = new Promise<any>((resolve) => {
      hostSocket.on('playerLeft', (data: any) => {
        resolve(data);
      });
    });

    player2Socket.disconnect();

    const leftData = await playerLeftPromise;
    assert.strictEqual(leftData.leftPlayer.pseudo, 'Bob');

    // Vérification du Mode Fantôme : Bob est toujours dans room.players, avec isDisconnected = true
    const room = rooms[roomCode];
    assert.ok(room);
    const bob = Object.values(room.players).find((p) => p.pseudo === 'Bob');
    assert.ok(bob);
    assert.strictEqual(bob.isDisconnected, true);
    assert.strictEqual(bob.screenData.length, 1);
  });

  it('5. Nettoyage mémoire - supprime la room en attente quand tous les joueurs sont partis', async () => {
    const hostSocket = await createClient();

    const createRes = await new Promise<any>((resolve) => {
      hostSocket.emit(
        'createRoom',
        { pseudo: 'Alice', screenData: [] },
        (res: any) => resolve(res)
      );
    });
    const roomCode = createRes.roomCode;
    assert.ok(rooms[roomCode]);

    // Déconnexion de l'unique joueur
    const disconnectPromise = new Promise<void>((resolve) => {
      hostSocket.on('disconnect', () => resolve());
    });
    hostSocket.disconnect();
    await disconnectPromise;

    // Laisser un court délai pour le traitement des sockets
    await new Promise((r) => setTimeout(r, 100));

    // La room doit avoir été nettoyée de la mémoire
    assert.strictEqual(rooms[roomCode], undefined);
  });

  it('6. Condition de lancement startGame - minimum 3 joueurs et hôte uniquement', async () => {
    const hostSocket = await createClient();
    const p2Socket = await createClient();
    const p3Socket = await createClient();

    const createRes = await new Promise<any>((resolve) => {
      hostSocket.emit(
        'createRoom',
        { pseudo: 'Alice', screenData: [] },
        (res: any) => resolve(res)
      );
    });
    const roomCode = createRes.roomCode;

    // 1 joueur : tentative de start -> échec (< 3 joueurs)
    const failStart1 = await new Promise<any>((resolve) => {
      hostSocket.emit('startGame', {}, (res: any) => resolve(res));
    });
    assert.strictEqual(failStart1.success, false);
    assert.match(failStart1.message, /au moins 3 joueurs/i);

    // Joueur 2 rejoint
    await new Promise<any>((resolve) => {
      p2Socket.emit(
        'joinRoom',
        { roomCode, pseudo: 'Bob', screenData: [] },
        (res: any) => resolve(res)
      );
    });

    // 2 joueurs : tentative par le joueur 2 (non hôte) -> échec
    const failStartNonHost = await new Promise<any>((resolve) => {
      p2Socket.emit('startGame', {}, (res: any) => resolve(res));
    });
    assert.strictEqual(failStartNonHost.success, false);
    assert.match(failStartNonHost.message, /seul l'hôte/i);

    // Joueur 3 rejoint
    await new Promise<any>((resolve) => {
      p3Socket.emit(
        'joinRoom',
        { roomCode, pseudo: 'Charlie', screenData: [] },
        (res: any) => resolve(res)
      );
    });

    // 3 joueurs : l'hôte lance la partie -> succès !
    const gameStartedPromise = new Promise<any>((resolve) => {
      p3Socket.on('gameStarted', (data: any) => resolve(data));
    });

    const successStart = await new Promise<any>((resolve) => {
      hostSocket.emit('startGame', {}, (res: any) => resolve(res));
    });
    assert.strictEqual(successStart.success, true);

    const startedData = await gameStartedPromise;
    assert.strictEqual(startedData.roomId, roomCode);
    assert.strictEqual(startedData.status, 'playing');
    assert.strictEqual(rooms[roomCode].status, 'playing');
  });
});
