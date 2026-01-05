import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:uuid/uuid.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:crypto/crypto.dart';

class EmailService {
  // Configuration SMTP - À configurer selon votre service
  static const String smtpHost = 'smtp.sendgrid.net'; // ou votre service SMTP
  static const int smtpPort = 587;
  static const String smtpUsername = ''; // À configurer
  static const String smtpPassword = ''; // À configurer
  static const String fromEmail = 'noreply@petconnect.com';
  static const String fromName = 'PetConnect';
  
  // Alternative: Utiliser une API REST (SendGrid, AWS SES, etc.)
  static const String apiKey = ''; // API Key pour SendGrid/AWS SES
  static const String apiUrl = 'https://api.sendgrid.com/v3/mail/send';
  
  /// Envoie un email de réinitialisation de mot de passe
  static Future<bool> sendPasswordResetEmail({
    required String toEmail,
    required String resetToken,
    required String resetLink,
  }) async {
    try {
      final subject = 'Réinitialisation de votre mot de passe - PetConnect';
      final htmlBody = _generatePasswordResetEmailTemplate(
        resetLink: resetLink,
        expirationMinutes: 60,
      );
      
      // Enregistrer le token et son expiration
      await _saveResetToken(toEmail, resetToken);
      
      // Option 1: Utiliser SendGrid API
      if (apiKey.isNotEmpty) {
        return await _sendViaSendGrid(
          toEmail: toEmail,
          subject: subject,
          htmlBody: htmlBody,
        );
      }
      
      // Option 2: Utiliser mailer package (SMTP direct)
      // Note: mailer nécessite une configuration SMTP
      // Pour l'instant, on simule l'envoi
      await _logEmailSent(toEmail, subject);
      return true;
      
    } catch (e) {
      print('Erreur lors de l\'envoi de l\'email: $e');
      return false;
    }
  }
  
  /// Envoie un email de notification de verrouillage de compte
  static Future<bool> sendAccountLockedEmail({
    required String toEmail,
    required bool isPermanent,
    required Duration lockDuration,
  }) async {
    try {
      final subject = 'Sécurité de votre compte - PetConnect';
      final htmlBody = _generateAccountLockedEmailTemplate(
        isPermanent: isPermanent,
        lockDuration: lockDuration,
      );
      
      if (apiKey.isNotEmpty) {
        return await _sendViaSendGrid(
          toEmail: toEmail,
          subject: subject,
          htmlBody: htmlBody,
        );
      }
      
      await _logEmailSent(toEmail, subject);
      return true;
      
    } catch (e) {
      print('Erreur lors de l\'envoi de l\'email: $e');
      return false;
    }
  }
  
  /// Génère un token de réinitialisation sécurisé
  static String generateResetToken() {
    return const Uuid().v4();
  }
  
  /// Vérifie si un token de réinitialisation est valide
  static Future<bool> verifyResetToken(String email, String token) async {
    final prefs = await SharedPreferences.getInstance();
    final savedToken = prefs.getString('reset_token_$email');
    final tokenExpiry = prefs.getInt('reset_token_expiry_$email') ?? 0;
    
    if (savedToken == null || savedToken != token) {
      return false;
    }
    
    final now = DateTime.now().millisecondsSinceEpoch;
    if (now > tokenExpiry) {
      // Token expiré
      await _clearResetToken(email);
      return false;
    }
    
    return true;
  }
  
  /// Sauvegarde le token de réinitialisation
  static Future<void> _saveResetToken(String email, String token) async {
    final prefs = await SharedPreferences.getInstance();
    final expiry = DateTime.now().add(const Duration(hours: 1)).millisecondsSinceEpoch;
    await prefs.setString('reset_token_$email', token);
    await prefs.setInt('reset_token_expiry_$email', expiry);
  }
  
