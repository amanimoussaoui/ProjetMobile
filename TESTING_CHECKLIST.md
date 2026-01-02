# ✅ Checklist de Tests - PetConnect Event

## 📋 Avant de Commencer

- [ ] Android Studio est installé et à jour
- [ ] Le projet s'ouvre sans erreur
- [ ] Gradle sync réussit
- [ ] Firebase est configuré avec `google-services.json`
- [ ] Firestore est activé en mode test
- [ ] Des événements de test sont créés dans Firestore
- [ ] Un émulateur ou appareil physique est prêt

---

## 🏗️ Tests de Compilation

### Build du Projet
- [ ] **Build > Make Project** : Succès ✅
- [ ] **Build > Clean Project** puis **Rebuild** : Succès ✅
- [ ] Aucune erreur dans les logs de compilation
- [ ] Toutes les classes Java sont reconnues

### Vérifications des Ressources
- [ ] Layouts XML sans erreur
- [ ] Drawables présents : ic_event, ic_calendar, ic_location, ic_people, ic_add_event
- [ ] Fichier strings.xml complet
- [ ] Fichier colors.xml présent
- [ ] Fichier themes.xml configuré

---

## 📱 Tests Fonctionnels

### 1. Lancement de l'Application

#### Test 1.1 : Démarrage
- [ ] L'application se lance sans crash
- [ ] Pas d'erreur dans Logcat
- [ ] L'écran de liste des événements s'affiche
- [ ] La toolbar affiche "Événements"

#### Test 1.2 : Chargement Initial
- [ ] Les événements Firestore se chargent
- [ ] La liste n'est pas vide (si des données existent)
- [ ] Le RecyclerView affiche correctement les cartes
- [ ] Le FAB (+) est visible en bas à droite

---

### 2. Liste des Événements

#### Test 2.1 : Affichage
- [ ] Chaque événement affiche :
  - [ ] Titre correct
  - [ ] Date formatée (format français)
  - [ ] Lieu complet
  - [ ] Image par défaut (ic_event)
- [ ] Les cartes ont des coins arrondis
- [ ] L'espacement entre les cartes est correct
- [ ] Le scroll fonctionne si > 5 événements

#### Test 2.2 : Interaction
- [ ] Cliquer sur un événement ouvre les détails
- [ ] Le FAB est toujours visible lors du scroll
- [ ] Pas de lag lors du scroll

---

### 3. Créer un Événement

#### Test 3.1 : Ouverture du Formulaire
- [ ] Cliquer sur le FAB ouvre AddEditEventActivity
- [ ] Le titre affiche "Créer un événement"
- [ ] Tous les champs sont vides
- [ ] Le champ Date n'est pas éditable directement

#### Test 3.2 : Sélection de Date
- [ ] Cliquer sur le champ Date ouvre le DatePicker
- [ ] Impossible de sélectionner une date passée
- [ ] Après sélection de la date, le TimePicker s'ouvre
- [ ] La date + heure sélectionnées s'affichent correctement

#### Test 3.3 : Validation des Champs
- [ ] Laisser un champ vide → Message d'erreur
- [ ] Entrer 0 participants → Message d'erreur
- [ ] Entrer un nombre négatif → Message d'erreur
- [ ] Tous les champs remplis → Permet de sauvegarder

#### Test 3.4 : Sauvegarde
- [ ] Remplir tous les champs correctement
- [ ] Cliquer sur "Enregistrer"
- [ ] Toast "Événement créé avec succès"
- [ ] Retour à la liste automatique
- [ ] Le nouvel événement apparaît dans la liste
- [ ] Vérifier dans Firestore que l'événement existe

#### Test 3.5 : Données Créées
Vérifier dans Firestore que l'événement contient :
- [ ] ID généré automatiquement
- [ ] title, description, location corrects
- [ ] eventDate au bon format (Timestamp)
- [ ] maxParticipants > 0
- [ ] currentParticipants = 0
- [ ] isActive = true
- [ ] createdAt (timestamp actuel)
- [ ] organizerId présent

---

### 4. Détails d'un Événement

#### Test 4.1 : Affichage Complet
- [ ] Titre affiché en grand
- [ ] Description complète et lisible
- [ ] Date formatée : "15 février 2026 à 14:00"
- [ ] Lieu complet
- [ ] Nombre de participants : "5 / 20 participants"
- [ ] Image par défaut affichée
- [ ] Bouton "S'inscrire" visible

#### Test 4.2 : Icônes
- [ ] Icône calendrier à côté de la date
- [ ] Icône localisation à côté du lieu
- [ ] Icône personnes à côté des participants
- [ ] Couleur des icônes : #329DA3

#### Test 4.3 : Bouton d'Inscription
- [ ] Si places disponibles → Bouton actif "S'inscrire"
- [ ] Si événement complet → Bouton désactivé "Complet"

---

### 5. Inscription à un Événement

#### Test 5.1 : Ouverture du Formulaire
- [ ] Cliquer sur "S'inscrire" ouvre RegisterEventActivity
- [ ] Le titre de l'événement s'affiche
- [ ] Formulaire avec 3 champs : Nom, Email, Notes
- [ ] Champ Notes est optionnel

#### Test 5.2 : Validation
- [ ] Laisser Nom vide → Erreur
- [ ] Laisser Email vide → Erreur
- [ ] Email sans @ → Erreur "Email invalide"
- [ ] Notes vide → Accepté (optionnel)
- [ ] Nom + Email valides → Permet de confirmer

