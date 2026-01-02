package com.example.petconnect.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Chatbot d'assistance pour conseils sur l'adoption et les soins des animaux
 */
public class ChatbotAssistant {
    
    private static final Map<String, String[]> RESPONSES = new HashMap<>();
    
    static {
        // Réponses sur l'adoption
        RESPONSES.put("adoption", new String[]{
            "L'adoption est un acte merveilleux ! Assurez-vous d'être prêt à prendre soin d'un animal à long terme.",
            "Avant d'adopter, réfléchissez à votre mode de vie et au temps que vous pouvez consacrer à votre animal.",
            "Visitez plusieurs centres d'adoption pour trouver l'animal qui correspond le mieux à votre personnalité.",
            "N'oubliez pas que l'adoption est un engagement à vie. Les animaux ont besoin d'amour et d'attention quotidienne."
        });
        
        // Réponses sur les soins
        RESPONSES.put("soins", new String[]{
            "Les animaux ont besoin d'une alimentation équilibrée, d'exercice régulier et de visites vétérinaires.",
            "Assurez-vous de fournir un environnement sûr et confortable à votre animal.",
            "La socialisation est importante, surtout pour les chiens et les chats.",
            "N'oubliez pas de stériliser ou castrer votre animal pour sa santé et pour éviter la surpopulation."
        });
        
        // Réponses sur le comportement
        RESPONSES.put("comportement", new String[]{
            "Le comportement d'un animal dépend de sa race, de son histoire et de son environnement.",
            "La patience et la cohérence sont essentielles pour éduquer un animal.",
            "Si votre animal présente des comportements problématiques, consultez un vétérinaire ou un comportementaliste.",
            "Chaque animal est unique. Prenez le temps de comprendre les besoins spécifiques de votre compagnon."
        });
        
        // Réponses générales
        RESPONSES.put("general", new String[]{
            "Je suis là pour vous aider ! Posez-moi des questions sur l'adoption, les soins ou le comportement des animaux.",
            "N'hésitez pas à me demander des conseils. Je peux vous aider avec l'adoption, les soins et le comportement.",
            "Comment puis-je vous aider aujourd'hui ? Je peux vous donner des conseils sur l'adoption et les soins des animaux."
        });
    }
    
    /**
     * Génère une réponse du chatbot basée sur la question de l'utilisateur
     * @param question La question de l'utilisateur
     * @return Une réponse appropriée
     */
    public static String getResponse(String question) {
        if (question == null || question.trim().isEmpty()) {
            return getRandomResponse("general");
        }
        
        String lowerQuestion = question.toLowerCase();
        
        // Détecter le type de question
        if (containsKeywords(lowerQuestion, new String[]{"adopter", "adoption", "adopter un"})) {
            return getRandomResponse("adoption");
        } else if (containsKeywords(lowerQuestion, new String[]{"soin", "soins", "nourrir", "alimentation", "santé", "vétérinaire"})) {
            return getRandomResponse("soins");
        } else if (containsKeywords(lowerQuestion, new String[]{"comportement", "éduquer", "dresser", "problème", "agressif"})) {
            return getRandomResponse("comportement");
        } else {
            return getRandomResponse("general");
        }
    }
    
    /**
     * Vérifie si la question contient certains mots-clés
     */
    private static boolean containsKeywords(String text, String[] keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtient une réponse aléatoire d'une catégorie
     */
    private static String getRandomResponse(String category) {
        String[] responses = RESPONSES.get(category);
        if (responses == null || responses.length == 0) {
            return "Je ne peux pas répondre à cette question pour le moment.";
        }
        
        Random random = new Random();
        return responses[random.nextInt(responses.length)];
    }
    
    /**
     * Obtient des conseils spécifiques pour un type d'animal
     * @param petType Le type d'animal
     * @return Des conseils personnalisés
     */
    public static String getPetSpecificAdvice(String petType) {
        if (petType == null) {
            return "Je peux vous donner des conseils sur différents types d'animaux. Quel animal vous intéresse ?";
        }
        
        String lowerType = petType.toLowerCase();
        
        if (lowerType.contains("chien") || lowerType.contains("dog")) {
            return "Les chiens ont besoin d'exercice quotidien, de socialisation et d'une éducation cohérente. " +
                   "Assurez-vous d'avoir assez de temps et d'espace pour un chien avant d'adopter.";
        } else if (lowerType.contains("chat") || lowerType.contains("cat")) {
            return "Les chats sont plus indépendants mais ont toujours besoin d'attention, de jeu et de soins réguliers. " +
                   "Ils s'adaptent bien à la vie en appartement.";
        } else if (lowerType.contains("oiseau") || lowerType.contains("bird")) {
            return "Les oiseaux nécessitent une cage spacieuse, une alimentation variée et de l'interaction sociale. " +
                   "Certaines espèces peuvent vivre très longtemps.";
        } else if (lowerType.contains("lapin") || lowerType.contains("rabbit")) {
            return "Les lapins sont des animaux sociaux qui ont besoin d'espace, d'une alimentation riche en foin " +
                   "et de beaucoup d'attention.";
        } else {
            return "Chaque type d'animal a ses besoins spécifiques. Renseignez-vous bien avant d'adopter pour " +
                   "assurer le bien-être de votre futur compagnon.";
        }
    }
}

