# Instructions pour ajouter le fichier Rive

Pour ajouter l'animation Rive bear dans la page de login:

1. Placez votre fichier `4765-9622-login.riv` dans le dossier `app/src/main/res/raw/`
2. Renommez-le en `login.riv` (tout en minuscules)
3. Dans `app/src/main/res/layout/activity_login.xml`, décommentez le bloc RiveAnimationView:
   ```xml
   <app.rive.runtime.kotlin.RiveAnimationView
       android:id="@+id/riveAnimationView"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       app:riveResource="@raw/login" />
   ```
4. (Optionnel) Supprimez ou commentez l'ImageView de fallback si vous voulez uniquement l'animation Rive
5. Rebuild le projet

L'animation sera automatiquement chargée dans LoginActivity.

**Note:** Les noms de fichiers dans `res/raw/` doivent contenir uniquement des lettres minuscules, des chiffres et des underscores.

**Alternative:** Si vous préférez charger Rive programmatiquement, vous devrez utiliser l'API Kotlin de Rive (la bibliothèque Rive est principalement conçue pour Kotlin).

