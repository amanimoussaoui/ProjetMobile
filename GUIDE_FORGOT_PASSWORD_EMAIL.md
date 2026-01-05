# 📧 Guide : Forgot Password - Envoi d'Email Réel

## ✅ Configuration Actuelle

Votre application **envoie déjà des emails réels** via Firebase Authentication !

### Comment ça fonctionne

1. **L'utilisateur clique sur "Forgot Password"**
2. **Il entre son email**
3. **Firebase envoie automatiquement un email de réinitialisation** à cette adresse
4. **L'email contient un lien sécurisé** pour réinitialiser le mot de passe

### Code actuel

Dans `ForgotPasswordActivity.java`, la méthode `handleSendEmail()` appelle :
```java
authManager.resetPassword(email, this, callback)
```

Qui utilise `FirebaseAuth.sendPasswordResetEmail(email)` - **c'est un email réel envoyé par Firebase** !

## 🎨 Personnaliser l'Email de Réinitialisation

### Option 1 : Personnaliser via Firebase Console (Recommandé)

1. Allez sur [Firebase Console](https://console.firebase.google.com/)
2. Sélectionnez votre projet **PetConnect**
3. Allez dans **Authentication** → **Templates**
4. Cliquez sur **Password reset**
5. Personnalisez :
   - **Subject** : "Reset your PetConnect password"
   - **Body** : Le contenu de l'email
   - **Action URL** : L'URL de votre page de réinitialisation

### Option 2 : Utiliser un Domaine Personnalisé

1. Dans Firebase Console → **Authentication** → **Settings**
2. Section **Authorized domains**
3. Ajoutez votre domaine personnalisé
4. Configurez l'email pour utiliser votre domaine

### Exemple de Template Personnalisé

```
Subject: Reset your PetConnect password 🐾

Hello,

You requested to reset your password for your PetConnect account.

Click the link below to reset your password:
[Reset Password Link]

This link will expire in 1 hour.

If you didn't request this, please ignore this email.

Best regards,
The PetConnect Team
```

## 🔗 Configurer l'URL de Réinitialisation

### Pour Android App

1. Dans Firebase Console → **Authentication** → **Settings**
2. Section **Action URL**
3. Configurez l'URL de redirection après réinitialisation
4. Exemple : `petconnect://reset-password` (deep link)

### Pour Web

1. Configurez une page web de réinitialisation
2. Exemple : `https://petconnect.com/reset-password`
3. Ajoutez cette URL dans Firebase Console

## ✅ Vérification

Pour tester que l'email est bien envoyé :

1. Lancez l'application
2. Allez sur la page de login
3. Cliquez sur "Forgot Password"
4. Entrez une adresse email valide (qui existe dans Firebase)
5. Cliquez sur "Send Email"
6. **Vérifiez votre boîte email** - vous devriez recevoir un email de Firebase

## 📝 Notes Importantes

- ✅ **L'email est déjà réel** - Firebase envoie automatiquement
- ✅ **Pas besoin de serveur SMTP** - Firebase gère tout
- ✅ **Sécurisé** - Les liens expirent après 1 heure
- ✅ **Gratuit** - Jusqu'à 100 emails/jour avec Firebase

## 🚨 Si l'Email n'arrive pas

1. **Vérifiez les spams** - L'email peut être dans les spams
2. **Vérifiez que l'email existe** dans Firebase Authentication
3. **Vérifiez les logs Firebase** pour voir les erreurs
4. **Vérifiez la limite** - Firebase limite à 100 emails/jour (gratuit)

## 🎯 Prochaines Étapes

1. ✅ L'email fonctionne déjà - Testez-le !
2. Personnalisez le template dans Firebase Console
3. Configurez votre domaine personnalisé (optionnel)
4. Configurez l'URL de redirection après réinitialisation



