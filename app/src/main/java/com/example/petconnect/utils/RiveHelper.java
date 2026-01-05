package com.example.petconnect.utils;

import android.util.Log;
import android.view.View;
import app.rive.runtime.kotlin.RiveAnimationView;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class pour contrôler Rive depuis Java
 * Utilise l'API Rive Android via reflection pour compatibilité
 */
public class RiveHelper {
    
    /**
     * Active/désactive un input booléen dans Rive
     */
    public static void setBooleanInput(RiveAnimationView riveView, String inputName, boolean value) {
        if (riveView == null) return;
        
        try {
            // Utiliser reflection pour accéder à l'API Rive
            Method getStateMachineMethod = riveView.getClass().getMethod("getStateMachine");
            Object stateMachine = getStateMachineMethod.invoke(riveView);
            
            if (stateMachine != null) {
                Method inputMethod = stateMachine.getClass().getMethod("input", String.class);
                Object input = inputMethod.invoke(stateMachine, inputName);
                
                if (input != null) {
                    // Vérifier si c'est un input booléen
                    Class<?> inputClass = input.getClass();
                    String className = inputClass.getSimpleName();
                    if (className.contains("Boolean") || className.contains("SMIBool")) {
                        Method setValueMethod = inputClass.getMethod("setValue", Boolean.class);
                        setValueMethod.invoke(input, value);
                        Log.d("RiveHelper", "Input " + inputName + " mis à jour: " + value);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("RiveHelper", "Erreur lors de la mise à jour de l'input " + inputName + ": " + e.getMessage());
            // Essayer une approche alternative avec l'API publique
            tryAlternativeInput(riveView, inputName, value);
        }
    }
    
    /**
     * Méthode alternative pour définir un input
     */
    private static void tryAlternativeInput(RiveAnimationView riveView, String inputName, boolean value) {
        try {
            // Essayer d'utiliser setBooleanState directement si disponible
            Method method = riveView.getClass().getMethod("setBooleanState", String.class, Boolean.class);
            method.invoke(riveView, inputName, value);
            Log.d("RiveHelper", "Input " + inputName + " mis à jour (méthode alternative): " + value);
        } catch (Exception e) {
            Log.w("RiveHelper", "Impossible de définir l'input " + inputName + ": " + e.getMessage());
        }
    }
    
    /**
     * Déclenche un trigger dans Rive
     */
    public static void trigger(RiveAnimationView riveView, String triggerName) {
        if (riveView == null) return;
        
        try {
            // Utiliser reflection pour accéder à l'API Rive
            Method getStateMachineMethod = riveView.getClass().getMethod("getStateMachine");
            Object stateMachine = getStateMachineMethod.invoke(riveView);
            
            if (stateMachine != null) {
                Method triggerMethod = stateMachine.getClass().getMethod("trigger", String.class);
                Object trigger = triggerMethod.invoke(stateMachine, triggerName);
                
                if (trigger != null) {
                    Method fireMethod = trigger.getClass().getMethod("fire");
                    fireMethod.invoke(trigger);
                    Log.d("RiveHelper", "Trigger " + triggerName + " déclenché");
                }
            }
        } catch (Exception e) {
            Log.e("RiveHelper", "Erreur lors du déclenchement du trigger " + triggerName + ": " + e.getMessage());
            // Essayer une approche alternative
            tryAlternativeTrigger(riveView, triggerName);
        }
    }
    
    /**
     * Méthode alternative pour déclencher un trigger
     */
    private static void tryAlternativeTrigger(RiveAnimationView riveView, String triggerName) {
        try {
            // Essayer d'utiliser fireTrigger directement si disponible
            Method method = riveView.getClass().getMethod("fireTrigger", String.class);
            method.invoke(riveView, triggerName);
            Log.d("RiveHelper", "Trigger " + triggerName + " déclenché (méthode alternative)");
        } catch (Exception e) {
            Log.w("RiveHelper", "Impossible de déclencher le trigger " + triggerName + ": " + e.getMessage());
        }
    }
    
    /**
     * Configure les yeux pour suivre la saisie (pour email)
     * Active l'input "isChecking" ou "look" selon le modèle Rive
     */
    public static void followTyping(RiveAnimationView riveView, boolean isActive) {
        if (riveView == null || riveView.getVisibility() != View.VISIBLE) return;
        
        // Essayer différents noms d'inputs courants pour suivre avec les yeux
        List<String> possibleInputs = Arrays.asList("isChecking", "look", "typing", "isTyping", "follow", "Look");
        for (String inputName : possibleInputs) {
            try {
                setBooleanInput(riveView, inputName, isActive);
                return;
            } catch (Exception e) {
                // Continuer avec le prochain nom
            }
        }
    }
    
    /**
     * Cache les yeux (pour password)
     * Active l'input "isHandsUp" ou "handsUp" selon le modèle Rive
     */
    public static void coverEyes(RiveAnimationView riveView, boolean isCovered) {
        if (riveView == null || riveView.getVisibility() != View.VISIBLE) return;
        
        // Essayer différents noms d'inputs courants pour cacher les yeux
        List<String> possibleInputs = Arrays.asList("isHandsUp", "handsUp", "coverEyes", "hideEyes", "password", "HandsUp");
        for (String inputName : possibleInputs) {
            try {
                setBooleanInput(riveView, inputName, isCovered);
                return;
            } catch (Exception e) {
                // Continuer avec le prochain nom
            }
        }
    }
}


