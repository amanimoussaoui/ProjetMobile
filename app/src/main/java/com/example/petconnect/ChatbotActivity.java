package com.example.petconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect.utils.ChatbotApiService;
import com.example.petconnect.utils.ChatbotAssistant;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activité pour le chatbot d'assistance
 */
public class ChatbotActivity extends AppCompatActivity {
    
    private ScrollView chatScrollView;
    private LinearLayout chatContainer;
    private TextInputLayout messageInputLayout;
    private EditText messageEditText;
    private Button sendButton;
    private ImageView botAvatar;
    private View loadingMessageView; // Pour retirer le message de chargement
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        
        chatScrollView = findViewById(R.id.chatScrollView);
        chatContainer = findViewById(R.id.chatContainer);
        messageInputLayout = findViewById(R.id.messageInputLayout);
        messageEditText = messageInputLayout.getEditText();
        sendButton = findViewById(R.id.sendButton);
        botAvatar = findViewById(R.id.botAvatar);
        
        // Afficher un message de bienvenue
        addBotMessage("Bonjour ! Je suis votre assistant virtuel PetConnect. " +
                      "Je peux vous aider avec des questions sur l'adoption, les soins et le comportement des animaux. " +
                      "Comment puis-je vous aider aujourd'hui ?");
        
        sendButton.setOnClickListener(v -> sendMessage());
        
        // Permettre d'envoyer en appuyant sur Entrée
        if (messageEditText != null) {
            messageEditText.setOnEditorActionListener((v, actionId, event) -> {
                sendMessage();
                return true;
            });
        }
    }
    
    private void sendMessage() {
        String message = messageEditText != null ? messageEditText.getText().toString().trim() : "";
        
        if (message.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un message", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Afficher le message de l'utilisateur
        addUserMessage(message);
        
        // Effacer le champ de saisie
        if (messageEditText != null) {
            messageEditText.setText("");
        }
        
        // Afficher un indicateur de chargement
        loadingMessageView = addLoadingMessage();
        
        // Appeler l'API du chatbot
        ChatbotApiService.getResponse(message, new ChatbotApiService.ChatbotCallback() {
            @Override
            public void onSuccess(String response) {
                // Retirer le message de chargement et ajouter la vraie réponse
                runOnUiThread(() -> {
                    if (loadingMessageView != null && loadingMessageView.getParent() != null) {
                        chatContainer.removeView(loadingMessageView);
                    }
                    addBotMessage(response);
                });
            }
            
            @Override
            public void onError(String error) {
                // En cas d'erreur, utiliser les réponses prédéfinies
                runOnUiThread(() -> {
                    if (loadingMessageView != null && loadingMessageView.getParent() != null) {
                        chatContainer.removeView(loadingMessageView);
                    }
                    String fallbackResponse = ChatbotAssistant.getResponse(message);
                    addBotMessage(fallbackResponse);
                });
            }
        });
    }
    
    private void addUserMessage(String message) {
        View messageView = getLayoutInflater().inflate(R.layout.item_chat_message_user, chatContainer, false);
        TextView messageText = messageView.findViewById(R.id.messageText);
        TextView timeText = messageView.findViewById(R.id.timeText);
        
        if (messageText != null) {
            messageText.setText(message);
        }
        if (timeText != null) {
            timeText.setText(getCurrentTime());
        }
        
        chatContainer.addView(messageView);
        scrollToBottom();
    }
    
    private void addBotMessage(String message) {
        View messageView = getLayoutInflater().inflate(R.layout.item_chat_message_bot, chatContainer, false);
        TextView messageText = messageView.findViewById(R.id.messageText);
        TextView timeText = messageView.findViewById(R.id.timeText);
        
        if (messageText != null) {
            messageText.setText(message);
        }
        if (timeText != null) {
            timeText.setText(getCurrentTime());
        }
        
        chatContainer.addView(messageView);
        scrollToBottom();
    }
    
    private void scrollToBottom() {
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }
    
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    private View addLoadingMessage() {
        View messageView = getLayoutInflater().inflate(R.layout.item_chat_message_bot, chatContainer, false);
        TextView messageText = messageView.findViewById(R.id.messageText);
        TextView timeText = messageView.findViewById(R.id.timeText);
        
        if (messageText != null) {
            messageText.setText("En train de réfléchir...");
        }
        if (timeText != null) {
            timeText.setText(getCurrentTime());
        }
        
        chatContainer.addView(messageView);
        scrollToBottom();
        return messageView;
    }
}

