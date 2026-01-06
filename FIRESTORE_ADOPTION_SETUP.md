# 📱 GUIDE: Ajouter Collection ADOPTION dans Firestore

**Objectif**: Ajouter une collection "pets" avec des données d'adoption de chiens et chats  
**Temps estimé**: 5-10 minutes  
**Difficulté**: Facile ✅

---

## 🎯 Méthode 1: Via Firebase Console (GUI) ⭐ RECOMMANDÉ

### **Étape 1: Accéder à Firestore**
```
1. Ouvrir https://console.firebase.google.com
2. Sélectionner le projet PetConnect
3. Aller à "Firestore Database" dans le menu gauche
4. Cliquer sur "Créer une collection"
```

### **Étape 2: Créer la collection "pets"**
```
ID de collection: pets
↓ Cliquer "Suivant"
```

### **Étape 3: Ajouter le premier document**
```
ID du document: AUTO_ID (ou pet001)
↓ Ajouter les champs suivants:
```

| Champ | Type | Valeur |
|-------|------|--------|
| `name` | String | "Max" |
| `breed` | String | "Labrador" |
| `age` | Number | 3 |
| `gender` | String | "Mâle" |
| `description` | String | "Chien gentil et affectueux" |
| `image` | String | "https://..." |
| `adoptionStatus` | String | "Disponible" |
| `weight` | Number | 32 |
| `color` | String | "Marron" |
| `vaccinated` | Boolean | true |
| `createdAt` | Timestamp | [Date actuelle] |

---

## 🎯 Méthode 2: Importer JSON (Plus Rapide)

### **Option A: Installer Firebase CLI**

```bash
# Windows PowerShell
npm install -g firebase-tools

# Connexion Firebase
firebase login

# Télécharger les données
firebase firestore:export --project=your-project-id firestore_export/

# Importer les données
firebase firestore:import firestore_export/ --project=your-project-id
```

### **Option B: Utiliser Firebase Console Import**

```
1. Aller à Firestore Database
2. Cliquer sur le bouton "⋮" (3 points)
3. "Importer une collection"
4. Sélectionner le fichier JSON
5. Choisir la collection destination: "pets"
```

---

## 📋 Données d'Exemple à Ajouter

### **Document 1: Max (Labrador)**
```json
{
  "name": "Max",
  "breed": "Labrador Retriever",
  "age": 3,
  "gender": "Mâle",
  "description": "Labrador brun très affectueux et énergique. Adorable avec les enfants, parfait pour la famille.",
  "image": "https://images.unsplash.com/photo-1633722715463-d30628cfa6a8",
  "adoptionStatus": "Disponible",
  "weight": 32,
  "color": "Marron",
  "vaccinated": true,
  "neutered": true,
  "features": ["Actif", "Affectueux", "Intelligent"],
  "adoptionFee": 150,
  "location": "Refuge Paris",
  "createdAt": "2026-01-06T10:00:00Z"
}
```

### **Document 2: Luna (Berger Allemand)**
```json
{
  "name": "Luna",
  "breed": "Berger Allemand",
  "age": 2,
  "gender": "Femelle",
  "description": "Berger Allemand noir et feu, très dressée et obéissante. Garde et protection naturelle.",
  "image": "https://images.unsplash.com/photo-1568572933382-74d440642117",
  "adoptionStatus": "Disponible",
  "weight": 28,
  "color": "Noir et Feu",
  "vaccinated": true,
  "neutered": true,
  "features": ["Protecteur", "Obéissant", "Intelligent"],
  "adoptionFee": 200,
  "location": "Refuge Lyon",
  "createdAt": "2026-01-05T15:30:00Z"
}
```

### **Document 3: Milo (Golden Retriever)**
```json
{
  "name": "Milo",
  "breed": "Golden Retriever",
  "age": 4,
  "gender": "Mâle",
  "description": "Golden Retriever roux, très sociable et doux. Excellent avec les enfants et autres animaux.",
  "image": "https://images.unsplash.com/photo-1600011689520-08ab36fd3f37",
  "adoptionStatus": "Disponible",
  "weight": 35,
  "color": "Roux doré",
  "vaccinated": true,
  "neutered": true,
  "features": ["Doux", "Sociable", "Actif"],
  "adoptionFee": 180,
  "location": "Refuge Marseille",
  "createdAt": "2026-01-04T12:00:00Z"
}
```

