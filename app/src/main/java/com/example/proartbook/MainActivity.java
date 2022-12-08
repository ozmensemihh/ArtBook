package com.example.proartbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity {



    ListView listView ;
    static ArrayList<Bitmap> artsImage;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);

        ArrayList<String> artsName = new ArrayList<>();
        artsImage = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,artsName);
        listView.setAdapter(adapter);
        String URL = "content://com.example.proartbook.ArtContentProvider";
        Uri artUri = Uri.parse(URL);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(artUri,null,null,null,ArtContentProvider.NAME);
        if (cursor != null){

            while (cursor.moveToNext()){

                artsName.add(cursor.getString(cursor.getColumnIndex(ArtContentProvider.NAME)));
                byte [] bytes = cursor.getBlob(cursor.getColumnIndex(ArtContentProvider.IMAGE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                artsImage.add(bitmap);

                adapter.notifyDataSetChanged();

            }

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                intent.putExtra("info","old");
                intent.putExtra("position",i);
                intent.putExtra("name",artsName.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
            intent.putExtra("info","new");
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}