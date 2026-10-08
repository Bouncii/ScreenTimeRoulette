import http from 'http';
import express from 'express';
import cors from 'cors';
import { Server } from 'socket.io';
import { registerSocketHandlers } from './socket.js';
import { rooms } from './state.js';

export function createServerApp() {
  const app = express();

  // Middleware CORS pour Express (autorise toutes les origines pour le dev mobile)
  app.use(cors({ origin: '*' }));
  app.use(express.json());

  // Routes HTTP de base
  app.get('/', (_req, res) => {
    res.json({
      name: 'Screen Time Roulette API',
      version: '1.0.0',
      status: 'online'
    });
  });

  app.get('/health', (_req, res) => {
    res.json({
      status: 'ok',
      activeRooms: Object.keys(rooms).length,
      uptime: process.uptime()
    });
  });

  // Endpoint de débogage pour visualiser les salles en cours (sans données privées)
  app.get('/rooms', (_req, res) => {
    const publicRooms = Object.values(rooms).map((r) => ({
      roomId: r.roomId,
      hostId: r.hostId,
      status: r.status,
      playerCount: Object.keys(r.players).length,
      connectedPlayers: Object.values(r.players).filter((p) => !p.isDisconnected).length
    }));
    res.json(publicRooms);
  });

  const server = http.createServer(app);

  // Configuration Socket.io avec CORS ouvert (origin: '*')
  const io = new Server(server, {
    cors: {
      origin: '*',
      methods: ['GET', 'POST']
    }
  });

  registerSocketHandlers(io);

  return { app, server, io };
}
