package com.example.petconnect_event.event.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.petconnect_event.R;
import com.example.petconnect_event.event.model.Event;
import com.example.petconnect_event.event.model.EventRegistration;
import com.example.petconnect_event.event.repository.EventRepository;
import com.example.petconnect_event.event.repository.EventRegistrationRepository;
import com.example.petconnect_event.event.util.FavoritesManager;
import com.example.petconnect_event.event.util.QRCodeGenerator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;
import androidx.annotation.Nullable;

public class EventDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "event_id";

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private TextView participantsTextView;
    private ImageView eventImageView;
    private Button registerButton;

    private EventRepository eventRepository;
    private EventRegistrationRepository registrationRepository;
    private FavoritesManager favoritesManager;
    private String eventId;
    private Event currentEvent;
    private boolean isRegistered = false;
    private String registrationId;
    private String userId;
    private String userName;
    private String userEmail;
    private ListenerRegistration eventListener;
    private ListenerRegistration registrationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Erreur: ID événement manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initRepository();
        loadEventDetails();
    }

    private void initViews() {
        titleTextView = findViewById(R.id.textViewEventTitle);
        descriptionTextView = findViewById(R.id.textViewEventDescription);
        dateTextView = findViewById(R.id.textViewEventDate);
        locationTextView = findViewById(R.id.textViewEventLocation);
        participantsTextView = findViewById(R.id.textViewParticipants);
        eventImageView = findViewById(R.id.imageViewEventDetail);
        registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(v -> onRegisterToggle());
    }

    private void initRepository() {
        eventRepository = new EventRepository();
        registrationRepository = new EventRegistrationRepository();
        favoritesManager = new FavoritesManager(this);
        restoreUserProfile();
        if (userId == null || userId.isEmpty()) {
            userId = "user-" + UUID.randomUUID().toString();
            saveUserProfile(userId, userName, userEmail);
        }
    }

    private void loadEventDetails() {
        eventRepository.getEventById(eventId, new EventRepository.OnEventLoadedListener() {
            @Override
            public void onLoaded(Event event) {
                currentEvent = event;
                displayEventDetails(event);
                startEventListener();
                startRegistrationListener();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EventDetailActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayEventDetails(Event event) {
        if (event == null) {
            Toast.makeText(this, "Événement introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleTextView.setText(event.getTitle() != null ? event.getTitle() : "Sans titre");
        descriptionTextView.setText(event.getDescription() != null ? event.getDescription() : "Aucune description");
        locationTextView.setText(event.getLocation() != null ? event.getLocation() : "Lieu non défini");

        // Formater la date
        if (event.getEventDate() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);
                dateTextView.setText(sdf.format(event.getEventDate()));
            } catch (Exception e) {
                dateTextView.setText("Date non disponible");
            }
        } else {
            dateTextView.setText("Date non définie");
        }

        // Afficher les participants (par défaut à 0/0 si valeurs nulles)
        int current = event.getCurrentParticipants() >= 0 ? event.getCurrentParticipants() : 0;
        int max = event.getMaxParticipants() > 0 ? event.getMaxParticipants() : 0;
        String participants = current + " / " + max + " participants";
        participantsTextView.setText(participants);

        // Désactiver le bouton si complet
        updateRegisterButtonState();

        // Charger l'image avec Glide si disponible
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.ic_event)
                    .error(R.drawable.ic_event)
                    .into(eventImageView);
        } else {
            eventImageView.setImageResource(R.drawable.ic_event);
        }
    }

    private void onRegisterToggle() {
        if (currentEvent == null) {
            Toast.makeText(this, "Événement non chargé", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isRegistered) {
            unregister();
        } else {
            ensureProfileAndRegister();
        }
    }

    private void ensureProfileAndRegister() {
        if (userName != null && !userName.isEmpty() && userEmail != null && !userEmail.isEmpty()) {
            register();
            return;
        }

        // Demander nom/email une seule fois
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vos informations");
        final EditText inputName = new EditText(this);
        inputName.setHint("Nom");
        final EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email");
        inputEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, padding);
        container.addView(inputName);
        container.addView(inputEmail);
        builder.setView(container);

        builder.setPositiveButton("Continuer", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nom et email requis", Toast.LENGTH_SHORT).show();
                return;
            }
            userName = name;
            userEmail = email;
            saveUserProfile(userId, userName, userEmail);
            register();
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void register() {
        if (currentEvent == null) return;

        registrationId = registrationRepository.buildRegistrationId(eventId, userId);
        EventRegistration registration = new EventRegistration(
                registrationId,
                eventId,
                userId,
                userName != null ? userName : "Invité",
                userEmail != null ? userEmail : ""
        );

        eventRepository.registerWithQuota(registration)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Inscription confirmée", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void unregister() {
        if (registrationId == null) {
            registrationId = registrationRepository.buildRegistrationId(eventId, userId);
        }

        eventRepository.unregisterWithQuota(eventId, registrationId)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Inscription annulée", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void startEventListener() {
        if (eventId == null) return;
        if (eventListener != null) {
            eventListener.remove();
        }
        eventListener = FirebaseFirestore.getInstance()
            .collection("events")
                .document(eventId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null || !value.exists()) return;
                    try {
                        Event updated = value.toObject(Event.class);
                        if (updated != null) {
                            updated.setId(value.getId());
                            currentEvent = updated;
                            displayEventDetails(updated);
                        }
                    } catch (Exception ignored) {}
                });
    }

    private void startRegistrationListener() {
        if (eventId == null || userId == null) return;
        if (registrationListener != null) {
            registrationListener.remove();
        }
        String regId = registrationRepository.buildRegistrationId(eventId, userId);
        registrationListener = registrationRepository.registrationDoc(regId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) return;
                        isRegistered = value != null && value.exists();
                        registrationId = isRegistered ? value.getId() : regId;
                        updateRegisterButtonState();
                    }
                });
    }

    private void updateRegisterButtonState() {
        if (registerButton == null || currentEvent == null) return;

        int max = currentEvent.getMaxParticipants() > 0 ? currentEvent.getMaxParticipants() : 0;
        int cur = currentEvent.getCurrentParticipants() >= 0 ? currentEvent.getCurrentParticipants() : 0;
        boolean isFull = max > 0 && cur >= max;

        if (isRegistered) {
            registerButton.setEnabled(true);
            registerButton.setText("Se désinscrire");
        } else if (isFull) {
            registerButton.setEnabled(false);
            registerButton.setText("Complet");
        } else {
            registerButton.setEnabled(true);
            registerButton.setText("S'inscrire");
        }
    }

    private void saveUserProfile(String uid, String name, String email) {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        prefs.edit()
                .putString("user_id", uid)
                .putString("user_name", name)
                .putString("user_email", email)
                .apply();
    }

    private void restoreUserProfile() {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);
        userName = prefs.getString("user_name", "");
        userEmail = prefs.getString("user_email", "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            eventListener.remove();
        }
        if (registrationListener != null) {
            registrationListener.remove();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem favoriteItem = menu.findItem(R.id.action_favorite);
        if (favoriteItem != null && eventId != null) {
            boolean isFav = favoritesManager.isFavorite(eventId);
            favoriteItem.setIcon(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
            favoriteItem.setTitle(isFav ? "Retirer des favoris" : "Ajouter aux favoris");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void toggleFavorite() {
        if (eventId == null) return;
        favoritesManager.toggleFavorite(eventId);
        invalidateOptionsMenu();
        Toast.makeText(this, favoritesManager.isFavorite(eventId) ? "Ajouté aux favoris" : "Retiré des favoris", Toast.LENGTH_SHORT).show();
    }

    private void shareEvent() {
        if (currentEvent == null) {
            Toast.makeText(this, "Événement non chargé", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder qrContent = new StringBuilder();
        qrContent.append("📅 ").append(currentEvent.getTitle() != null ? currentEvent.getTitle() : "Événement").append("\n\n");

        if (currentEvent.getDescription() != null && !currentEvent.getDescription().isEmpty()) {
            qrContent.append(currentEvent.getDescription()).append("\n\n");
        }

        if (currentEvent.getEventDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);
            qrContent.append("📆 Date : ").append(sdf.format(currentEvent.getEventDate())).append("\n");
        }

        if (currentEvent.getLocation() != null && !currentEvent.getLocation().isEmpty()) {
            qrContent.append("📍 Lieu : ").append(currentEvent.getLocation()).append("\n");
        }

        int max = currentEvent.getMaxParticipants();
        int cur = currentEvent.getCurrentParticipants();
        if (max > 0) {
            qrContent.append("👥 Places : ").append(cur).append("/").append(max).append("\n");
        }

        // Generate QR Code
        Bitmap qrBitmap = QRCodeGenerator.generateQRCode(qrContent.toString(), 512, 512);
        if (qrBitmap == null) {
            Toast.makeText(this, "Erreur génération QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show in dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QR Code - " + currentEvent.getTitle());
        
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(qrBitmap);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        imageView.setPadding(padding, padding, padding, padding);
        builder.setView(imageView);
        
        builder.setPositiveButton("Fermer", null);
        builder.setNeutralButton("Partager image", (dialog, which) -> shareQRImage(qrBitmap));
        builder.show();
    }

    private void shareQRImage(Bitmap qrBitmap) {
        try {
            java.io.File cachePath = new java.io.File(getCacheDir(), "images");
            cachePath.mkdirs();
            java.io.File file = new java.io.File(cachePath, "qr_code.png");
            java.io.FileOutputStream stream = new java.io.FileOutputStream(file);
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Partager le QR code via"));
        } catch (Exception e) {
            Toast.makeText(this, "Erreur partage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (currentEvent == null) {
            Toast.makeText(this, "Événement non chargé", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddEditEventActivity.class);
            intent.putExtra(AddEditEventActivity.EXTRA_EVENT_ID, currentEvent.getId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            confirmDelete();
            return true;
        } else if (id == R.id.action_favorite) {
            toggleFavorite();
            return true;
        } else if (id == R.id.action_share) {
            shareEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer l'événement")
                .setMessage("Voulez-vous vraiment supprimer cet événement ?")
                .setPositiveButton("Supprimer", (dialog, which) -> deleteEvent())
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteEvent() {
        if (eventId == null) {
            Toast.makeText(this, "ID d'événement manquant", Toast.LENGTH_SHORT).show();
            return;
        }

        eventRepository.deleteEvent(eventId, new EventRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailActivity.this, "Événement supprimé", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EventDetailActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}