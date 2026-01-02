# Guide d'installation - PetConnect Flutter

## 📍 Où exécuter `flutter pub get` ?

La commande `flutter pub get` doit être exécutée dans le **répertoire racine du projet Flutter**, c'est-à-dire dans le dossier qui contient le fichier `pubspec.yaml`.

Dans votre cas :
```
C:\Users\amani\Desktop\4SLEAM1\PetConnect
```

## 🔧 Étape 1 : Installer Flutter

### Option A : Installation Windows (Recommandé)

1. **Télécharger Flutter** :
   - Allez sur https://flutter.dev/docs/get-started/install/windows
   - Téléchargez le SDK Flutter

2. **Extraire Flutter** :
   - Extrayez le fichier ZIP dans un dossier (par exemple `C:\src\flutter`)
   - **Ne pas** extraire dans un dossier avec des espaces ou des permissions spéciales

3. **Ajouter Flutter au PATH** :
   - Ouvrez "Variables d'environnement" dans Windows
   - Ajoutez le chemin vers Flutter : `C:\src\flutter\bin`
   - Ou ajoutez-le temporairement dans PowerShell :
     ```powershell
     $env:PATH += ";C:\src\flutter\bin"
     ```

4. **Vérifier l'installation** :
   ```powershell
   flutter doctor
   ```

### Option B : Utiliser Chocolatey (Plus rapide)

```powershell
# Installer Chocolatey si pas déjà installé
# Puis installer Flutter
choco install flutter
```

### Option C : Utiliser Git (Pour développeurs)

```powershell
# Cloner Flutter depuis GitHub
git clone https://github.com/flutter/flutter.git -b stable
# Ajouter au PATH
$env:PATH += ";C:\chemin\vers\flutter\bin"
```

## 🚀 Étape 2 : Vérifier Flutter

Après l'installation, vérifiez que Flutter fonctionne :

```powershell
flutter --version
flutter doctor
```

## 📦 Étape 3 : Installer les dépendances

Une fois Flutter installé, allez dans le dossier du projet :

```powershell
cd C:\Users\amani\Desktop\4SLEAM1\PetConnect
```

Puis exécutez :

```powershell
flutter pub get
```

Cette commande va :
- Lire le fichier `pubspec.yaml`
- Télécharger toutes les dépendances listées
- Installer les packages nécessaires

## 🎯 Étape 4 : Lancer l'application

```powershell
# Vérifier les appareils disponibles
flutter devices

# Lancer l'application
flutter run
```

## 📱 Prérequis supplémentaires

### Pour Android :
1. Installer Android Studio
2. Installer Android SDK
3. Configurer un émulateur Android ou connecter un appareil

### Pour iOS (Mac uniquement) :
1. Installer Xcode
2. Installer CocoaPods
3. Configurer un simulateur iOS

## 🔍 Résolution des problèmes

### Erreur : "flutter n'est pas reconnu"
- Vérifiez que Flutter est dans le PATH
- Redémarrez le terminal/PowerShell
- Vérifiez l'installation avec `flutter doctor`

### Erreur : "No devices found"
- Connectez un appareil Android/iOS
- Ou lancez un émulateur/simulateur
- Vérifiez avec `flutter devices`

### Erreur : "Pub get failed"
- Vérifiez votre connexion internet
- Vérifiez que le fichier `pubspec.yaml` est valide
- Essayez `flutter pub cache repair`

## 📝 Commandes utiles

```powershell
# Vérifier l'état de Flutter
flutter doctor

# Nettoyer le projet
flutter clean

# Installer les dépendances
flutter pub get

# Mettre à jour les dépendances
flutter pub upgrade

# Lancer l'application
flutter run

# Builder pour Android
flutter build apk

# Voir les appareils disponibles
flutter devices
```

## 🎓 Ressources

- Documentation Flutter : https://flutter.dev/docs
- Guide d'installation Windows : https://flutter.dev/docs/get-started/install/windows
- Flutter sur GitHub : https://github.com/flutter/flutter

## ⚡ Démarrage rapide (Une fois Flutter installé)

```powershell
# 1. Aller dans le dossier du projet
cd C:\Users\amani\Desktop\4SLEAM1\PetConnect

# 2. Installer les dépendances
flutter pub get

# 3. Vérifier les appareils
flutter devices

# 4. Lancer l'application
flutter run
```

---

**Note** : Si vous préférez ne pas installer Flutter maintenant, vous pouvez :
1. Utiliser un IDE comme Android Studio ou VS Code avec l'extension Flutter
2. Utiliser un service cloud comme Codemagic ou GitHub Actions
3. Travailler avec le code Android natif existant dans le dossier `app/`



