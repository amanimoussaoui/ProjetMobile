@echo off
echo ========================================
echo Ajout d'images de test a la galerie Android
echo ========================================
echo.

REM Vérifier que ADB est disponible
where adb >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] ADB n'est pas trouve dans le PATH
    echo.
    echo Veuillez :
    echo 1. Installer Android SDK Platform Tools
    echo 2. Ajouter le chemin vers adb.exe dans votre PATH
    echo    (ex: C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools)
    echo.
    pause
    exit /b 1
)

echo [1/4] Verification de la connexion de l'appareil...
adb devices
echo.

REM Vérifier qu'un appareil est connecté
adb devices | findstr "device$" >nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Aucun appareil Android connecte
    echo.
    echo Veuillez :
    echo 1. Demarrer votre emulateur Android
    echo 2. Ou connecter un appareil physique avec USB debugging active
    echo.
    pause
    exit /b 1
)

echo [2/4] Creation du dossier Pictures...
adb shell mkdir -p /sdcard/Pictures
adb shell mkdir -p /sdcard/Pictures/PetConnect_Test
echo.

echo [3/4] Copie des images...

REM Copier depuis assets/images si le dossier existe
if exist "assets\images\*.jpg" (
    echo   - Copie des images JPG depuis assets/images...
    adb push "assets\images\*.jpg" /sdcard/Pictures/PetConnect_Test/ 2>nul
)

if exist "assets\images\*.png" (
    echo   - Copie des images PNG depuis assets/images...
    adb push "assets\images\*.png" /sdcard/Pictures/PetConnect_Test/ 2>nul
)

REM Copier depuis app/src/main/res/drawable si les fichiers existent
if exist "app\src\main\res\drawable\logo.png" (
    echo   - Copie de logo.png...
    adb push "app\src\main\res\drawable\logo.png" /sdcard/Pictures/PetConnect_Test/logo.png 2>nul
)

if exist "app\src\main\res\drawable\pett.png" (
    echo   - Copie de pett.png...
    adb push "app\src\main\res\drawable\pett.png" /sdcard/Pictures/PetConnect_Test/pett.png 2>nul
)

echo.

echo [4/4] Rafraichissement de la galerie...
adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/PetConnect_Test/ >nul 2>&1
echo.

echo ========================================
echo [SUCCES] Images ajoutees avec succes!
echo ========================================
echo.
echo Les images ont ete copiees dans :
echo   /sdcard/Pictures/PetConnect_Test/
echo.
echo Pour voir les images :
echo 1. Ouvrez l'application Galerie sur votre emulateur
echo 2. Ou testez l'upload dans votre app PetConnect
echo.
echo Astuce : Vous pouvez aussi glisser-deposer des images
echo          directement sur l'ecran de l'emulateur !
echo.
pause


