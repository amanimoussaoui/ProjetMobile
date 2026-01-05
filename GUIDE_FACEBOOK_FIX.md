# 🔧 Guide de Correction Facebook Login - Erreur "URL not allowed"

## ❌ Erreur Rencontrée

```
Given URL is not allowed by the Application configuration: 
One or more of the given URLs is not allowed by the App's settings. 
To use this URL you must add a valid native platform in your App's settings.
```

## ✅ Solution Rapide : Configurer Facebook Developer Console

### Étape 1 : Accéder à Facebook Developer Console

1. Allez sur [Facebook Developers](https://developers.facebook.com/)
2. Connectez-vous avec votre compte Facebook
3. Sélectionnez votre application (App ID: `1363328681667400`)

### Étape 2 : Ajouter la Plateforme Android

1. Dans le menu de gauche, allez dans **Settings** → **Basic**
2. Faites défiler jusqu'à la section **Platforms**
3. Si Android n'est pas présent, cliquez sur **Add Platform** → **Android**

### Étape 3 : Configurer la Plateforme Android

Remplissez les champs suivants :

1. **Package Name** : 
   ```
   com.example.petconnect
   ```

2. **Class Name** : 
   ```
   com.example.petconnect.LoginActivity
   ```

3. **Key Hashes** :
   - Obtenez votre SHA-1 avec cette commande (Windows PowerShell) :
   ```powershell
   keytool -list -v -keystore $env:USERPROFILE\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
   - Cherchez "SHA1:" dans la sortie et copiez la valeur (sans les deux-points)
   - Ajoutez-la dans Facebook Developer Console → Settings → Basic → Key Hashes

### Étape 4 : Configurer les URLs de Redirection OAuth

1. Allez dans **Facebook Login** → **Settings**
2. Dans **Valid OAuth Redirect URIs**, ajoutez ces URLs :
   ```
   fb1363328681667400://authorize
   https://www.facebook.com/connect/login_success.html
   https://www.facebook.com/connect/login_success.html?code=
   ```

### Étape 5 : Vérifier les Paramètres

1. **Client Token** : Vérifiez qu'il correspond à celui dans `strings.xml` :
   ```xml
   <string name="facebook_client_token">62848ce2ccff856d6b542f72842d1173</string>
   ```

2. **App ID** : Vérifiez qu'il correspond :
   ```xml
   <string name="facebook_app_id">1363328681667400</string>
   ```

3. **Permissions** : Dans **Facebook Login** → **Settings**, vérifiez que ces permissions sont activées :
   - ✅ `email`
   - ✅ `public_profile`

### Étape 6 : Mode Développement

**Pour le développement :**
- Assurez-vous que votre application est en **Mode Développement**
- Ajoutez-vous comme **Testeur** ou **Développeur** dans **Roles** → **Roles**

## 🔍 Vérification

Après avoir configuré :

1. **Sauvegardez** les changements dans Facebook Developer Console
2. **Attendez 2-3 minutes** pour que les changements soient propagés
3. **Rebuild** votre application Android
4. **Testez** la connexion Facebook

## 📝 Checklist de Configuration

- [ ] Plateforme Android ajoutée dans Facebook Developer Console
- [ ] Package Name configuré : `com.example.petconnect`
- [ ] Class Name configuré : `com.example.petconnect.LoginActivity`
- [ ] SHA-1 ajouté dans Key Hashes
- [ ] URLs de redirection OAuth configurées (3 URLs)
- [ ] Client Token vérifié
- [ ] App ID vérifié
- [ ] Permissions `email` et `public_profile` activées
- [ ] Application en Mode Développement (pour les tests)

## 🐛 Si le Problème Persiste

1. **Vérifiez les logs** dans Logcat pour plus de détails
2. **Vérifiez que** `google-services.json` est à jour
3. **Vérifiez que** Facebook est activé dans Firebase Console → Authentication → Sign-in method
4. **Vérifiez que** l'App ID et Client Token sont corrects dans `strings.xml`
5. **Attendez quelques minutes** après avoir modifié la configuration Facebook
6. **Déconnectez-vous et reconnectez-vous** à l'application Facebook sur votre appareil

## 📚 Ressources

- [Documentation Facebook Login Android](https://developers.facebook.com/docs/facebook-login/android)
- [Firebase Authentication Facebook](https://firebase.google.com/docs/auth/android/facebook-login)

