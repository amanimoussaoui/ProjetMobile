# 📧 Configuration de l'Envoi d'Email avec Photo

## ✅ Ce qui a été implémenté

### 1. **Service Email (EmailService.java)**
- ✅ Service créé pour gérer l'envoi d'emails
- ✅ Les demandes d'envoi sont stockées dans Firestore dans la collection `email_queue`
- ✅ Chaque demande contient : email du destinataire, sujet, corps, URL de l'image, statut

### 2. **Intégration dans PhotoVerificationActivity**
- ✅ Après l'upload de la photo, l'email est automatiquement mis en queue
- ✅ Le message informatif est affiché à l'utilisateur

### 3. **Amélioration de la Caméra**
- ✅ La caméra s'ouvre automatiquement au démarrage de l'activité
- ✅ Si la caméra n'est pas disponible, bascule automatiquement vers la galerie
- ✅ Meilleure gestion des erreurs

## 🔧 Configuration Nécessaire

### Option 1 : Firebase Functions (Recommandé)

Vous devez créer une Firebase Function qui :
1. Écoute les nouveaux documents dans `email_queue`
2. Télécharge l'image depuis Firebase Storage
3. Envoie l'email avec pièce jointe
4. Met à jour le statut à "sent" ou "failed"

**Exemple de Firebase Function (Node.js) :**

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
const axios = require('axios');

admin.initializeApp();

// Configurez votre transporteur email (Gmail, SendGrid, etc.)
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: functions.config().email.user,
    pass: functions.config().email.password
  }
});

exports.sendVerificationEmail = functions.firestore
  .document('email_queue/{emailId}')
  .onCreate(async (snap, context) => {
    const emailData = snap.data();
    
    if (emailData.status !== 'pending') {
      return null;
    }

    try {
      // Télécharger l'image depuis Firebase Storage
      const imageResponse = await axios.get(emailData.imageUrl, {
        responseType: 'arraybuffer'
      });
      const imageBuffer = Buffer.from(imageResponse.data);

      // Préparer l'email
      const mailOptions = {
        from: functions.config().email.user,
        to: emailData.to,
        subject: emailData.subject,
        text: emailData.body,
        attachments: [{
          filename: 'verification_photo.jpg',
          content: imageBuffer,
          contentType: 'image/jpeg'
        }]
      };

      // Envoyer l'email
      await transporter.sendMail(mailOptions);

      // Mettre à jour le statut
      await snap.ref.update({
        status: 'sent',
        sentAt: admin.firestore.FieldValue.serverTimestamp()
      });

      console.log('Email sent successfully to:', emailData.to);
      return null;
    } catch (error) {
      console.error('Error sending email:', error);
      
      // Mettre à jour le statut en erreur
      await snap.ref.update({
        status: 'failed',
        error: error.message,
        failedAt: admin.firestore.FieldValue.serverTimestamp()
      });
      
      return null;
    }
  });
```

**Déployer la fonction :**
```bash
firebase deploy --only functions:sendVerificationEmail
```

**Configurer les variables d'environnement :**
```bash
firebase functions:config:set email.user="votre-email@gmail.com" email.password="votre-mot-de-passe"
```

### Option 2 : Utiliser un Service Email Tiers (SendGrid, Mailgun, etc.)

Si vous utilisez SendGrid :

```javascript
const sgMail = require('@sendgrid/mail');
sgMail.setApiKey(functions.config().sendgrid.key);

// Dans votre Firebase Function
const msg = {
  to: emailData.to,
  from: 'noreply@petconnect.com',
  subject: emailData.subject,
  text: emailData.body,
  attachments: [{
    content: imageBuffer.toString('base64'),
    filename: 'verification_photo.jpg',
    type: 'image/jpeg',
    disposition: 'attachment'
  }]
};

await sgMail.send(msg);
```

## 📋 Structure de la Collection `email_queue`

Chaque document dans `email_queue` a la structure suivante :

```json
{
  "to": "user@example.com",
  "subject": "Vérification de sécurité - PetConnect",
  "body": "Bonjour,\n\nUne tentative de connexion...",
  "imageUrl": "https://firebasestorage.googleapis.com/...",
  "status": "pending",  // pending, sent, failed
  "createdAt": 1234567890,
  "type": "verification_photo"
}
```

## 🔒 Règles de Sécurité Firestore

Ajoutez ces règles pour la collection `email_queue` :

```javascript
match /email_queue/{emailId} {
  // Permettre la création pour tous les utilisateurs authentifiés
  allow create: if request.auth != null;
  
  // Seuls les admins ou le système peuvent lire/modifier
  allow read, update: if request.auth != null && 
    (request.auth.token.admin == true || 
     resource.data.to == request.auth.token.email);
}
```

## 🧪 Test

1. **Tester localement** :
   - Tentez de vous connecter avec un mauvais mot de passe 3 fois
   - Prenez ou sélectionnez une photo
   - Vérifiez que le document est créé dans `email_queue`

2. **Vérifier dans Firebase Console** :
   - Allez dans Firestore → `email_queue`
   - Vérifiez que le document est créé avec `status: "pending"`

3. **Vérifier l'envoi** :
   - Si vous avez configuré Firebase Functions, l'email devrait être envoyé automatiquement
   - Vérifiez que le statut passe à "sent" ou "failed"

## ⚠️ Notes Importantes

1. **Permissions** : Android nécessite toujours une permission pour la caméra. Le code la demande automatiquement, mais l'utilisateur doit l'accepter.

2. **Backend requis** : L'envoi d'email avec pièce jointe nécessite un backend. L'application stocke seulement la demande dans Firestore.

3. **Sécurité** : Ne stockez jamais les mots de passe email en clair. Utilisez les variables d'environnement de Firebase Functions.

4. **Alternative simple** : Si vous ne voulez pas configurer Firebase Functions, vous pouvez :
   - Utiliser un service comme EmailJS (gratuit jusqu'à 200 emails/mois)
   - Créer un petit backend Node.js/Python
   - Utiliser un service serverless comme Vercel Functions ou AWS Lambda

## 🚀 Prochaines Étapes

1. **Installer Firebase Functions CLI** :
   ```bash
   npm install -g firebase-tools
   firebase login
   firebase init functions
   ```

2. **Créer la fonction d'envoi d'email** (voir exemple ci-dessus)

3. **Déployer la fonction** :
   ```bash
   firebase deploy --only functions
   ```

4. **Tester le flux complet** :
   - Tentative de connexion échouée 3 fois
   - Capture de photo
   - Vérification de l'envoi d'email

---

**Note** : Pour l'instant, les demandes d'envoi d'email sont stockées dans Firestore. Une fois que vous aurez configuré Firebase Functions, les emails seront envoyés automatiquement.


