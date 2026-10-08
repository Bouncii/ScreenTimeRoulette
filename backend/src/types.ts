export interface ScreenDataItem {
  name: string;
  timeInMinutes: number;
  opens: number;
}

export interface Player {
  pseudo: string;
  score: number;
  isDisconnected: boolean; // Gestion du mode "fantôme"
  screenData: ScreenDataItem[];
}

export type RoomStatus = 'waiting' | 'playing' | 'finished';

export interface Guess {
  guessedId: string;
  timeToGuess: number; // en secondes
}

export interface Room {
  roomId: string; // Code à 6 caractères (Alphanumérique)
  hostId: string; // socket.id du créateur
  status: RoomStatus; // 'waiting' | 'playing' | 'finished'
  players: {
    [socketId: string]: Player;
  };
  // État de la Game Loop
  playOrder: string[]; // Tableau de socketId mélangés aléatoirement
  currentRoundIndex: number; // Commence à 0
  roundStartTime: number; // Timestamp du début du round
  activeTimeout: NodeJS.Timeout | null; // Pour stocker le setTimeout (round ou intermission)
  currentGuesses: {
    [guesserId: string]: Guess;
  };
}

export interface PublicPlayer {
  id: string; // socketId
  pseudo: string;
  score: number;
  isDisconnected: boolean;
  isHost: boolean;
}

export interface CandidatePlayer {
  id: string;
  pseudo: string;
}

export interface RoomPayload {
  roomId: string;
  hostId: string;
  status: RoomStatus;
  players: PublicPlayer[];
}

// Payload schemas for Socket.io events
export interface CreateRoomPayload {
  pseudo: string;
  screenData?: ScreenDataItem[];
}

export interface JoinRoomPayload {
  roomCode: string;
  pseudo: string;
  screenData?: ScreenDataItem[];
}

export interface SubmitGuessPayload {
  guessedPlayerId: string;
}

export interface SocketCallbackResponse {
  success: boolean;
  message?: string;
  roomCode?: string;
}

export interface StartRoundPayload {
  roundNumber: number;
  totalRounds: number;
  screenData: ScreenDataItem[];
  candidates: CandidatePlayer[];
  duration: number; // en secondes
}

export interface GuesserResult {
  guessedId: string;
  guessedPseudo: string;
  isCorrect: boolean;
  pointsEarned: number;
  timeToGuess: number;
}

export interface RoundEndedPayload {
  roundNumber: number;
  totalRounds: number;
  mysteryPlayer: {
    id: string;
    pseudo: string;
  };
  guesses: {
    [guesserId: string]: GuesserResult;
  };
  leaderboard: PublicPlayer[];
}

export interface GameEndedPayload {
  roomId: string;
  totalRounds: number;
  podium: PublicPlayer[];
}
