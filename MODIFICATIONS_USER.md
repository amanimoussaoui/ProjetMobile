# Modifications apportées - Projet User

## 📁 Organisation des fichiers

Tous les fichiers ont été réorganisés dans le dossier `lib/user/` pour faciliter la gestion du groupe :
- `lib/user/screens/` - Tous les écrans
- `lib/user/constants/` - Constantes et couleurs
- `lib/user/services/` - Services (authentification, etc.)

## ✅ Fonctionnalités implémentées

### 1. Authentification sociale (Google, Facebook, GitHub)

**Service créé :** `lib/user/services/auth_service.dart`

- ✅ Connexion avec Google (`signInWithGoogle()`)
- ✅ Connexion avec Facebook (`signInWithFacebook()`)
- ⚠️ Connexion avec GitHub (placeholder - nécessite implémentation OAuth personnalisée)

**Utilisation dans login_screen.dart :**
- Les boutons sociaux sont maintenant fonctionnels
- Gestion des erreurs et des annulations
- Sauvegarde des données utilisateur dans SharedPreferences

### 2. Avatar style Bitmoji avec Avatar View Library

**Bibliothèque utilisée :** `avatar_view`

**Implémentation :**
- Avatar affiché dans le profil principal
- Avatar dans le dialog d'édition de profil
- Support des images personnalisées

### 3. Animation RIVE dans Login Screen

**Fichier RIVE :** `assets/rive/teddy_login.riv` (copié depuis le projet Android)

**Fonctionnalités :**
- ✅ Animation du teddy bear affichée au-dessus des champs de connexion
- ✅ Quand on tape dans l'email : le teddy bear suit avec ses yeux (via input `handsUp` ou `Look`)
- ✅ Quand on tape dans le mot de passe : le teddy bear cache ses yeux (via input `handsUp` ou `peek`)

**Note :** Les noms d'inputs peuvent varier selon votre fichier RIVE. Le code essaie automatiquement différents noms courants :
- `handsUp`, `HandsUp`, `isHandsUp`
- `Look`, `look`, `eyeLook`
- `peek`, `Peek`

### 4. Ajout de photos dans Edit Profile

**Bibliothèque utilisée :** `image_picker`

**Fonctionnalités :**
- ✅ Sélection de photos depuis la galerie
- ✅ Prise de photo avec l'appareil photo
- ✅ Dialog de choix de source (Galerie / Appareil photo)
- ✅ Affichage de l'image sélectionnée dans le dialog et dans le profil
- ✅ Support des avatars avec `AvatarView`

**Note sur les permissions :**
- `image_picker` gère automatiquement les permissions sur Android et iOS
- Pour Android, les permissions sont déjà déclarées dans `app/src/main/AndroidManifest.xml`
- Pour un projet Flutter pur, ajoutez les permissions dans `android/app/src/main/AndroidManifest.xml` si nécessaire

## 📦 Dépendances ajoutées

Toutes les dépendances ont été ajoutées dans `pubspec.yaml` :

```yaml
dependencies:
  rive: ^0.13.0
  image_picker: ^1.0.7
  google_sign_in: ^6.2.1
  flutter_facebook_auth: ^7.2.0
  avatar_view: ^2.0.0
  path_provider: ^2.1.2
```

**Action requise :** Exécutez `flutter pub get` pour installer les dépendances.

## 🔧 Configuration requise

### Google Sign-In
1. Configurez Google Sign-In dans la [Console Firebase](https://console.firebase.google.com/)
2. Ajoutez le SHA-1 de votre clé de signature dans Firebase Console
3. Téléchargez `google-services.json` (déjà présent dans `app/google-services.json`)

### Facebook Auth
1. Créez une application dans [Facebook Developers](https://developers.facebook.com/)
2. Ajoutez l'ID de l'application Facebook dans les configurations Android/iOS
3. Pour Android : ajoutez dans `android/app/src/main/res/values/strings.xml` :
   ```xml
   <string name="facebook_app_id">VOTRE_APP_ID</string>
   ```

### GitHub Auth
- L'authentification GitHub nécessite une implémentation OAuth personnalisée
- Vous pouvez utiliser `flutter_web_auth` ou `url_launcher` avec un backend OAuth
- Pour l'instant, un placeholder est en place

## 📝 Fichiers modifiés

### Nouveaux fichiers
- `lib/user/services/auth_service.dart` - Service d'authentification
- `assets/rive/teddy_login.riv` - Animation RIVE

### Fichiers modifiés
- `lib/main.dart` - Import mis à jour
- `lib/user/screens/login_screen.dart` - RIVE intégré, authentification sociale
- `lib/user/screens/profile_screen.dart` - Avatar View, image_picker
- `pubspec.yaml` - Nouvelles dépendances et assets

## 🚀 Prochaines étapes

1. **Installer les dépendances :**
   ```bash
   flutter pub get
   ```

2. **Configurer les services d'authentification :**
   - Google Sign-In (Firebase Console)
   - Facebook Auth (Facebook Developers)
   - GitHub Auth (si nécessaire)

3. **Tester l'animation RIVE :**
   - Vérifiez que les noms des inputs dans votre fichier RIVE correspondent
   - Si les animations ne fonctionnent pas, vérifiez les noms des inputs dans Rive Editor

4. **Tester image_picker :**
   - Testez sur un appareil réel ou un émulateur
   - Les permissions sont demandées automatiquement

5. **Note sur la galerie vide :**
   - Si la galerie apparaît vide, cela peut être dû aux permissions
   - `image_picker` devrait gérer cela automatiquement
   - Vérifiez que les permissions sont accordées dans les paramètres de l'appareil

## ⚠️ Notes importantes

- Le projet semble être un hybride Android natif + Flutter
- Les permissions Android sont déjà configurées dans `app/src/main/AndroidManifest.xml`
- Pour un projet Flutter pur, vérifiez `android/app/src/main/AndroidManifest.xml`
- L'animation RIVE peut nécessiter des ajustements selon votre fichier `.riv` spécifique





