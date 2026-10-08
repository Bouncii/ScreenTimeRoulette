import { Server, Socket } from 'socket.io';
import {
  rooms,
  socketRoomMap,
  generateRoomCode,
  shuffleArray,
  calculateScore,
  clearRoomTimeout,
  getPublicPlayers,
  getConnectedPlayersCount,
  cleanupWaitingRoomIfEmpty,
  GAME_CONFIG
} from './state.js';
import {
  Room,
  CreateRoomPayload,
  JoinRoomPayload,
  SubmitGuessPayload,
  SocketCallbackResponse,
  CandidatePlayer,
  GuesserResult,
  RoundEndedPayload
} from './types.js';

export function registerSocketHandlers(io: Server): void {
  io.on('connection', (socket: Socket) => {
    console.log(`[Socket connected] ID: ${socket.id}`);

    /**
     * Événement createRoom
     * Client -> Serveur : createRoom
     * Payload : { pseudo: string, screenData: Array }
     * Callback : { success: boolean, roomCode?: string, message?: string }
     */
    socket.on('createRoom', (payload: CreateRoomPayload, callback?: (res: SocketCallbackResponse) => void) => {
      try {
        const pseudo = payload?.pseudo?.trim();
        if (!pseudo) {
          return callback?.({
            success: false,
            message: 'Le pseudo est obligatoire.'
          });
        }

        // Si le socket est déjà dans une salle, on le détache
        const existingRoomId = socketRoomMap.get(socket.id);
        if (existingRoomId) {
          handlePlayerLeave(socket, existingRoomId, io);
        }

        const roomCode = generateRoomCode();

        rooms[roomCode] = {
          roomId: roomCode,
          hostId: socket.id,
          status: 'waiting',
          players: {
            [socket.id]: {
              pseudo,
              score: 0,
              isDisconnected: false,
              screenData: Array.isArray(payload?.screenData) ? payload.screenData : []
            }
          },
          playOrder: [],
          currentRoundIndex: 0,
          roundStartTime: 0,
          activeTimeout: null,
          currentGuesses: {}
        };

        socketRoomMap.set(socket.id, roomCode);
        socket.join(roomCode);

        console.log(`[Room créée] Code: ${roomCode} par ${pseudo} (${socket.id})`);

        callback?.({
          success: true,
          roomCode
        });

        io.to(roomCode).emit('playerJoined', {
          roomId: roomCode,
          hostId: socket.id,
          joinedPlayer: { id: socket.id, pseudo },
          players: getPublicPlayers(rooms[roomCode])
        });
      } catch (err: any) {
        console.error('[createRoom error]', err);
        callback?.({
          success: false,
          message: err?.message || 'Erreur lors de la création de la salle.'
        });
      }
    });

    /**
     * Événement joinRoom
     * Client -> Serveur : joinRoom
     * Payload : { roomCode: string, pseudo: string, screenData: Array }
     * Callback : { success: boolean, message?: string }
     */
    socket.on('joinRoom', (payload: JoinRoomPayload, callback?: (res: SocketCallbackResponse) => void) => {
      try {
        const roomCode = payload?.roomCode ? String(payload.roomCode).trim().toUpperCase() : '';
        const pseudo = payload?.pseudo?.trim();

        if (!roomCode) {
          return callback?.({
            success: false,
            message: 'Le code de la salle est obligatoire.'
          });
        }

        if (!pseudo) {
          return callback?.({
            success: false,
            message: 'Le pseudo est obligatoire.'
          });
        }

        const room = rooms[roomCode];

        if (!room) {
          return callback?.({
            success: false,
            message: 'Salle introuvable avec ce code.'
          });
        }

        if (room.status !== 'waiting') {
          return callback?.({
            success: false,
            message: 'La partie a déjà commencé dans cette salle.'
          });
        }

        const pseudoExists = Object.values(room.players).some(
          (p) => !p.isDisconnected && p.pseudo.toLowerCase() === pseudo.toLowerCase()
        );
        if (pseudoExists) {
          return callback?.({
            success: false,
            message: 'Ce pseudo est déjà utilisé dans cette salle.'
          });
        }

        const existingRoomId = socketRoomMap.get(socket.id);
        if (existingRoomId && existingRoomId !== roomCode) {
          handlePlayerLeave(socket, existingRoomId, io);
        }

        room.players[socket.id] = {
          pseudo,
          score: 0,
          isDisconnected: false,
          screenData: Array.isArray(payload?.screenData) ? payload.screenData : []
        };

        socketRoomMap.set(socket.id, roomCode);
        socket.join(roomCode);

        console.log(`[Joueur a rejoint] Room ${roomCode}: ${pseudo} (${socket.id})`);

        callback?.({
          success: true,
          roomCode
        });

        io.to(roomCode).emit('playerJoined', {
          roomId: roomCode,
          hostId: room.hostId,
          joinedPlayer: { id: socket.id, pseudo },
          players: getPublicPlayers(room)
        });
      } catch (err: any) {
        console.error('[joinRoom error]', err);
        callback?.({
          success: false,
          message: err?.message || 'Erreur lors de la tentative de rejoindre la salle.'
        });
      }
    });

    /**
     * Événement startGame
     * Condition de lancement : émis par le hostId et minimum 3 joueurs connectés.
     */
    socket.on('startGame', (_payload: any, callback?: (res: SocketCallbackResponse) => void) => {
      try {
        const roomId = socketRoomMap.get(socket.id);
        if (!roomId) {
          return callback?.({
            success: false,
            message: 'Vous n\'êtes actuellement dans aucune salle.'
          });
        }

        const room = rooms[roomId];
        if (!room) {
          return callback?.({
            success: false,
            message: 'Salle introuvable.'
          });
        }

        if (room.hostId !== socket.id) {
          return callback?.({
            success: false,
            message: 'Seul l\'hôte peut lancer la partie.'
          });
        }

        if (room.status !== 'waiting') {
          return callback?.({
            success: false,
            message: 'La partie est déjà lancée ou terminée.'
          });
        }

        const connectedPlayersCount = getConnectedPlayersCount(room);
        if (connectedPlayersCount < 3) {
          return callback?.({
            success: false,
            message: `Au moins 3 joueurs connectés sont requis pour lancer la partie (${connectedPlayersCount}/3 actuellement).`
          });
        }

        // Réinitialisation des scores de tous les joueurs pour une nouvelle partie
        for (const player of Object.values(room.players)) {
          player.score = 0;
        }

        // Création de la playlist des rounds (1 round par joueur connecté présent au lancement)
        const connectedSocketIds = Object.keys(room.players).filter((id) => !room.players[id].isDisconnected);
        room.playOrder = shuffleArray(connectedSocketIds);
        room.currentRoundIndex = 0;
        room.status = 'playing';

        console.log(`[Partie lancée] Room: ${roomId} avec ${connectedPlayersCount} rounds (${room.playOrder.join(', ')})`);

        callback?.({
          success: true
        });

        io.to(roomId).emit('gameStarted', {
          roomId,
          hostId: room.hostId,
          status: room.status,
          players: getPublicPlayers(room)
        });

        // Déclenchement du premier round
        startCurrentRound(io, room);
      } catch (err: any) {
        console.error('[startGame error]', err);
        callback?.({
          success: false,
          message: err?.message || 'Erreur lors du lancement de la partie.'
        });
      }
    });

    /**
     * Événement submitGuess
     * Client -> Serveur : submitGuess
     * Payload : { guessedPlayerId: string }
     * Tout le monde vote (y compris le joueur mystère).
     */
    socket.on('submitGuess', (payload: SubmitGuessPayload, callback?: (res: SocketCallbackResponse) => void) => {
      try {
        const roomId = socketRoomMap.get(socket.id);
        if (!roomId) {
          return callback?.({
            success: false,
            message: 'Vous n\'êtes actuellement dans aucune salle.'
          });
        }

        const room = rooms[roomId];
        if (!room || room.status !== 'playing') {
          return callback?.({
            success: false,
            message: 'Aucun round en cours.'
          });
        }

        // Vérification si le joueur a déjà voté dans ce round
        if (room.currentGuesses[socket.id]) {
          return callback?.({
            success: false,
            message: 'Vous avez déjà soumis votre vote pour ce round.'
          });
        }

        const guessedPlayerId = payload?.guessedPlayerId;
        if (!guessedPlayerId || !room.players[guessedPlayerId]) {
          return callback?.({
            success: false,
            message: 'Joueur cible invalide.'
          });
        }

        // Calcul du temps mis pour répondre
        const elapsedSeconds = Math.max(0.05, (Date.now() - room.roundStartTime) / 1000);

        room.currentGuesses[socket.id] = {
          guessedId: guessedPlayerId,
          timeToGuess: elapsedSeconds
        };

        console.log(`[Vote reçu] Room: ${roomId}, Joueur: ${room.players[socket.id]?.pseudo} a voté pour ${room.players[guessedPlayerId]?.pseudo} en ${elapsedSeconds.toFixed(2)}s`);

        callback?.({
          success: true
        });

        // Vérifie si TOUS les joueurs actifs connectés ont voté
        checkAndAdvanceIfAllGuessed(io, room);
      } catch (err: any) {
        console.error('[submitGuess error]', err);
        callback?.({
          success: false,
          message: err?.message || 'Erreur lors de la soumission du vote.'
        });
      }
    });

    /**
     * Quitter explicitement une room sans se déconnecter du socket
     */
    socket.on('leaveRoom', (_payload: any, callback?: (res: SocketCallbackResponse) => void) => {
      const roomId = socketRoomMap.get(socket.id);
      if (roomId) {
        handlePlayerLeave(socket, roomId, io);
        callback?.({ success: true });
      } else {
        callback?.({ success: false, message: 'Non présent dans une salle.' });
      }
    });

    /**
     * Système : disconnect
     */
    socket.on('disconnect', () => {
      console.log(`[Socket disconnected] ID: ${socket.id}`);
      const roomId = socketRoomMap.get(socket.id);
      if (roomId) {
        handlePlayerLeave(socket, roomId, io);
      }
    });
  });
}

