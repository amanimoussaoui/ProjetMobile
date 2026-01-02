# 🚀 Démarrage rapide - PetConnect

## 📍 Où exécuter les commandes Flutter ?

Toutes les commandes Flutter doivent être exécutées dans le **dossier racine du projet** :

```
C:\Users\amani\Desktop\4SLEAM1\PetConnect
```

(C'est le dossier qui contient le fichier `pubspec.yaml`)

## ⚡ Étapes rapides

### 1. Ouvrir PowerShell ou Terminal
Ouvrez PowerShell dans le dossier du projet :
```powershell
cd C:\Users\amani\Desktop\4SLEAM1\PetConnect
```

### 2. Vérifier que Flutter est installé
```powershell
flutter --version
```

Si vous voyez une erreur "flutter n'est pas reconnu", vous devez d'abord installer Flutter.
Voir le fichier `INSTALLATION_GUIDE.md` pour les instructions.

### 3. Installer les dépendances
```powershell
flutter pub get
```

Cette commande lit le fichier `pubspec.yaml` et installe tous les packages nécessaires.

### 4. Vérifier les appareils disponibles
```powershell
flutter devices
```

Vous devriez voir au moins un appareil (émulateur Android, iPhone simulé, ou appareil physique).

### 5. Lancer l'application
```powershell
flutter run
```

## 📱 Options de lancement

### Sur un émulateur Android
1. Ouvrez Android Studio
2. Lancez l'émulateur Android
3. Exécutez `flutter run`

### Sur un appareil Android physique
1. Activez le mode développeur sur votre téléphone
2. Activez le débogage USB
3. Connectez votre téléphone via USB
4. Exécutez `flutter run`

### Sur un simulateur iOS (Mac uniquement)
1. Ouvrez Xcode
2. Lancez le simulateur iOS
3. Exécutez `flutter run`

### Sur le web
```powershell
flutter run -d chrome
```

## 🎯 Commandes importantes

| Commande | Description |
|----------|-------------|
| `flutter pub get` | Installer les dépendances |
| `flutter clean` | Nettoyer le projet |
| `flutter run` | Lancer l'application |
| `flutter devices` | Voir les appareils disponibles |
| `flutter doctor` | Vérifier l'installation Flutter |

## ❓ Problèmes courants

### "flutter n'est pas reconnu"
→ Flutter n'est pas installé ou pas dans le PATH
→ Voir `INSTALLATION_GUIDE.md`

### "No devices found"
→ Aucun appareil/émulateur n'est disponible
→ Lancez un émulateur ou connectez un appareil

### "Pub get failed"
→ Problème de connexion ou de dépendances
→ Vérifiez votre connexion internet
→ Vérifiez le fichier `pubspec.yaml`

## 📝 Checklist avant de lancer

- [ ] Flutter est installé (`flutter --version`)
- [ ] Les dépendances sont installées (`flutter pub get`)
- [ ] Un appareil/émulateur est disponible (`flutter devices`)
- [ ] Les images sont dans `assets/images/` (optionnel, des icônes de remplacement seront utilisées)

## 🎉 C'est tout !

Une fois que vous avez exécuté `flutter pub get` et `flutter run`, l'application devrait se lancer !

---

**Besoin d'aide ?** Consultez `INSTALLATION_GUIDE.md` pour plus de détails.



