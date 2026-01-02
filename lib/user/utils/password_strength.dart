import 'package:flutter/material.dart';

enum PasswordStrength {
  weak,
  fair,
  good,
  strong,
}

class PasswordStrengthChecker {
  static PasswordStrength checkStrength(String password) {
    if (password.isEmpty) return PasswordStrength.weak;
    
    int score = 0;
    
    // Longueur
    if (password.length >= 8) score++;
    if (password.length >= 12) score++;
    
    // Caractères minuscules
    if (password.contains(RegExp(r'[a-z]'))) score++;
    
    // Caractères majuscules
    if (password.contains(RegExp(r'[A-Z]'))) score++;
    
    // Chiffres
    if (password.contains(RegExp(r'[0-9]'))) score++;
    
    // Caractères spéciaux
    if (password.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'))) score++;
    
    if (score <= 2) return PasswordStrength.weak;
    if (score <= 3) return PasswordStrength.fair;
    if (score <= 4) return PasswordStrength.good;
    return PasswordStrength.strong;
  }
  
  static String getStrengthText(PasswordStrength strength) {
    switch (strength) {
      case PasswordStrength.weak:
        return 'Faible';
      case PasswordStrength.fair:
        return 'Moyen';
      case PasswordStrength.good:
        return 'Bon';
      case PasswordStrength.strong:
        return 'Fort';
    }
  }
  
  static Color getStrengthColor(PasswordStrength strength) {
    switch (strength) {
      case PasswordStrength.weak:
        return Colors.red;
      case PasswordStrength.fair:
        return Colors.orange;
      case PasswordStrength.good:
        return Colors.blue;
      case PasswordStrength.strong:
        return Colors.green;
    }
  }
  
  static double getStrengthPercentage(PasswordStrength strength) {
    switch (strength) {
      case PasswordStrength.weak:
        return 0.25;
      case PasswordStrength.fair:
        return 0.50;
      case PasswordStrength.good:
        return 0.75;
      case PasswordStrength.strong:
        return 1.0;
    }
  }
}

