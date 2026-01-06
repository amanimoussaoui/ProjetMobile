# 📱 GUIDE PAS À PAS - AJOUTER LES DONNÉES MANUELLEMENT

**Méthode**: Via Firebase Console (interface web)  
**Temps**: ~10 minutes pour les 6 pets  
**Difficulté**: Très facile ✅

---

## 🌐 ÉTAPE 1: Ouvrir Firebase Console

```
1. Aller à: https://console.firebase.google.com
2. Sélectionner le projet "PetConnect"
3. Dans le menu gauche, cliquer sur "Firestore Database"
```

---

## 📊 ÉTAPE 2: Voir la Collection "pets"

**État actuel**:
```
pets/ (vide - 0 document)
```

**À faire**:
```
1. Voir le panneau gauche
2. Cliquer sur "pets" pour la sélectionner
3. Vous devriez voir "Ajouter un document" ou "+" button
```

---

## ➕ ÉTAPE 3: Ajouter le Premier Pet (MAX)

### **Étape 3.1: Créer le Document**
```
1. Cliquer sur "+ Ajouter un document"
2. Champ "ID du document": 
   ⟶ Écrire: pet_001
   (ou laisser "Auto-ID" générer)
3. Cliquer "Suivant" ou "Créer"
```

### **Étape 3.2: Ajouter les Champs**

**COPIER-COLLER les données suivantes EXACTEMENT**:

| # | Champ | Type | Valeur |
|---|-------|------|--------|
| 1 | name | String | Max |
| 2 | breed | String | Labrador Retriever |
| 3 | age | Number | 3 |
| 4 | gender | String | Mâle |
| 5 | weight | Number | 32 |
| 6 | color | String | Marron |
| 7 | description | String | Labrador brun très affectueux et énergique. Adorable avec les enfants, parfait pour la famille. Aime les jeux et la baignade. |
| 8 | image | String | https://images.unsplash.com/photo-1633722715463-d30628cfa6a8?w=500 |
| 9 | adoptionStatus | String | Disponible |
| 10 | vaccinated | Boolean | true |
| 11 | neutered | Boolean | true |
| 12 | adoptionFee | Number | 150 |
| 13 | location | String | Refuge Paris |

**Comment ajouter les champs**:
```
1. Voir le champ vide "Ajouter un champ"
2. Cliquer dessus
3. Écrire le nom du champ (ex: "name")
4. Choisir le type (String, Number, Boolean, etc.)
5. Écrire la valeur
6. Cliquer "Ajouter un champ" pour ajouter le suivant
7. Répéter pour tous les 13 champs
```

### **Étape 3.3: Enregistrer**
```
1. Cliquer sur "Enregistrer"
2. ✅ Le document pet_001 apparaît dans la liste
```

---

## 🐕 PET 2: LUNA

### **Étape 4: Ajouter le 2ème Document**

```
1. Cliquer "+ Ajouter un document" (de nouveau)
2. ID: pet_002
3. Ajouter les champs:
```

| Champ | Type | Valeur |
|-------|------|--------|
| name | String | Luna |
| breed | String | Berger Allemand |
| age | Number | 2 |
| gender | String | Femelle |
| weight | Number | 28 |
| color | String | Noir et Feu |
| description | String | Berger Allemand noir et feu, très dressée et obéissante. Garde et protection naturelle. Excellente pour sécurité et protection. |
| image | String | https://images.unsplash.com/photo-1568572933382-74d440642117?w=500 |
| adoptionStatus | String | Disponible |
| vaccinated | Boolean | true |
| neutered | Boolean | true |
| adoptionFee | Number | 200 |
| location | String | Refuge Lyon |

```
4. Cliquer "Enregistrer"
5. ✅ pet_002 apparaît
```

---

## 🐕 PET 3: MILO

### **Étape 5: Ajouter pet_003**

```
ID: pet_003
```

| Champ | Type | Valeur |
|-------|------|--------|
| name | String | Milo |
| breed | String | Golden Retriever |
| age | Number | 4 |
| gender | String | Mâle |
| weight | Number | 35 |
| color | String | Roux doré |
| description | String | Golden Retriever roux, très sociable et doux. Excellent avec les enfants et autres animaux. Idéal pour familles aimantes. |
| image | String | https://images.unsplash.com/photo-1600011689520-08ab36fd3f37?w=500 |
| adoptionStatus | String | Disponible |
| vaccinated | Boolean | true |
| neutered | Boolean | true |
| adoptionFee | Number | 180 |
| location | String | Refuge Marseille |

---

## 🐱 PET 4: BELLA

### **Étape 6: Ajouter pet_004**

```
ID: pet_004
```

| Champ | Type | Valeur |
|-------|------|--------|
| name | String | Bella |
| breed | String | Chat Persan |
| age | Number | 2 |
| gender | String | Femelle |
| weight | Number | 4 |
| color | String | Blanc et gris |
| description | String | Adorable chat Persan blanc et gris. Calme et affectueux, adore les caresses et les endroits confortables. |
| image | String | https://images.unsplash.com/photo-1574158622682-e40e69881006?w=500 |
| adoptionStatus | String | Disponible |
| vaccinated | Boolean | true |
| neutered | Boolean | true |
| adoptionFee | Number | 80 |
| location | String | Refuge Paris |

---

## 🐱 PET 5: SIMBA

### **Étape 7: Ajouter pet_005**

```
ID: pet_005
```

