# Configuration Flutter pour PetConnect

## ⚠️ Important

Ce projet contient actuellement à la fois :
- Un projet **Android natif** (Java/Kotlin) dans le dossier `app/`
- Un projet **Flutter** (Dart) dans le dossier `lib/`

## 🎯 Option 1 : Créer un nouveau projet Flutter

Si vous voulez un projet Flutter pur, suivez ces étapes :

1. **Créer un nouveau projet Flutter** :
   ```bash
   flutter create petconnect_flutter
   cd petconnect_flutter
   ```

2. **Copier les fichiers** :
   - Copiez le dossier `lib/` dans le nouveau projet
   - Copiez le dossier `assets/` dans le nouveau projet
   - Copiez `pubspec.yaml` (ou fusionnez les dépendances)
   - Copiez `analysis_options.yaml`

3. **Installer les dépendances** :
   ```bash
   flutter pub get
   ```

4. **Ajouter les images** :
   - Placez `logo.png` et `pett.png` dans `assets/images/`

5. **Lancer l'application** :
   ```bash
   flutter run
   ```

## 🎯 Option 2 : Convertir ce projet en projet Flutter

Si vous voulez convertir le projet actuel en projet Flutter :

1. **Initialiser Flutter dans le projet actuel** :
   ```bash
   flutter create .
   ```

2. **Remplacer les fichiers** :
   - Les fichiers dans `lib/` sont déjà créés
   - Le `pubspec.yaml` est déjà configuré
   - Les assets sont déjà déclarés

3. **Installer les dépendances** :
   ```bash
   flutter pub get
   ```

4. **Ajouter les images** :
   - Placez `logo.png` et `pett.png` dans `assets/images/`

5. **Lancer l'application** :
   ```bash
   flutter run
   ```

## 📱 Structure Flutter complète

Un projet Flutter devrait avoir cette structure :

```
petconnect/
├── android/              # Code Android natif (généré par Flutter)
├── ios/                  # Code iOS natif (généré par Flutter)
├── lib/                  # Code Dart/Flutter
│   ├── main.dart
│   ├── constants/
│   └── screens/
├── assets/               # Ressources
│   └── images/
├── pubspec.yaml          # Configuration Flutter
└── README.md
```

## 🔧 Commandes utiles

```bash
# Vérifier l'installation Flutter
flutter doctor

# Nettoyer le projet
flutter clean

# Installer les dépendances
flutter pub get

# Lancer l'application
flutter run

# Builder pour Android
flutter build apk

# Builder pour iOS
flutter build ios

# Builder pour Web
flutter build web
```

## 🎨 Personnalisation

### Changer les couleurs
Modifiez `lib/constants/colors.dart`

### Changer les polices
Modifiez `lib/main.dart` (section `GoogleFonts`)

### Ajouter des écrans
Créez de nouveaux fichiers dans `lib/screens/`

## 📝 Notes

- Le dossier `app/` contient du code Android natif qui n'est pas nécessaire pour Flutter
- Flutter génère automatiquement les dossiers `android/` et `ios/` lors de la création du projet
- Les assets doivent être déclarés dans `pubspec.yaml`
- Les images doivent être dans `assets/images/` pour être accessibles

## 🚀 Démarrage rapide

1. Assurez-vous d'avoir Flutter installé
2. Exécutez `flutter pub get`
3. Ajoutez les images dans `assets/images/`
4. Exécutez `flutter run`

---

**Note** : Si vous préférez travailler avec Android natif (Java/Kotlin), vous devrez réimplémenter les écrans dans le code Android natif. Le code Flutter que j'ai créé est spécifique à Flutter/Dart.



