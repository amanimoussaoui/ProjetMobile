# 📸 Guide pour ajouter vos vraies images

## 📁 Où placer les images

Placez vos images dans le dossier suivant :
```
app/src/main/res/drawable/
```

## 🖼️ Images nécessaires

1. **logo.png** - Logo de l'application
   - Taille recommandée : 512x512 pixels ou plus
   - Format : PNG (avec transparence de préférence)

2. **pett.png** - Image d'animaux pour le splash screen
   - Taille recommandée : 800x600 pixels ou plus
   - Format : PNG ou JPG

## 📝 Étapes pour ajouter les images

### Méthode 1 : Via Android Studio (Recommandé)

1. **Ouvrir Android Studio**
2. **Aller dans le dossier** : `app/src/main/res/drawable/`
3. **Clic droit** sur le dossier `drawable`
4. **New → Image Asset** ou **New → File**
5. **Copier vos images** dans ce dossier :
   - `logo.png`
   - `pett.png`

### Méthode 2 : Via l'explorateur de fichiers

1. **Ouvrir l'explorateur de fichiers**
2. **Naviguer vers** : `PetConnect/app/src/main/res/drawable/`
3. **Copier vos images** dans ce dossier :
   - `logo.png`
   - `pett.png`

### Méthode 3 : Via le terminal/commande

```bash
# Windows PowerShell
Copy-Item "C:\chemin\vers\logo.png" "app\src\main\res\drawable\logo.png"
Copy-Item "C:\chemin\vers\pett.png" "app\src\main\res\drawable\pett.png"
```

## ✅ Vérification

Après avoir ajouté les images :

1. **Synchroniser Gradle** dans Android Studio :
   ```
   File → Sync Project with Gradle Files
   ```

2. **Vérifier que les images sont bien dans le projet** :
   - Les fichiers doivent apparaître dans `app/src/main/res/drawable/`
   - Les noms doivent être exactement : `logo.png` et `pett.png`

3. **Rebuild le projet** :
   ```
   Build → Rebuild Project
   ```

## 🎨 Utilisation dans les layouts

Les layouts sont déjà configurés pour utiliser :
- `@drawable/logo` pour le logo
- `@drawable/pett` pour l'image d'animaux

Si vos images ont des noms différents, modifiez les layouts en conséquence.

## 📐 Tailles recommandées

### logo.png
- **Taille** : 512x512 pixels (ou multiple de 2)
- **Format** : PNG avec transparence
- **Poids** : < 500 KB

### pett.png
- **Taille** : 800x600 pixels ou plus
- **Format** : PNG ou JPG
- **Poids** : < 1 MB

## 🔧 Résolution des problèmes

### Les images ne s'affichent pas
1. Vérifiez que les noms sont exactement `logo.png` et `pett.png`
2. Vérifiez que les images sont dans `app/src/main/res/drawable/`
3. Synchronisez Gradle : `File → Sync Project with Gradle Files`
4. Nettoyez le projet : `Build → Clean Project`
5. Rebuild : `Build → Rebuild Project`

### Erreur "Resource not found"
- Vérifiez l'orthographe des noms de fichiers
- Les noms doivent être en minuscules
- Pas d'espaces dans les noms de fichiers
- Pas de caractères spéciaux (sauf underscore _)

### Images floues
- Utilisez des images de haute résolution
- Pour différentes densités d'écran, créez des dossiers :
  - `drawable-mdpi/` (1x)
  - `drawable-hdpi/` (1.5x)
  - `drawable-xhdpi/` (2x)
  - `drawable-xxhdpi/` (3x)
  - `drawable-xxxhdpi/` (4x)

## 📱 Structure finale

```
app/src/main/res/
├── drawable/
│   ├── logo.png          ← Votre logo
│   ├── pett.png          ← Votre image d'animaux
│   ├── ic_logo.xml       (icône de secours)
│   ├── ic_pet.xml        (icône de secours)
│   └── ... (autres drawables)
```

---

**Une fois les images ajoutées, elles s'afficheront automatiquement dans l'application ! 🎉**