### **Document 4: Bella (Chat Persan)**
```json
{
  "name": "Bella",
  "breed": "Chat Persan",
  "age": 2,
  "gender": "Femelle",
  "description": "Adorable chat Persan blanc et gris. Calme et affectueux, adore les caresses.",
  "image": "https://images.unsplash.com/photo-1574158622682-e40e69881006",
  "adoptionStatus": "Disponible",
  "weight": 4,
  "color": "Blanc et gris",
  "vaccinated": true,
  "neutered": true,
  "features": ["Calme", "Affectueux", "Maison"],
  "adoptionFee": 80,
  "location": "Refuge Paris",
  "createdAt": "2026-01-03T14:20:00Z"
}
```

### **Document 5: Simba (Chat Noir)**
```json
{
  "name": "Simba",
  "breed": "Chat Européen",
  "age": 1,
  "gender": "Mâle",
  "description": "Petit chat noir tout mignon, très joueur et curieux. Idéal pour appartement.",
  "image": "https://images.unsplash.com/photo-1573865526014-f3550b887b70",
  "adoptionStatus": "Disponible",
  "weight": 3,
  "color": "Noir",
  "vaccinated": true,
  "neutered": false,
  "features": ["Joueur", "Curieux", "Affectueux"],
  "adoptionFee": 50,
  "location": "Refuge Lyon",
  "createdAt": "2026-01-02T09:00:00Z"
}
```

### **Document 6: Charlie (Cocker Spaniel)**
```json
{
  "name": "Charlie",
  "breed": "Cocker Spaniel",
  "age": 5,
  "gender": "Mâle",
  "description": "Cocker noir très obéissant et affectueux. Senior mais encore très actif et joueur.",
  "image": "https://images.unsplash.com/photo-1619036633304-2284e1b2b61b",
  "adoptionStatus": "Disponible",
  "weight": 15,
  "color": "Noir",
  "vaccinated": true,
  "neutered": true,
  "features": ["Affectueux", "Obéissant", "Joueur"],
  "adoptionFee": 120,
  "location": "Refuge Toulouse",
  "createdAt": "2026-01-01T11:00:00Z"
}
```

---

## 🎯 Méthode 3: Ajouter via Code Android

### **Option A: Utiliser Adoption MainActivity existant**

**Fichier**: `PetConnect/app/src/main/java/com/example/petconnect/adoption/MainActivity.java`

```java
private void addSamplePets() {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    // Pet 1: Max
    Map<String, Object> pet1 = new HashMap<>();
    pet1.put("name", "Max");
    pet1.put("breed", "Labrador Retriever");
    pet1.put("age", 3);
    pet1.put("gender", "Mâle");
    pet1.put("description", "Labrador brun très affectueux");
    pet1.put("adoptionStatus", "Disponible");
    pet1.put("weight", 32);
    pet1.put("color", "Marron");
    pet1.put("vaccinated", true);
    pet1.put("neutered", true);
    pet1.put("image", "https://images.unsplash.com/photo-1633722715463-d30628cfa6a8");
    pet1.put("adoptionFee", 150);
    pet1.put("location", "Refuge Paris");
    
    db.collection("pets")
        .document("pet_001")
        .set(pet1)
        .addOnSuccessListener(aVoid -> {
            Log.d("Firestore", "Pet 1 ajouté avec succès");
        })
        .addOnFailureListener(e -> {
            Log.e("Firestore", "Erreur: " + e.getMessage());
        });
    
    // Pet 2: Luna
    Map<String, Object> pet2 = new HashMap<>();
    pet2.put("name", "Luna");
    pet2.put("breed", "Berger Allemand");
    pet2.put("age", 2);
    pet2.put("gender", "Femelle");
    pet2.put("description", "Berger Allemand noir et feu");
    pet2.put("adoptionStatus", "Disponible");
    pet2.put("weight", 28);
    pet2.put("color", "Noir et Feu");
    pet2.put("vaccinated", true);
    pet2.put("neutered", true);
    pet2.put("image", "https://images.unsplash.com/photo-1568572933382-74d440642117");
    pet2.put("adoptionFee", 200);
    pet2.put("location", "Refuge Lyon");
    
    db.collection("pets")
        .document("pet_002")
        .set(pet2)
        .addOnSuccessListener(aVoid -> {
            Log.d("Firestore", "Pet 2 ajouté avec succès");
        })
        .addOnFailureListener(e -> {
            Log.e("Firestore", "Erreur: " + e.getMessage());
        });
}

// Appeler dans onCreate()
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.adoption_activity_main);
    
    // Ajouter les pets d'exemple (à faire une seule fois!)
    addSamplePets();
}
```

