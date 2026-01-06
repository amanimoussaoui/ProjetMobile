# 📊 DONNÉES D'EXEMPLE - COLLECTION ADOPTION FIRESTORE

**Répertoire**: PetConnect/app/src/main/java/com/example/petconnect/adoption/models/  
**Format**: JSON prêt à copier-coller dans Firestore

---

## 📋 TABLEAU DES DONNÉES

| ID Doc | Nom | Race | Âge | Sexe | Poids | Couleur | Vaccin | Stérilisé | Frais |
|--------|-----|------|-----|------|-------|---------|--------|-----------|-------|
| pet_001 | Max | Labrador Retriever | 3 ans | Mâle | 32 kg | Marron | ✅ | ✅ | 150€ |
| pet_002 | Luna | Berger Allemand | 2 ans | Femelle | 28 kg | Noir/Feu | ✅ | ✅ | 200€ |
| pet_003 | Milo | Golden Retriever | 4 ans | Mâle | 35 kg | Roux doré | ✅ | ✅ | 180€ |
| pet_004 | Bella | Chat Persan | 2 ans | Femelle | 4 kg | Blanc/Gris | ✅ | ✅ | 80€ |
| pet_005 | Simba | Chat Européen | 1 an | Mâle | 3 kg | Noir | ✅ | ❌ | 50€ |
| pet_006 | Charlie | Cocker Spaniel | 5 ans | Mâle | 15 kg | Noir | ✅ | ✅ | 120€ |

---

## 📄 FORMAT JSON (Copier-Coller Direct)

### **Pet 1: Max**
```json
{
  "name": "Max",
  "breed": "Labrador Retriever",
  "age": 3,
  "gender": "Mâle",
  "weight": 32,
  "color": "Marron",
  "description": "Labrador brun très affectueux et énergique. Adorable avec les enfants, parfait pour la famille. Aime les jeux et la baignade.",
  "image": "https://images.unsplash.com/photo-1633722715463-d30628cfa6a8?w=500",
  "adoptionStatus": "Disponible",
  "vaccinated": true,
  "neutered": true,
  "adoptionFee": 150,
  "location": "Refuge Paris",
  "features": [
    "Actif",
    "Affectueux",
    "Intelligent",
    "Famille"
  ],
  "createdAt": "2026-01-06T10:00:00Z"
}
```

### **Pet 2: Luna**
```json
{
  "name": "Luna",
  "breed": "Berger Allemand",
  "age": 2,
  "gender": "Femelle",
  "weight": 28,
  "color": "Noir et Feu",
  "description": "Berger Allemand noir et feu, très dressée et obéissante. Garde et protection naturelle. Excellente pour sécurité et protection.",
  "image": "https://images.unsplash.com/photo-1568572933382-74d440642117?w=500",
  "adoptionStatus": "Disponible",
  "vaccinated": true,
  "neutered": true,
  "adoptionFee": 200,
  "location": "Refuge Lyon",
  "features": [
    "Protecteur",
    "Obéissant",
    "Intelligent",
    "Vigilant"
  ],
  "createdAt": "2026-01-05T15:30:00Z"
}
```

### **Pet 3: Milo**
```json
{
  "name": "Milo",
  "breed": "Golden Retriever",
  "age": 4,
  "gender": "Mâle",
  "weight": 35,
  "color": "Roux doré",
  "description": "Golden Retriever roux, très sociable et doux. Excellent avec les enfants et autres animaux. Idéal pour familles aimantes.",
  "image": "https://images.unsplash.com/photo-1600011689520-08ab36fd3f37?w=500",
  "adoptionStatus": "Disponible",
  "vaccinated": true,
  "neutered": true,
  "adoptionFee": 180,
  "location": "Refuge Marseille",
  "features": [
    "Doux",
    "Sociable",
    "Actif",
    "Aimant"
  ],
  "createdAt": "2026-01-04T12:00:00Z"
}
```

### **Pet 4: Bella**
```json
{
  "name": "Bella",
  "breed": "Chat Persan",
  "age": 2,
  "gender": "Femelle",
  "weight": 4,
  "color": "Blanc et gris",
  "description": "Adorable chat Persan blanc et gris. Calme et affectueux, adore les caresses et les endroits confortables.",
  "image": "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=500",
  "adoptionStatus": "Disponible",
  "vaccinated": true,
  "neutered": true,
  "adoptionFee": 80,
  "location": "Refuge Paris",
  "features": [
    "Calme",
    "Affectueux",
    "Maison",
    "Doux"
  ],
  "createdAt": "2026-01-03T14:20:00Z"
}
```

### **Pet 5: Simba**
```json
{
  "name": "Simba",
  "breed": "Chat Européen",
  "age": 1,
  "gender": "Mâle",
  "weight": 3,
  "color": "Noir",
  "description": "Petit chat noir tout mignon, très joueur et curieux. Idéal pour appartement. Aime les jeux et l'exploration.",
  "image": "https://images.unsplash.com/photo-1573865526014-f3550b887b70?w=500",
  "adoptionStatus": "Disponible",
  "vaccinated": true,
  "neutered": false,
  "adoptionFee": 50,
  "location": "Refuge Lyon",
  "features": [
    "Joueur",
    "Curieux",
    "Affectueux",
    "Jeune"
  ],
  "createdAt": "2026-01-02T09:00:00Z"
}
```

