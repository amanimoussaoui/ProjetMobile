package com.example.petconnect.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Classe pour créer des avatars personnalisés (style Bitmoji)
 * pour les utilisateurs
 */
public class AvatarBuilder {
    
    /**
     * Crée un avatar personnalisé basé sur les préférences utilisateur
     * @param seed Une valeur unique pour générer l'avatar (ex: userId hash)
     * @param width Largeur de l'avatar
     * @param height Hauteur de l'avatar
     * @return Bitmap de l'avatar
     */
    public static Bitmap createAvatar(String seed, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Générer des couleurs basées sur le seed
        int hash = seed.hashCode();
        int bgColor = generateColor(hash, 0xFFFFFFFF);
        int faceColor = generateColor(hash + 1, 0xFFFFDBB3); // Couleur peau
        int hairColor = generateColor(hash + 2, 0xFF8B4513); // Couleur cheveux
        int eyeColor = generateColor(hash + 3, 0xFF000000); // Couleur yeux
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // Fond
        paint.setColor(bgColor);
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint);
        
        // Visage
        paint.setColor(faceColor);
        float faceRadius = width * 0.35f;
        canvas.drawCircle(width / 2f, height / 2f + height * 0.1f, faceRadius, paint);
        
        // Cheveux
        paint.setColor(hairColor);
        RectF hairRect = new RectF(
            width * 0.2f,
            height * 0.15f,
            width * 0.8f,
            height * 0.5f
        );
        canvas.drawOval(hairRect, paint);
        
        // Yeux
        paint.setColor(eyeColor);
        float eyeSize = width * 0.08f;
        float eyeY = height * 0.45f;
        canvas.drawCircle(width * 0.4f, eyeY, eyeSize, paint);
        canvas.drawCircle(width * 0.6f, eyeY, eyeSize, paint);
        
        // Bouche
        paint.setColor(eyeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width * 0.02f);
        RectF mouthRect = new RectF(
            width * 0.4f,
            height * 0.6f,
            width * 0.6f,
            height * 0.65f
        );
        canvas.drawArc(mouthRect, 0, 180, false, paint);
        
        return bitmap;
    }
    
    /**
     * Génère une couleur basée sur un hash
     */
    private static int generateColor(int hash, int defaultColor) {
        // Utiliser le hash pour générer des couleurs cohérentes
        int r = Math.abs(hash) % 256;
        int g = Math.abs(hash / 256) % 256;
        int b = Math.abs(hash / 65536) % 256;
        
        // Mélanger avec la couleur par défaut pour un meilleur résultat
        int defaultR = (defaultColor >> 16) & 0xFF;
        int defaultG = (defaultColor >> 8) & 0xFF;
        int defaultB = defaultColor & 0xFF;
        
        r = (r + defaultR) / 2;
        g = (g + defaultG) / 2;
        b = (b + defaultB) / 2;
        
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Sauvegarde les paramètres d'avatar dans un JSON
     */
    public static String saveAvatarData(String seed, Map<String, String> preferences) {
        try {
            JSONObject json = new JSONObject();
            json.put("seed", seed);
            if (preferences != null) {
                for (Map.Entry<String, String> entry : preferences.entrySet()) {
                    json.put(entry.getKey(), entry.getValue());
                }
            }
            return json.toString();
        } catch (JSONException e) {
            return "{\"seed\":\"" + seed + "\"}";
        }
    }
    
    /**
     * Charge les paramètres d'avatar depuis un JSON
     */
    public static Map<String, String> loadAvatarData(String jsonData) {
        Map<String, String> data = new HashMap<>();
        try {
            JSONObject json = new JSONObject(jsonData);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                data.put(key, json.getString(key));
            }
        } catch (JSONException e) {
            // Retourner une map vide en cas d'erreur
        }
        return data;
    }
}

