package com.example.facerecognitionimages;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;




import java.io.FileDescriptor;
import java.io.IOException;


public class RecognitionActivity extends AppCompatActivity {
        CardView galleryCard,cameraCard;
        ImageView imageView;
        Uri image_uri;
        public static final int PERMISSION_CODE = 100;


        //TODO declare face detector


        //TODO declare face recognizer


        //TODO get the image from gallery and display it
        ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            image_uri = result.getData().getData();
                            Bitmap inputImage = uriToBitmap(image_uri);
                            Bitmap rotated = rotateBitmap(inputImage);
                            imageView.setImageBitmap(rotated);
                        }
                    }
                });

        //TODO capture the image using camera and display it
        ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Bitmap inputImage = uriToBitmap(image_uri);
                            Bitmap rotated = rotateBitmap(inputImage);
                            imageView.setImageBitmap(rotated);
                        }
                    }
                });

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            //TODO handling permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                }
            }

            //TODO initialize views
            galleryCard = findViewById(R.id.gallerycard);
            cameraCard = findViewById(R.id.cameracard);
            imageView = findViewById(R.id.imageView2);

            //TODO code for choosing images from gallery
            galleryCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryActivityResultLauncher.launch(galleryIntent);
                }
            });

            //TODO code for capturing images using camera
            cameraCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED){
                            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, PERMISSION_CODE);
                        }
                        else {
                            openCamera();
                        }
                    }

                    else {
                        openCamera();
                    }
                }
            });

            //TODO initialize face detector


            //TODO initialize face recognition model

        }

        //TODO opens camera so that user can capture image
        private void openCamera() {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
            image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
            cameraActivityResultLauncher.launch(cameraIntent);
        }

        //TODO takes URI of the image and returns bitmap
        private Bitmap uriToBitmap(Uri selectedFileUri) {
            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(selectedFileUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

                parcelFileDescriptor.close();
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        //TODO rotate image if image captured on samsung devices
        //TODO Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
        @SuppressLint("Range")
        public Bitmap rotateBitmap(Bitmap input){
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = getContentResolver().query(image_uri, orientationColumn, null, null, null);
            int orientation = -1;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            }
            Log.d("tryOrientation",orientation+"");
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.setRotate(orientation);
            Bitmap cropped = Bitmap.createBitmap(input,0,0, input.getWidth(), input.getHeight(), rotationMatrix, true);
            return cropped;
        }

        //TODO perform face detection


        //TODO perform face recognition


        @Override
        protected void onDestroy() {
            super.onDestroy();

        }
    }