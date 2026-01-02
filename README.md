# 🐾 PetConnect Event - Application Mobile Android

Application mobile de gestion d'événements pour animaux de compagnie développée avec Android Studio.

## 📋 Prérequis

- **Android Studio** : Hedgehog (2023.1.1) ou version ultérieure
- **JDK** : Version 17 ou supérieure
- **Android SDK** : API 26 (Android 8.0) minimum
- **Compte Firebase** : Pour la base de données Firestore

## 🚀 Installation et Configuration

### 1. Cloner/Ouvrir le Projet

```bash
# Ouvrir le dossier du projet dans Android Studio
# File > Open > Sélectionner : PetConnect_Event
```

### 2. Configuration Firebase

#### A. Créer un Projet Firebase

1. Allez sur [Firebase Console](https://console.firebase.google.com/)
2. Cliquez sur "Ajouter un projet"
3. Nommez votre projet (ex: "PetConnect-Event")
4. Suivez les étapes de création

#### B. Ajouter une Application Android

1. Dans Firebase Console, cliquez sur l'icône Android
2. Renseignez le nom du package : `com.example.petconnect_event`
3. Téléchargez le fichier `google-services.json`
4. Placez `google-services.json` dans : `app/`

**⚠️ IMPORTANT** : Le fichier `google-services.json` existe déjà mais doit être remplacé par le vôtre!

#### C. Activer Firestore

1. Dans Firebase Console, allez dans **"Build > Firestore Database"**
2. Cliquez sur **"Créer une base de données"**
3. Choisissez **"Commencer en mode test"** (pour le développement)
4. Sélectionnez une région (ex: europe-west1)

#### D. Règles de Sécurité Firestore (Mode Test)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Permettre la lecture/écriture pendant le développement (30 jours)
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2026, 2, 1);
    }
  }
}
```

**⚠️ ATTENTION** : En production, utilisez des règles de sécurité appropriées!

### 3. Synchronisation Gradle

```bash
# Dans Android Studio
# File > Sync Project with Gradle Files
# ou cliquez sur l'icône "Sync" dans la toolbar
```

### 4. Créer des Données de Test dans Firestore

#### A. Collection "events"

Dans Firebase Console > Firestore Database, créez la collection `events` avec des documents de test :

**Document 1 :**
```javascript
{
  "title": "Promenade canine au parc",
  "description": "Rejoignez-nous pour une belle promenade avec vos chiens au parc central. Tous les chiens sont les bienvenus!",
  "location": "Parc Central, Paris",
  "eventDate": Timestamp (ex: 15 février 2026 14:00),
  "imageUrl": "",
  "organizerId": "user123",
  "maxParticipants": 20,
  "currentParticipants": 5,
  "createdAt": Timestamp (maintenant),
  "isActive": true
}
```

**Document 2 :**
```javascript
{
  "title": "Adoption de chats",
  "description": "Venez rencontrer nos adorables chats à la recherche d'une famille aimante.",
  "location": "Refuge des Animaux, Lyon",
  "eventDate": Timestamp (ex: 20 février 2026 10:00),
  "imageUrl": "",
  "organizerId": "user123",
  "maxParticipants": 30,
  "currentParticipants": 12,
  "createdAt": Timestamp (maintenant),
  "isActive": true
}
```

**Document 3 :**
```javascript
{
  "title": "Atelier dressage canin",
  "description": "Apprenez les bases du dressage positif avec un éducateur canin professionnel.",
  "location": "Centre Canin, Marseille",
  "eventDate": Timestamp (ex: 25 février 2026 15:30),
  "imageUrl": "",
  "organizerId": "user456",
  "maxParticipants": 15,
  "currentParticipants": 8,
  "createdAt": Timestamp (maintenant),
  "isActive": true
}
```

#### B. Collection "event_registrations" (optionnel)

Cette collection se créera automatiquement lors des inscriptions dans l'app.

## 📱 Lancer l'Application

### Option 1 : Émulateur Android

1. **Créer un AVD (Android Virtual Device)** :
   - Tools > Device Manager
   - Create Device
   - Sélectionner un appareil (ex: Pixel 6)
   - Sélectionner une image système (API 34 recommandée)
   - Finish

2. **Lancer l'émulateur** :
   - Cliquer sur le bouton Play ▶️ à côté de l'émulateur
   - Attendre que l'émulateur démarre

3. **Exécuter l'application** :
   - Cliquer sur Run ▶️ dans Android Studio
   - Sélectionner l'émulateur
   - L'application se lance automatiquement

### Option 2 : Appareil Physique

1. **Activer le Mode Développeur** sur votre téléphone :
   - Paramètres > À propos du téléphone
   - Appuyer 7 fois sur "Numéro de build"

2. **Activer le Débogage USB** :
   - Paramètres > Options de développement
   - Activer "Débogage USB"

3. **Connecter le téléphone** :
   - Brancher via USB
   - Autoriser le débogage sur le téléphone

4. **Lancer l'app** :
   - Run ▶️ > Sélectionner votre appareil

## 🧪 Tester les Fonctionnalités

### 1. Liste des Événements
- ✅ Au démarrage, la liste des événements Firestore s'affiche
- ✅ Scroll pour voir tous les événements
- ✅ Vérifier que les dates, lieux et titres s'affichent correctement

### 2. Créer un Événement
- ✅ Cliquer sur le bouton FAB (➕) en bas à droite
- ✅ Remplir tous les champs
- ✅ Sélectionner une date future
- ✅ Cliquer sur "Enregistrer"
- ✅ Vérifier que l'événement apparaît dans la liste

### 3. Voir les Détails
- ✅ Cliquer sur un événement
- ✅ Vérifier que tous les détails s'affichent
- ✅ Vérifier le nombre de participants

### 4. S'inscrire à un Événement
- ✅ Dans les détails, cliquer sur "S'inscrire"
- ✅ Remplir nom et email
- ✅ Cliquer sur "Confirmer l'inscription"
- ✅ Vérifier le message de confirmation
- ✅ Vérifier dans Firestore que l'inscription est enregistrée

### 5. Événement Complet
- ✅ Créer un événement avec maxParticipants = 1
- ✅ S'inscrire à l'événement
- ✅ Vérifier que le bouton devient "Complet" et désactivé

## 🗂️ Structure du Projet

```
app/src/main/java/com/example/petconnect_event/
├── MainActivity.java                      # Point d'entrée
└── event/
    ├── activity/                          # Écrans (UI)
    │   ├── EventListActivity.java         # Liste des événements
    │   ├── EventDetailActivity.java       # Détails événement
    │   ├── AddEditEventActivity.java      # Créer/Modifier
    │   └── RegisterEventActivity.java     # Inscription
    ├── adapter/                           # RecyclerView Adapters
    │   ├── EventAdapter.java              # Adapter liste
    │   ├── EventViewHolder.java           # ViewHolder
    │   └── EventRegistrationAdapter.java  # Adapter inscriptions
    ├── model/                             # Modèles de données
    │   ├── Event.java                     # Entité Événement
    │   └── EventRegistration.java         # Entité Inscription
    ├── repository/                        # Accès données Firebase
    │   ├── EventRepository.java           # CRUD événements
    │   └── EventRegistrationRepository.java
    └── service/                           # Logique métier
        ├── EventService.java              # Service événements
        └── NotificationService.java       # Notifications