  /// Efface le token de réinitialisation après utilisation
  static Future<void> _clearResetToken(String email) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('reset_token_$email');
    await prefs.remove('reset_token_expiry_$email');
  }
  
  /// Envoie un email via SendGrid API
  static Future<bool> _sendViaSendGrid({
    required String toEmail,
    required String subject,
    required String htmlBody,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {
          'Authorization': 'Bearer $apiKey',
          'Content-Type': 'application/json',
        },
        body: jsonEncode({
          'personalizations': [
            {
              'to': [
                {'email': toEmail}
              ]
            }
          ],
          'from': {
            'email': fromEmail,
            'name': fromName,
          },
          'subject': subject,
          'content': [
            {
              'type': 'text/html',
              'value': htmlBody,
            }
          ],
        }),
      );
      
      if (response.statusCode >= 200 && response.statusCode < 300) {
        await _logEmailSent(toEmail, subject);
        return true;
      } else {
        print('Erreur SendGrid: ${response.statusCode} - ${response.body}');
        return false;
      }
    } catch (e) {
      print('Erreur lors de l\'envoi via SendGrid: $e');
      return false;
    }
  }
  
  /// Log l'envoi d'un email
  static Future<void> _logEmailSent(String toEmail, String subject) async {
    final prefs = await SharedPreferences.getInstance();
    final logs = prefs.getStringList('email_logs') ?? [];
    final logEntry = '${DateTime.now().toIso8601String()}|$toEmail|$subject';
    logs.add(logEntry);
    
    // Garder seulement les 100 derniers logs
    if (logs.length > 100) {
      logs.removeRange(0, logs.length - 100);
    }
    
    await prefs.setStringList('email_logs', logs);
  }
  
  /// Template HTML pour l'email de réinitialisation
  static String _generatePasswordResetEmailTemplate({
    required String resetLink,
    required int expirationMinutes,
  }) {
    return '''
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <style>
    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
    .header { background-color: #12ABB0; color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
    .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
    .button { display: inline-block; padding: 12px 30px; background-color: #12ABB0; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>🐾 PetConnect</h1>
    </div>
    <div class="content">
      <h2>Réinitialisation de votre mot de passe</h2>
      <p>Bonjour,</p>
      <p>Vous avez demandé à réinitialiser votre mot de passe. Cliquez sur le bouton ci-dessous pour procéder :</p>
      <div style="text-align: center;">
        <a href="$resetLink" class="button">Réinitialiser mon mot de passe</a>
      </div>
      <p>Ou copiez-collez ce lien dans votre navigateur :</p>
      <p style="word-break: break-all; color: #12ABB0;">$resetLink</p>
      <p><strong>⚠️ Important :</strong> Ce lien expirera dans $expirationMinutes minutes.</p>
      <p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>
      <p>Cordialement,<br>L'équipe PetConnect</p>
    </div>
    <div class="footer">
      <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
    </div>
  </div>
</body>
</html>
''';
  }
  
  /// Template HTML pour l'email de verrouillage de compte
  static String _generateAccountLockedEmailTemplate({
    required bool isPermanent,
    required Duration lockDuration,
  }) {
    final minutes = lockDuration.inMinutes;
    final lockMessage = isPermanent
        ? 'Votre compte a été verrouillé de façon permanente en raison de nombreuses tentatives de connexion échouées.'
        : 'Votre compte a été verrouillé temporairement pour $minutes minutes en raison de 3 tentatives de connexion échouées.';
    
    return '''
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <style>
    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
    .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
    .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
    .alert { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }
    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>🐾 PetConnect - Sécurité</h1>
    </div>
    <div class="content">
      <h2>Notification de sécurité</h2>
      <p>Bonjour,</p>
      <div class="alert">
        <p><strong>🔒 $lockMessage</strong></p>
      </div>
      ${isPermanent ? '<p>Veuillez contacter le support pour déverrouiller votre compte.</p>' : '<p>Veuillez réessayer après la période de verrouillage, ou utilisez la fonction "Mot de passe oublié" si nécessaire.</p>'}
      <p>Cordialement,<br>L'équipe PetConnect</p>
    </div>
    <div class="footer">
      <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
    </div>
  </div>
</body>
</html>
''';
  }
}





