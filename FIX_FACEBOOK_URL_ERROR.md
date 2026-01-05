# 🔧 Correction de l'Erreur Facebook "URL not allowed"

## ❌ Erreur Rencontrée

```
Given URL is not allowed by the Application configuration: 
One or more of the given URLs is not allowed by the App's settings. 
To use this URL you must add a valid native platform in your App's settings.
```

## ✅ Solution : Configurer les URLs Autorisées dans Facebook Developer Console

### Étape 1 : Accéder à Facebook Developer Console

1. Allez sur [Facebook Developers](https://developers.facebook.com/)
2. Connectez-vous avec votre compte Facebook
3. Sélectionnez votre application (App ID: `1363328681667400`)

### Étape 2 : Ajouter la Plateforme Android

1. Dans le menu de gauche, allez dans **Settings** → **Basic**
2. Faites défiler jusqu'à la section **Platforms**
3. Cliquez sur **Add Platform**
4. Sélectionnez **Android**

### Étape 3 : Configurer la Plateforme Android

Remplissez les champs suivants :

1. **Package Name** : 
   ```
   com.example.petconnect
   ```
   (Vérifiez dans `app/build.gradle.kts` → `applicationId`)

2. **Class Name** : 
   ```
   com.example.petconnect.LoginActivity
   ```
   (L'activité qui gère le login Facebook)

3. **Key Hashes** :
   - Pour le développement, vous devez ajouter votre SHA-1
   - **Méthode 1** : Obtenez votre SHA-1 avec cette commande (Windows PowerShell) :
   ```powershell
   keytool -list -v -keystore $env:USERPROFILE\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
   - **Méthode 2** : Utilisez Gradle (depuis la racine du projet) :
   ```powershell
   .\gradlew signingReport
   ```
   - Cherchez "SHA1:" dans la sortie et copiez la valeur (sans les deux-points)
   - Ajoutez-la dans Facebook Developer Console → Settings → Basic → Key Hashes

### Étape 4 : Configurer les URLs de Redirection

1. Allez dans **Settings** → **Basic**
2. Faites défiler jusqu'à **App Domains**
3. Ajoutez ces domaines (si nécessaire) :
   ```
   facebook.com
   fbcdn.net
   ```

4. Allez dans **Facebook Login** → **Settings**
5. Dans **Valid OAuth Redirect URIs**, ajoutez :
   ```
   fb1363328681667400://authorize
   https://www.facebook.com/connect/login_success.html
   https://www.facebook.com/connect/login_success.html?code=
   ```

### Étape 5 : Vérifier le Client Token

1. Allez dans **Settings** → **Basic**
2. Vérifiez que le **Client Token** correspond à celui dans `strings.xml` :
   ```xml
   <string name="facebook_client_token">62848ce2ccff856d6b542f72842d1173</string>
   ```

### Étape 6 : Vérifier les Permissions

1. Allez dans **Facebook Login** → **Settings**
2. Vérifiez que les permissions suivantes sont activées :
   - ✅ `email`
   - ✅ `public_profile`

### Étape 7 : Mode Développement vs Production

**Pour le développement :**
- Assurez-vous que votre application est en **Mode Développement**
- Ajoutez-vous comme **Testeur** ou **Développeur** dans **Roles** → **Roles**

**Pour la production :**
- Passez l'application en **Mode Live**
- Soumettez l'application pour révision si nécessaire

## 🔍 Vérification

Après avoir configuré :

1. **Rebuild** votre application Android
2. **Testez** la connexion Facebook
3. L'erreur devrait être résolue

## 📝 Checklist de Configuration Facebook

- [ ] Plateforme Android ajoutée dans Facebook Developer Console
- [ ] Package Name configuré : `com.example.petconnect`
- [ ] Class Name configuré : `com.example.petconnect.LoginActivity`
- [ ] SHA-1 ajouté dans Key Hashes
- [ ] URLs de redirection configurées
- [ ] Client Token vérifié
- [ ] Permissions `email` et `public_profile` activées
- [ ] Application en Mode Développement (pour les tests)

## 🐛 Si le Problème Persiste

1. **Vérifiez les logs** dans Logcat pour plus de détails
2. **Vérifiez que** `google-services.json` est à jour
3. **Vérifiez que** Facebook est activé dans Firebase Console → Authentication
4. **Vérifiez que** l'App ID et Client Token sont corrects dans `strings.xml`

## 📚 Ressources

- [Documentation Facebook Login Android](https://developers.facebook.com/docs/facebook-login/android)
- [Firebase Authentication Facebook](https://firebase.google.com/docs/auth/android/facebook-login)

