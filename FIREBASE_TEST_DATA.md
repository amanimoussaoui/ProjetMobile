# 🔥 DONNÉES DE TEST FIREBASE POUR PETCONNECT

## 📊 GUIDE D'AJOUT DES DONNÉES

### Étape 1: Accéder à Firebase Console
1. Ouvrir https://console.firebase.google.com
2. Sélectionner projet **PetConnect**
3. Aller dans **Firestore Database**

---

## 🐾 COLLECTION: `pets` (Pour module Adoption)

### Document 1: `pet001`
```json
{
  "id": "pet001",
  "name": "Max",
  "emoji": "🐕",
  "age": "2 ans",
  "gender": "Mâle",
  "breed": "Golden Retriever",
  "location": "Paris, France",
  "photoPath": "https://images.unsplash.com/photo-1633722715463-d30f4f325e24?w=800",
  "about": "Max est un golden retriever affectueux et joueur. Parfait pour une famille avec enfants.",
  "requirements": [
    "Jardin ou espace extérieur",
    "Promenades quotidiennes",
    "Temps de jeu régulier"
  ],
  "isAdopted": false,
  "createdAt": "2026-01-06T00:00:00Z"
}
```

### Document 2: `pet002`
```json
{
  "id": "pet002",
  "name": "Luna",
  "emoji": "🐈",
  "age": "1 an",
  "gender": "Femelle",
  "breed": "Chat Européen",
  "location": "Lyon, France",
  "photoPath": "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800",
  "about": "Luna est une chatte calme et indépendante. Idéale pour un appartement.",
  "requirements": [
    "Litière propre",
    "Nourriture de qualité",
    "Arbre à chat"
  ],
  "isAdopted": false,
  "createdAt": "2026-01-06T00:00:00Z"
}
```

### Document 3: `pet003`
```json
{
  "id": "pet003",
  "name": "Rocky",
  "emoji": "🐕",
  "age": "3 ans",
  "gender": "Mâle",
  "breed": "Berger Allemand",
  "location": "Marseille, France",
  "photoPath": "https://images.unsplash.com/photo-1568572933382-74d440642117?w=800",
  "about": "Rocky est un chien protecteur et loyal. Excellent gardien et compagnon fidèle.",
  "requirements": [
    "Maison avec jardin",
    "Exercice quotidien",
    "Dressage continu"
  ],
  "isAdopted": false,
  "createdAt": "2026-01-06T00:00:00Z"
}
```

### Document 4: `pet004`
```json
{
  "id": "pet004",
  "name": "Bella",
  "emoji": "🐕",
  "age": "1 an",
  "gender": "Femelle",
  "breed": "Labrador",
  "location": "Toulouse, France",
  "photoPath": "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=800",
  "about": "Bella est une labrador énergique qui adore jouer et nager. Parfaite pour les activités en plein air.",
  "requirements": [
    "Famille active",
    "Accès à l'eau",
    "Beaucoup d'exercice"
  ],
  "isAdopted": false,
  "createdAt": "2026-01-06T00:00:00Z"
}
```

### Document 5: `pet005`
```json
{
  "id": "pet005",
  "name": "Milo",
  "emoji": "🐈",
  "age": "6 mois",
  "gender": "Mâle",
  "breed": "Maine Coon",
  "location": "Nice, France",
  "photoPath": "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800",
  "about": "Milo est un chaton Maine Coon très sociable et affectueux. Il adore les câlins.",
  "requirements": [
    "Brossage régulier",
    "Jouets interactifs",
    "Attention quotidienne"
  ],
  "isAdopted": false,
  "createdAt": "2026-01-06T00:00:00Z"
}
```

---

## 📅 COLLECTION: `events` (Pour module Events)

### Document 1: (Déjà existant - Atelier Dressage)
✅ Visible dans les captures d'écran

---

## 🛒 COLLECTION: `products` (Pour module Shop)

### Document 1: (Croquettes Premium)
✅ Visible dans les captures d'écran

---

## 🔧 COMMANDES POUR AJOUTER LES DONNÉES

### Via Firebase Console (Interface Web)

1. **Aller dans Firestore Database**
2. **Créer collection `pets`**
3. **Ajouter chaque document** avec l'ID correspondant (pet001, pet002, etc.)
4. **Copier-coller les données JSON** ci-dessus

### Ou via Script (si vous avez Node.js)

```bash
# Dans le terminal
cd c:\Users\raeda\AndroidStudioProjects\PetConnect
node add_test_data.js
```

---

## ✅ VÉRIFICATION

Après avoir ajouté les données:

1. **Rebuild l'app**: `gradlew clean build`
2. **Lancer l'app**
3. **Aller dans Adoption**
4. **Vérifier**: Les 5 animaux doivent apparaître en swipe

---

## 🐛 PROBLÈMES RÉSOLUS

| Problème | Solution | Statut |
|----------|----------|--------|
| Register - CONFIGURATION_NOT_FOUND | Vérifier `google-services.json` + Firebase init | ⚠️ À vérifier |
| Adoption - ViewPager introuvable | Changé layout `activity_main` → `adoption_activity_main` | ✅ Corrigé |
| Adoption - Aucun pet Firebase | Ajouter données test ci-dessus | 📝 À faire |
| Invoice - QR Code invisible | Amélioré logs + forcé visibilité | ✅ Corrigé |

---

## 🚀 PROCHAINES ÉTAPES

1. ✅ Rebuild l'application
2. 📝 Ajouter les données pets dans Firebase Console
3. ✅ Tester module Adoption (devrait fonctionner)
4. ✅ Tester QR Code Invoice (logs dans Logcat)
5. ⚠️ Si Register bug persiste → Vérifier `google-services.json`
