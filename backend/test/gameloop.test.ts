import { describe, it, beforeEach, afterEach } from 'node:test';
import assert from 'node:assert';
import { io as ioClient, Socket as ClientSocket } from 'socket.io-client';
import { createServerApp } from '../src/server.js';
import { rooms, resetState, setGameConfig } from '../src/state.js';
import http from 'http';

describe('Screen Time Roulette - Game Loop & Scoring', () => {
  let server: http.Server;
  let port: number;
  let clientSockets: ClientSocket[] = [];

  beforeEach(async () => {
    resetState();
    // Accélère les timers pour les tests unitaires (évite d'attendre 8s ou 30s)
    setGameConfig({
      ROUND_DURATION_SECONDS: 30,
      SERVER_TIMEOUT_MS: 500,
      INTERMISSION_DURATION_MS: 150
    });

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
    resetState();
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

  it('1. startRound - diffuse les screenData anonymisées et la liste des candidats à tous les joueurs', async () => {
    const host = await createClient();
    const p2 = await createClient();
    const p3 = await createClient();

    // Alice
    const createRes = await new Promise<any>((resolve) => {
      host.emit('createRoom', {
        pseudo: 'Alice',
        screenData: [{ name: 'Instagram', timeInMinutes: 120, opens: 40 }]
      }, resolve);
    });
    const roomCode = createRes.roomCode;

    // Bob
    await new Promise<any>((resolve) => {
      p2.emit('joinRoom', {
        roomCode,
        pseudo: 'Bob',
        screenData: [{ name: 'TikTok', timeInMinutes: 80, opens: 25 }]
      }, resolve);
    });

    // Charlie
    await new Promise<any>((resolve) => {
      p3.emit('joinRoom', {
        roomCode,
        pseudo: 'Charlie',
        screenData: [{ name: 'YouTube', timeInMinutes: 200, opens: 15 }]
      }, resolve);
    });

    const startRoundPromise = new Promise<any>((resolve) => {
      p2.on('startRound', (data: any) => resolve(data));
    });

    // Lancement du jeu
    await new Promise<any>((resolve) => {
      host.emit('startGame', {}, resolve);
    });

    const roundData = await startRoundPromise;
    assert.strictEqual(roundData.roundNumber, 1);
    assert.strictEqual(roundData.totalRounds, 3);
    assert.strictEqual(roundData.duration, 30);
    assert.strictEqual(roundData.candidates.length, 3);
    assert.ok(Array.isArray(roundData.screenData));
    assert.ok(roundData.screenData.length > 0);
  });

  it('2. submitGuess & Fin anticipée - tout le monde vote (y compris le joueur mystère) et marque des points', async () => {
    const host = await createClient();
    const p2 = await createClient();
    const p3 = await createClient();

    const createRes = await new Promise<any>((resolve) => {
      host.emit('createRoom', {
        pseudo: 'Alice',
        screenData: [{ name: 'Duolingo', timeInMinutes: 15, opens: 2 }]
      }, resolve);
    });
    const roomCode = createRes.roomCode;

    await new Promise<any>((resolve) => {
      p2.emit('joinRoom', {
        roomCode,
        pseudo: 'Bob',
        screenData: [{ name: 'Reddit', timeInMinutes: 90, opens: 30 }]
      }, resolve);
    });

    await new Promise<any>((resolve) => {
      p3.emit('joinRoom', {
        roomCode,
        pseudo: 'Charlie',
        screenData: [{ name: 'WhatsApp', timeInMinutes: 60, opens: 50 }]
      }, resolve);
    });

    let roundEndedData: any = null;
    const roundEndedPromise = new Promise<any>((resolve) => {
      host.on('roundEnded', (data: any) => {
        roundEndedData = data;
        resolve(data);
      });
    });

    // Écoute de startRound pour connaître l'ID du joueur mystère côté serveur
    await new Promise<any>((resolve) => {
      host.emit('startGame', {}, resolve);
    });

    const room = rooms[roomCode];
    const mysteryId = room.playOrder[0];

    // Les 3 joueurs votent (dont le joueur mystère lui-même)
    // Joueur 1 (Alice) vote pour le bon joueur mystère
    const res1 = await new Promise<any>((resolve) => {
      host.emit('submitGuess', { guessedPlayerId: mysteryId }, resolve);
    });
    assert.strictEqual(res1.success, true);

    // Joueur 2 (Bob) vote pour le bon joueur mystère
    const res2 = await new Promise<any>((resolve) => {
      p2.emit('submitGuess', { guessedPlayerId: mysteryId }, resolve);
    });
    assert.strictEqual(res2.success, true);

    // Joueur 3 (Charlie) fait exprès de se tromper (vote pour un faux ID ou un autre joueur)
    const otherId = [host.id, p2.id, p3.id].find((id) => id !== mysteryId)!;
    const res3 = await new Promise<any>((resolve) => {
      p3.emit('submitGuess', { guessedPlayerId: otherId }, resolve);
    });
    assert.strictEqual(res3.success, true);

    // Dès que le 3ème joueur a voté, roundEnded se déclenche IMMÉDIATEMENT (sans attendre le timeout)
    const result = await roundEndedPromise;
    assert.ok(result);
    assert.strictEqual(result.roundNumber, 1);
    assert.strictEqual(result.mysteryPlayer.id, mysteryId);

    // Vérification des points
    const aliceGuess = result.guesses[host.id!];
    const bobGuess = result.guesses[p2.id!];
    const charlieGuess = result.guesses[p3.id!];

    assert.strictEqual(aliceGuess.isCorrect, true);
    assert.ok(aliceGuess.pointsEarned > 800, `Alice devrait marquer ~900+ points, obtenu: ${aliceGuess.pointsEarned}`);

    assert.strictEqual(bobGuess.isCorrect, true);
    assert.ok(bobGuess.pointsEarned > 800, `Bob devrait marquer ~900+ points, obtenu: ${bobGuess.pointsEarned}`);

    assert.strictEqual(charlieGuess.isCorrect, false);
    assert.strictEqual(charlieGuess.pointsEarned, 0);

    // Leaderboard vérifié
    assert.strictEqual(result.leaderboard.length, 3);
  });

  it('3. Déroulement complet des rounds et gameEnded avec podium', async () => {
    const host = await createClient();
    const p2 = await createClient();
    const p3 = await createClient();

    const createRes = await new Promise<any>((resolve) => {
      host.emit('createRoom', { pseudo: 'Alice', screenData: [] }, resolve);
    });
    const roomCode = createRes.roomCode;

    await new Promise<any>((resolve) => {
      p2.emit('joinRoom', { roomCode, pseudo: 'Bob', screenData: [] }, resolve);
    });
    await new Promise<any>((resolve) => {
      p3.emit('joinRoom', { roomCode, pseudo: 'Charlie', screenData: [] }, resolve);
    });

    const gameEndedPromise = new Promise<any>((resolve) => {
      host.on('gameEnded', (data: any) => resolve(data));
    });

    // Auto-réponse à chaque startRound pour faire avancer les 3 rounds rapidement
    const handleStartRound = (socket: ClientSocket) => {
      socket.on('startRound', async (data: any) => {
        // Vote pour le premier candidat de la liste
        socket.emit('submitGuess', { guessedPlayerId: data.candidates[0].id });
      });
    };

    handleStartRound(host);
    handleStartRound(p2);
    handleStartRound(p3);

    // Début de la partie
    await new Promise<any>((resolve) => {
      host.emit('startGame', {}, resolve);
    });

    const finalGameData = await gameEndedPromise;
    assert.strictEqual(finalGameData.roomId, roomCode);
    assert.strictEqual(finalGameData.totalRounds, 3);
    assert.strictEqual(finalGameData.podium.length, 3);

    // La room doit être repassée en statut 'waiting' pour rejouer
    const room = rooms[roomCode];
    assert.ok(room);
    assert.strictEqual(room.status, 'waiting');
  });

  it('4. Nettoyage de mémoire et annulation des timers si déconnexion', async () => {
    const host = await createClient();
    const p2 = await createClient();
    const p3 = await createClient();

    const createRes = await new Promise<any>((resolve) => {
      host.emit('createRoom', { pseudo: 'Alice', screenData: [] }, resolve);
    });
    const roomCode = createRes.roomCode;

    await new Promise<any>((resolve) => {
      p2.emit('joinRoom', { roomCode, pseudo: 'Bob', screenData: [] }, resolve);
    });
    await new Promise<any>((resolve) => {
      p3.emit('joinRoom', { roomCode, pseudo: 'Charlie', screenData: [] }, resolve);
    });

    await new Promise<any>((resolve) => {
      host.emit('startGame', {}, resolve);
    });

    assert.ok(rooms[roomCode]);
    assert.ok(rooms[roomCode].activeTimeout !== null);

    // Tous les joueurs se déconnectent
    host.disconnect();
    p2.disconnect();
    p3.disconnect();

    await new Promise((r) => setTimeout(r, 100));

    // La room doit avoir été détruite et le timer nettoyé
    assert.strictEqual(rooms[roomCode], undefined);
  });
});
