package com.example.petconnect.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service pour interagir avec une API de chatbot
 * Utilise Hugging Face Inference API (gratuite) ou OpenAI
 */
public class ChatbotApiService {
    
    private static final String TAG = "ChatbotApiService";
    
    // Option 1: Hugging Face (gratuit, pas besoin de clé API pour les modèles publics)
    private static final String HUGGINGFACE_API_URL = "https://api-inference.huggingface.co/models/microsoft/DialoGPT-medium";
    
    // Option 2: OpenAI (nécessite une clé API - à configurer dans les strings.xml)
    // private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    /**
     * Envoie un message à l'API et récupère une réponse
     * @param message Le message de l'utilisateur
     * @param callback Callback pour gérer la réponse
     */
    public static void getResponse(String message, ChatbotCallback callback) {
        new Thread(() -> {
            try {
                // Utiliser Hugging Face API (gratuit)
                String response = callHuggingFaceAPI(message);
                if (response != null && !response.isEmpty()) {
                    callback.onSuccess(response);
                } else {
                    // Fallback vers les réponses prédéfinies si l'API échoue
                    callback.onError("API non disponible, utilisation des réponses prédéfinies");
                    callback.onSuccess(ChatbotAssistant.getResponse(message));
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur API chatbot: " + e.getMessage(), e);
                // Fallback vers les réponses prédéfinies
                callback.onSuccess(ChatbotAssistant.getResponse(message));
            }
        }).start();
    }
    
    /**
     * Appelle l'API Hugging Face pour obtenir une réponse
     * Note: Cette API peut nécessiter une clé API pour certains modèles
     * Pour utiliser une clé API, ajoutez: conn.setRequestProperty("Authorization", "Bearer YOUR_API_KEY");
     */
    private static String callHuggingFaceAPI(String message) {
        try {
            URL url = new URL(HUGGINGFACE_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // Si vous avez une clé API Hugging Face, décommentez la ligne suivante:
            // conn.setRequestProperty("Authorization", "Bearer YOUR_HUGGINGFACE_API_KEY");
            conn.setDoOutput(true);
            conn.setConnectTimeout(8000); // 8 secondes
            conn.setReadTimeout(8000);
            
            // Créer le JSON de requête
            JSONObject requestBody = new JSONObject();
            requestBody.put("inputs", message);
            
            // Envoyer la requête
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes("UTF-8"));
            os.close();
            
            // Lire la réponse
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                // Parser la réponse JSON
                String responseStr = response.toString();
                if (responseStr.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(responseStr);
                    if (jsonArray.length() > 0) {
                        JSONObject firstResponse = jsonArray.getJSONObject(0);
                        if (firstResponse.has("generated_text")) {
                            String generatedText = firstResponse.getString("generated_text");
                            // Nettoyer la réponse (enlever le message original si présent)
                            if (generatedText.startsWith(message)) {
                                generatedText = generatedText.substring(message.length()).trim();
                            }
                            return generatedText.isEmpty() ? null : generatedText;
                        }
                    }
                } else if (responseStr.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(responseStr);
                    if (jsonObject.has("generated_text")) {
                        String generatedText = jsonObject.getString("generated_text");
                        if (generatedText.startsWith(message)) {
                            generatedText = generatedText.substring(message.length()).trim();
                        }
                        return generatedText.isEmpty() ? null : generatedText;
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                Log.w(TAG, "API Hugging Face nécessite une clé API");
                return null;
            } else {
                Log.w(TAG, "API Hugging Face retourne le code: " + responseCode);
                return null;
            }
        } catch (java.net.SocketTimeoutException e) {
            Log.w(TAG, "Timeout lors de l'appel à l'API");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'appel à Hugging Face API: " + e.getMessage(), e);
            return null;
        }
        return null;
    }
    
    /**
     * Interface pour les callbacks du chatbot
     */
    public interface ChatbotCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}