### **Pet 6: Charlie**
```json
{
  "name": "Charlie",
  "breed": "Cocker Spaniel",
  "age": 5,
  "gender": "Mâle",
  "weight": 15,
  "color": "Noir",
  "description": "Cocker noir très obéissant et affectueux. Senior mais encore très actif et joueur. Calme mais plein de vie.",
  "image": "https://images.unsplash.com/photo-1619036633304-2284e1b2b61b?w=500",
  "adoptionStatus": "Disponible",
  "vaccinated": true,
  "neutered": true,
  "adoptionFee": 120,
  "location": "Refuge Toulouse",
  "features": [
    "Affectueux",
    "Obéissant",
    "Joueur",
    "Senior"
  ],
  "createdAt": "2026-01-01T11:00:00Z"
}
```

---

## 💻 FORMAT JAVA - Code pour Ajouter les Pets

### **Classe Pet Model**
```java
public class Pet {
    private String name;
    private String breed;
    private int age;
    private String gender;
    private double weight;
    private String color;
    private String description;
    private String image;
    private String adoptionStatus;
    private boolean vaccinated;
    private boolean neutered;
    private int adoptionFee;
    private String location;
    private List<String> features;
    
    public Pet() {}
    
    public Pet(String name, String breed, int age, String gender, 
               double weight, String color, String description, 
               String image, boolean vaccinated, boolean neutered, 
               int adoptionFee, String location) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.color = color;
        this.description = description;
        this.image = image;
        this.vaccinated = vaccinated;
        this.neutered = neutered;
        this.adoptionFee = adoptionFee;
        this.location = location;
        this.features = new ArrayList<>();
    }
    
    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getAdoptionStatus() { return adoptionStatus; }
    public void setAdoptionStatus(String status) { this.adoptionStatus = status; }
    
    public boolean isVaccinated() { return vaccinated; }
    public void setVaccinated(boolean vaccinated) { this.vaccinated = vaccinated; }
    
    public boolean isNeutered() { return neutered; }
    public void setNeutered(boolean neutered) { this.neutered = neutered; }
    
    public int getAdoptionFee() { return adoptionFee; }
    public void setAdoptionFee(int fee) { this.adoptionFee = fee; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
}
```

### **Méthode pour Ajouter les Pets**
```java
public class FirestoreDataHelper {
    
    public static void addAllPets(FirebaseFirestore db) {
        // Pet 1: Max
        Pet pet1 = new Pet("Max", "Labrador Retriever", 3, "Mâle", 32, 
                          "Marron", 
                          "Labrador brun très affectueux et énergique.",
                          "https://images.unsplash.com/photo-1633722715463-d30628cfa6a8?w=500",
                          true, true, 150, "Refuge Paris");
        pet1.setAdoptionStatus("Disponible");
        pet1.setFeatures(Arrays.asList("Actif", "Affectueux", "Intelligent", "Famille"));
        
        // Pet 2: Luna
        Pet pet2 = new Pet("Luna", "Berger Allemand", 2, "Femelle", 28, 
                          "Noir et Feu",
                          "Berger Allemand noir et feu, très dressée.",
                          "https://images.unsplash.com/photo-1568572933382-74d440642117?w=500",
                          true, true, 200, "Refuge Lyon");
        pet2.setAdoptionStatus("Disponible");
        pet2.setFeatures(Arrays.asList("Protecteur", "Obéissant", "Intelligent", "Vigilant"));
        
        // Pet 3: Milo
        Pet pet3 = new Pet("Milo", "Golden Retriever", 4, "Mâle", 35, 
                          "Roux doré",
                          "Golden Retriever roux, très sociable.",
                          "https://images.unsplash.com/photo-1600011689520-08ab36fd3f37?w=500",
                          true, true, 180, "Refuge Marseille");
        pet3.setAdoptionStatus("Disponible");
        pet3.setFeatures(Arrays.asList("Doux", "Sociable", "Actif", "Aimant"));
        
        // Pet 4: Bella
        Pet pet4 = new Pet("Bella", "Chat Persan", 2, "Femelle", 4, 
                          "Blanc et gris",
                          "Adorable chat Persan blanc et gris.",
                          "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=500",
                          true, true, 80, "Refuge Paris");
        pet4.setAdoptionStatus("Disponible");
        pet4.setFeatures(Arrays.asList("Calme", "Affectueux", "Maison", "Doux"));
        
        // Pet 5: Simba
        Pet pet5 = new Pet("Simba", "Chat Européen", 1, "Mâle", 3, 
                          "Noir",
                          "Petit chat noir tout mignon, très joueur.",
                          "https://images.unsplash.com/photo-1573865526014-f3550b887b70?w=500",
                          true, false, 50, "Refuge Lyon");
        pet5.setAdoptionStatus("Disponible");
        pet5.setFeatures(Arrays.asList("Joueur", "Curieux", "Affectueux", "Jeune"));
        
        // Pet 6: Charlie
        Pet pet6 = new Pet("Charlie", "Cocker Spaniel", 5, "Mâle", 15, 
                          "Noir",
                          "Cocker noir très obéissant et affectueux.",
                          "https://images.unsplash.com/photo-1619036633304-2284e1b2b61b?w=500",
                          true, true, 120, "Refuge Toulouse");
        pet6.setAdoptionStatus("Disponible");
        pet6.setFeatures(Arrays.asList("Affectueux", "Obéissant", "Joueur", "Senior"));
        
        // Liste de tous les pets
        List<Pet> pets = Arrays.asList(pet1, pet2, pet3, pet4, pet5, pet6);
        
        // Batch write pour ajouter tous les pets
        WriteBatch batch = db.batch();
        
        for (int i = 0; i < pets.size(); i++) {
            String docId = "pet_" + String.format("%03d", i + 1);
            DocumentReference docRef = db.collection("pets").document(docId);
            batch.set(docRef, pets.get(i));
        }
        
        batch.commit()
            .addOnSuccessListener(aVoid -> {
                Log.d("Firestore", "✅ Tous les " + pets.size() + " pets ajoutés avec succès!");
            })
            .addOnFailureListener(e -> {
                Log.e("Firestore", "❌ Erreur lors de l'ajout: " + e.getMessage());
            });
    }
}

// Usage dans MainActivity
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.adoption_activity_main);
    
    // Ajouter les pets (une seule fois!)
    FirestoreDataHelper.addAllPets(FirebaseFirestore.getInstance());
}
```