| Champ | Type | Valeur |
|-------|------|--------|
| name | String | Simba |
| breed | String | Chat Européen |
| age | Number | 1 |
| gender | String | Mâle |
| weight | Number | 3 |
| color | String | Noir |
| description | String | Petit chat noir tout mignon, très joueur et curieux. Idéal pour appartement. Aime les jeux et l'exploration. |
| image | String | https://images.unsplash.com/photo-1573865526014-f3550b887b70?w=500 |
| adoptionStatus | String | Disponible |
| vaccinated | Boolean | true |
| neutered | Boolean | false |
| adoptionFee | Number | 50 |
| location | String | Refuge Lyon |

---

## 🐕 PET 6: CHARLIE

### **Étape 8: Ajouter pet_006**

```
ID: pet_006
```

| Champ | Type | Valeur |
|-------|------|--------|
| name | String | Charlie |
| breed | String | Cocker Spaniel |
| age | Number | 5 |
| gender | String | Mâle |
| weight | Number | 15 |
| color | String | Noir |
| description | String | Cocker noir très obéissant et affectueux. Senior mais encore très actif et joueur. Calme mais plein de vie. |
| image | String | https://images.unsplash.com/photo-1619036633304-2284e1b2b61b?w=500 |
| adoptionStatus | String | Disponible |
| vaccinated | Boolean | true |
| neutered | Boolean | true |
| adoptionFee | Number | 120 |
| location | String | Refuge Toulouse |

---

## ✅ RÉSULTAT FINAL

**Après avoir ajouté les 6 pets, Firestore affichera**:

```
pets/ (6 documents)
├── pet_001 (Max)
├── pet_002 (Luna)
├── pet_003 (Milo)
├── pet_004 (Bella)
├── pet_005 (Simba)
└── pet_006 (Charlie)
```

---

## 🧪 VÉRIFICATION

### **Dans Firestore Console**
```
1. Cliquer sur "pets"
2. Voir les 6 documents listés
3. Cliquer sur "pet_001" → voir les champs de Max
```

### **Dans l'Application Android**
```
1. Rebuild l'app: ./gradlew assembleDebug
2. Lancer l'app
3. Aller à la section "Adoption"
4. Voir: Max, Luna, Milo (chiens)
5. Voir: Bella, Simba (chats)
6. Voir: Charlie (cocker)
7. ✅ Message "Aucun animal trouvé" → DISPARU! 🎉
```

---

## 🎨 AIDE VISUELLE

### **Où cliquer dans Firebase Console**

```
Firebase Console
├── PetConnect (projet)
│   ├── [Menu gauche]
│   │   └── Firestore Database ← CLIQUER ICI
│   │
│   └── [Panneau principal]
│       ├── pets ← Sélectionner cette collection
│       │
│       └── "+ Ajouter un document" ← CLIQUER POUR CHAQUE PET
```

### **Formulaire d'Ajout**

```
┌─────────────────────────────────────┐
│ Ajouter un document                 │
├─────────────────────────────────────┤
│ ID du document: [pet_001          ] │
├─────────────────────────────────────┤
│ Champs:                             │
│                                     │
│ Champ 1: [name    ] = [Max       ] │
│ Champ 2: [breed   ] = [Labrador  ] │
│ Champ 3: [age     ] = [3         ] │
│ ... (autres champs)                 │
│                                     │
│ [+ Ajouter un champ] [Enregistrer] │
└─────────────────────────────────────┘
```

---

## 💡 ASTUCES

### **Copier-Coller Rapide**
```
1. Laisser ouvert ce guide
2. Copier les valeurs de chaque champ
3. Les coller dans Firestore
4. Plus rapide que de taper manuellement!
```

### **Vérifier les Types**
```
String ← "Max", "Labrador", descriptions
Number ← 3, 32, 150 (SANS guillemets)
Boolean ← true ou false (SANS guillemets)
```

### **Si vous faites une erreur**
```
1. Cliquer sur le document
2. Modifier le champ
3. Cliquer "Enregistrer"
4. C'est tout!
```

---

## 🚨 PROBLÈMES COURANTS

### **Problème: "Le champ ne peut pas être vide"**
**Solution**: 
```
- Vérifier que vous avez écrit une valeur
- Vérifier que le type est correct
- Ne pas laisser de champs vides
```

### **Problème: "Document déjà existant"**
**Solution**:
```
- Utiliser un ID différent (pet_001, pet_002, etc.)
- Ou utiliser "Auto-ID" qui génère des IDs automatiques
```

### **Problème: "L'image ne s'affiche pas"**
**Solution**:
```
- Les URLs doivent commencer par https://
- Vérifier que le lien est valide (cliquable dans navigateur)
- Les images Unsplash sont publiques ✅
```

---

## ⏱️ TEMPS ESTIMÉ

```
Pet 1 (Max):      2-3 minutes (première fois plus lent)
Pet 2 (Luna):     1-2 minutes
Pet 3 (Milo):     1-2 minutes
Pet 4 (Bella):    1-2 minutes
Pet 5 (Simba):    1-2 minutes
Pet 6 (Charlie):  1-2 minutes
─────────────────────────────
TOTAL:            ~10 minutes
```

---

**C'est fini! Vous pouvez maintenant tester l'app! 🎉**

Créé le: 6 janvier 2026  
Dernière mise à jour: Maintenant  
Guide version: 2.0 (Manuel)