### **Option B: Créer une classe Helper**

**Fichier**: `PetConnect/app/src/main/java/com/example/petconnect/adoption/utils/FirestoreHelper.java`

```java
public class FirestoreHelper {
    
    public static void addSamplePets(FirebaseFirestore db) {
        // Array de pets
        List<Map<String, Object>> pets = new ArrayList<>();
        
        // Pet 1
        Map<String, Object> pet1 = new HashMap<>();
        pet1.put("name", "Max");
        pet1.put("breed", "Labrador Retriever");
        pet1.put("age", 3);
        pet1.put("gender", "Mâle");
        pet1.put("description", "Labrador brun très affectueux");
        pet1.put("adoptionStatus", "Disponible");
        pet1.put("weight", 32);
        pet1.put("color", "Marron");
        pet1.put("vaccinated", true);
        pet1.put("neutered", true);
        pets.add(pet1);
        
        // Pet 2, 3, 4... (ajouter les autres)
        
        // Batch write
        WriteBatch batch = db.batch();
        
        for (int i = 0; i < pets.size(); i++) {
            DocumentReference docRef = db.collection("pets").document("pet_00" + (i + 1));
            batch.set(docRef, pets.get(i));
        }
        
        batch.commit()
            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Tous les pets ajoutés"))
            .addOnFailureListener(e -> Log.e("Firestore", "Erreur: " + e.getMessage()));
    }
}

// Usage
FirestoreHelper.addSamplePets(FirebaseFirestore.getInstance());
```

---

## 📝 Fichier JSON Complet à Importer

Créer le fichier: `firestore_adoption_data.json`

```json
{
  "pets": [
    {
      "name": "Max",
      "breed": "Labrador Retriever",
      "age": 3,
      "gender": "Mâle",
      "description": "Labrador brun très affectueux et énergique",
      "image": "https://images.unsplash.com/photo-1633722715463-d30628cfa6a8",
      "adoptionStatus": "Disponible",
      "weight": 32,
      "color": "Marron",
      "vaccinated": true,
      "neutered": true,
      "features": ["Actif", "Affectueux", "Intelligent"],
      "adoptionFee": 150,
      "location": "Refuge Paris",
      "createdAt": "2026-01-06T10:00:00Z"
    },
    {
      "name": "Luna",
      "breed": "Berger Allemand",
      "age": 2,
      "gender": "Femelle",
      "description": "Berger Allemand noir et feu, très dressée",
      "image": "https://images.unsplash.com/photo-1568572933382-74d440642117",
      "adoptionStatus": "Disponible",
      "weight": 28,
      "color": "Noir et Feu",
      "vaccinated": true,
      "neutered": true,
      "features": ["Protecteur", "Obéissant", "Intelligent"],
      "adoptionFee": 200,
      "location": "Refuge Lyon",
      "createdAt": "2026-01-05T15:30:00Z"
    },
    {
      "name": "Milo",
      "breed": "Golden Retriever",
      "age": 4,
      "gender": "Mâle",
      "description": "Golden Retriever roux, très sociable",
      "image": "https://images.unsplash.com/photo-1600011689520-08ab36fd3f37",
      "adoptionStatus": "Disponible",
      "weight": 35,
      "color": "Roux doré",
      "vaccinated": true,
      "neutered": true,
      "features": ["Doux", "Sociable", "Actif"],
      "adoptionFee": 180,
      "location": "Refuge Marseille",
      "createdAt": "2026-01-04T12:00:00Z"
    },
    {
      "name": "Bella",
      "breed": "Chat Persan",
      "age": 2,
      "gender": "Femelle",
      "description": "Adorable chat Persan blanc et gris",
      "image": "https://images.unsplash.com/photo-1574158622682-e40e69881006",
      "adoptionStatus": "Disponible",
      "weight": 4,
      "color": "Blanc et gris",
      "vaccinated": true,
      "neutered": true,
      "features": ["Calme", "Affectueux", "Maison"],
      "adoptionFee": 80,
      "location": "Refuge Paris",
      "createdAt": "2026-01-03T14:20:00Z"
    },
    {
      "name": "Simba",
      "breed": "Chat Européen",
      "age": 1,
      "gender": "Mâle",
      "description": "Petit chat noir très joueur",
      "image": "https://images.unsplash.com/photo-1573865526014-f3550b887b70",
      "adoptionStatus": "Disponible",
      "weight": 3,
      "color": "Noir",
      "vaccinated": true,
      "neutered": false,
      "features": ["Joueur", "Curieux", "Affectueux"],
      "adoptionFee": 50,
      "location": "Refuge Lyon",
      "createdAt": "2026-01-02T09:00:00Z"
    },
    {
      "name": "Charlie",
      "breed": "Cocker Spaniel",
      "age": 5,
      "gender": "Mâle",
      "description": "Cocker noir très obéissant",
      "image": "https://images.unsplash.com/photo-1619036633304-2284e1b2b61b",
      "adoptionStatus": "Disponible",
      "weight": 15,
      "color": "Noir",
      "vaccinated": true,
      "neutered": true,
      "features": ["Affectueux", "Obéissant", "Joueur"],
      "adoptionFee": 120,
      "location": "Refuge Toulouse",
      "createdAt": "2026-01-01T11:00:00Z"
    }
  ]
}
```

