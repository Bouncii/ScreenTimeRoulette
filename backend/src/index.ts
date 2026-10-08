import { createServerApp } from './server.js';

const PORT = process.env.PORT ? parseInt(process.env.PORT, 10) : 3000;
const { server } = createServerApp();

server.listen(PORT, '0.0.0.0', () => {
  console.log(`===============================================`);
  console.log(`🚀 Screen Time Roulette Backend démarré !`);
  console.log(`📡 Port : ${PORT}`);
  console.log(`🌐 Accès local : http://localhost:${PORT}`);
  console.log(`🔌 Socket.io prêt (CORS: *)`);
  console.log(`===============================================`);
});
