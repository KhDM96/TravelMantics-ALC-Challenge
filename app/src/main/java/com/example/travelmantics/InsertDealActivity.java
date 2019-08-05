package com.example.travelmantics;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class InsertDealActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private final int PICK_IMAGE_REQUEST = 19191;
    private String collectionRefString = "travelDeals";


    //FIREBASE
    private FirebaseFirestore firestoreDB;
    private CollectionReference firestoreColRef;
    private StorageReference mStorageRef;
    private UploadTask uploadTask;


    private FloatingActionButton fabSaveButton;
    private TextInputEditText titleEditText, descEditText, priceEditText;
    private CircleImageView holidayImageView;

    //VARIABLES
    private TextView messageTextView, continueTextView, cancelTextView;
    private ProgressBar mProgressBar;
    private ProgressBar mProgressBarComplete;
    private AlertDialog dialog;

    //VARIABLES
    //private boolean registrationIsComplete = false;
    private String title;
    private String description;
    private double price;
    private Uri filepath = null;
    //private Bitmap bitmap;
    private String holidayImageLink;
    private int directionActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_deal);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Holiday Deal");
        toolbar.setTitleTextColor(getResources().getColor(R.color.airForceBlue));

        titleEditText = findViewById(R.id.editText_title);
        descEditText = findViewById(R.id.editText_description);
        priceEditText = findViewById(R.id.editText_price);

        holidayImageView = findViewById(R.id.holidayImage);
        holidayImageView.setOnClickListener(this);
        fabSaveButton = findViewById(R.id.fab_saveButton);
        fabSaveButton.setOnClickListener(this);

        FirebaseUtil.openFirebaseReference(collectionRefString,this);
        firestoreDB = FirebaseUtil.mFirestore;
        firestoreColRef = FirebaseUtil.mCollectionReference;
        mStorageRef = FirebaseUtil.mStorageRef;
    }

    private boolean callProfileImageValidator(View mView) {
        return filepath != null;
    }

    private void alertDialogPrompt() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_profile_image_prompt, null);

        messageTextView = mView.findViewById(R.id.dialog_textView_message);
        continueTextView = mView.findViewById(R.id.dialog_textView_continue);
        cancelTextView = mView.findViewById(R.id.dialog_textView_cancel);
        mProgressBar = mView.findViewById(R.id.mProgressBar);
        mProgressBarComplete = mView.findViewById(R.id.mProgressBarComplete);

        messageTextView.setText("Proceed with upload?");

        mBuilder.setView(mView);

        dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.show();

        continueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                cancelTextView.setVisibility(View.INVISIBLE);
                cancelTextView.setEnabled(false);
                continueTextView.setVisibility(View.INVISIBLE);
                continueTextView.setEnabled(false);
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (registrationIsComplete)
                dialog.dismiss();
                if (directionActivity == 0) {
                    dialog.dismiss();
                    finish();
                } else {
                    dialog.dismiss();
                }
                //onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_saveButton:
                saveDeal(view);
                //Snackbar.make(view, "unfortunately something went wrong", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.holidayImage:
                browseForImage();
                break;
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final StorageReference fileRef = mStorageRef.
                child(System.currentTimeMillis()
                        + "_holidayImage" +
                        "." + getFileExtension(filepath));

        uploadTask = fileRef.putFile(filepath);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                mProgressBarComplete.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                mProgressBar.setProgress((int) progress);
                messageTextView.setText("uploading holiday image..." + Integer.toString((int) progress) + "%");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    messageTextView.setText(task.getException().getMessage());
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                messageTextView.setText(task.getException().getMessage());
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    holidayImageLink = downloadUri.toString();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            mProgressBar.setProgress(0);
//                            messageTextView.setText("uploading profile image..."+Double.toString(progress)+"%");
                            uploadData(holidayImageLink);
                        }
                    }, 3500);
                    //Toast.makeText(MainActivity.this, downloadUri.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    // Handle failures
                    // ...
                    messageTextView.setText("Something went wrong");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mProgressBarComplete.setVisibility(View.INVISIBLE);
                    cancelTextView.setVisibility(View.VISIBLE);
                    cancelTextView.setEnabled(true);
                    cancelTextView.setText("Close");
                }
            }
        });
    }

    private void saveDeal(final View mView) {
        if (callProfileImageValidator(mView)) {
            alertDialogPrompt();
        } else {
            Snackbar.make(mView, "Holiday image unselected", Snackbar.LENGTH_LONG).show();
        }
    }

    private void uploadData(String holidayImageLink) {
        if (validator()) {
            title = titleEditText.getText().toString();
            description = descEditText.getText().toString();
            price = Double.valueOf(priceEditText.getText().toString());

            TravelDeals deals = new TravelDeals(title, description, price, holidayImageLink);
                firestoreColRef.add(deals)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                messageTextView.setText("Travel deal added!");
                                mProgressBar.setVisibility(View.INVISIBLE);
                                mProgressBarComplete.setVisibility(View.INVISIBLE);
                                cancelTextView.setVisibility(View.VISIBLE);
                                cancelTextView.setEnabled(true);
                                cancelTextView.setText("finish");
                                directionActivity = 0;
                                //Snackbar.make(mView, "!", Snackbar.LENGTH_LONG).show();
                            } else {
                                messageTextView.setText("Something went wrong");
                                mProgressBar.setVisibility(View.INVISIBLE);
                                mProgressBarComplete.setVisibility(View.INVISIBLE);
                                cancelTextView.setVisibility(View.VISIBLE);
                                cancelTextView.setEnabled(true);
                                cancelTextView.setText("Close");
                                directionActivity = 1;
                            }
                        }
                    });
        }
    }

    private void browseForImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    filepath = data.getData();
//                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
//                        holidayImageView.setImageBitmap(bitmap);
                    Picasso.get().load(filepath).into(holidayImageView);
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean validator() {

        description = descEditText.getText().toString().trim();
        title = titleEditText.getText().toString().trim();

        if (description.isEmpty()) {
//                    progressBar.setVisibility(View.INVISIBLE);
            descEditText.setError("A description required");
            descEditText.requestFocus();
            return false;
        }
        if (title.isEmpty()) {
//                    progressBar.setVisibility(View.INVISIBLE);
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            return false;
        }
        if (priceEditText.getText().toString().trim().isEmpty()) {
//                    progressBar.setVisibility(View.INVISIBLE);
            priceEditText.setError("Price required");
            priceEditText.requestFocus();
            return false;
        }
        return true;
    }

}
