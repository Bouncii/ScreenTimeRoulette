# Backend - Screen Time Roulette 📱🎲

Serveur de jeu multijoueur en temps réel pour **Screen Time Roulette**, développé avec **Node.js**, **Express**, **Socket.io** et **TypeScript**.

L'état des parties est entièrement géré en mémoire vive (RAM) via un dictionnaire global `rooms`.

---

## 🚀 Démarrage Rapide

### Prérequis
- Node.js >= 18 (testé avec Node.js 26)
- npm

### Installation des dépendances
```bash
npm install
```

### Lancement en mode développement (avec rechargement à chaud)
```bash
npm run dev
```

### Compilation et exécution en production
```bash
npm run build
npm start
```

### Exécution des tests unitaires et d'intégration
```bash
npm test
```

---

## 📱 Connexion depuis l'application Android

- **Émulateur Android Studio** : connectez votre client Socket.io à `http://10.0.2.2:3000`
- **Appareil physique (même réseau Wi-Fi)** : connectez votre client à `http://<IP_LOCALE_DE_VOTRE_PC>:3000` (ex: `http://192.168.1.50:3000`)
- **CORS** : Le serveur accepte toutes les origines (`origin: '*'`), facilitant les tests mobiles.

---

## 🎮 Moteur de Jeu (Game Loop)

### Déroulement chronologique
1. **Lancement (`startGame`)** :
   - Minimum 3 joueurs connectés requis, émis par l'hôte.
   - Les scores de tous les joueurs sont réinitialisés à 0.
   - Une playlist de rounds est générée aléatoirement (`playOrder`) avec **1 round par joueur**.
2. **Début de Round (`startRound`)** :
   - Le serveur diffuse les `screenData` du joueur mystère de manière anonymisée.
   - Tous les joueurs reçoivent également la liste des `candidates` (pseudos et IDs des joueurs) pour afficher les boutons de vote.
   - Durée annoncée au mobile : **30 secondes**.
   - Le serveur arme un timer de sécurité silencieux de **32 secondes** (2s de marge de tolérance réseau).
3. **Soumission des votes (`submitGuess`)** :
   - **Tout le monde vote**, y compris le Joueur Mystère (il doit lui-même deviner s'il s'agit de son profil ou d'un autre !).
   - Dès que **tous les joueurs actifs connectés ont voté**, le timer du serveur est annulé et le round se termine **immédiatement**.
4. **Fin de Round (`roundEnded`)** :
   - Calcul des points façon **Kahoot** pour chaque joueur :
     - Mauvaise réponse ou pas de réponse : `0 point`.
     - Bonne réponse : `1000 * ((30 - tempsMisPourRépondre) / 30)`.
   - Diffusion des résultats : qui était le joueur mystère, qui a voté quoi, points gagnés et classement mis à jour.
   - **Pause de 8 secondes** pour laisser le temps de voir les scores avant le round suivant.
5. **Fin de Partie (`gameEnded`)** :
   - Une fois tous les rounds joués, diffusion du podium final trié par score décroissant.
   - La room repasse automatiquement en statut `'waiting'` pour permettre de relancer une partie.

---

## 🔌 Spécification de l'API Socket.io

### 1. Phase de Lobby

#### `createRoom` (Client -> Serveur)
- Payload : `{ "pseudo": "Alice", "screenData": [ { "name": "Instagram", "timeInMinutes": 120, "opens": 45 } ] }`
- Callback : `{ "success": true, "roomCode": "X7B9Q2" }`

#### `joinRoom` (Client -> Serveur)
- Payload : `{ "roomCode": "X7B9Q2", "pseudo": "Bob", "screenData": [ ... ] }`
- Callback : `{ "success": true, "roomCode": "X7B9Q2" }` (ou `{ "success": false, "message": "..." }`)

#### `playerJoined` (Serveur -> Room)
- Diffusé à chaque nouvel arrivant avec la liste des joueurs connectés et leur pseudo.

#### `startGame` (Hôte -> Serveur)
- Payload : `{}`
- Callback : `{ "success": true }` (ou `{ "success": false, "message": "..." }`)
- Diffuse `gameStarted` à la room, puis déclenche le premier `startRound`.

---

### 2. Phase de Round (Game Loop)

#### `startRound` (Serveur -> Clients)
Diffusé à tous les joueurs au début de chaque round :
```json
{
  "roundNumber": 1,
  "totalRounds": 3,
  "screenData": [
    { "name": "TikTok", "timeInMinutes": 90, "opens": 30 },
    { "name": "YouTube", "timeInMinutes": 60, "opens": 15 }
  ],
  "candidates": [
    { "id": "socket_alice", "pseudo": "Alice" },
    { "id": "socket_bob", "pseudo": "Bob" },
    { "id": "socket_charlie", "pseudo": "Charlie" }
  ],
  "duration": 30
}
```

#### `submitGuess` (Client -> Serveur)
Envoyé par chaque joueur (y compris le joueur mystère s'il pense que c'est son propre profil) :
```json
{
  "guessedPlayerId": "socket_bob"
}
```
Callback : `{ "success": true }`

#### `roundEnded` (Serveur -> Clients)
Diffusé dès que tout le monde a voté ou à l'expiration du temps :
```json
{
  "roundNumber": 1,
  "totalRounds": 3,
  "mysteryPlayer": {
    "id": "socket_bob",
    "pseudo": "Bob"
  },
  "guesses": {
    "socket_alice": {
      "guessedId": "socket_bob",
      "guessedPseudo": "Bob",
      "isCorrect": true,
      "pointsEarned": 920,
      "timeToGuess": 2.4
    },
    "socket_bob": {
      "guessedId": "socket_bob",
      "guessedPseudo": "Bob",
      "isCorrect": true,
      "pointsEarned": 850,
      "timeToGuess": 4.5
    },
    "socket_charlie": {
      "guessedId": "socket_alice",
      "guessedPseudo": "Alice",
      "isCorrect": false,
      "pointsEarned": 0,
      "timeToGuess": 5.1
    }
  },
  "leaderboard": [
    { "id": "socket_alice", "pseudo": "Alice", "score": 920, "isDisconnected": false, "isHost": true },
    { "id": "socket_bob", "pseudo": "Bob", "score": 850, "isDisconnected": false, "isHost": false },
    { "id": "socket_charlie", "pseudo": "Charlie", "score": 0, "isDisconnected": false, "isHost": false }
  ]
}
```

#### `gameEnded` (Serveur -> Clients)
Diffusé à la fin de tous les rounds :
```json
{
  "roomId": "X7B9Q2",
  "totalRounds": 3,
  "podium": [
    { "id": "socket_alice", "pseudo": "Alice", "score": 2650, "isDisconnected": false, "isHost": true },
    { "id": "socket_bob", "pseudo": "Bob", "score": 1800, "isDisconnected": false, "isHost": false },
    { "id": "socket_charlie", "pseudo": "Charlie", "score": 950, "isDisconnected": false, "isHost": false }
  ]
}
```

---

### 3. Gestion des Déconnexions & Mode Fantôme

- **Mode Fantôme** : Si un joueur quitte en cours de partie, son profil et ses temps d'écran restent dans la rotation des rounds. Les autres joueurs peuvent toujours deviner son profil.
- **Fin anticipée préservée** : Le calcul "tous les joueurs ont voté" ne prend en compte que les joueurs **actuellement connectés** (`!isDisconnected`).
- **Nettoyage automatique** : Dès que tous les joueurs sont déconnectés, les timers actifs sont détruits (`clearTimeout`) et la room est supprimée de la mémoire pour garantir zéro fuite de RAM.
