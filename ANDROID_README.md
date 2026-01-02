# PetConnect - Application Android Native 🐾

Une application Android native en Java pour l'adoption d'animaux de compagnie avec un design moderne et amusant.

## 📱 Description

PetConnect est une application mobile Android développée en Java qui permet aux utilisateurs de découvrir et d'adopter des animaux de compagnie. L'application propose un design moderne avec un thème turquoise (#12ABB0) et des animations fluides.

## 🎨 Caractéristiques

### Écrans implémentés

1. **WelcomeActivity (Splash Screen)**
   - Animation zoom-in sur le logo
   - Texte animé "Welcome to PetConnect"
   - Image d'animaux
   - Redirection automatique vers la page de connexion après 3 secondes

2. **LoginActivity**
   - Champs email et mot de passe
   - Lien "Forgot Password?"
   - Lien "Create Account"
   - Boutons de connexion sociale (Google, Facebook, GitHub)
   - Design moderne avec coins arrondis

3. **RegisterActivity**
   - Formulaire d'inscription complet
   - Champs : First Name, Last Name, Email, Password
   - Animation slide-in à l'apparition
   - Validation des champs

4. **ForgotPasswordActivity**
   - Champ email pour réinitialisation
   - Bouton "Send Verification Email"
   - Message de confirmation

5. **HomeActivity**
   - Section "Tips of the Day"
   - Grille d'animaux adoptables
   - Bouton "See More"
   - Navigation avec BottomNavigationView

6. **ProfileActivity**
   - Informations utilisateur
   - Bouton "Edit Profile"
   - Section badges (Adopt Lover, Animal Hero)
   - Bouton "Logout"

## 🚀 Installation

### Prérequis

- Android Studio (version récente)
- JDK 11 ou supérieur
- Android SDK (API level 24 minimum)
- Gradle

### Étapes

1. **Cloner ou télécharger le projet**

2. **Ouvrir le projet dans Android Studio**
   - File → Open → Sélectionner le dossier du projet

3. **Synchroniser Gradle**
   - Android Studio devrait synchroniser automatiquement
   - Sinon : File → Sync Project with Gradle Files

4. **Ajouter les images (optionnel)**
   - Placez `logo.png` dans `app/src/main/res/drawable/`
   - Placez `pett.png` dans `app/src/main/res/drawable/`
   - Ou modifiez les références dans les layouts pour utiliser vos propres images

5. **Lancer l'application**
   - Connectez un appareil Android ou lancez un émulateur
   - Cliquez sur Run (▶️) ou appuyez sur Shift+F10

## 📁 Structure du projet

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/petconnect/
│   │   │   ├── WelcomeActivity.java
│   │   │   ├── LoginActivity.java
│   │   │   ├── RegisterActivity.java
│   │   │   ├── ForgotPasswordActivity.java
│   │   │   ├── HomeActivity.java
│   │   │   └── ProfileActivity.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_welcome.xml
│   │   │   │   ├── activity_login.xml
│   │   │   │   ├── activity_register.xml
│   │   │   │   ├── activity_forgot_password.xml
│   │   │   │   ├── activity_home.xml
│   │   │   │   ├── activity_profile.xml
│   │   │   │   └── dialog_edit_profile.xml
│   │   │   ├── drawable/
│   │   │   │   ├── rounded_button.xml
│   │   │   │   ├── rounded_edittext.xml
│   │   │   │   └── social_button_bg.xml
│   │   │   ├── anim/
│   │   │   │   ├── zoom_in.xml
│   │   │   │   ├── zoom_out.xml
│   │   │   │   ├── slide_in_up.xml
│   │   │   │   └── fade_in.xml
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── menu/
│   │   │       └── bottom_navigation.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
```

## 🎨 Palette de couleurs

- **Couleur principale** : #12ABB0 (turquoise)
- **Couleur principale foncée** : #0E8B8F
- **Couleurs secondaires** : Blanc et Noir
- **Gris clair** : #F5F5F5
- **Gris moyen** : #9E9E9E

## 🔧 Dépendances

Les dépendances sont gérées par Gradle et incluent :

- `androidx.appcompat:appcompat`
- `com.google.android.material:material`
- `androidx.activity:activity`
- `androidx.constraintlayout:constraintlayout`

## 📝 Notes importantes

### Images

Les layouts utilisent des icônes système par défaut. Pour utiliser vos propres images :

1. Placez vos images dans `app/src/main/res/drawable/`
2. Modifiez les références dans les layouts XML :
   ```xml
   android:src="@drawable/logo"
   ```

### Authentification sociale

Les boutons de connexion sociale (Google, Facebook, GitHub) affichent actuellement des Toasts. Pour une implémentation complète :

1. Ajoutez les SDK appropriés dans `build.gradle.kts`
2. Configurez les clés API dans les services respectifs
3. Implémentez la logique d'authentification dans les activités

### Base de données

L'application n'utilise pas encore de base de données. Pour persister les données :

1. Utilisez SharedPreferences pour les données simples
2. Ou intégrez Room Database pour des données plus complexes
3. Ou connectez-vous à une API backend

## 🐛 Résolution des problèmes

### Erreur de compilation
- Vérifiez que tous les fichiers sont sauvegardés
- Nettoyez le projet : Build → Clean Project
- Rebuild : Build → Rebuild Project

### Erreur "R cannot be resolved"
- Synchronisez Gradle : File → Sync Project with Gradle Files
- Invalidez les caches : File → Invalidate Caches / Restart

### L'application ne se lance pas
- Vérifiez que vous avez un appareil/émulateur connecté
- Vérifiez les logs dans Logcat pour les erreurs
- Vérifiez que toutes les activités sont déclarées dans AndroidManifest.xml

## 🚀 Prochaines étapes

- [ ] Ajouter une base de données pour stocker les données utilisateur
- [ ] Implémenter l'authentification sociale complète
- [ ] Ajouter une API backend pour les animaux adoptables
- [ ] Implémenter un système de badges dynamique
- [ ] Ajouter plus d'animations et d'effets visuels
- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Optimiser les performances
- [ ] Ajouter le support multilingue

## 👥 Équipe

Projet développé par l'équipe PetConnect

## 📄 Licence

Ce projet est développé dans le cadre d'un projet académique.

---

**Made with ❤️ and 🐾 for pet lovers!**


