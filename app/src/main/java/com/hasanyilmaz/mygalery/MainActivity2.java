package com.hasanyilmaz.mygalery;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hasanyilmaz.mygalery.databinding.ActivityMain2Binding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;

    // bunlari galery e gittimiz zaman neler yapilacak ondan kullaniliyor ama bunlari bir metodla kayit etmemiz lazim
    ActivityResultLauncher<Intent> activityResultLauncher; //bunu galeriye gitmek ve gelmek icin kullaniyoruz
    ActivityResultLauncher<String> permissionLauncher;  // bunu galeriden resim almak icin ve izin istemek icin kullaniyoruz
    Bitmap selectImage;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        kayit();


    }

    public void image(View view){
    // ilk olarak izinleri kontrol ediyoruz
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin isteme mantigini gostermek lazim

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){

                    Snackbar.make(view,"galeriye gitmek icin izin lazim",Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();

                } else{
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    //burda izin vermedi ise izin isteyecegim

                }

            }


            else
            {
                //izin verilmis galeriye gidecegiz ama bu kod sadece galeriye gidiyoruz secme ve vb olaylar aktivitylauncher ile oluyor
                Intent intenttogalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intenttogalery);
            }


        }
        else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin isteme mantigini gostermek lazim

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                    Snackbar.make(view,"galeriye gitmek icin izin lazim",Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();

                } else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //burda izin vermedi ise izin isteyecegim

                }

            }


            else
            {
                //izin verilmis galeriye gidecegiz ama bu kod sadece galeriye gidiyoruz secme ve vb olaylar aktivitylauncher ile oluyor
                Intent intenttogalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intenttogalery);
            }


        }


    }
 // launcer vn ne yapacagini burda tanimlamayi burda ayri olarak yapmaliyiz
    private void kayit(){

        //galeriye gitmek icin launcher kullaniyoruz
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //burda kullanici galeriye gitti ve ordan result ok yani bir sey secti anlamina geliyor

                if (result.getResultCode()==RESULT_OK){
                    //galeriye gittim ve ordan aldigim data
                    Intent intentresult=result.getData();
                    if (intentresult !=null){
                        //burda url aliyoruz yani resim nerede
                        Uri imagedate =intentresult.getData();
                        try {
                            //resimi bitmape cevirmeliyim
                          selectImage=MediaStore.Images.Media.getBitmap(MainActivity2.this.getContentResolver(),imagedate);
                          binding.imageView.setImageBitmap(selectImage);


                        }catch (Exception e){
                            e.printStackTrace();

                        }

                    }

                }
            }
        });
   // once izin islemini yapiyoruz
   permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
       @Override
       public void onActivityResult(Boolean result) {
         if (result==true){
             //izin verildi
             Intent intenttogalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               activityResultLauncher.launch(intenttogalery);
         }
         else{
             //izin verilmedi
             Toast.makeText(MainActivity2.this, "izne ihtiyacim var", Toast.LENGTH_SHORT).show();
         }
       }
   });


    }

    public void kaydet(){
  // kaydetmeden once resmimizi boyutunu ayarlamimiz lazim daha sonra onu veri haline getirmemiz gerekli
        String isim=binding.name1.getText().toString();
        String yas=binding.year1.getText().toString();
        Bitmap kucultulmusiamge=kucult(selectImage,300);
        // burda bitmapi veri ye cevirecegiz
        ByteArrayOutputStream veri=new ByteArrayOutputStream();
        kucultulmusiamge.compress(Bitmap.CompressFormat.PNG,50,veri);
        byte[] bytes= veri.toByteArray();

        try {
            sqLiteDatabase=this.openOrCreateDatabase("galery",MODE_PRIVATE,null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS galery(id INTEGER PRIMARY KEY, isim VARCAHER,yas VARCHAR, selectImage BLOB)");
          String  sqLitedata="INSERT INTO galery (isim,yas,selectImage) VALUES(?,?,?,?)";
          SQLiteStatement sqliteStament= sqLiteDatabase.compileStatement(sqLitedata);
            sqliteStament.bindString(1,isim);
            sqliteStament.bindString(2,yas);
            sqliteStament.bindBlob(3,bytes);

            finish();





        }
        catch (Exception e){
   e.printStackTrace();
        }


    }

    public Bitmap kucult(Bitmap selectImage,int maxi){

        int width= selectImage.getWidth();
        int heigth=selectImage.getWidth();

        float oran=(float) width/(float) heigth;
        if (oran>1)
        {
            width=maxi;
            heigth=(int)(width/oran);
        }
        else
        {
         heigth=maxi;
         width=(int)(heigth*oran);
        }
        return selectImage.createScaledBitmap(selectImage,width,heigth,true);
    }

}