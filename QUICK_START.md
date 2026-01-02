# 🚀 Guide de Démarrage Rapide - 5 Minutes

## ⚡ Configuration Express

### 1️⃣ Firebase (2 min)

1. **Créer un projet Firebase**
   - [console.firebase.google.com](https://console.firebase.google.com/)
   - Nouveau projet → "PetConnect-Event"

2. **Ajouter une app Android**
   - Package name: `com.example.petconnect_event`
   - Télécharger `google-services.json`
   - Placer dans: `app/google-services.json`

3. **Activer Firestore**
   - Build → Firestore Database
   - "Créer une base de données"
   - **Mode test** ✅
   - Région: `europe-west1`

### 2️⃣ Données de Test (1 min)

Dans Firestore Console, créer 3 événements:

**Collection:** `events`

**Document 1:**
```
ID: (auto)
title: "Promenade canine"
description: "Belle promenade au parc avec vos chiens"
location: "Parc Central, Paris"
eventDate: 15 février 2026, 14:00 (Timestamp)
imageUrl: ""
organizerId: "user123"
maxParticipants: 20
currentParticipants: 0
isActive: true
createdAt: (timestamp actuel)
```

**Document 2:**
```
ID: (auto)
title: "Adoption de chats"
description: "Venez adopter un adorable chat"
location: "Refuge, Lyon"
eventDate: 20 février 2026, 10:00 (Timestamp)
imageUrl: ""
organizerId: "user123"
maxParticipants: 30
currentParticipants: 0
isActive: true
createdAt: (timestamp actuel)
```

**Document 3:**
```
ID: (auto)
title: "Atelier dressage"
description: "Apprenez le dressage positif"
location: "Centre Canin, Marseille"
eventDate: 25 février 2026, 15:30 (Timestamp)
imageUrl: ""
organizerId: "user456"
maxParticipants: 15
currentParticipants: 0
isActive: true
createdAt: (timestamp actuel)
```

### 3️⃣ Lancer l'App (2 min)

1. **Ouvrir dans Android Studio**
   ```
   File → Open → Sélectionner le dossier PetConnect_Event
   ```

2. **Sync Gradle**
   - Attendre la synchronisation automatique
   - Ou cliquer sur l'icône "Sync" 🔄

3. **Créer un émulateur** (si nécessaire)
   - Tools → Device Manager
   - Create Device → Pixel 6
   - System Image: API 34 (Android 14)

4. **Run!**
   - Cliquer sur ▶️ Run
   - Sélectionner l'émulateur
   - Attendre le build (~2-3 min la première fois)

---

## ✅ Vérification Rapide

L'app doit afficher:
- ✅ Liste des 3 événements
- ✅ Bouton FAB (+) en bas à droite
- ✅ Cliquer sur un événement → Voir les détails
- ✅ Bouton "S'inscrire" fonctionnel

---

## 🐛 Problème?

### App ne démarre pas
```bash
# Dans Android Studio Terminal
./gradlew clean
./gradlew build
```

### Liste vide
1. Vérifier que Firestore est en **mode test**
2. Vérifier que les 3 événements existent
3. Vérifier `google-services.json` est bien placé dans `app/`

### Erreur Firebase
- Vérifier que le package name est: `com.example.petconnect_event`
- Re-télécharger `google-services.json`
- Rebuild le projet

---

## 📱 Tester les Fonctionnalités

### Créer un événement
1. Cliquer sur FAB (+)
2. Remplir tous les champs
3. Sélectionner une date future
4. Enregistrer → Doit apparaître dans la liste

### S'inscrire
1. Cliquer sur un événement
2. Cliquer sur "S'inscrire"
3. Remplir nom + email
4. Confirmer → Toast de confirmation

### Vérifier dans Firestore
- Collection `events` → Voir les événements
- Collection `event_registrations` → Voir les inscriptions

---

## 📚 Documentation Complète

Pour plus de détails, consulter:
- [README.md](README.md) - Documentation complète
- [TESTING_CHECKLIST.md](TESTING_CHECKLIST.md) - Tests détaillés

---

## 🎉 C'est Prêt!

Votre application PetConnect Event est maintenant fonctionnelle et prête à être testée!

**Temps total:** ~5-10 minutes

**Prochaines étapes:**
- Tester toutes les fonctionnalités
- Personnaliser les couleurs/textes
- Ajouter vos propres événements
- Déployer sur un appareil réel

**Bon développement! 🐾**
