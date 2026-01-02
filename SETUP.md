# Guide de configuration - PetConnect 🐾

## 📋 Prérequis

1. **Flutter SDK** : Assurez-vous d'avoir Flutter installé (version 3.0.0 ou supérieure)
   ```bash
   flutter --version
   ```

2. **Dépendances** : Installez les dépendances du projet
   ```bash
   flutter pub get
   ```

## 🖼️ Configuration des assets

### Images requises

Placez les images suivantes dans le dossier `assets/images/` :

1. **logo.png** 
   - Logo de l'application
   - Taille recommandée : 512x512 pixels
   - Format : PNG avec transparence
   - Utilisé dans : Splash Screen, Login Screen, Profile Screen

2. **pett.png**
   - Image de chiens/animaux drôles
   - Taille recommandée : 800x600 pixels ou plus
   - Format : PNG ou JPG
   - Utilisé dans : Splash Screen, Home Screen (Tips section)

### Si vous n'avez pas les images

L'application fonctionnera avec des icônes de remplacement si les images ne sont pas trouvées. Vous pouvez :
- Utiliser des icônes Material Design par défaut
- Ajouter vos propres images plus tard
- Utiliser des images de placeholder depuis des services comme Unsplash

## 🚀 Lancement de l'application

### Sur Android
```bash
flutter run
```

### Sur iOS
```bash
flutter run
```

### Sur Web
```bash
flutter run -d chrome
```

## 📱 Structure du projet

```
lib/
├── main.dart                 # Point d'entrée de l'application
├── constants/
│   ├── colors.dart          # Constantes de couleurs
│   └── app_constants.dart   # Constantes de l'application
└── screens/
    ├── welcome_screen.dart      # Splash Screen
    ├── login_screen.dart        # Page de connexion
    ├── register_screen.dart     # Page d'inscription
    ├── forgot_password_screen.dart  # Réinitialisation mot de passe
    ├── home_screen.dart         # Page d'accueil
    └── profile_screen.dart      # Page de profil
```

## 🎨 Personnalisation

### Changer les couleurs

Modifiez le fichier `lib/constants/colors.dart` pour changer la palette de couleurs.

### Changer les polices

Modifiez `lib/main.dart` pour changer la police Google Fonts utilisée.

### Ajouter des écrans

1. Créez un nouveau fichier dans `lib/screens/`
2. Ajoutez la navigation dans les écrans existants
3. Mettez à jour la structure de navigation si nécessaire

## 🔧 Dépannage

### Erreur : "Unable to load asset"
- Vérifiez que les images sont dans `assets/images/`
- Vérifiez que `pubspec.yaml` contient la section `assets:`
- Exécutez `flutter pub get` après modification de `pubspec.yaml`

### Erreur : "Package not found"
- Exécutez `flutter pub get`
- Vérifiez votre connexion internet
- Vérifiez que les versions dans `pubspec.yaml` sont correctes

### L'application ne démarre pas
- Vérifiez que Flutter est correctement installé : `flutter doctor`
- Nettoyez le projet : `flutter clean`
- Réinstallez les dépendances : `flutter pub get`

## 📝 Notes importantes

- Les fonctionnalités d'authentification sociale (Google, Facebook, GitHub) nécessitent une configuration supplémentaire pour fonctionner en production
- Les données utilisateur sont stockées localement (vous pouvez ajouter une base de données plus tard)
- Les badges dans le profil sont statiques pour le moment (peuvent être étendus avec un système de récompenses)

## 🎯 Prochaines étapes

1. Ajoutez les images dans `assets/images/`
2. Configurez l'authentification sociale si nécessaire
3. Ajoutez une base de données pour stocker les données
4. Implémentez un système de badges dynamique
5. Ajoutez plus d'animations et d'effets visuels

---

Bon développement ! 🚀🐾