```

## 🐛 Résolution de Problèmes

### Erreur: "google-services.json not found"
➡️ **Solution** : Assurez-vous que `google-services.json` est dans `app/`

### Erreur: "FirebaseApp not initialized"
➡️ **Solution** : Vérifiez que `google-services.json` contient les bonnes informations

### Liste vide au démarrage
➡️ **Solution** : 
1. Vérifiez la connexion Internet
2. Vérifiez les règles Firestore (mode test activé)
3. Créez des événements de test dans Firestore

### Erreur de compilation Gradle
➡️ **Solution** :
```bash
# Dans le terminal Android Studio
./gradlew clean
./gradlew build
```

### Images ne s'affichent pas
➡️ **Note** : Glide est commenté dans le code. Pour activer :
```java
// Dans EventViewHolder.java et EventDetailActivity.java
// Décommenter les lignes Glide
Glide.with(context).load(imageUrl).into(imageView);
```

## 🔒 Sécurité Firebase (Production)

**⚠️ IMPORTANT** : Avant de déployer en production, modifiez les règles Firestore :

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /events/{eventId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    match /event_registrations/{registrationId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow delete: if request.auth.uid == resource.data.userId;
    }
  }
}
```

## 📦 Build APK pour Installation

```bash
# Dans Android Studio
Build > Build Bundle(s) / APK(s) > Build APK(s)

# L'APK sera dans :
# app/build/outputs/apk/debug/app-debug.apk
```

## 🚀 Prochaines Étapes (Améliorations)

- [ ] Ajouter Firebase Authentication
- [ ] Implémenter les notifications push
- [ ] Ajouter upload d'images avec Firebase Storage
- [ ] Créer un système de filtres/recherche
- [ ] Ajouter des catégories d'événements
- [ ] Implémenter un système de favoris
- [ ] Ajouter la géolocalisation
- [ ] Tests unitaires et d'intégration

## 📞 Support

Pour toute question ou problème :
- Vérifiez les logs Android Studio (Logcat)
- Consultez la documentation Firebase
- Vérifiez que toutes les dépendances sont à jour

## 📝 Licence

Ce projet est à usage éducatif.

---

**Bon développement! 🐾**
