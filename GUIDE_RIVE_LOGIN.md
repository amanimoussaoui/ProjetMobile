# 🎨 Guide : Intégration Rive dans la page Login

## ✅ Configuration effectuée

### 1. Dépendance Rive activée
- **Fichier**: `app/build.gradle.kts`
- **Dépendance**: `app.rive:rive-android:8.6.0` ✅ Activée

### 2. Provider Rive activé
- **Fichier**: `app/src/main/AndroidManifest.xml`
- **Provider**: `RiveInitializer` ✅ Activé

### 3. Layout Login simplifié
- **Fichier**: `app/src/main/res/layout/activity_login.xml`
- **Contenu**: 
  - Animation Rive (280dp x 280dp)
  - Champ Email
  - Champ Password
  - Bouton Login
  - Lien "Forgot Password"

### 4. Helper Kotlin créé
- **Fichier**: `app/src/main/java/com/example/petconnect/utils/RiveHelper.kt`
- **Fonctions**:
  - `followTyping()` - Active le suivi des yeux
  - `coverEyes()` - Cache les yeux
  - `trigger()` - Déclenche des triggers

### 5. LoginActivity mis à jour
- **Fichier**: `app/src/main/java/com/example/petconnect/LoginActivity.java`
- **Fonctionnalités**:
  - ✅ Teddy bear suit avec ses yeux quand on tape l'email
  - ✅ Teddy bear cache ses yeux quand on tape le password
  - ✅ Gestion du focus des champs
  - ✅ Triggers "fail" et "shake" en cas d'erreur

## 📋 Structure du fichier Rive

Votre fichier `teddy_login.riv` doit être placé dans :
```
app/src/main/res/raw/teddy_login.riv
```

### Inputs booléens nécessaires

Le helper essaie automatiquement ces noms d'inputs (dans l'ordre) :

**Pour suivre avec les yeux (email) :**
- `isChecking`
- `look`
- `typing`
- `isTyping`
- `follow`

**Pour cacher les yeux (password) :**
- `isHandsUp`
- `handsUp`
- `coverEyes`
- `hideEyes`
- `password`

### Triggers nécessaires

**Pour les erreurs :**
- `fail` - Déclenché en cas d'erreur de connexion
- `shake` - Déclenché pour l'animation de shake

## 🎯 Comportement

### Quand on tape dans le champ Email :
1. Le teddy bear suit avec ses yeux la saisie
2. Input activé : `isChecking` (ou équivalent)
3. Les yeux ne sont plus cachés

### Quand on tape dans le champ Password :
1. Le teddy bear cache ses yeux avec ses mains
2. Input activé : `isHandsUp` (ou équivalent)
3. Le suivi des yeux est désactivé

### Quand il y a une erreur :
1. Trigger `fail` déclenché
2. Trigger `shake` déclenché
3. Animation de shake sur le bouton

## 🔧 Vérification

1. **Vérifier que le fichier existe** :
   ```
   app/src/main/res/raw/teddy_login.riv
   ```

2. **Vérifier les noms des inputs dans Rive Editor** :
   - Ouvrez votre fichier `.riv` dans Rive Editor
   - Vérifiez les noms des inputs booléens
   - Si les noms sont différents, modifiez `RiveHelper.kt`

3. **Rebuild le projet** :
   - Build > Clean Project
   - Build > Rebuild Project

## 🐛 Dépannage

### L'animation Rive ne s'affiche pas ?
1. Vérifiez que `teddy_login.riv` existe dans `res/raw/`
2. Vérifiez que le nom est en minuscules
3. Vérifiez les logs : `adb logcat | grep Rive`

### Les yeux ne suivent pas ?
1. Vérifiez les noms des inputs dans Rive Editor
2. Ajoutez le nom correct dans `RiveHelper.kt` (liste `possibleInputs`)
3. Vérifiez que la state machine est bien configurée

### Les yeux ne se cachent pas ?
1. Vérifiez les noms des inputs dans Rive Editor
2. Ajoutez le nom correct dans `RiveHelper.kt` (liste `possibleInputs`)
3. Vérifiez que l'input est bien booléen

## 📝 Notes

- Le helper essaie automatiquement plusieurs noms d'inputs courants
- Si votre modèle Rive utilise des noms différents, modifiez les listes dans `RiveHelper.kt`
- Les logs Android afficheront quel input a été trouvé et activé