---

## 📱 CHAMPS EXPLIQUÉS

| Champ | Type | Exemple | Description |
|-------|------|---------|-------------|
| `name` | String | "Max" | Nom de l'animal |
| `breed` | String | "Labrador Retriever" | Race/Espèce |
| `age` | Number | 3 | Âge en années |
| `gender` | String | "Mâle" | Sexe de l'animal |
| `weight` | Number | 32 | Poids en kg |
| `color` | String | "Marron" | Couleur/Description physique |
| `description` | String | "Labrador très affectueux..." | Description complète |
| `image` | String | URL | Lien vers photo |
| `adoptionStatus` | String | "Disponible" | Status (Disponible, Adopté, En attente) |
| `vaccinated` | Boolean | true | Vacciné? |
| `neutered` | Boolean | true | Stérilisé/Castré? |
| `adoptionFee` | Number | 150 | Frais d'adoption en euros |
| `location` | String | "Refuge Paris" | Refuge/Localisation |
| `features` | Array | ["Actif", "Affectueux"] | Tags/Caractéristiques |
| `createdAt` | Timestamp | 2026-01-06T10:00:00Z | Date d'ajout |

---

## ✅ LISTE DE VÉRIFICATION

Avant d'ajouter les pets, vérifier:

- [ ] Collection "pets" créée dans Firestore
- [ ] Firebase projet connecté
- [ ] Rules Firestore permettent les écritures (auth != null)
- [ ] Fichier `google-services.json` présent
- [ ] Internet activé sur l'appareil/émulateur

---

## 🔍 VÉRIFICATION APRÈS AJOUT

```java
// Code pour vérifier que les pets sont bien ajoutés
FirebaseFirestore.getInstance()
    .collection("pets")
    .get()
    .addOnSuccessListener(queryDocumentSnapshots -> {
        Log.d("Adoption", "✅ Total pets: " + queryDocumentSnapshots.size());
        
        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
            String name = doc.getString("name");
            String breed = doc.getString("breed");
            int age = doc.getLong("age").intValue();
            
            Log.d("Pet", name + " - " + breed + " (" + age + " ans)");
        }
    })
    .addOnFailureListener(e -> {
        Log.e("Adoption", "❌ Erreur: " + e.getMessage());
    });
```

---

## 📸 IMAGES UTILISÉES

Tous les liens utilisent Unsplash (images libres):

- **Max (Labrador)**: https://images.unsplash.com/photo-1633722715463-d30628cfa6a8
- **Luna (Berger)**: https://images.unsplash.com/photo-1568572933382-74d440642117
- **Milo (Golden)**: https://images.unsplash.com/photo-1600011689520-08ab36fd3f37
- **Bella (Chat Persan)**: https://images.unsplash.com/photo-1574158622682-e40e69881006
- **Simba (Chat Noir)**: https://images.unsplash.com/photo-1573865526014-f3550b887b70
- **Charlie (Cocker)**: https://images.unsplash.com/photo-1619036633304-2284e1b2b61b

---

**Créé le**: 6 janvier 2026  
**Nombre de pets**: 6  
**Formats**: JSON, Java, Tableau  
**Prêt à utiliser**: ✅
