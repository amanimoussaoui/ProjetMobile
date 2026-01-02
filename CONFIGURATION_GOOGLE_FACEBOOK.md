# Configuration Google et Facebook Sign-In

## 🔐 Configuration Google Sign-In

### Problème: "Erreur de connexion Google"

**Solution:**

1. **Obtenir le Web Client ID:**
   - Allez sur [Firebase Console](https://console.firebase.google.com/)
   - Sélectionnez votre projet
   - Allez dans **Project Settings** (⚙️) → **General**
   - Dans la section **Your apps**, trouvez votre application Android
   - Cliquez sur **Add fingerprint** et ajoutez votre SHA-1
   - Allez dans **Authentication** → **Sign-in method** → **Google**
   - Activez Google Sign-In
   - Le **Web Client ID** se trouve dans la section OAuth 2.0

2. **Ajouter le Web Client ID dans strings.xml:**
   ```xml
   <string name="default_web_client_id">VOTRE_WEB_CLIENT_ID_ICI.apps.googleusercontent.com</string>
   ```

3. **Alternative - Obtenir depuis Google Cloud Console:**
   - Allez sur [Google Cloud Console](https://console.cloud.google.com/)
   - Sélectionnez votre projet
   - Allez dans **APIs & Services** → **Credentials**
   - Trouvez **OAuth 2.0 Client IDs**
   - Copiez le **Client ID** de type "Web application"
   - Ajoutez-le dans `strings.xml`

## 📘 Configuration Facebook Sign-In

### Ajout du Facebook SDK

Le Facebook SDK a été ajouté dans `build.gradle.kts`:
```kotlin
implementation("com.facebook.android:facebook-login:16.2.0")
```

### Configuration requise:

1. **Créer une application Facebook:**
   - Allez sur [Facebook Developers](https://developers.facebook.com/)
   - Créez une nouvelle application
   - Ajoutez **Facebook Login** comme produit

2. **Obtenir App ID et App Secret:**
   - Dans les paramètres de votre application Facebook
   - Copiez l'**App ID** et l'**App Secret**

3. **Configurer dans Firebase:**
   - Allez dans Firebase Console → **Authentication** → **Sign-in method**
   - Activez **Facebook**
   - Ajoutez l'**App ID** et l'**App Secret**

4. **Ajouter dans strings.xml:**
   ```xml
   <string name="facebook_app_id">VOTRE_APP_ID</string>
   <string name="fb_login_protocol_scheme">fbVOTRE_APP_ID</string>
   ```

5. **Ajouter dans AndroidManifest.xml:**
   ```xml
   <meta-data
       android:name="com.facebook.sdk.ApplicationId"
       android:value="@string/facebook_app_id"/>
   <meta-data
       android:name="com.facebook.sdk.ClientToken"
       android:value="@string/facebook_client_token"/>
   ```

## 🔧 Configuration GitHub Sign-In

### Problème: "Erreur de connexion GitHub"

**Solution:**

1. **Activer GitHub dans Firebase:**
   - Allez dans Firebase Console → **Authentication** → **Sign-in method**
   - Activez **GitHub**
   - Aucune configuration supplémentaire requise côté client

2. **Vérifier les permissions:**
   - Assurez-vous que GitHub est bien activé dans Firebase
   - Vérifiez votre connexion internet

## 🎨 Configuration Rive Animation

### Ajout du fichier Rive

1. **Placez votre fichier Rive:**
   - Renommez votre fichier `4765-9622-login.riv` en `teddy_login.riv`
   - Placez-le dans `app/src/main/res/raw/teddy_login.riv`

2. **Structure de la State Machine:**
   - La state machine doit s'appeler **"Login Machine"**
   - Inputs booléens: `isChecking`, `isHandsUp`
   - Triggers: `fail`, `shake`

3. **L'animation se chargera automatiquement** dans LoginActivity

## ⚠️ Notes Importantes

- **Google:** Le Web Client ID est différent du Client ID Android
- **Facebook:** Nécessite une application Facebook configurée
- **GitHub:** Fonctionne immédiatement après activation dans Firebase
- **Rive:** Le fichier doit être en minuscules dans `res/raw/`

## 🔍 Dépannage

### Google Sign-In ne fonctionne toujours pas:
1. Vérifiez que le Web Client ID est correct (doit se terminer par `.apps.googleusercontent.com`)
2. Vérifiez que votre SHA-1 est ajouté dans Firebase Console
3. Vérifiez que Google Sign-In est activé dans Firebase Authentication

### Facebook Sign-In ne fonctionne pas:
1. Vérifiez que l'App ID est correct dans `strings.xml`
2. Vérifiez que Facebook Login est activé dans votre application Facebook
3. Vérifiez que Facebook est activé dans Firebase Authentication

### GitHub Sign-In ne fonctionne pas:
1. Vérifiez que GitHub est activé dans Firebase Authentication
2. Vérifiez votre connexion internet
3. Vérifiez les logs pour plus de détails

### Rive ne s'affiche pas:
1. Vérifiez que le fichier `teddy_login.riv` existe dans `res/raw/`
2. Vérifiez que le nom de la state machine est "Login Machine"
3. Vérifiez les logs pour les erreurs de chargement

