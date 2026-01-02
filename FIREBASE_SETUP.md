# 🔥 Configuration Firebase - PetConnect

Ce guide vous explique comment configurer Firebase pour l'application PetConnect.

## 📋 Prérequis

- Un compte Google
- Un projet Android Studio configuré
- Android Studio installé avec les SDK requis

## 🚀 Étapes de configuration

### 1. Créer un projet Firebase

1. Allez sur [Firebase Console](https://console.firebase.google.com/)
2. Cliquez sur **"Ajouter un projet"** ou **"Add project"**
3. Entrez le nom de votre projet (ex: "PetConnect")
4. Suivez les étapes pour créer le projet

### 2. Ajouter une application Android au projet Firebase

1. Dans la console Firebase, cliquez sur l'icône Android (ou "Ajouter une application")
2. Renseignez les informations suivantes :
   - **Nom du package Android** : `com.example.petconnect`
     (Vérifiez dans `app/build.gradle.kts` dans `defaultConfig.applicationId`)
   - **Surnom de l'application** : `PetConnect` (optionnel)
   - **Certificat de signature** : Optionnel pour le moment

3. Cliquez sur **"Enregistrer l'application"** ou **"Register app"**

### 3. Télécharger et ajouter google-services.json

1. **Téléchargez** le fichier `google-services.json`
2. **Placez-le** dans le répertoire `app/` de votre projet Android
   ```
   PetConnect/
   └── app/
       ├── google-services.json  ← Placez le fichier ici
       ├── build.gradle.kts
       └── src/
   ```

⚠️ **Important** : Le fichier doit être exactement dans `app/` et non dans `app/src/main/`

### 4. Activer les services Firebase

#### 4.1. Authentication (Authentification)

1. Dans la console Firebase, allez dans **"Authentication"** (dans le menu de gauche)
2. Cliquez sur **"Get started"** ou **"Commencer"**
3. Dans l'onglet **"Sign-in method"** ou **"Méthode de connexion"** :
   - Activez **"Email/Password"** (Email/Mot de passe)
   - Cliquez sur **"Email/Password"** puis activez-le
   - Cliquez sur **"Enregistrer"** ou **"Save"**

#### 4.2. Firestore Database (Base de données)

1. Dans la console Firebase, allez dans **"Firestore Database"** (dans le menu de gauche)
2. Cliquez sur **"Create database"** ou **"Créer une base de données"**
3. Choisissez le mode :
   - **Mode production** : Recommandé pour la production
   - **Mode test** : Pour le développement (expire après 30 jours)
4. Choisissez l'emplacement de votre base de données (ex: `europe-west1`)
5. Cliquez sur **"Enable"** ou **"Activer"**

#### 4.3. Storage (Stockage) - Optionnel pour l'instant

1. Dans la console Firebase, allez dans **"Storage"** (dans le menu de gauche)
2. Cliquez sur **"Get started"** ou **"Commencer"**
3. Suivez les étapes pour configurer les règles de sécurité

### 5. Configurer les règles de sécurité Firestore

1. Dans **"Firestore Database"**, allez dans l'onglet **"Rules"** ou **"Règles"**
2. Remplacez les règles par défaut par :

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Règles pour la collection users
    match /users/{userId} {
      // Un utilisateur ne peut lire/écrire que son propre document
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

3. Cliquez sur **"Publish"** ou **"Publier"**

⚠️ **Note** : Ces règles permettent à chaque utilisateur de lire/écrire uniquement son propre document. Vous pouvez ajuster ces règles selon vos besoins.

### 6. Vérifier la configuration dans Android Studio

1. **Synchronisez Gradle** :
   - Menu : `File → Sync Project with Gradle Files`
   - Ou cliquez sur l'icône de synchronisation dans la barre d'outils

2. **Vérifiez que le plugin Google Services est bien appliqué** dans `app/build.gradle.kts` :
   ```kotlin
   plugins {
       alias(libs.plugins.android.application)
       id("com.google.gms.google-services")
   }
   ```

3. **Vérifiez les dépendances Firebase** dans `app/build.gradle.kts` :
   ```kotlin
   dependencies {
       // Firebase BOM
       implementation(platform(libs.firebase.bom))
       implementation(libs.firebase.auth)
       implementation(libs.firebase.firestore)
       implementation(libs.firebase.storage)
       // ... autres dépendances
   }
   ```

## ✅ Vérification

Pour vérifier que tout fonctionne :

1. **Compilez le projet** : `Build → Make Project`
2. Si aucune erreur n'apparaît, la configuration est correcte
3. **Lancez l'application** sur un appareil ou un émulateur
4. Essayez de créer un compte dans l'application

## 📱 Tester l'authentification

1. Lancez l'application
2. Allez sur l'écran d'inscription
3. Créez un compte avec email/mot de passe
4. Vérifiez dans la console Firebase → **Authentication** → **Users** qu'un nouvel utilisateur apparaît
5. Connectez-vous avec ce compte
6. Vérifiez dans **Firestore Database** → **Data** qu'un document a été créé dans la collection `users`

## 🗂️ Structure Firestore

L'application crée automatiquement les collections suivantes :

### Collection: `users`
Chaque document représente un utilisateur avec les champs suivants :
- `userId` (string) : ID de l'utilisateur (identique à l'UID Firebase Auth)
- `firstName` (string) : Prénom
- `lastName` (string) : Nom
- `email` (string) : Email
- `phoneNumber` (string, optionnel) : Numéro de téléphone
- `address` (string, optionnel) : Adresse
- `photoUrl` (string, optionnel) : URL de la photo de profil
- `role` (string) : Rôle de l'utilisateur (`user`, `admin`, `adoption_center`)
- `animalPreferences` (map) : Préférences d'animaux
- `createdAt` (number) : Date de création (timestamp)
- `updatedAt` (number) : Date de mise à jour (timestamp)

## 🔧 Dépannage

### Erreur : "File google-services.json is missing"
- Vérifiez que le fichier `google-services.json` est bien dans le répertoire `app/`
- Synchronisez Gradle : `File → Sync Project with Gradle Files`

### Erreur : "Default FirebaseApp is not initialized"
- Vérifiez que le plugin Google Services est bien appliqué dans `app/build.gradle.kts`
- Vérifiez que le fichier `google-services.json` est valide

### L'application crash au démarrage
- Vérifiez les logs dans Logcat pour voir l'erreur exacte
- Vérifiez que les services Firebase sont activés dans la console Firebase

### Les données ne s'enregistrent pas dans Firestore
- Vérifiez les règles de sécurité Firestore
- Vérifiez que Firestore est bien activé dans la console Firebase
- Vérifiez les logs dans Logcat

### L'email de vérification n'est pas envoyé
- Vérifiez que l'authentification Email/Password est activée dans Firebase Console
- Vérifiez les paramètres de votre compte Firebase

## 📚 Ressources

- [Documentation Firebase Android](https://firebase.google.com/docs/android/setup)
- [Documentation Firebase Authentication](https://firebase.google.com/docs/auth)
- [Documentation Cloud Firestore](https://firebase.google.com/docs/firestore)
- [Documentation Firebase Storage](https://firebase.google.com/docs/storage)

## 🎯 Prochaines étapes

Une fois Firebase configuré, vous pouvez :
1. ✅ Tester l'inscription et la connexion
2. ✅ Tester la gestion du profil utilisateur
3. 📸 Ajouter le téléchargement de photos de profil (Firebase Storage)
4. 🔐 Implémenter le changement de mot de passe
5. 👥 Ajouter d'autres rôles (admin, adoption_center)
6. 🐾 Créer les collections pour les animaux, événements, etc.

---

**Note** : N'oubliez pas de ne jamais commiter le fichier `google-services.json` contenant des informations sensibles sur un dépôt public. Il devrait déjà être dans `.gitignore`.