/**
 * Lance le round courant pour la room.
 */
function startCurrentRound(io: Server, room: Room): void {
  // Fin de la partie si tous les rounds sont joués
  if (room.currentRoundIndex >= room.playOrder.length) {
    endGame(io, room);
    return;
  }

  const mysteryPlayerId = room.playOrder[room.currentRoundIndex];
  const mysteryPlayer = room.players[mysteryPlayerId];

  if (!mysteryPlayer) {
    console.warn(`[startRound] Joueur mystère ${mysteryPlayerId} introuvable, passage au round suivant.`);
    room.currentRoundIndex++;
    startCurrentRound(io, room);
    return;
  }

  // Réinitialisation des votes pour ce round
  room.currentGuesses = {};
  room.roundStartTime = Date.now();
  clearRoomTimeout(room);

  // Liste de tous les candidats parmi lesquels choisir
  const candidates: CandidatePlayer[] = Object.entries(room.players).map(([id, player]) => ({
    id,
    pseudo: player.pseudo
  }));

  console.log(`[Début Round ${room.currentRoundIndex + 1}/${room.playOrder.length}] Room: ${room.roomId} (Joueur mystère: ${mysteryPlayer.pseudo})`);

  // Diffusion aux clients : screenData sans le pseudo
  io.to(room.roomId).emit('startRound', {
    roundNumber: room.currentRoundIndex + 1,
    totalRounds: room.playOrder.length,
    screenData: mysteryPlayer.screenData,
    candidates,
    duration: GAME_CONFIG.ROUND_DURATION_SECONDS
  });

  // Timer de sécurité silencieux côté serveur (30s + marge réseau)
  room.activeTimeout = setTimeout(() => {
    room.activeTimeout = null;
    console.log(`[Timer écoulé] Fin automatique du round ${room.currentRoundIndex + 1} pour la room ${room.roomId}`);
    endCurrentRound(io, room);
  }, GAME_CONFIG.SERVER_TIMEOUT_MS);
}

