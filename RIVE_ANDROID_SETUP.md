# Configuration Rive pour Android

## 📋 Instructions

### 1. Ajouter le fichier Rive

1. Renommez votre fichier `4765-9622-login.riv` en `teddy_login.riv` (tout en minuscules)
2. Placez-le dans `app/src/main/res/raw/teddy_login.riv`

### 2. Structure de la State Machine

Votre fichier Rive doit avoir une state machine nommée **"Login Machine"** avec:

**Inputs booléens:**
- `isChecking` - Active quand l'utilisateur tape dans le champ email
- `isHandsUp` - Active quand l'utilisateur tape dans le champ password (couvre les yeux)

**Triggers:**
- `fail` - Déclenché en cas d'erreur de connexion
- `shake` - Déclenché en cas d'erreur (shake animation)

### 3. Utilisation dans le code

L'animation Rive est chargée automatiquement depuis le layout XML:
```xml
<app.rive.runtime.kotlin.RiveAnimationView
    android:id="@+id/riveAnimationView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:riveResource="@raw/teddy_login" />
```

### 4. Contrôles programmatiques

Les méthodes `triggerRiveInput()` et `triggerRiveTrigger()` sont déjà implémentées dans `LoginActivity.java`:
- Elles sont appelées automatiquement lors de la saisie de l'email/password
- Elles sont déclenchées en cas d'erreur de connexion

## ⚠️ Notes

- Le fichier Rive doit être en **minuscules** dans `res/raw/`
- La state machine doit s'appeler exactement **"Login Machine"**
- Les noms des inputs et triggers doivent correspondre exactement

## 🔧 Dépannage

Si l'animation ne s'affiche pas:
1. Vérifiez que le fichier `teddy_login.riv` existe dans `res/raw/`
2. Vérifiez que le nom de la state machine est "Login Machine"
3. Vérifiez les logs pour les erreurs de chargement
4. Vérifiez que la dépendance Rive est bien ajoutée dans `build.gradle.kts`

