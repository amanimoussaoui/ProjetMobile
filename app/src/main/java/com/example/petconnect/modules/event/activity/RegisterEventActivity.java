package com.example.petconnect.modules.event.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.petconnect.R;
import com.example.petconnect.modules.event.model.EventRegistration;
import com.example.petconnect.modules.event.repository.EventRegistrationRepository;
import java.util.UUID;

public class RegisterEventActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "event_id";
    public static final String EXTRA_EVENT_TITLE = "event_title";

    private TextView eventTitleTextView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText notesEditText;
    private Button confirmButton;

    private EventRegistrationRepository registrationRepository;
    private String eventId;
    private String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        eventTitle = getIntent().getStringExtra(EXTRA_EVENT_TITLE);

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Erreur: ID événement manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initRepository();
        setupListeners();
        
        if (eventTitle != null) {
            eventTitleTextView.setText(eventTitle);
        }
    }

    private void initViews() {
        eventTitleTextView = findViewById(R.id.textViewEventTitle);
        nameEditText = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmail);
        notesEditText = findViewById(R.id.editTextNotes);
        confirmButton = findViewById(R.id.buttonConfirm);
    }

    private void initRepository() {
        registrationRepository = new EventRegistrationRepository();
    }

    private void setupListeners() {
        confirmButton.setOnClickListener(v -> registerForEvent());
    }

    private void registerForEvent() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();

        // Validation
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer l'inscription
        String registrationId = UUID.randomUUID().toString();
        String userId = "user_" + System.currentTimeMillis(); // TODO: Utiliser l'ID utilisateur réel

        EventRegistration registration = new EventRegistration(
                registrationId,
                eventId,
                userId,
                name,
                email
        );
        registration.setNotes(notes);

        // Enregistrer l'inscription
        registrationRepository.addRegistration(registration)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Inscription confirmée!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
