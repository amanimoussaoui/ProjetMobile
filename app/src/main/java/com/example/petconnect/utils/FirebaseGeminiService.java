package com.example.petconnect.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour interagir avec le chatbot PetConnect
 * Utilise ChatbotAssistant comme système de fallback car Firebase AI SDK n'est pas disponible publiquement
 * 
 * Note: Pour utiliser Firebase AI (Gemini), vous devez:
 * 1. Activer Firebase AI dans Firebase Console
 * 2. Ajouter la dépendance Firebase AI SDK quand elle sera disponible
 * 3. Décommenter le code Firebase AI ci-dessous
 */
public class FirebaseGeminiService {
    
    private static final String TAG = "FirebaseGeminiService";
    
    private static FirebaseGeminiService instance;
    private List<ChatMessage> conversationHistory;
    
    private FirebaseGeminiService(Context context) {
        this.conversationHistory = new ArrayList<>();
        Log.d(TAG, "FirebaseGeminiService initialized (using ChatbotAssistant fallback)");
    }
    
    public static synchronized FirebaseGeminiService getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseGeminiService(context);
        }
        return instance;
    }
    
    /**
     * Envoie un message et récupère une réponse
     * @param message Le message de l'utilisateur
     * @param context Le contexte Android
     * @param callback Callback pour gérer la réponse
     */
    public void getResponse(String message, Context context, GeminiCallback callback) {
        // Ajouter le message utilisateur à l'historique
        conversationHistory.add(new ChatMessage("user", message));
        
        // Utiliser ChatbotAssistant comme système de fallback
        // TODO: Remplacer par Firebase AI quand le SDK sera disponible
        try {
            String response = ChatbotAssistant.getResponse(message);
            
            // Ajouter la réponse à l'historique
            conversationHistory.add(new ChatMessage("model", response));
            
            // Limiter l'historique à 10 messages
            if (conversationHistory.size() > 10) {
                conversationHistory.remove(0);
            }
            
            callback.onSuccess(response);
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la génération de la réponse: " + e.getMessage(), e);
            callback.onError("Erreur lors de la génération de la réponse: " + e.getMessage());
        }
    }
    
    /**
     * Réinitialise l'historique de conversation
     */
    public void clearHistory() {
        conversationHistory.clear();
    }
    
    /**
     * Classe interne pour représenter un message de conversation
     */
    private static class ChatMessage {
        private String role;
        private String content;
        
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() {
            return role;
        }
        
        public String getContent() {
            return content;
        }
    }
    
    /**
     * Interface pour les callbacks Gemini
     */
    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}
