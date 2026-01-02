# Structure du projet PetConnect

## 📁 Organisation des fichiers

```
PetConnect/
│
├── lib/                          # Code source Dart/Flutter
│   ├── main.dart                # Point d'entrée principal
│   ├── constants/               # Constantes de l'application
│   │   ├── colors.dart         # Palette de couleurs
│   │   └── app_constants.dart  # Constantes générales
│   └── screens/                 # Écrans de l'application
│       ├── welcome_screen.dart      # Page 1: Splash Screen
│       ├── login_screen.dart        # Page 2: Login Page
│       ├── register_screen.dart     # Page 3: Register Page
│       ├── forgot_password_screen.dart  # Page 4: Forgot Password
│       ├── home_screen.dart         # Page 5: Home Page
│       └── profile_screen.dart      # Page 6: Profile Page
│
├── assets/                      # Ressources statiques
│   └── images/                  # Images de l'application
│       ├── logo.png            # Logo (à ajouter)
│       └── pett.png            # Image animaux (à ajouter)
│
├── pubspec.yaml                 # Configuration Flutter et dépendances
├── analysis_options.yaml        # Configuration du linter
├── README.md                    # Documentation principale
├── SETUP.md                     # Guide de configuration
└── .gitignore                   # Fichiers ignorés par Git
```

## 🎯 Écrans implémentés

### 1. WelcomeScreen (Splash Screen)
- ✅ Fond turquoise #12ABB0
- ✅ Logo centré avec animation zoom
- ✅ Texte animé "Welcome to PetConnect"
- ✅ Image pett.png
- ✅ Redirection automatique après 3 secondes

### 2. Login Screen
- ✅ Fond blanc
- ✅ Logo en haut
- ✅ Champs email et password
- ✅ Lien "Forgot Password?"
- ✅ Lien "Create Account"
- ✅ Bouton Login
- ✅ Boutons sociaux (Google, Facebook, GitHub)

### 3. Register Screen
- ✅ Fond blanc
- ✅ Champs : First Name, Last Name, Email, Password
- ✅ Emojis animaux dans les champs
- ✅ Animation slide-in
- ✅ Lien vers Login

### 4. Forgot Password Screen
- ✅ Champ email
- ✅ Bouton "Send Verification Email"
- ✅ Message de confirmation
- ✅ Design minimaliste

### 5. Home Screen
- ✅ Navbar avec onglets Home/Profile
- ✅ Section "Tips of the Day"
- ✅ Grille d'animaux adoptables
- ✅ Bouton "See More"
- ✅ Cœurs animés sur les cartes

### 6. Profile Screen
- ✅ Photo de profil
- ✅ Nom, prénom, email
- ✅ Bouton "Edit Profile"
- ✅ Section badges
- ✅ Bouton "Logout"

## 🎨 Thème

- **Couleur principale** : #12ABB0 (turquoise)
- **Couleurs secondaires** : Blanc et Noir
- **Style** : Moderne, doux, mignon
- **Police** : Google Fonts (Poppins)

## 📦 Dépendances principales

- `google_fonts` : Polices modernes
- `animated_text_kit` : Animations de texte
- `shared_preferences` : Stockage local
- `cached_network_image` : Images en cache

## 🚀 Prochaines étapes

1. Ajouter les images dans `assets/images/`
2. Configurer l'authentification sociale
3. Ajouter une base de données
4. Implémenter un système de badges dynamique
5. Ajouter plus d'animations

---

**Note** : Ce projet Flutter peut coexister avec le projet Android natif existant dans le dossier `app/`. Pour un projet Flutter pur, vous pouvez créer un nouveau projet Flutter séparé ou utiliser cette structure.