/**
 * Vérifie si tous les joueurs actifs (connectés) ont voté.
 * Si oui, termine le round immédiatement.
 */
function checkAndAdvanceIfAllGuessed(io: Server, room: Room): void {
  const connectedPlayers = Object.entries(room.players).filter(([_, p]) => !p.isDisconnected);
  const allVoted = connectedPlayers.length > 0 && connectedPlayers.every(([id]) => room.currentGuesses[id] !== undefined);

  if (allVoted) {
    console.log(`[Tous les joueurs ont voté] Fin anticipée du round ${room.currentRoundIndex + 1} pour la room ${room.roomId}`);
    clearRoomTimeout(room);
    endCurrentRound(io, room);
  }
}

/**
 * Termine le round courant, calcule les scores et programme le round suivant après la pause.
 */
function endCurrentRound(io: Server, room: Room): void {
  clearRoomTimeout(room);

  const mysteryPlayerId = room.playOrder[room.currentRoundIndex];
  const mysteryPlayer = room.players[mysteryPlayerId];

  const guessesResult: { [guesserId: string]: GuesserResult } = {};

  // Calcul des points pour chaque joueur (façon Kahoot)
  for (const [playerId, player] of Object.entries(room.players)) {
    const guess = room.currentGuesses[playerId];

    if (guess) {
      const isCorrect = guess.guessedId === mysteryPlayerId;
      const pointsEarned = isCorrect
        ? calculateScore(guess.timeToGuess, GAME_CONFIG.ROUND_DURATION_SECONDS)
        : 0;

      // Attribution des points au joueur
      player.score += pointsEarned;

      guessesResult[playerId] = {
        guessedId: guess.guessedId,
        guessedPseudo: room.players[guess.guessedId]?.pseudo || 'Inconnu',
        isCorrect,
        pointsEarned,
        timeToGuess: parseFloat(guess.timeToGuess.toFixed(2))
      };
    } else {
      // Aucun vote soumis (temps écoulé sans réponse)
      guessesResult[playerId] = {
        guessedId: '',
        guessedPseudo: 'Aucune réponse',
        isCorrect: false,
        pointsEarned: 0,
        timeToGuess: GAME_CONFIG.ROUND_DURATION_SECONDS
      };
    }
  }

  // Classement mis à jour trié par score décroissant
  const leaderboard = getPublicPlayers(room).sort((a, b) => b.score - a.score);

  const roundEndedPayload: RoundEndedPayload = {
    roundNumber: room.currentRoundIndex + 1,
    totalRounds: room.playOrder.length,
    mysteryPlayer: {
      id: mysteryPlayerId,
      pseudo: mysteryPlayer?.pseudo || 'Joueur Mystère'
    },
    guesses: guessesResult,
    leaderboard
  };

  console.log(`[Fin Round ${room.currentRoundIndex + 1}] Room: ${room.roomId}. Résultats diffusés.`);
  io.to(room.roomId).emit('roundEnded', roundEndedPayload);

  // Passage à l'index suivant
  room.currentRoundIndex++;

  // Pause d'intermission (8 secondes par défaut) avant le round suivant
  room.activeTimeout = setTimeout(() => {
    room.activeTimeout = null;
    startCurrentRound(io, room);
  }, GAME_CONFIG.INTERMISSION_DURATION_MS);
}

