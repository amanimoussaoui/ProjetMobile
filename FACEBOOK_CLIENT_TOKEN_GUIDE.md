# 🔑 Guide : Trouver le Facebook Client Token

## 📋 Ce qui est déjà configuré

✅ **Google Web Client ID** : `322627227660-es9qbonsnuvd7igcdad0brhgirlgj1fm.apps.googleusercontent.com`
✅ **Facebook App ID** : `1363328681667400`
✅ **Facebook Login Protocol Scheme** : `fb1363328681667400`

## 🔍 Trouver le Facebook Client Token

Le **Client Token** (aussi appelé "App Secret" ou "Code Secret") est différent de l'App ID. Voici comment le trouver :

### Méthode 1 : Depuis Facebook Developers Console

1. Allez sur [Facebook Developers](https://developers.facebook.com/)
2. Connectez-vous avec votre compte Facebook
3. Sélectionnez votre application (App ID: `1363328681667400`)
4. Dans le menu de gauche, allez dans **Settings** → **Basic**
5. Vous verrez deux champs importants :
   - **App ID** : `1363328681667400` (déjà configuré ✅)
   - **App Secret** : C'est votre Client Token (à copier)

### Méthode 2 : Depuis Firebase Console

1. Allez sur [Firebase Console](https://console.firebase.google.com/)
2. Sélectionnez votre projet
3. Allez dans **Authentication** → **Sign-in method**
4. Cliquez sur **Facebook**
5. Vous verrez :
   - **App ID** : `1363328681667400` (déjà configuré ✅)
   - **App Secret** : C'est votre Client Token (à copier)

## 📝 Configuration finale

Une fois que vous avez le **Client Token**, ajoutez-le dans `app/src/main/res/values/strings.xml` :

```xml
<string name="facebook_client_token">VOTRE_CLIENT_TOKEN_ICI</string>
```

Remplacez `YOUR_FACEBOOK_CLIENT_TOKEN_HERE` par votre vrai Client Token.

## ⚠️ Important

- **Ne partagez jamais** votre Client Token publiquement
- Le Client Token est différent de l'App ID
- Si vous ne trouvez pas le Client Token, vous pouvez le réinitialiser depuis Facebook Developers Console

## 🔧 Vérification

Après avoir ajouté le Client Token, rebuild l'application :
1. **Build** → **Clean Project**
2. **Build** → **Rebuild Project**

Les connexions Google et Facebook devraient maintenant fonctionner !

## 📱 Test

1. Lancez l'application
2. Sur la page de login, testez :
   - **Sign in with Google** → Devrait ouvrir le sélecteur de compte Google
   - **Sign in with Facebook** → Devrait ouvrir le login Facebook

## 🆘 Si ça ne fonctionne pas

1. Vérifiez que Google Sign-In est activé dans Firebase Console
2. Vérifiez que Facebook Sign-In est activé dans Firebase Console avec l'App ID et App Secret
3. Vérifiez que votre SHA-1 est ajouté dans Firebase Console (pour Google)
4. Vérifiez les logs Android pour voir les erreurs exactes

