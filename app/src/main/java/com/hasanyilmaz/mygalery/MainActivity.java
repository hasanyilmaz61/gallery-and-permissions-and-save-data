package com.hasanyilmaz.mygalery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.hasanyilmaz.mygalery.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<galery> galeryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getdata();
        galeryArrayList=new ArrayList<>();

    }

    // burda kaydettigimiz verileri aliyoruz

    private void getdata(){
        try {
            SQLiteDatabase sqLiteDatabase =this.openOrCreateDatabase("galery",MODE_PRIVATE,null);
            Cursor cursor =sqLiteDatabase.rawQuery("SELECT * FROM galery",null);
            int isimid=cursor.getColumnIndex("isim");
            int idid=cursor.getColumnIndex("id");
             while (cursor.moveToNext()){
                 String name=cursor.getString(isimid);
                 int id =cursor.getInt(idid);

                 galery galery=new galery(name,id);
                 galeryArrayList.add(galery);




             }

             cursor.close();

        }catch (Exception e){e.printStackTrace(); }


    }

    //once menuyu ekliyoruz


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //burda baglama islemini yapiyoruz menuyu bagliyoruz

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menuekle,menu);
        return super.onCreateOptionsMenu(menu);
    }

// burda da menuye tiklaninca ne olacak o kodu yaziyoruz


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

         //bunu yapiyoruz cunku birden fazla item bar olabilir daha fazla olursa if else seklinde devam ediyoruz
        if (item.getItemId()==R.id.bar){

            Intent intent=new Intent(this,MainActivity2.class);
            startActivity(intent);
        }



        return super.onOptionsItemSelected(item);
    }
}