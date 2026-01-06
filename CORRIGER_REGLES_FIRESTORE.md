# 🔐 CORRIGER LES RÈGLES FIRESTORE

**Problème**: Permission denied ❌  
**Solution**: Ajouter les bonnes règles Firestore ✅

---

## 📋 PAS À PAS

### **Étape 1: Ouvrir Firebase Console**

```
1. Aller à: https://console.firebase.google.com
2. Sélectionner "PetConnect"
3. Firestore Database → Onglet "Règles"
```

### **Étape 2: Remplacer les Règles**

**Supprimer** tout ce qui est actuel et **copier-coller**:

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Permettre la lecture de la collection "pet"
    match /pet/{document=**} {
      allow read;
      allow create, update, delete: if request.auth != null;
    }
    
    // Permettre la lecture de la collection "pets" (si vous l'aviez créée)
    match /pets/{document=**} {
      allow read;
      allow create, update, delete: if request.auth != null;
    }
    
    // Autres collections (avec authentification requise)
    match /events/{document=**} {
      allow read, write: if request.auth != null;
    }
    
    match /products/{document=**} {
      allow read, write: if request.auth != null;
    }
    
    match /users/{document=**} {
      allow read, write: if request.auth != null;
    }
    
    match /posts/{document=**} {
      allow read, write: if request.auth != null;
    }
    
    // Par défaut: authentification requise
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### **Étape 3: Publier les Règles**

```
1. Cliquer le bouton bleu "Publier"
2. Attendre le message "Règles publiées"
3. ✅ Les règles sont maintenant actives
```

---

## ⏱️ TEMPS D'APPLICATION

Les règles prennent généralement **quelques secondes** à s'appliquer.

Si ça ne marche pas immédiatement:
```
1. Attendre 30 secondes
2. Redémarrer l'émulateur
3. Rebuild l'app: ./gradlew clean assembleDebug
4. Relancer l'app
```

---

## ✅ VÉRIFICATION

Après avoir publié les règles et relancé l'app:

**Vous devriez voir dans les logs**:
```
D/PetRepository: 🔥 Fetching pets from Firestore collection: 'pet'
D/PetRepository: ✅ Firestore query successful
D/PetRepository: 📦 Documents found: 1
D/PetRepository: ✅ Pet loaded: Max (ID: vYfsZ9aFEzu1iC9kGyEa)
D/PetRepository: 🎉 Total pets loaded: 1
D/MainActivity: ✅ Pets loaded from Firebase: 1 pets
```

**Et dans l'app**:
```
✅ Max (Labrador) affiche avec sa photo
✅ Message "Aucun animal trouvé" → DISPARU 🎉
```

---

## 🔧 CODE MODIFIÉ

J'ai aussi changé le code Java pour chercher **"pet"** au lieu de **"Pets"**:

```java
// AVANT (❌ Cherchait "Pets")
db.collection("Pets").get()

// APRÈS (✅ Cherche "pet")
db.collection("pet").get()
```

---

**Faites ces étapes et testez!** 🚀
