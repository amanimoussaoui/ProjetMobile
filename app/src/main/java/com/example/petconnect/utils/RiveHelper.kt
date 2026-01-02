package com.example.petconnect.utils

import android.view.View
import app.rive.runtime.kotlin.RiveAnimationView

/**
 * Helper class pour contrôler Rive depuis Java
 * Utilise l'API Rive Android via reflection pour compatibilité
 */
object RiveHelper {
    
    /**
     * Active/désactive un input booléen dans Rive
     */
    @JvmStatic
    fun setBooleanInput(riveView: RiveAnimationView?, inputName: String, value: Boolean) {
        if (riveView == null) return
        
        try {
            // Utiliser reflection pour accéder à l'API Rive
            val stateMachine = riveView.javaClass.getMethod("getStateMachine").invoke(riveView)
            if (stateMachine != null) {
                val inputMethod = stateMachine.javaClass.getMethod("input", String::class.java)
                val input = inputMethod.invoke(stateMachine, inputName)
                
                if (input != null) {
                    // Vérifier si c'est un input booléen
                    val inputClass = input.javaClass
                    if (inputClass.simpleName.contains("Boolean") || 
                        inputClass.simpleName.contains("SMIBool")) {
                        val setValueMethod = inputClass.getMethod("setValue", Boolean::class.java)
                        setValueMethod.invoke(input, value)
                        android.util.Log.d("RiveHelper", "Input $inputName mis à jour: $value")
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RiveHelper", "Erreur lors de la mise à jour de l'input $inputName: ${e.message}")
            // Essayer une approche alternative avec l'API publique
            tryAlternativeInput(riveView, inputName, value)
        }
    }
    
    /**
     * Méthode alternative pour définir un input
     */
    private fun tryAlternativeInput(riveView: RiveAnimationView, inputName: String, value: Boolean) {
        try {
            // Essayer d'utiliser setBooleanState directement si disponible
            val method = riveView.javaClass.getMethod("setBooleanState", String::class.java, Boolean::class.java)
            method.invoke(riveView, inputName, value)
            android.util.Log.d("RiveHelper", "Input $inputName mis à jour (méthode alternative): $value")
        } catch (e: Exception) {
            android.util.Log.w("RiveHelper", "Impossible de définir l'input $inputName: ${e.message}")
        }
    }
    
    /**
     * Déclenche un trigger dans Rive
     */
    @JvmStatic
    fun trigger(riveView: RiveAnimationView?, triggerName: String) {
        if (riveView == null) return
        
        try {
            // Utiliser reflection pour accéder à l'API Rive
            val stateMachine = riveView.javaClass.getMethod("getStateMachine").invoke(riveView)
            if (stateMachine != null) {
                val triggerMethod = stateMachine.javaClass.getMethod("trigger", String::class.java)
                val trigger = triggerMethod.invoke(stateMachine, triggerName)
                
                if (trigger != null) {
                    val fireMethod = trigger.javaClass.getMethod("fire")
                    fireMethod.invoke(trigger)
                    android.util.Log.d("RiveHelper", "Trigger $triggerName déclenché")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RiveHelper", "Erreur lors du déclenchement du trigger $triggerName: ${e.message}")
            // Essayer une approche alternative
            tryAlternativeTrigger(riveView, triggerName)
        }
    }
    
    /**
     * Méthode alternative pour déclencher un trigger
     */
    private fun tryAlternativeTrigger(riveView: RiveAnimationView, triggerName: String) {
        try {
            // Essayer d'utiliser fireTrigger directement si disponible
            val method = riveView.javaClass.getMethod("fireTrigger", String::class.java)
            method.invoke(riveView, triggerName)
            android.util.Log.d("RiveHelper", "Trigger $triggerName déclenché (méthode alternative)")
        } catch (e: Exception) {
            android.util.Log.w("RiveHelper", "Impossible de déclencher le trigger $triggerName: ${e.message}")
        }
    }
    
    /**
     * Configure les yeux pour suivre la saisie (pour email)
     * Active l'input "isChecking" ou "look" selon le modèle Rive
     */
    @JvmStatic
    fun followTyping(riveView: RiveAnimationView?, isActive: Boolean) {
        if (riveView == null || riveView.visibility != View.VISIBLE) return
        
        // Essayer différents noms d'inputs courants pour suivre avec les yeux
        val possibleInputs = listOf("isChecking", "look", "typing", "isTyping", "follow", "Look")
        for (inputName in possibleInputs) {
            try {
                setBooleanInput(riveView, inputName, isActive)
                return
            } catch (e: Exception) {
                // Continuer avec le prochain nom
            }
        }
    }
    
    /**
     * Cache les yeux (pour password)
     * Active l'input "isHandsUp" ou "handsUp" selon le modèle Rive
     */
    @JvmStatic
    fun coverEyes(riveView: RiveAnimationView?, isCovered: Boolean) {
        if (riveView == null || riveView.visibility != View.VISIBLE) return
        
        // Essayer différents noms d'inputs courants pour cacher les yeux
        val possibleInputs = listOf("isHandsUp", "handsUp", "coverEyes", "hideEyes", "password", "HandsUp")
        for (inputName in possibleInputs) {
            try {
                setBooleanInput(riveView, inputName, isCovered)
                return
            } catch (e: Exception) {
                // Continuer avec le prochain nom
            }
        }
    }
}

