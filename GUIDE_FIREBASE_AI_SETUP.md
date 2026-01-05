# Guide de Configuration Firebase AI (Gemini Developer)

## 📋 Vue d'ensemble

Ce guide explique comment configurer Firebase AI avec l'API Gemini Developer directement depuis Firebase Console, sans avoir besoin de gérer une clé API manuellement.

## 🔧 Étape 1 : Activer Firebase AI dans Firebase Console

1. **Allez sur Firebase Console** : https://console.firebase.google.com/
2. **Sélectionnez votre projet** PetConnect
3. **Allez dans "Build" → "AI"** (ou "Extensions" → "Firebase AI")
4. **Cliquez sur "Get started"** ou "Activer Firebase AI"
5. **Sélectionnez "Gemini Developer API"** comme fournisseur
6. **Acceptez les conditions d'utilisation**
7. **Cliquez sur "Activer"**

Firebase va automatiquement :
- Créer une clé API Gemini pour vous
- Activer les API requises
- Configurer l'accès aux modèles Gemini

## 📦 Étape 2 : Vérifier les Dépendances

Les dépendances Firebase AI Logic ont été ajoutées dans `app/build.gradle.kts` :

```kotlin
// Firebase AI Logic SDK
implementation("com.google.firebase:firebase-ai")

// Required for Java (ListenableFuture from Guava Android)
implementation("com.google.guava:guava:31.0.1-android")

// Required for streaming operations (Publisher from Reactive Streams)
implementation("org.reactivestreams:reactive-streams:1.0.4")
```

**Action requise** : Sync Gradle pour télécharger les dépendances.

## 🔑 Étape 3 : Configuration Automatique

**Bonne nouvelle** : Avec Firebase AI Logic SDK, vous n'avez **PAS besoin** de gérer une clé API manuellement ! 

Firebase AI Logic utilise automatiquement :
- La configuration de votre projet Firebase
- Les credentials Firebase de votre application
- La clé API générée automatiquement par Firebase

**Aucune configuration supplémentaire n'est nécessaire** dans votre code Android !

## 🔐 Étape 4 : Configurer Firebase App Check (Recommandé)

Firebase App Check protège votre API contre les abus :

1. **Dans Firebase Console** → **Build** → **App Check**
2. **Cliquez sur "Get started"**
3. **Pour le développement** : Activez "Debug provider"
4. **Pour la production** : Configurez Play Integrity (Android) ou DeviceCheck (iOS)

**Note** : Pour le développement, le code utilise déjà `DebugAppCheckProviderFactory`.

## 🚀 Étape 5 : Utilisation

Le chatbot utilise maintenant Firebase AI automatiquement :

1. **Ouvrez l'application** PetConnect
2. **Allez dans Profile** → **Assistant Chatbot**
3. **Posez vos questions** sur les animaux
4. **Recevez des réponses intelligentes** via Gemini AI

## 🎯 Avantages de Firebase AI

### ✅ Avantages
- **Pas besoin de clé API manuelle** : Firebase gère tout automatiquement
- **Sécurité intégrée** : App Check protège contre les abus
- **Monitoring** : Surveillez l'utilisation dans Firebase Console
- **Remote Config** : Mettez à jour les prompts à distance
- **Tests A/B** : Testez différents modèles et prompts
- **Modèles expérimentaux** : Accès aux derniers modèles Gemini

### 📊 Modèles Disponibles
- **gemini-2.5-flash** : Rapide et efficace (utilisé par défaut)
- **gemini-1.5-flash** : Alternative rapide
- **gemini-1.5-pro** : Plus puissant, plus lent
- **Modèles expérimentaux** : Accès via Firebase Console

**Note** : Le modèle peut être changé dans `FirebaseGeminiService.java` :
```java
.generativeModel("gemini-1.5-pro") // au lieu de gemini-2.5-flash
```

## 🔍 Vérification

Pour vérifier que Firebase AI est bien configuré :

1. **Firebase Console** → **Build** → **AI**
2. Vérifiez que "Gemini Developer API" est activé
3. Vérifiez les quotas et l'utilisation

## 💰 Coûts

Firebase AI utilise les mêmes quotas que l'API Gemini directe :
- **Tier gratuit** : 60 requêtes/minute, 1,500 requêtes/jour
- **Au-delà** : Tarification payante (voir [pricing](https://ai.google.dev/pricing))

## 🐛 Dépannage

### Erreur : "Firebase AI n'est pas initialisé"
- Vérifiez que Firebase AI est activé dans Firebase Console
- Vérifiez que `google-services.json` est à jour
- Vérifiez les logs pour plus de détails

### Erreur : "Model not found"
- Vérifiez que le modèle est disponible dans votre région
- Essayez de changer le modèle dans `FirebaseGeminiService.java` :
  ```java
  .setModelName("gemini-1.5-pro") // au lieu de gemini-1.5-flash
  ```

### Erreur : "App Check failed"
- Pour le développement : Vérifiez que Debug provider est activé
- Pour la production : Configurez Play Integrity

## 📝 Fichiers Modifiés

- ✅ `app/build.gradle.kts` - Ajout dépendances Firebase AI Logic (firebase-ai, guava, reactive-streams)
- ✅ `app/src/main/java/com/example/petconnect/utils/FirebaseGeminiService.java` - Réécrit pour utiliser Firebase AI Logic SDK officiel
- ✅ Plus besoin de clé API dans `strings.xml` - Firebase gère tout automatiquement !

## 🎉 Résultat

Votre chatbot utilise maintenant **Firebase AI (Gemini Developer)** directement depuis Firebase, avec :
- ✅ Configuration automatique
- ✅ Sécurité intégrée (App Check)
- ✅ Monitoring dans Firebase Console
- ✅ Pas de gestion manuelle de clé API