#### Test 5.3 : Confirmation
- [ ] Remplir Nom: "Jean Dupont"
- [ ] Remplir Email: "jean.dupont@test.com"
- [ ] Optionnel: Ajouter une note
- [ ] Cliquer sur "Confirmer l'inscription"
- [ ] Toast "Inscription confirmée!"
- [ ] Retour automatique à l'écran précédent

#### Test 5.4 : Vérification Firestore
Dans la collection `event_registrations` :
- [ ] Nouveau document créé
- [ ] eventId correspond à l'événement
- [ ] userName = "Jean Dupont"
- [ ] userEmail = "jean.dupont@test.com"
- [ ] status = "CONFIRMED"
- [ ] registrationDate (timestamp actuel)
- [ ] notes (si renseigné)

---

### 6. Événement Complet

#### Test 6.1 : Configuration
- [ ] Créer un événement avec maxParticipants = 2
- [ ] S'inscrire une première fois (différent nom/email)
- [ ] Retourner aux détails

#### Test 6.2 : Vérification
- [ ] Participants affiche "1 / 2"
- [ ] Bouton toujours actif "S'inscrire"

#### Test 6.3 : Deuxième Inscription
- [ ] S'inscrire une deuxième fois (autre nom/email)
- [ ] Retourner aux détails
- [ ] Participants affiche "2 / 2"
- [ ] Bouton désactivé et texte "Complet"
- [ ] Bouton grisé et non cliquable

---

### 7. Navigation

#### Test 7.1 : Back Navigation
- [ ] Liste → Détails → Back → Retour à la liste
- [ ] Liste → Créer → Back → Retour à la liste
- [ ] Détails → Inscription → Back → Retour aux détails

#### Test 7.2 : Actualisation
- [ ] Créer un événement → Retour → Événement visible
- [ ] S'inscrire → Retour détails → Compteur mis à jour

---

### 8. Tests de Robustesse

#### Test 8.1 : Connexion Internet
- [ ] Désactiver le Wi-Fi/Data
- [ ] Lancer l'app → Liste vide avec message
- [ ] Réactiver Internet
- [ ] Tirer pour actualiser (si implémenté) ou relancer

#### Test 8.2 : Données Extrêmes
- [ ] Créer événement avec description très longue (500+ caractères)
- [ ] Créer événement avec maxParticipants = 1000
- [ ] Titre avec caractères spéciaux: "Événement été 2026!"
- [ ] Lieu avec accents: "Café du Château à Côté"

#### Test 8.3 : Actions Rapides
- [ ] Cliquer rapidement plusieurs fois sur un événement
- [ ] Cliquer rapidement sur "Enregistrer"
- [ ] Scroll rapide dans la liste

---

### 9. Tests UI/UX

#### Test 9.1 : Design
- [ ] Couleurs cohérentes (Primary: #329DA3)
- [ ] Police lisible et cohérente
- [ ] Espacement agréable entre les éléments
- [ ] Cards avec ombres et coins arrondis

#### Test 9.2 : Responsive
- [ ] Tester en mode Portrait
- [ ] Tester en mode Paysage
- [ ] Textes ne dépassent pas
- [ ] Boutons accessibles

#### Test 9.3 : Accessibilité
- [ ] contentDescription sur toutes les images
- [ ] Textes assez grands (min 14sp)
- [ ] Contraste suffisant

---

### 10. Logs et Erreurs

#### Test 10.1 : Logcat
- [ ] Pas d'erreurs rouges pendant l'utilisation normale
- [ ] Pas de warnings critiques
- [ ] Messages de Firebase visibles

#### Test 10.2 : Cas d'Erreurs Gérées
- [ ] Événement inexistant → Message d'erreur + retour
- [ ] Erreur réseau → Toast informatif
- [ ] Validation formulaire → Messages clairs

---

## 🎯 Tests Supplémentaires Recommandés

### Performance
- [ ] Temps de chargement < 2 secondes
- [ ] Pas de freeze lors du scroll
- [ ] Animations fluides

### Sécurité Firebase
- [ ] Règles Firestore en mode test activées
- [ ] Données visibles dans la console Firebase
- [ ] Pas de données sensibles en clair

### Multi-utilisateurs
- [ ] Créer événement depuis émulateur A
- [ ] Voir événement depuis émulateur B
- [ ] S'inscrire depuis B
- [ ] Voir compteur mis à jour depuis A (après refresh)

---

## 📊 Résumé des Tests

**Total Tests à Effectuer** : ~80 points de vérification

### Statut Global
- [ ] ✅ Tous les tests passent
- [ ] ⚠️ Tests partiels (noter les échecs)
- [ ] ❌ Tests échoués (corriger avant déploiement)

### Notes de Test
```
Date du test : ______________
Testeur : ______________
Appareil/Émulateur : ______________
Version Android : ______________

Bugs trouvés :
1. ______________________________
2. ______________________________
3. ______________________________

Améliorations suggérées :
1. ______________________________
2. ______________________________
3. ______________________________
```

---

## 🐛 Problèmes Courants et Solutions

| Problème | Solution |
|----------|----------|
| Liste vide au démarrage | Vérifier connexion + règles Firestore + données de test |
| App crash au lancement | Vérifier google-services.json + Logcat pour l'erreur exacte |
| Dates non formatées | Vérifier SimpleDateFormat en français |
| Images ne s'affichent pas | Normal (Glide commenté), décommenter si besoin |
| Toast ne s'affiche pas | Vérifier que l'activité n'est pas finish() trop tôt |

---

**🎉 Une fois tous les tests validés, votre application est prête!**
