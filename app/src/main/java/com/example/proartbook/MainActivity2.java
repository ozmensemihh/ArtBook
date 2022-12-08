package com.example.proartbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    ImageView imageView;
    EditText edt_name;
    Button btn_save,btn_delete,btn_update;
    Bitmap selectedImage;
    String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);
        edt_name = findViewById(R.id.edt_name);
        btn_save = findViewById(R.id.btn_save);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);


        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if(info.matches("new")){
            Bitmap background = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_baseline_image_search_24);
            imageView.setImageBitmap(background);
            edt_name.setText("");
            btn_save.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.INVISIBLE);
            btn_update.setVisibility(View.INVISIBLE);

        }else{
            String name = intent.getStringExtra("name");
            edt_name.setText(name);
            firstName = name;
            int position = intent.getIntExtra("position",0);
            imageView.setImageBitmap(MainActivity.artsImage.get(position));


            btn_save.setVisibility(View.INVISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);

        }

    }

    public void saveRecord(View view){

        String artName = edt_name.getText().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte [] bytes = outputStream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ArtContentProvider.NAME,artName);
        values.put(ArtContentProvider.IMAGE,bytes);

        getContentResolver().insert(ArtContentProvider.CONTENT_URI,values);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }

    public void deleteRecord(View view){

        getContentResolver().delete(ArtContentProvider.CONTENT_URI,"name=?",new String[]{edt_name.getText().toString()});
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }

    public void updateRecord(View view){

        String artName = edt_name.getText().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap(); // imageview  içindeki resmi bitmap olarak alıyor.
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte [] bytes = outputStream.toByteArray();


        ContentValues values = new ContentValues();
        values.put(ArtContentProvider.NAME,artName);
        values.put(ArtContentProvider.IMAGE,bytes);
        getContentResolver().update(ArtContentProvider.CONTENT_URI,values,"name=?",new String[]{firstName});

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);




    }

    public void selectImage(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            Uri image = data.getData();

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}