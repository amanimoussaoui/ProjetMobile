# 📸 Comment ajouter vos vraies images

## 📍 Emplacement exact

Placez vos images dans ce dossier :
```
PetConnect/app/src/main/res/drawable/
```

## 🖼️ Images à ajouter

1. **logo.png** - Logo de l'application
2. **pett.png** - Image d'animaux

## 📝 Instructions étape par étape

### Option 1 : Via Android Studio (Le plus simple)

1. **Ouvrir Android Studio**
2. Dans le **Project view** (à gauche), naviguer vers :
   ```
   app → src → main → res → drawable
   ```
3. **Clic droit** sur le dossier `drawable`
4. Choisir **"Show in Explorer"** (Windows) ou **"Reveal in Finder"** (Mac)
5. **Copier vos images** dans ce dossier :
   - `logo.png`
   - `pett.png`
6. Retourner dans Android Studio
7. **Clic droit** sur le dossier `drawable` → **"Refresh"** ou **"Synchronize"**

### Option 2 : Via l'explorateur de fichiers Windows

1. **Ouvrir l'explorateur de fichiers**
2. **Naviguer vers** :
   ```
   C:\Users\amani\Desktop\4SLEAM1\PetConnect\app\src\main\res\drawable
   ```
3. **Copier vos images** dans ce dossier :
   - `logo.png`
   - `pett.png`

### Option 3 : Glisser-déposer dans Android Studio

1. **Ouvrir Android Studio**
2. Dans le **Project view**, aller dans `app/src/main/res/drawable/`
3. **Glisser vos images** depuis votre ordinateur directement dans le dossier `drawable` dans Android Studio
4. Android Studio vous demandera de confirmer → Cliquer **"OK"**

## ✅ Vérification

Après avoir ajouté les images :

1. **Vérifier dans Android Studio** :
   - Les fichiers `logo.png` et `pett.png` doivent apparaître dans `app/src/main/res/drawable/`
   - Les noms doivent être **exactement** : `logo.png` et `pett.png` (en minuscules)

2. **Synchroniser Gradle** :
   ```
   File → Sync Project with Gradle Files
   ```

3. **Nettoyer le projet** (optionnel mais recommandé) :
   ```
   Build → Clean Project
   ```

4. **Rebuild** :
   ```
   Build → Rebuild Project
   ```

5. **Lancer l'application** :
   - Vos images devraient maintenant s'afficher !

## 📐 Spécifications des images

### logo.png
- **Nom exact** : `logo.png` (tout en minuscules)
- **Taille recommandée** : 512x512 pixels ou plus
- **Format** : PNG (avec transparence de préférence)
- **Poids** : < 500 KB

### pett.png
- **Nom exact** : `pett.png` (tout en minuscules)
- **Taille recommandée** : 800x600 pixels ou plus
- **Format** : PNG ou JPG
- **Poids** : < 1 MB

## ⚠️ Règles importantes

- ✅ **Noms en minuscules** : `logo.png` et `pett.png` (pas `Logo.png` ou `PETT.png`)
- ✅ **Pas d'espaces** dans les noms de fichiers
- ✅ **Pas de caractères spéciaux** (sauf underscore _)
- ✅ **Format PNG ou JPG** uniquement
- ✅ **Placées dans** `app/src/main/res/drawable/`

## 🔧 Si les images ne s'affichent pas

1. **Vérifier les noms** : Doivent être exactement `logo.png` et `pett.png`
2. **Vérifier l'emplacement** : Doivent être dans `app/src/main/res/drawable/`
3. **Synchroniser Gradle** : `File → Sync Project with Gradle Files`
4. **Nettoyer le projet** : `Build → Clean Project`
5. **Rebuild** : `Build → Rebuild Project`
6. **Redémarrer Android Studio** si nécessaire

## 📱 Structure finale attendue

```
PetConnect/
└── app/
    └── src/
        └── main/
            └── res/
                └── drawable/
                    ├── logo.png          ← Votre logo ici
                    ├── pett.png          ← Votre image d'animaux ici
                    ├── ic_logo.xml        (icône de secours)
                    ├── ic_pet.xml         (icône de secours)
                    └── ... (autres fichiers)
```

## 🎯 Résultat

Une fois les images ajoutées :
- ✅ Le logo s'affichera sur le splash screen
- ✅ L'image pet s'affichera sur le splash screen
- ✅ Le logo s'affichera sur la page de connexion
- ✅ L'image pet s'affichera dans la section Tips of the Day

---

**C'est tout ! Ajoutez simplement vos images dans le dossier `drawable/` et elles s'afficheront automatiquement ! 🎉**


