package com.example.petconnect.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class InputValidator {
    
    // Pattern pour email valide
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    // Pattern pour mot de passe fort (au moins 8 caractères, 1 majuscule, 1 minuscule, 1 chiffre)
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$";
    
    // Pattern pour téléphone (format international)
    private static final String PHONE_PATTERN = 
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$";
    
    /**
     * Valide un email
     */
    public static ValidationResult validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return new ValidationResult(false, "L'email est requis");
        }
        
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            return new ValidationResult(false, "Format d'email invalide");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Valide un mot de passe
     */
    public static ValidationResult validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return new ValidationResult(false, "Le mot de passe est requis");
        }
        
        if (password.length() < 6) {
            return new ValidationResult(false, "Le mot de passe doit contenir au moins 6 caractères");
        }
        
        if (password.length() < 8) {
            return new ValidationResult(false, "Pour plus de sécurité, utilisez au moins 8 caractères");
        }
        
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            return new ValidationResult(false, 
                "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Évalue la force d'un mot de passe
     */
    public static PasswordStrength getPasswordStrength(String password) {
        if (TextUtils.isEmpty(password)) {
            return PasswordStrength.EMPTY;
        }
        
        if (password.length() < 6) {
            return PasswordStrength.WEAK;
        }
        
        int strength = 0;
        
        // Longueur
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;
        
        // Complexité
        if (password.matches(".*[a-z].*")) strength++; // Minuscule
        if (password.matches(".*[A-Z].*")) strength++; // Majuscule
        if (password.matches(".*\\d.*")) strength++; // Chiffre
        if (password.matches(".*[@$!%*?&].*")) strength++; // Caractère spécial
        
        if (strength <= 2) return PasswordStrength.WEAK;
        if (strength <= 4) return PasswordStrength.MEDIUM;
        if (strength <= 5) return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }
    
    /**
     * Valide un nom (prénom ou nom de famille)
     */
    public static ValidationResult validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            return new ValidationResult(false, "Ce champ est requis");
        }
        
        if (name.length() < 2) {
            return new ValidationResult(false, "Le nom doit contenir au moins 2 caractères");
        }
        
        if (name.length() > 50) {
            return new ValidationResult(false, "Le nom ne peut pas dépasser 50 caractères");
        }
        
        if (!name.matches("^[a-zA-ZÀ-ÿ\\s'-]+$")) {
            return new ValidationResult(false, "Le nom ne peut contenir que des lettres");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Valide un numéro de téléphone
     */
    public static ValidationResult validatePhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return new ValidationResult(true, null); // Optionnel
        }
        
        // Nettoyer le numéro (enlever espaces, tirets, etc.)
        String cleanedPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        
        if (cleanedPhone.length() < 8 || cleanedPhone.length() > 15) {
            return new ValidationResult(false, "Format de téléphone invalide");
        }
        
        if (!cleanedPhone.matches("^[+]?[0-9]+$")) {
            return new ValidationResult(false, "Le téléphone ne peut contenir que des chiffres");
        }
        
        return new ValidationResult(true, null);
    }
    
    /**
     * Classe pour le résultat de validation
     */
    public static class ValidationResult {
        private boolean isValid;
        private String errorMessage;
        
        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    
    /**
     * Enum pour la force du mot de passe
     */
    public enum PasswordStrength {
        EMPTY,
        WEAK,
        MEDIUM,
        STRONG,
        VERY_STRONG
    }
}

