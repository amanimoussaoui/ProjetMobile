# Guide d'Intégration Firebase AI (Gemini) pour le Chatbot

## 📋 Vue d'ensemble

Ce guide explique comment configurer et utiliser Firebase AI (Gemini) pour le chatbot PetConnect.

## 🔑 Étape 1 : Obtenir une clé API Gemini

1. **Allez sur Google AI Studio** : https://makersuite.google.com/app/apikey
2. **Connectez-vous** avec votre compte Google
3. **Créez une nouvelle clé API** :
   - Cliquez sur "Create API Key"
   - Sélectionnez votre projet Google Cloud (ou créez-en un nouveau)
   - Copiez la clé API générée

## 🔧 Étape 2 : Configurer la clé API dans l'application

1. **Ouvrez** `app/src/main/res/values/strings.xml`
2. **Remplacez** `YOUR_GEMINI_API_KEY_HERE` par votre clé API :

```xml
<string name="gemini_api_key">VOTRE_CLE_API_ICI</string>
```

⚠️ **Important** : Ne commitez jamais votre clé API dans Git ! Ajoutez `strings.xml` à `.gitignore` ou utilisez des variables d'environnement.

## 🚀 Étape 3 : Utilisation

Le chatbot utilise maintenant Firebase Gemini AI automatiquement :

1. **Ouvrez l'application** PetConnect
2. **Allez dans Profile** → **Assistant Chatbot**
3. **Posez vos questions** sur :
   - L'adoption d'animaux
   - Les soins et la santé
   - Le comportement animal
   - La nutrition
   - Les urgences vétérinaires

## 🎯 Fonctionnalités

### Contexte Personnalisé
Le chatbot est configuré avec un prompt système spécialisé pour PetConnect :
- Expert en animaux de compagnie
- Conseils sur l'adoption, soins, comportement
- Réponses amicales et professionnelles
- Recommandations vétérinaires quand nécessaire

### Historique de Conversation
- Maintient le contexte des 5 derniers messages
- Permet des conversations naturelles et cohérentes
- Limite automatiquement à 10 messages pour optimiser les performances

### Fallback Intelligent
- Si Gemini API échoue, utilise les réponses prédéfinies de `ChatbotAssistant`
- Garantit toujours une réponse à l'utilisateur

## 📊 Modèle Utilisé

- **Modèle** : `gemini-pro` via API v1
- **URL API** : `https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent`
- **Température** : 0.7 (équilibre créativité/précision)
- **Max Tokens** : 1024 (réponses concises)

**Note** : Si vous obtenez une erreur 404, essayez ces alternatives :
- `gemini-1.5-flash` : `v1/models/gemini-1.5-flash:generateContent`
- `gemini-1.5-pro` : `v1/models/gemini-1.5-pro:generateContent`
- `gemini-pro` : `v1/models/gemini-pro:generateContent` (actuel)

## 🔒 Sécurité

### Bonnes Pratiques :
1. **Ne partagez jamais** votre clé API publiquement
2. **Limitez les quotas** dans Google Cloud Console
3. **Surveillez l'utilisation** via Google Cloud Console
4. **Utilisez des restrictions** d'IP si possible

### Configuration des Quotas :
1. Allez sur [Google Cloud Console](https://console.cloud.google.com/)
2. Sélectionnez votre projet
3. Allez dans **APIs & Services** → **Quotas**
4. Configurez les limites pour Gemini API

## 💰 Coûts

Gemini API propose un **tier gratuit généreux** :
- **60 requêtes par minute** (gratuit)
- **1,500 requêtes par jour** (gratuit)
- Au-delà : tarification payante (voir [pricing](https://ai.google.dev/pricing))

## 🐛 Dépannage

### Erreur : "API key not configured"
- Vérifiez que `gemini_api_key` est bien défini dans `strings.xml`
- Assurez-vous que la clé n'est pas vide

### Erreur : "API key invalid"
- Vérifiez que la clé API est correcte
- Vérifiez que l'API Gemini est activée dans Google Cloud Console

### Erreur : "Quota exceeded"
- Vous avez dépassé la limite gratuite
- Attendez ou augmentez le quota dans Google Cloud Console

### Réponses lentes
- Vérifiez votre connexion Internet
- Les réponses peuvent prendre 2-5 secondes (normal)

## 📝 Fichiers Modifiés

- ✅ `app/src/main/java/com/example/petconnect/utils/FirebaseGeminiService.java` (nouveau)
- ✅ `app/src/main/java/com/example/petconnect/ChatbotActivity.java` (modifié)
- ✅ `app/src/main/res/values/strings.xml` (ajout clé API)

## 🎉 Résultat

Votre chatbot utilise maintenant **Firebase AI (Gemini)** pour fournir des réponses intelligentes et contextuelles sur les animaux de compagnie !

