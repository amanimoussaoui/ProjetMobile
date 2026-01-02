package com.example.petconnect_event.event.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.petconnect_event.R;
import com.example.petconnect_event.event.model.Event;
import com.example.petconnect_event.event.repository.EventRepository;
import com.example.petconnect_event.event.util.ReminderManager;
import com.example.petconnect_event.event.util.EventChangeDetector;
import com.example.petconnect_event.event.util.EventUpdateNotificationManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditEventActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "event_id";

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private EditText dateEditText;
    private EditText maxParticipantsEditText;
    private EditText imageUrlEditText;
    private Button saveButton;
    private ImageView imagePreview;
    private CheckBox reminderCheckBox;
    private Spinner reminderSpinner;

    private EventRepository eventRepository;
    private ReminderManager reminderManager;
    private EventUpdateNotificationManager updateNotificationManager;
    private String eventId;
    private boolean isEditMode;
    private Calendar selectedCalendar;
    private int currentParticipantsExisting = 0;
    private Date createdAtExisting;
    private Event oldEventData; // Pour détecter les changements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);

        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        isEditMode = eventId != null;

        selectedCalendar = Calendar.getInstance();
        reminderManager = new ReminderManager(this);
        updateNotificationManager = new EventUpdateNotificationManager(this);

        initViews();
        initRepository();
        setupListeners();

        if (isEditMode) {
            setTitle("Modifier l'événement");
            loadEventData();
        } else {
            setTitle("Créer un événement");
        }
    }

    private void initViews() {
        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        locationEditText = findViewById(R.id.editTextLocation);
        dateEditText = findViewById(R.id.editTextDate);
        maxParticipantsEditText = findViewById(R.id.editTextMaxParticipants);
        imageUrlEditText = findViewById(R.id.editTextImageUrl);
        saveButton = findViewById(R.id.buttonSave);
        imagePreview = findViewById(R.id.imageViewEventPreview);
        reminderCheckBox = findViewById(R.id.checkboxReminder);
        reminderSpinner = findViewById(R.id.spinnerReminderTime);

        // Désactiver l'édition directe de la date
        dateEditText.setFocusable(false);
        dateEditText.setClickable(true);

        // Gérer la visibilité du spinner de rappel
        reminderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderSpinner.setEnabled(isChecked);
        });
        reminderSpinner.setEnabled(false);

        // Preview image on URL change
        imageUrlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(AddEditEventActivity.this)
                            .load(url)
                            .placeholder(R.drawable.ic_event)
                            .error(R.drawable.ic_event)
                            .into(imagePreview);
                } else {
                    imagePreview.setImageResource(R.drawable.ic_event);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initRepository() {
        eventRepository = new EventRepository();
    }

    private void setupListeners() {
        dateEditText.setOnClickListener(v -> showDateTimePicker());
        saveButton.setOnClickListener(v -> saveEvent());
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    // Afficher le time picker après la sélection de la date
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedCalendar.set(Calendar.MINUTE, minute);
                                
                                // Afficher la date et l'heure sélectionnées
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
                                dateEditText.setText(sdf.format(selectedCalendar.getTime()));
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadEventData() {
        eventRepository.getEventById(eventId, new EventRepository.OnEventLoadedListener() {
            @Override
            public void onLoaded(Event event) {
                titleEditText.setText(event.getTitle());
                descriptionEditText.setText(event.getDescription());
                locationEditText.setText(event.getLocation());
                maxParticipantsEditText.setText(String.valueOf(event.getMaxParticipants()));
                
                if (event.getEventDate() != null) {
                    selectedCalendar.setTime(event.getEventDate());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
                    dateEditText.setText(sdf.format(event.getEventDate()));
                }

                currentParticipantsExisting = event.getCurrentParticipants();
                createdAtExisting = event.getCreatedAt();

                // Sauvegarder les données existantes pour détecter les changements
                oldEventData = new Event();
                oldEventData.setId(event.getId());
                oldEventData.setTitle(event.getTitle());
                oldEventData.setDescription(event.getDescription());
                oldEventData.setLocation(event.getLocation());
                oldEventData.setEventDate(event.getEventDate());
                oldEventData.setMaxParticipants(event.getMaxParticipants());
                oldEventData.setActive(event.isActive());

                if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                    imageUrlEditText.setText(event.getImageUrl());
                }

                // Charger la configuration du rappel
                if (reminderManager.hasReminder(eventId)) {
                    reminderCheckBox.setChecked(true);
                    int minutes = reminderManager.getReminderMinutes(eventId);
                    
                    int[] minuteArray = getResources().getIntArray(R.array.reminder_minutes);
                    for (int i = 0; i < minuteArray.length; i++) {
                        if (minuteArray[i] == minutes) {
                            reminderSpinner.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditEventActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveEvent() {
        // Validation
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String dateStr = dateEditText.getText().toString().trim();
        String maxParticipantsStr = maxParticipantsEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || 
            dateStr.isEmpty() || maxParticipantsStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        int maxParticipants;
        try {
            maxParticipants = Integer.parseInt(maxParticipantsStr);
            if (maxParticipants <= 0) {
                Toast.makeText(this, "Le nombre de participants doit être positif", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nombre de participants invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer ou mettre à jour l'événement
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventDate(selectedCalendar.getTime());
        event.setMaxParticipants(maxParticipants);
        event.setOrganizerId("user123"); // TODO: Utiliser l'ID utilisateur réel
        event.setCurrentParticipants(isEditMode ? currentParticipantsExisting : 0);
        event.setActive(true);
        event.setCreatedAt(isEditMode && createdAtExisting != null ? createdAtExisting : new Date());

        // Set image URL from text field
        String imageUrl = imageUrlEditText.getText().toString().trim();
        event.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);

        if (isEditMode) {
            event.setId(eventId);
            updateEvent(event);
        } else {
            android.util.Log.d("AddEditEvent", "Adding event: title=" + title + " date=" + event.getEventDate());
            addEvent(event);
        }

        // Gérer le rappel
        if (reminderCheckBox.isChecked()) {
            int[] minuteArray = getResources().getIntArray(R.array.reminder_minutes);
            int selectedIndex = reminderSpinner.getSelectedItemPosition();
            int minutesBefore = minuteArray[selectedIndex];
            
            if (minutesBefore > 0) {
                String eventId = isEditMode ? this.eventId : event.getId();
                reminderManager.setReminder(
                    eventId,
                    title,
                    selectedCalendar.getTimeInMillis(),
                    minutesBefore
                );
            }
        } else {
            // Annuler le rappel s'il existe
            if (isEditMode) {
                reminderManager.cancelReminder(eventId);
            }
        }
    }

    private void addEvent(Event event) {
        eventRepository.addEvent(event, new EventRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddEditEventActivity.this, "Événement créé avec succès", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("AddEditEvent", "Erreur lors de l'ajout: " + error);
                Toast.makeText(AddEditEventActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEvent(Event event) {
        // Détecter les changements
        List<EventChangeDetector.EventChange> changes = EventChangeDetector.detectChanges(oldEventData, event);
        
        // Envoyer les notifications si des changements
        if (!changes.isEmpty()) {
            updateNotificationManager.notifyParticipantsOfChanges(
                event.getId(),
                event.getTitle(),
                changes
            );
            android.util.Log.d("AddEditEvent", "Notifications envoyées pour " + changes.size() + " changements");
        }

        eventRepository.updateEvent(event, new EventRepository.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddEditEventActivity.this, "Événement mis à jour", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditEventActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}