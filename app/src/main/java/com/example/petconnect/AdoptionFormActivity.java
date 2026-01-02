package com.example.petconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class AdoptionFormActivity extends AppCompatActivity {
    private static final String TAG = "AdoptionForm";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_OUTDOOR_IMAGE_REQUEST = 2;
    private static final int SMS_PERMISSION_CODE = 100;

    // ⚠️ CONFIGURATION EMAIL - REMPLACEZ PAR VOS INFORMATIONS
    private static final String SENDER_EMAIL = "sarra.amami12345@gmail.com";
    private static final String SENDER_PASSWORD = "ikba ujtj bkpy dfia";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Views
    private ImageView backButton, petPhotoForm;
    private TextView petEmojiForm, formTitle;
    private CardView scoreCard;
    private TextView compatibilityScore, scoreDescription, scoreLevel;
    private ProgressBar scoreProgressBar;
    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private Spinner governorateSpinner;
    private EditText addressInput;
    private RadioGroup housingTypeGroup, outdoorSpaceGroup;
    private RadioGroup previousPetGroup, timeAvailableGroup;
    private EditText motivationInput;
    private ImageView photoLivingSpace, photoOutdoorSpace;
    private Button uploadLivingButton, uploadOutdoorButton, deleteLivingButton, deleteOutdoorButton;
    private TextView outdoorPhotoLabel;
    private CheckBox commitmentCare, commitmentConditions, commitmentContact;
    private Button submitButton;
    private Pet pet;

    // Photo URIs
    private Uri livingSpaceUri, outdoorSpaceUri;

    // Score calculation
    private int totalScore = 0;
    private boolean formInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_form);

        pet = (Pet) getIntent().getSerializableExtra("pet");
        if (pet == null) {
            Toast.makeText(this, "Erreur: Animal introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Vérifier et demander la permission SMS
        checkSmsPermission();

        initViews(); // SEULEMENT le minimum pour afficher l'écran

// ⚡ Afficher l’UI immédiatement
        new Handler().post(() -> {
            setupGovernorateSpinner();
            displayPetInfo();
            setupListeners();

            formInitialized = true;
            updateSubmitButtonState();
        });
    }

    /**
     * Vérifie et demande la permission d'envoyer des SMS
     */
    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "✅ Permission SMS accordée");
            } else {
                Log.w(TAG, "⚠️ Permission SMS refusée");
                Toast.makeText(this, "⚠️ Permission SMS refusée - Les notifications par SMS ne seront pas envoyées", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initViews() {
        backButton = findViewById(R.id.backButtonForm);
        petPhotoForm = findViewById(R.id.petPhotoForm);
        petEmojiForm = findViewById(R.id.petEmojiForm);
        formTitle = findViewById(R.id.formTitle);

        scoreCard = findViewById(R.id.scoreCard);
        compatibilityScore = findViewById(R.id.compatibilityScore);
        scoreDescription = findViewById(R.id.scoreDescription);
        scoreLevel = findViewById(R.id.scoreLevel);
        scoreProgressBar = findViewById(R.id.scoreProgressBar);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);

        governorateSpinner = findViewById(R.id.governorateSpinner);
        addressInput = findViewById(R.id.addressInput);
        housingTypeGroup = findViewById(R.id.housingTypeGroup);
        outdoorSpaceGroup = findViewById(R.id.outdoorSpaceGroup);

        previousPetGroup = findViewById(R.id.previousPetGroup);
        timeAvailableGroup = findViewById(R.id.timeAvailableGroup);

        motivationInput = findViewById(R.id.motivationInput);

        photoLivingSpace = findViewById(R.id.photoLivingSpace);
        photoOutdoorSpace = findViewById(R.id.photoOutdoorSpace);
        uploadLivingButton = findViewById(R.id.uploadLivingButton);
        uploadOutdoorButton = findViewById(R.id.uploadOutdoorButton);
        deleteLivingButton = findViewById(R.id.deleteLivingButton);
        deleteOutdoorButton = findViewById(R.id.deleteOutdoorButton);
        outdoorPhotoLabel = findViewById(R.id.outdoorPhotoLabel);

        commitmentCare = findViewById(R.id.commitmentCare);
        commitmentConditions = findViewById(R.id.commitmentConditions);
        commitmentContact = findViewById(R.id.commitmentContact);

        submitButton = findViewById(R.id.submitButton);
    }

    private void setupGovernorateSpinner() {
        String[] governorates = {
                "Sélectionnez un gouvernorat",
                "Tunis", "Ariana", "Ben Arous", "Manouba",
                "Sfax", "Sousse", "Nabeul", "Bizerte",
                "Kairouan", "Gabès", "Gafsa", "Tozeur",
                "Kébili", "Médenine", "Tataouine", "Mahdia",
                "Monastir", "Siliana", "Zaghouan", "Béja",
                "Jendouba", "Le Kef", "Kasserine", "Sidi Bouzid"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item_custom, governorates);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        governorateSpinner.setAdapter(adapter);

        governorateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSubmitButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void displayPetInfo() {
        String photoUrl = pet.getPhotoPath();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            petEmojiForm.setVisibility(View.GONE);
            petPhotoForm.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.color.teal_primary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .thumbnail(0.1f)
                    .into(petPhotoForm);
        } else {
            petPhotoForm.setVisibility(View.GONE);
            petEmojiForm.setVisibility(View.VISIBLE);
            petEmojiForm.setText(pet.getEmoji());
        }

        formTitle.setText("Adopter " + pet.getName());
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> {
            v.setEnabled(false);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
        });

        setupRealtimeValidation();

        uploadLivingButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        uploadOutdoorButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_OUTDOOR_IMAGE_REQUEST);
        });

        deleteLivingButton.setOnClickListener(v -> {
            livingSpaceUri = null;
            photoLivingSpace.setVisibility(View.GONE);
            deleteLivingButton.setVisibility(View.GONE);
            Toast.makeText(this, "Photo du logement supprimée", Toast.LENGTH_SHORT).show();
            updateSubmitButtonState();
        });

        deleteOutdoorButton.setOnClickListener(v -> {
            outdoorSpaceUri = null;
            photoOutdoorSpace.setVisibility(View.GONE);
            deleteOutdoorButton.setVisibility(View.GONE);
            Toast.makeText(this, "Photo de l'espace extérieur supprimée", Toast.LENGTH_SHORT).show();
            updateSubmitButtonState();
        });

        housingTypeGroup.setOnCheckedChangeListener((g, id) -> {
            calculateScore();
            updateSubmitButtonState();
        });

        outdoorSpaceGroup.setOnCheckedChangeListener((g, id) -> {
            if (id == R.id.outdoorYes) {
                outdoorPhotoLabel.setVisibility(View.VISIBLE);
                uploadOutdoorButton.setVisibility(View.VISIBLE);
            } else {
                outdoorPhotoLabel.setVisibility(View.GONE);
                uploadOutdoorButton.setVisibility(View.GONE);
                photoOutdoorSpace.setVisibility(View.GONE);
                deleteOutdoorButton.setVisibility(View.GONE);
                outdoorSpaceUri = null;
            }
            calculateScore();
            updateSubmitButtonState();
        });

        previousPetGroup.setOnCheckedChangeListener((g, id) -> {
            calculateScore();
            updateSubmitButtonState();
        });

        timeAvailableGroup.setOnCheckedChangeListener((g, id) -> {
            calculateScore();
            updateSubmitButtonState();
        });

        commitmentCare.setOnCheckedChangeListener((btn, checked) -> updateSubmitButtonState());
        commitmentConditions.setOnCheckedChangeListener((btn, checked) -> updateSubmitButtonState());
        commitmentContact.setOnCheckedChangeListener((btn, checked) -> updateSubmitButtonState());

        submitButton.setOnClickListener(v -> submitForm());
    }

    private void setupRealtimeValidation() {
        firstNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.isEmpty() && !text.matches("[a-zA-Zàâäéèêëïîôùûüÿç ]*")) {
                    firstNameInput.setError("Seulement des lettres");
                } else {
                    firstNameInput.setError(null);
                }
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lastNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.isEmpty() && !text.matches("[a-zA-Zàâäéèêëïîôùûüÿç ]*")) {
                    lastNameInput.setError("Seulement des lettres");
                } else {
                    lastNameInput.setError(null);
                }
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Format e-mail invalide");
                } else {
                    emailInput.setError(null);
                }
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString();
                if (!phone.isEmpty() && !phone.matches("[0-9+ ]*")) {
                    phoneInput.setError("Format invalide");
                } else {
                    phoneInput.setError(null);
                }
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = s.toString();
                if (!address.isEmpty()) {
                    boolean hasLetters = address.matches(".*[a-zA-Zàâäéèêëïîôùûüÿç]+.*");
                    int digitCount = address.replaceAll("[^0-9]", "").length();

                    if (!hasLetters || digitCount < 5) {
                        addressInput.setError("Doit contenir des lettres et au moins 5 chiffres");
                    } else {
                        addressInput.setError(null);
                    }
                }
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        motivationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void calculateScore() {
        if (!formInitialized) return;

        totalScore = 0;

        // Housing type (0-30 points)
        int housingId = housingTypeGroup.getCheckedRadioButtonId();
        if (housingId == R.id.housingHouse) {
            totalScore += 30;
        } else if (housingId == R.id.housingApartment) {
            totalScore += 10;
        }

        // Outdoor space (0-30 points)
        int outdoorId = outdoorSpaceGroup.getCheckedRadioButtonId();
        if (outdoorId == R.id.outdoorYes) {
            totalScore += 30;
        } else if (outdoorId == R.id.outdoorNo) {
            totalScore += 5;
        }

        // Previous pet experience (0-20 points)
        int previousId = previousPetGroup.getCheckedRadioButtonId();
        if (previousId == R.id.previousPetYes) {
            totalScore += 20;
        } else if (previousId == R.id.previousPetNo) {
            totalScore += 5;
        }

        // Time available (0-20 points)
        int timeId = timeAvailableGroup.getCheckedRadioButtonId();
        if (timeId == R.id.timeMore3h) {
            totalScore += 20;
        } else if (timeId == R.id.time1to3h) {
            totalScore += 10;
        } else if (timeId == R.id.timeLess1h) {
            totalScore += 2;
        }

        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        if (totalScore > 0) {
            scoreCard.setVisibility(View.VISIBLE);
        }

        scoreProgressBar.setProgress(totalScore);
        compatibilityScore.setText(totalScore + "%");

        String level;
        int color;

        if (totalScore >= 85) {
            level = "🟢 IDÉAL";
            color = getResources().getColor(R.color.score_ideal);
            scoreDescription.setText("Conditions optimales pour l'adoption !");
        } else if (totalScore >= 65) {
            level = "🟡 FORT";
            color = getResources().getColor(R.color.score_good);
            scoreDescription.setText("Conditions favorables à l'adoption");
        } else if (totalScore >= 45) {
            level = "🟠 MOYEN";
            color = getResources().getColor(R.color.score_medium);
            scoreDescription.setText("Conditions acceptables mais nécessitent un entretien");
        } else if (totalScore > 0) {
            level = "🔴 FAIBLE";
            color = getResources().getColor(R.color.score_low);
            scoreDescription.setText("Conditions insuffisantes pour l'adoption");
        } else {
            level = "⚪ NON ÉVALUÉ";
            color = getResources().getColor(R.color.score_none);
            scoreDescription.setText("Remplissez les conditions pour voir votre score");
        }

        compatibilityScore.setTextColor(color);
        scoreLevel.setText(level);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            if (requestCode == PICK_IMAGE_REQUEST) {
                livingSpaceUri = imageUri;
                photoLivingSpace.setVisibility(View.VISIBLE);
                deleteLivingButton.setVisibility(View.VISIBLE);
                Glide.with(this).load(imageUri).into(photoLivingSpace);
                Toast.makeText(this, "✅ Photo du logement ajoutée", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_OUTDOOR_IMAGE_REQUEST) {
                outdoorSpaceUri = imageUri;
                photoOutdoorSpace.setVisibility(View.VISIBLE);
                deleteOutdoorButton.setVisibility(View.VISIBLE);
                Glide.with(this).load(imageUri).into(photoOutdoorSpace);
                Toast.makeText(this, "✅ Photo de l'espace extérieur ajoutée", Toast.LENGTH_SHORT).show();
            }

            updateSubmitButtonState();
        }
    }

    private void updateSubmitButtonState() {
        boolean isValid = validateFormQuick();
        submitButton.setEnabled(isValid);
        submitButton.setAlpha(isValid ? 1.0f : 0.5f);
    }

    private boolean validateFormQuick() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        int governorate = governorateSpinner.getSelectedItemPosition();
        String motivation = motivationInput.getText().toString().trim();

        if (firstName.isEmpty() || !firstName.matches("[a-zA-Zàâäéèêëïîôùûüÿç ]+")) return false;
        if (lastName.isEmpty() || !lastName.matches("[a-zA-Zàâäéèêëïîôùûüÿç ]+")) return false;
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false;
        if (phone.isEmpty() || phone.length() < 8) return false;
        if (governorate == 0) return false;

        if (address.isEmpty()) return false;
        boolean hasLetters = address.matches(".*[a-zA-Zàâäéèêëïîôùûüÿç]+.*");
        int digitCount = address.replaceAll("[^0-9]", "").length();
        if (!hasLetters || digitCount < 5) return false;

        if (housingTypeGroup.getCheckedRadioButtonId() == -1) return false;
        if (outdoorSpaceGroup.getCheckedRadioButtonId() == -1) return false;
        if (previousPetGroup.getCheckedRadioButtonId() == -1) return false;
        if (timeAvailableGroup.getCheckedRadioButtonId() == -1) return false;
        if (motivation.isEmpty() || motivation.length() < 50) return false;
        if (livingSpaceUri == null) return false;

        if (outdoorSpaceGroup.getCheckedRadioButtonId() == R.id.outdoorYes && outdoorSpaceUri == null) {
            return false;
        }

        if (!commitmentCare.isChecked() || !commitmentConditions.isChecked() || !commitmentContact.isChecked()) {
            return false;
        }

        return true;
    }

    private void submitForm() {
        if (!validateFormQuick()) {
            Toast.makeText(this, "❌ Veuillez remplir tous les champs obligatoires", Toast.LENGTH_LONG).show();
            return;
        }

        showResultDialog();
    }

    private void showResultDialog() {
        String emailSubject, emailBody;

        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String governorate = governorateSpinner.getSelectedItem().toString();

        // MESSAGE SMS UNIQUE POUR TOUS (ne révèle pas le résultat)
        String smsMessage = "PetConnect: Merci pour votre interet pour " + pet.getName() +
                ". Veuillez consulter votre email pour connaitre le resultat de votre demande.";

        // CONTENU EMAIL SELON LE SCORE (révèle le résultat)
        if (totalScore >= 85) {
            // IDÉAL - Acceptation immédiate
            emailSubject = "✅ Acceptation de votre demande d'adoption - " + pet.getName();
            emailBody = "Bonjour " + firstName + " " + lastName + ",\n\n" +
                    "Nous sommes ravis de vous informer que votre demande d'adoption pour " + pet.getName() +
                    " a été ACCEPTÉE !\n\n" +
                    "Score de compatibilité : " + totalScore + "%\n" +
                    "Résultat : 🟢 PROFIL IDÉAL\n\n" +
                    "Détails de l'animal :\n" +
                    "- Nom : " + pet.getName() + "\n" +
                    "- Race : " + pet.getBreed() + "\n" +
                    "- Âge : " + pet.getAge() + "\n" +
                    "- Localisation : " + pet.getLocation() + "\n\n" +
                    "Nous vous contacterons prochainement au " + phone + " pour finaliser l'adoption.\n\n" +
                    "Cordialement,\nL'équipe PetConnect";

            transferToAdoptedPets(firstName, lastName, email, phone, address, governorate);

        } else if (totalScore >= 65) {
            // FORT - Acceptation avec entretien
            emailSubject = "📅 Entretien requis - Demande d'adoption pour " + pet.getName();
            emailBody = "Bonjour " + firstName + " " + lastName + ",\n\n" +
                    "Votre profil est prometteur pour l'adoption de " + pet.getName() + ".\n\n" +
                    "Score de compatibilité : " + totalScore + "%\n" +
                    "Résultat : 🟡 BON PROFIL - ACCEPTÉ AVEC ENTRETIEN\n\n" +
                    "Détails de l'animal :\n" +
                    "- Nom : " + pet.getName() + "\n" +
                    "- Race : " + pet.getBreed() + "\n" +
                    "- Âge : " + pet.getAge() + "\n\n" +
                    "Nous souhaitons organiser un entretien avec vous pour discuter de l'adoption. " +
                    "Nous vous contacterons prochainement au " + phone + ".\n\n" +
                    "Cordialement,\nL'équipe PetConnect";

            transferToAdoptedPets(firstName, lastName, email, phone, address, governorate);

        } else if (totalScore >= 45) {
            // MOYEN - Demande d'entretien obligatoire
            emailSubject = "⚠️ Entretien obligatoire - Demande d'adoption pour " + pet.getName();
            emailBody = "Bonjour " + firstName + " " + lastName + ",\n\n" +
                    "Nous avons bien reçu votre demande d'adoption pour " + pet.getName() + ".\n\n" +
                    "Score de compatibilité : " + totalScore + "%\n" +
                    "Résultat : 🟠 PROFIL MOYEN - ENTRETIEN REQUIS\n\n" +
                    "Votre profil nécessite un entretien approfondi pour évaluer si les conditions " +
                    "peuvent être améliorées. Nous vous contacterons au " + phone + " pour fixer un rendez-vous.\n\n" +
                    "Cordialement,\nL'équipe PetConnect";

        } else {
            // FAIBLE - Refus
            emailSubject = "❌ Refus de votre demande d'adoption - " + pet.getName();
            emailBody = "Bonjour " + firstName + " " + lastName + ",\n\n" +
                    "Nous avons examiné votre demande d'adoption pour " + pet.getName() + ".\n\n" +
                    "Score de compatibilité : " + totalScore + "%\n" +
                    "Résultat : 🔴 PROFIL INSUFFISANT - DEMANDE REFUSÉE\n\n" +
                    "Malheureusement, nous ne pouvons pas accepter votre demande car les conditions " +
                    "ne sont pas optimales pour le bien-être de l'animal.\n\n" +
                    "Nous vous encourageons à améliorer vos conditions (logement, espace, disponibilité) " +
                    "et à renouveler votre demande ultérieurement.\n\n" +
                    "Cordialement,\nL'équipe PetConnect";
        }

        // Log pour debug
        Log.d(TAG, "📧 Email destinataire: " + email);
        Log.d(TAG, "📱 Téléphone destinataire: " + phone);
        Log.d(TAG, "📝 Message SMS: " + smsMessage);

        // Envoyer l'email et le SMS
        sendEmail(email, emailSubject, emailBody);
        sendSMS(phone, smsMessage);

        // DIALOGUE GÉNÉRIQUE (ne révèle pas le résultat)
        new Handler().postDelayed(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("✅ Demande envoyée")
                    .setMessage("Merci pour votre demande d'adoption pour " + pet.getName() + " !\n\n" +
                            "📧 Un email contenant le résultat de votre demande vous a été envoyé à :\n" +
                            email + "\n\n" +
                            "📱 Un SMS de confirmation vous a été envoyé au :\n" +
                            phone + "\n\n" +
                            "Veuillez consulter votre boîte email pour connaître la décision concernant votre demande.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Toast.makeText(this, "✅ Demande traitée avec succès !", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(this::returnToMainActivity, 500);
                    })
                    .setCancelable(false)
                    .show();
        }, 300);
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Envoie un SMS de confirmation (message générique)
     */
    private void sendSMS(final String phoneNumber, final String message) {
        // Vérifier la permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "⚠️ Permission SMS non accordée - SMS non envoyé");
            Toast.makeText(this, "⚠️ Permission SMS requise pour envoyer la notification", Toast.LENGTH_SHORT).show();
            return;
        }

        // Valider le numéro de téléphone
        String cleanPhone = phoneNumber.replaceAll("[^0-9+]", "");
        Log.d(TAG, "📱 Numéro nettoyé : " + cleanPhone);

        if (cleanPhone.isEmpty() || cleanPhone.length() < 8) {
            Log.e(TAG, "❌ Numéro de téléphone invalide : " + cleanPhone);
            Toast.makeText(this, "⚠️ Numéro de téléphone invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Formater le numéro pour la Tunisie si nécessaire
        if (!cleanPhone.startsWith("+")) {
            if (cleanPhone.startsWith("00216")) {
                cleanPhone = "+" + cleanPhone.substring(2);
            } else if (cleanPhone.startsWith("216")) {
                cleanPhone = "+" + cleanPhone;
            } else if (cleanPhone.length() == 8) {
                cleanPhone = "+216" + cleanPhone;
            }
        }

        final String finalPhone = cleanPhone;
        Log.d(TAG, "📱 Numéro final : " + finalPhone);

        new Thread(() -> {
            try {
                Log.d(TAG, "📱 Tentative d'envoi SMS à : " + finalPhone);
                Log.d(TAG, "📝 Message : " + message);

                SmsManager smsManager = SmsManager.getDefault();

                // Si le message est trop long, le diviser en plusieurs parties
                if (message.length() > 160) {
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    Log.d(TAG, "📨 Message divisé en " + parts.size() + " parties");
                    smsManager.sendMultipartTextMessage(
                            finalPhone,
                            null,
                            parts,
                            null,
                            null
                    );
                    Log.d(TAG, "✅ SMS multipart envoyé");
                } else {
                    smsManager.sendTextMessage(finalPhone, null, message, null, null);
                    Log.d(TAG, "✅ SMS simple envoyé");
                }

                // Attendre un peu pour s'assurer que le SMS est envoyé
                Thread.sleep(500);

                runOnUiThread(() ->
                        Toast.makeText(AdoptionFormActivity.this,
                                "📱 SMS envoyé avec succès !",
                                Toast.LENGTH_SHORT).show()
                );

            } catch (SecurityException e) {
                Log.e(TAG, "❌ Erreur de sécurité SMS: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(AdoptionFormActivity.this,
                                "⚠️ Permission SMS manquante",
                                Toast.LENGTH_LONG).show()
                );
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "❌ Argument invalide pour SMS: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(AdoptionFormActivity.this,
                                "⚠️ Format de numéro invalide",
                                Toast.LENGTH_LONG).show()
                );
            } catch (Exception e) {
                Log.e(TAG, "❌ Erreur d'envoi SMS: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(AdoptionFormActivity.this,
                                "⚠️ Erreur d'envoi SMS: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    /**
     * Envoie un email avec le résultat détaillé de l'adoption
     */
    private void sendEmail(final String recipientEmail, final String subject, final String body) {
        new Thread(() -> {
            try {
                Log.d(TAG, "📧 Tentative d'envoi d'email à : " + recipientEmail);

                // Configuration SMTP
                Properties props = new Properties();
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                props.put("mail.smtp.connectiontimeout", "10000");
                props.put("mail.smtp.timeout", "10000");

                // Authentification
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });

                // Création du message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, "PetConnect"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject(subject);
                message.setText(body);

                // Envoi
                Transport.send(message);

                Log.d(TAG, "✅ Email envoyé avec succès à " + recipientEmail);

                runOnUiThread(() ->
                        Toast.makeText(AdoptionFormActivity.this,
                                "✅ Email envoyé avec succès !",
                                Toast.LENGTH_SHORT).show()
                );

            } catch (MessagingException e) {
                Log.e(TAG, "❌ Erreur d'envoi email (MessagingException): " + e.getMessage());
                e.printStackTrace();

                runOnUiThread(() ->
                        Toast.makeText(AdoptionFormActivity.this,
                                "⚠️ Erreur d'envoi email. Vérifiez vos identifiants.",
                                Toast.LENGTH_LONG).show()
                );

            } catch (Exception e) {
                Log.e(TAG, "❌ Erreur générale d'envoi email: " + e.getMessage());
                e.printStackTrace();

                runOnUiThread(() ->
                        Toast.makeText(AdoptionFormActivity.this,
                                "⚠️ Erreur inattendue lors de l'envoi.",
                                Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    /**
     * Transfère l'animal vers "PetsAdopted" et supprime de "Pets"
     * (Seulement pour les scores >= 65)
     */
    private void transferToAdoptedPets(String firstName, String lastName, String email,
                                       String phone, String address, String governorate) {
        String firestoreId = pet.getFirestoreId();
        if (firestoreId == null || firestoreId.isEmpty()) {
            Log.e(TAG, "❌ Animal sans ID Firestore, impossible de le supprimer");
            Toast.makeText(this, "⚠️ Erreur: Animal sans ID", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> adoptedPet = new HashMap<>();

        // Informations de l'animal
        adoptedPet.put("name", pet.getName());
        adoptedPet.put("age", pet.getAge());
        adoptedPet.put("gender", pet.getGender());
        adoptedPet.put("breed", pet.getBreed());
        adoptedPet.put("location", pet.getLocation());
        adoptedPet.put("emoji", pet.getEmoji());
        adoptedPet.put("photoPath", pet.getPhotoPath());
        adoptedPet.put("about", pet.getAbout());
        adoptedPet.put("requirements", pet.getRequirements());

        // Informations de l'adoptant
        adoptedPet.put("adopterFirstName", firstName);
        adoptedPet.put("adopterLastName", lastName);
        adoptedPet.put("adopterEmail", email);
        adoptedPet.put("adopterPhone", phone);
        adoptedPet.put("adopterAddress", address);
        adoptedPet.put("adopterGovernorate", governorate);
        adoptedPet.put("compatibilityScore", totalScore);
        adoptedPet.put("adoptionDate", System.currentTimeMillis());

        // Étape 1 : Ajouter à la collection "PetsAdopted"
        db.collection("PetsAdopted")
                .add(adoptedPet)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "✅ Animal ajouté à PetsAdopted: " + documentReference.getId());

                    // Étape 2 : Supprimer de la collection "Pets" avec PetRepository
                    PetRepository repo = new PetRepository();
                    repo.deletePet(firestoreId, new PetRepository.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "✅ Animal supprimé de Pets avec succès");
                            Log.d(TAG, "🎉 Transfert terminé : " + pet.getName() + " adopté par " + firstName + " " + lastName);

                            runOnUiThread(() -> {
                                Toast.makeText(AdoptionFormActivity.this,
                                        "✅ " + pet.getName() + " a été adopté et retiré de la liste",
                                        Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Erreur lors de la suppression de Pets: " + error);

                            runOnUiThread(() -> {
                                Toast.makeText(AdoptionFormActivity.this,
                                        "⚠️ Animal adopté mais erreur lors de la suppression de la liste",
                                        Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur lors de l'ajout à PetsAdopted: " + e.getMessage());
                    e.printStackTrace();

                    runOnUiThread(() -> {
                        Toast.makeText(AdoptionFormActivity.this,
                                "❌ Erreur lors du transfert de l'animal: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                });
    }
}