# PetConnect 🐾

Une application mobile Flutter amusante et moderne pour l'adoption d'animaux de compagnie.

## 🌈 Caractéristiques

- **Design moderne et mignon** : Thème turquoise (#12ABB0) avec des animations douces
- **6 écrans complets** :
  - Splash Screen avec animations
  - Page de connexion avec authentification sociale
  - Page d'inscription
  - Réinitialisation de mot de passe
  - Page d'accueil avec tips et animaux adoptables
  - Page de profil avec badges

## 🚀 Installation

1. Assurez-vous d'avoir Flutter installé sur votre machine
2. Clonez le projet
3. Installez les dépendances :
```bash
flutter pub get
```

4. Ajoutez les images dans le dossier `assets/images/` :
   - `logo.png` - Logo de l'application
   - `pett.png` - Image de chiens/animaux pour le splash screen

5. Lancez l'application :
```bash
flutter run
```

## 📱 Écrans

### 1. Welcome Screen (Splash)
- Animation zoom-in/zoom-out sur le logo
- Texte animé "Welcome to PetConnect"
- Redirection automatique vers la page de connexion après 3 secondes

### 2. Login Screen
- Champs email et password
- Liens "Forgot Password?" et "Create Account"
- Boutons de connexion sociale (Google, Facebook, GitHub)
- Design moderne avec coins arrondis

### 3. Register Screen
- Champs : First Name, Last Name, Email, Password
- Animation slide-in à l'apparition
- Emojis animaux dans les champs

### 4. Forgot Password Screen
- Champ email pour réinitialisation
- Message de confirmation après envoi

### 5. Home Screen
- Section "Tips of the Day" avec images
- Grille d'animaux adoptables
- Navigation entre Home et Profile
- Cœurs animés sur les cartes d'animaux

### 6. Profile Screen
- Photo de profil
- Informations utilisateur (nom, prénom, email)
- Bouton "Edit Profile"
- Section badges (Adopt Lover, Animal Hero)
- Bouton logout

## 🎨 Palette de couleurs

- **Couleur principale** : #12ABB0 (turquoise)
- **Couleurs secondaires** : Blanc et Noir
- **Style** : Moderne, doux, mignon (pet theme 🐶🐱)

## 📦 Dépendances

- `google_fonts` : Polices modernes
- `animated_text_kit` : Animations de texte
- `shared_preferences` : Stockage local
- `cached_network_image` : Images en cache

## 🛠️ Technologies utilisées

- Flutter
- Dart
- Material Design 3

## 📝 Notes

- Les images `logo.png` et `pett.png` doivent être ajoutées dans le dossier `assets/images/`
- L'authentification sociale nécessite une configuration supplémentaire pour fonctionner en production
- Les badges peuvent être étendus avec un système de récompenses

## 👥 Équipe

Projet développé par l'équipe PetConnect

---

Made with ❤️ and 🐾 for pet lovers!