---

## ✅ Vérification Après Ajout

### **Via Firebase Console**
```
1. Aller à Firestore Database
2. Voir la collection "pets" dans le menu gauche
3. Vérifier 6 documents présents
4. Cliquer sur un document pour vérifier les champs
```

### **Via Code Android**
```java
// Test: Récupérer les pets
FirebaseFirestore.getInstance()
    .collection("pets")
    .get()
    .addOnSuccessListener(queryDocumentSnapshots -> {
        int count = queryDocumentSnapshots.size();
        Log.d("Firestore", "Nombre de pets: " + count);
        
        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
            String name = doc.getString("name");
            String breed = doc.getString("breed");
            Log.d("Pet", name + " - " + breed);
        }
    });
```

---

## 🐛 Troubleshooting

### **Problème: "Aucun pet trouvé" dans l'app**

**Cause**: Collection ou documents vides

**Solutions**:
1. ✅ Vérifier la collection "pets" existe
2. ✅ Vérifier au moins 1 document présent
3. ✅ Vérifier les champs obligatoires (name, breed, age)
4. ✅ Vérifier les règles Firestore autorisent les lectures

### **Règles Firestore Requises**

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Permettre lecture publique des pets
    match /pets/{document=**} {
      allow read;
      allow create, update, delete: if request.auth != null;
    }
    
    // Autres collections...
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 📱 Intégration dans l'App

**Vérifier que le code lecture fonctionne**:

**Fichier**: [ProjetMobile-Adoption-module/app/src/main/java/com/example/petconnect/adoption/MainActivity.java](c:/Users/raeda/Desktop/DEVOPS/ProjetMobile-Adoption-module/app/src/main/java/com/example/petconnect/adoption/MainActivity.java)

```java
public class MainActivity extends AppCompatActivity {
    
    private PetRepository petRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        petRepository = new PetRepository();
        loadPets();
    }
    
    private void loadPets() {
        petRepository.getPets(new PetRepository.PetsCallback() {
            @Override
            public void onPetsLoaded(List<Pet> pets) {
                Log.d("Adoption", "Pets chargés: " + pets.size());
                // Afficher dans ViewPager
            }
            
            @Override
            public void onError(String error) {
                Log.e("Adoption", "Erreur: " + error);
                Toast.makeText(MainActivity.this, 
                    "Erreur: " + error, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

---

**Créé le**: 6 janvier 2026  
**Guide version**: 1.0  
**Temps**: 5-10 minutes pour compléter
