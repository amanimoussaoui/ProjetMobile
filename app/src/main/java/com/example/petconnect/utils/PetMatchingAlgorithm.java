package com.example.petconnect.utils;

import com.example.petconnect.models.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Algorithme de matching intelligent pour recommander des animaux
 * basé sur le profil utilisateur (mode de vie, habitation, expérience)
 */
public class PetMatchingAlgorithm {
    
    /**
     * Calcule un score de compatibilité entre un utilisateur et un type d'animal
     * @param user L'utilisateur
     * @param petType Le type d'animal (ex: "dog", "cat", "bird", "rabbit")
     * @return Score de compatibilité entre 0 et 100
     */
    public static int calculateCompatibilityScore(User user, String petType) {
        if (user == null || petType == null) {
            return 0;
        }
        
        int score = 50; // Score de base
        
        // Facteur 1: Préférences utilisateur (30 points)
        Map<String, Boolean> preferences = user.getAnimalPreferences();
        if (preferences != null && preferences.containsKey(petType)) {
            if (Boolean.TRUE.equals(preferences.get(petType))) {
                score += 30;
            } else {
                score -= 20;
            }
        }
        
        // Facteur 2: Mode de vie (25 points)
        String lifestyle = user.getLifestyle();
        if (lifestyle != null) {
            switch (lifestyle.toLowerCase()) {
                case "active":
                    if (petType.equals("dog")) {
                        score += 25;
                    } else if (petType.equals("cat")) {
                        score += 15;
                    } else {
                        score += 10;
                    }
                    break;
                case "moderate":
                    score += 15; // Compatible avec la plupart des animaux
                    break;
                case "calm":
                    if (petType.equals("cat") || petType.equals("rabbit") || petType.equals("bird")) {
                        score += 20;
                    } else if (petType.equals("dog")) {
                        score += 10;
                    }
                    break;
            }
        }
        
        // Facteur 3: Type d'habitation (20 points)
        String housingType = user.getHousingType();
        boolean hasGarden = user.isHasGarden();
        
        if (housingType != null) {
            switch (housingType.toLowerCase()) {
                case "apartment":
                    if (petType.equals("cat") || petType.equals("bird") || petType.equals("rabbit")) {
                        score += 20;
                    } else if (petType.equals("dog") && hasGarden) {
                        score += 15;
                    } else if (petType.equals("dog")) {
                        score += 5; // Chien dans appartement sans jardin
                    }
                    break;
                case "house":
                    score += 15; // Maison convient à tous
                    if (hasGarden) {
                        score += 5; // Bonus jardin
                    }
                    break;
                case "farm":
                    score += 20; // Ferme convient à tous
                    break;
            }
        }
        
        // Facteur 4: Niveau d'expérience (15 points)
        String experience = user.getExperienceLevel();
        if (experience != null) {
            switch (experience.toLowerCase()) {
                case "expert":
                    score += 15; // Peut gérer n'importe quel animal
                    break;
                case "intermediate":
                    score += 10;
                    break;
                case "beginner":
                    // Recommander des animaux plus faciles pour débutants
                    if (petType.equals("cat") || petType.equals("rabbit")) {
                        score += 15;
                    } else if (petType.equals("dog")) {
                        score += 5;
                    } else {
                        score += 10;
                    }
                    break;
            }
        }
        
        // S'assurer que le score reste entre 0 et 100
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Génère des recommandations personnalisées pour un utilisateur
     * @param user L'utilisateur
     * @return Map des types d'animaux avec leurs scores de compatibilité
     */
    public static Map<String, Integer> generateRecommendations(User user) {
        Map<String, Integer> recommendations = new HashMap<>();
        
        // Types d'animaux courants
        String[] petTypes = {"dog", "cat", "bird", "rabbit", "hamster", "fish"};
        
        for (String petType : petTypes) {
            int score = calculateCompatibilityScore(user, petType);
            recommendations.put(petType, score);
        }
        
        return recommendations;
    }
    
    /**
     * Obtient le type d'animal le plus recommandé pour un utilisateur
     * @param user L'utilisateur
     * @return Le type d'animal avec le meilleur score
     */
    public static String getBestMatch(User user) {
        Map<String, Integer> recommendations = generateRecommendations(user);
        
        String bestMatch = null;
        int bestScore = 0;
        
        for (Map.Entry<String, Integer> entry : recommendations.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestMatch = entry.getKey();
            }
        }
        
        return bestMatch != null ? bestMatch : "cat"; // Par défaut
    }
    
    /**
     * Obtient une description textuelle du score de compatibilité
     * @param score Le score de compatibilité (0-100)
     * @return Description du niveau de compatibilité
     */
    public static String getCompatibilityLevel(int score) {
        if (score >= 80) {
            return "Excellente compatibilité";
        } else if (score >= 60) {
            return "Bonne compatibilité";
        } else if (score >= 40) {
            return "Compatibilité moyenne";
        } else {
            return "Compatibilité faible";
        }
    }
}