/**
 * Termine la partie, diffuse le podium final et remet la room en 'waiting'.
 */
function endGame(io: Server, room: Room): void {
  clearRoomTimeout(room);
  room.status = 'finished';

  const podium = getPublicPlayers(room).sort((a, b) => b.score - a.score);

  console.log(`[Fin de Partie] Room: ${room.roomId}. Vainqueur: ${podium[0]?.pseudo} avec ${podium[0]?.score} pts !`);

  io.to(room.roomId).emit('gameEnded', {
    roomId: room.roomId,
    totalRounds: room.playOrder.length,
    podium
  });

  // La room repasse en statut 'waiting' pour permettre une revanche
  room.status = 'waiting';
  room.currentRoundIndex = 0;
  room.playOrder = [];
  room.currentGuesses = {};
}

/**
 * Gère le départ ou la déconnexion d'un joueur d'une salle.
 */
function handlePlayerLeave(socket: Socket, roomId: string, io: Server): void {
  socketRoomMap.delete(socket.id);
  socket.leave(roomId);

  const room = rooms[roomId];
  if (!room) return;

  const player = room.players[socket.id];
  if (!player) return;

  // Mode Fantôme : on conserve son profil et ses screenData
  player.isDisconnected = true;

  console.log(`[Départ joueur] Room: ${roomId}, Joueur: ${player.pseudo} (${socket.id})`);

  // Nettoyage complet : si tous les joueurs sont déconnectés, purge de la mémoire
  const connectedCount = getConnectedPlayersCount(room);
  if (connectedCount === 0) {
    clearRoomTimeout(room);
    delete rooms[roomId];
    console.log(`[Room supprimée] Room ${roomId} supprimée car vide.`);
    return;
  }

  // Si l'hôte part en phase d'attente, réattribution au premier connecté
  if (room.status === 'waiting' && room.hostId === socket.id) {
    const nextHostEntry = Object.entries(room.players).find(([_, p]) => !p.isDisconnected);
    if (nextHostEntry) {
      room.hostId = nextHostEntry[0];
      console.log(`[Nouvel hôte] Room ${roomId}: ${room.players[room.hostId]?.pseudo} (${room.hostId})`);
    }
  }

  // Si en cours de partie, vérifier si le départ permet de finir le round anticipativement
  if (room.status === 'playing') {
    checkAndAdvanceIfAllGuessed(io, room);
  }

  // Broadcast playerLeft à la room
  io.to(roomId).emit('playerLeft', {
    roomId,
    hostId: room.hostId,
    leftPlayer: {
      id: socket.id,
      pseudo: player.pseudo
    },
    players: getPublicPlayers(room)
  });
}
