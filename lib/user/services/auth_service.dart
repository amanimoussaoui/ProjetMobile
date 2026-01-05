import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter_facebook_auth/flutter_facebook_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

class AuthService {
  static final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes: ['email', 'profile'],
  );

  // Connexion avec Google
  static Future<Map<String, dynamic>?> signInWithGoogle() async {
    try {
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        return null; // L'utilisateur a annulé la connexion
      }

      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;

      return {
        'id': googleUser.id,
        'email': googleUser.email,
        'displayName': googleUser.displayName,
        'photoUrl': googleUser.photoUrl,
        'idToken': googleAuth.idToken,
        'accessToken': googleAuth.accessToken,
      };
    } catch (e) {
      print('Erreur lors de la connexion Google: $e');
      return null;
    }
  }

  // Connexion avec Facebook
  static Future<Map<String, dynamic>?> signInWithFacebook() async {
    try {
      final LoginResult result = await FacebookAuth.instance.login();
      
      if (result.status == LoginStatus.success) {
        final userData = await FacebookAuth.instance.getUserData();
        return {
          'id': userData['id'],
          'email': userData['email'],
          'displayName': userData['name'],
          'photoUrl': userData['picture']['data']['url'],
          'accessToken': result.accessToken?.tokenString,
        };
      } else {
        return null;
      }
    } catch (e) {
      print('Erreur lors de la connexion Facebook: $e');
      return null;
    }
  }

  // Connexion avec GitHub (nécessite une implémentation custom avec OAuth)
  // Pour l'instant, on va créer une méthode placeholder
  static Future<Map<String, dynamic>?> signInWithGitHub() async {
    try {
      // TODO: Implémenter l'authentification GitHub avec OAuth
      // Cela nécessite généralement l'utilisation de flutter_web_auth ou url_launcher
      // Pour l'instant, on retourne un placeholder
      return {
        'provider': 'github',
        'message': 'GitHub authentication will be implemented',
      };
    } catch (e) {
      print('Erreur lors de la connexion GitHub: $e');
      return null;
    }
  }

  // Déconnexion
  static Future<void> signOut() async {
    try {
      await _googleSignIn.signOut();
      await FacebookAuth.instance.logOut();
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove('user_data');
    } catch (e) {
      print('Erreur lors de la déconnexion: $e');
    }
  }

  // Sauvegarder les données utilisateur
  static Future<void> saveUserData(Map<String, dynamic> userData) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('user_data', jsonEncode(userData));
  }

  // Récupérer les données utilisateur
  static Future<Map<String, dynamic>?> getUserData() async {
    final prefs = await SharedPreferences.getInstance();
    final userDataString = prefs.getString('user_data');
    if (userDataString != null) {
      return jsonDecode(userDataString) as Map<String, dynamic>;
    }
    return null;
  }
}





