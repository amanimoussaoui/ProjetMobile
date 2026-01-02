# 📸 Guide : Ajouter des images à la galerie pour tester l'upload

## Méthode 1 : Via l'émulateur Android (Recommandé)

### Étape 1 : Ouvrir l'émulateur
1. Lancez votre émulateur Android dans Android Studio
2. Attendez qu'il soit complètement démarré

### Étape 2 : Ajouter des images via l'interface de l'émulateur
1. **Glisser-déposer** : 
   - Ouvrez votre explorateur de fichiers (Windows Explorer)
   - Trouvez des images (JPG, PNG) sur votre ordinateur
   - **Glissez et déposez** les images directement sur l'écran de l'émulateur
   - Les images seront automatiquement ajoutées à la galerie

2. **Via le menu de l'émulateur** :
   - Cliquez sur les **3 points** (⋮) à côté de l'émulateur
   - Allez dans **Settings** > **Extended controls**
   - Cliquez sur **Camera** ou **Photos**
   - Ajoutez des images depuis votre ordinateur

## Méthode 2 : Via ADB (Android Debug Bridge)

### Étape 1 : Préparer les images
1. Créez un dossier `test_images` sur votre ordinateur
2. Placez-y des images de test (JPG, PNG)

### Étape 2 : Utiliser ADB pour copier les images

Ouvrez un terminal/command prompt et exécutez :

```bash
# Vérifier que l'appareil est connecté
c

# Créer le dossier Pictures s'il n'existe pas
adb shell mkdir -p /sdcard/Pictures

# Copier une image vers la galerie
adb push chemin/vers/votre/image.jpg /sdcard/Pictures/

# Ou copier plusieurs images
adb push chemin/vers/dossier/*.jpg /sdcard/Pictures/
adb push chemin/vers/dossier/*.png /sdcard/Pictures/
```

### Exemple concret :
```bash
# Si vos images sont dans C:\Users\amani\Desktop\images\
adb push C:\Users\amani\Desktop\images\*.jpg /sdcard/Pictures/
adb push C:\Users\amani\Desktop\images\*.png /sdcard/Pictures/
```

## Méthode 3 : Via l'application Galerie de l'émulateur

1. Ouvrez l'application **Galerie** ou **Photos** sur l'émulateur
2. Utilisez l'option **Importer** ou **Ajouter des photos**
3. Sélectionnez les images depuis votre ordinateur

## Méthode 4 : Créer un script automatique

Créez un fichier `add_test_images.bat` (Windows) ou `add_test_images.sh` (Linux/Mac) :

### Pour Windows (`add_test_images.bat`) :
```batch
@echo off
echo Ajout d'images de test à la galerie Android...

REM Vérifier que ADB est disponible
where adb >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Erreur: ADB n'est pas dans le PATH
    echo Assurez-vous que Android SDK est installé
    pause
    exit /b 1
)

REM Créer le dossier Pictures
adb shell mkdir -p /sdcard/Pictures

REM Copier les images depuis le dossier assets/images
if exist "assets\images\*.jpg" (
    echo Copie des images JPG...
    adb push assets\images\*.jpg /sdcard/Pictures/
)

if exist "assets\images\*.png" (
    echo Copie des images PNG...
    adb push assets\images\*.png /sdcard/Pictures/
)

echo Images ajoutées avec succès!
echo Redémarrez l'application Galerie pour voir les nouvelles images.
pause
```

### Pour Linux/Mac (`add_test_images.sh`) :
```bash
#!/bin/bash
echo "Ajout d'images de test à la galerie Android..."

# Vérifier que ADB est disponible
if ! command -v adb &> /dev/null; then
    echo "Erreur: ADB n'est pas installé ou pas dans le PATH"
    exit 1
fi

# Créer le dossier Pictures
adb shell mkdir -p /sdcard/Pictures

# Copier les images depuis le dossier assets/images
if [ -d "assets/images" ]; then
    echo "Copie des images..."
    adb push assets/images/*.jpg /sdcard/Pictures/ 2>/dev/null
    adb push assets/images/*.png /sdcard/Pictures/ 2>/dev/null
fi

echo "Images ajoutées avec succès!"
echo "Redémarrez l'application Galerie pour voir les nouvelles images."
```

## Méthode 5 : Utiliser les images de l'application

Si vous voulez utiliser les images déjà dans votre projet :

### Étape 1 : Copier depuis assets/images
```bash
# Copier logo.png et pett.png vers la galerie
adb push app/src/main/res/drawable/logo.png /sdcard/Pictures/logo.png
adb push app/src/main/res/drawable/pett.png /sdcard/Pictures/pett.png
```

## Méthode 6 : Télécharger des images de test

1. Téléchargez des images de test depuis :
   - [Unsplash](https://unsplash.com) - Images libres de droits
   - [Pexels](https://www.pexels.com) - Photos gratuites
   - [Pixabay](https://pixabay.com) - Images gratuites

2. Enregistrez-les sur votre ordinateur
3. Utilisez la Méthode 1 (glisser-déposer) ou Méthode 2 (ADB) pour les ajouter

## Vérification

Après avoir ajouté les images :

1. **Redémarrez l'application Galerie** sur l'émulateur
   - Fermez complètement l'app Galerie
   - Rouvrez-la

2. **Ou forcez le rafraîchissement** :
   ```bash
   adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/
   ```

3. **Testez dans votre application** :
   - Ouvrez votre app PetConnect
   - Allez dans Profile > Edit Profile
   - Cliquez sur "📷 Galerie"
   - Les images devraient maintenant apparaître !

## Emplacements des dossiers de galerie Android

Les images peuvent être placées dans :
- `/sdcard/Pictures/` - Dossier principal des photos
- `/sdcard/DCIM/Camera/` - Dossier de la caméra
- `/sdcard/Download/` - Dossier de téléchargement

## Astuce : Créer un dossier de test

Créez un dossier spécifique pour vos images de test :

```bash
# Créer un dossier de test
adb shell mkdir -p /sdcard/Pictures/PetConnect_Test

# Copier les images dans ce dossier
adb push vos_images/*.jpg /sdcard/Pictures/PetConnect_Test/
```

## Dépannage

### Les images n'apparaissent pas ?
1. Vérifiez que l'émulateur est bien connecté : `adb devices`
2. Vérifiez les permissions : L'app doit avoir la permission `READ_MEDIA_IMAGES`
3. Redémarrez l'app Galerie
4. Forcez le scan média (voir commande ci-dessus)

### Erreur "permission denied" ?
- Vérifiez que vous utilisez `/sdcard/` et non `/storage/emulated/0/`
- Sur Android 10+, utilisez le Storage Access Framework (déjà implémenté dans votre code)

## Note importante

Les images ajoutées via ADB ou glisser-déposer dans l'émulateur **ne seront disponibles que dans l'émulateur**. Pour un appareil physique, vous devrez :
1. Connecter l'appareil via USB
2. Activer le débogage USB
3. Utiliser les mêmes commandes ADB


