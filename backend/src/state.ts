import { Room, PublicPlayer, RoomPayload } from './types.js';

// Configuration du jeu (durées et marges)
export const GAME_CONFIG = {
  ROUND_DURATION_SECONDS: 30,
  SERVER_TIMEOUT_MS: 32000, // 30s + 2s de tolérance réseau
  INTERMISSION_DURATION_MS: 8000 // 8s de pause entre chaque round
};

/**
 * Permet d'ajuster la configuration du jeu (très utile pour accélérer les tests unitaires).
 */
export function setGameConfig(overrides: Partial<typeof GAME_CONFIG>): void {
  Object.assign(GAME_CONFIG, overrides);
}

export function resetGameConfig(): void {
  GAME_CONFIG.ROUND_DURATION_SECONDS = 30;
  GAME_CONFIG.SERVER_TIMEOUT_MS = 32000;
  GAME_CONFIG.INTERMISSION_DURATION_MS = 8000;
}

// Dictionnaire global stocké en mémoire RAM indexé par roomId (code à 6 caractères majuscules)
export const rooms: Record<string, Room> = {};

// Association rapide socketId -> roomId
export const socketRoomMap: Map<string, string> = new Map();

const ROOM_CODE_CHARS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';

/**
 * Génère un code unique de room à 6 caractères alphanumériques majuscules (ex: "X7B9Q2").
 */
export function generateRoomCode(length: number = 6): string {
  let attempts = 0;
  const maxAttempts = 10000;

  while (attempts < maxAttempts) {
    let code = '';
    for (let i = 0; i < length; i++) {
      const randomIndex = Math.floor(Math.random() * ROOM_CODE_CHARS.length);
      code += ROOM_CODE_CHARS.charAt(randomIndex);
    }

    if (!rooms[code]) {
      return code;
    }
    attempts++;
  }

  throw new Error('Impossible de générer un code de room unique.');
}

/**
 * Mélange un tableau de manière aléatoire (algorithme de Fisher-Yates).
 */
export function shuffleArray<T>(array: T[]): T[] {
  const shuffled = [...array];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
}

/**
 * Calcule les points selon la formule Kahoot :
 * 1000 * ((duration - tempsMis) / duration)
 * Plafonne le temps entre 0 et duration, arrondi à l'entier supérieur ou égal à 0.
 */
export function calculateScore(timeInSeconds: number, duration: number = GAME_CONFIG.ROUND_DURATION_SECONDS): number {
  const clampedTime = Math.max(0, Math.min(duration, timeInSeconds));
  const rawScore = 1000 * ((duration - clampedTime) / duration);
  return Math.max(0, Math.round(rawScore));
}

/**
 * Nettoie le timer actif d'une room pour éviter les fuites de mémoire.
 */
export function clearRoomTimeout(room: Room): void {
  if (room.activeTimeout) {
    clearTimeout(room.activeTimeout);
    room.activeTimeout = null;
  }
}

/**
 * Retourne la liste publique des joueurs (sans exposer leurs données d'écran secrètes).
 */
export function getPublicPlayers(room: Room): PublicPlayer[] {
  return Object.entries(room.players).map(([id, player]) => ({
    id,
    pseudo: player.pseudo,
    score: player.score,
    isDisconnected: player.isDisconnected,
    isHost: id === room.hostId
  }));
}

/**
 * Retourne les données publiques complètes d'une room.
 */
export function getPublicRoomData(room: Room): RoomPayload {
  return {
    roomId: room.roomId,
    hostId: room.hostId,
    status: room.status,
    players: getPublicPlayers(room)
  };
}

/**
 * Compte le nombre de joueurs actuellement connectés dans une room.
 */
export function getConnectedPlayersCount(room: Room): number {
  return Object.values(room.players).filter((player) => !player.isDisconnected).length;
}

/**
 * Supprime la room si elle est en statut 'waiting' et qu'il n'y a plus aucun joueur connecté.
 * Retourne true si la room a été supprimée.
 */
export function cleanupWaitingRoomIfEmpty(roomId: string): boolean {
  const room = rooms[roomId];
  if (!room) return false;

  if (room.status === 'waiting' && getConnectedPlayersCount(room) === 0) {
    clearRoomTimeout(room);
    delete rooms[roomId];
    return true;
  }
  return false;
}

/**
 * Réinitialise l'état en mémoire (utilisé principalement pour les tests).
 */
export function resetState(): void {
  for (const key of Object.keys(rooms)) {
    clearRoomTimeout(rooms[key]);
    delete rooms[key];
  }
  socketRoomMap.clear();
  resetGameConfig();
}
