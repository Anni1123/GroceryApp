package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.Seller.MainSellerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;

public class AddProductActivity extends AppCompatActivity {

    ImageButton back;
    ImageView productImage;
    EditText titles,des;
    TextView category,quantity,price,discountprice,discountnote;
    SwitchCompat compat;
    Button add;
    private static final int CAMERA_REQUEST = 200;
    private static final int STORAGE_REQUEST = 500;
    String cameraPermission[];
    String storagePermission[];
    private Uri uri;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private FirebaseAuth firebaseAuth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        back=findViewById(R.id.backbtnuser);
        productImage=findViewById(R.id.productIcon);
        titles=findViewById(R.id.title);
        des=findViewById(R.id.description);
        category=findViewById(R.id.category);
        quantity=findViewById(R.id.quantity);
        price=findViewById(R.id.price);
        discountprice=findViewById(R.id.dprice);
        discountnote=findViewById(R.id.discountednote);
        compat=findViewById(R.id.discount);
        add=findViewById(R.id.addproduct);
        discountprice.setVisibility(View.INVISIBLE);
        discountnote.setVisibility(View.INVISIBLE);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseAuth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setTitle("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        compat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    discountprice.setVisibility(View.VISIBLE);
                    discountnote.setVisibility(View.VISIBLE);
                }
                else {
                    discountprice.setVisibility(View.INVISIBLE);
                    discountnote.setVisibility(View.INVISIBLE);
                }
            }
        });
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private String ptitle,pdesc,pcategory,pquantity,poroginalprice,discountp,discountnot;
    private boolean discountavailable=false;
    private void inputData() {
        ptitle=titles.getText().toString().trim();
        pdesc=des.getText().toString().trim();
        pcategory=category.getText().toString().trim();
        pquantity=quantity.getText().toString().trim();
        poroginalprice=price.getText().toString().trim();

        discountavailable=compat.isChecked();
        if(TextUtils.isEmpty(ptitle)){
            Toast.makeText(AddProductActivity.this,"Title cant be empty", LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(pcategory)){
            Toast.makeText(AddProductActivity.this,"Category cant be empty", LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(poroginalprice)){
            Toast.makeText(AddProductActivity.this,"Price cant be empty", LENGTH_LONG).show();
            return;
        }
        if(discountavailable){
            discountp=discountprice.getText().toString().trim();
            discountnot=discountnote.getText().toString().trim();
            if(TextUtils.isEmpty(discountp)){
                Toast.makeText(AddProductActivity.this,"Price cant be empty", LENGTH_LONG).show();
                return;
            }
        }
        else {
            discountp="0";
            discountnot="";
        }
        addProduct();
    }

    private void addProduct() {

        dialog.setMessage("Adding Product");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
      final  String timestmap=""+System.currentTimeMillis();
        if(uri==null){
            HashMap<String ,Object> hashMap=new HashMap<>();
            hashMap.put("title",ptitle);
            hashMap.put("description",pdesc);
            hashMap.put("productId",timestmap);
            hashMap.put("Category",pcategory);
            hashMap.put("Quantity",pquantity);
            hashMap.put("productIcon","");
            hashMap.put("originalPrice",poroginalprice);
            hashMap.put("discountprice",discountp);
            hashMap.put("discountnote",discountnot);
            hashMap.put("timestamp",timestmap);
            hashMap.put("discountAvailable",""+discountavailable);
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(timestmap).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          dialog.dismiss();
                          Toast.makeText(AddProductActivity.this,"Product Added Successfully", LENGTH_LONG).show();
                          cleardata();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(AddProductActivity.this,""+e.getMessage(), LENGTH_LONG).show();
                }
            });
        }
        else {

            String filepathname="productImage/" + ""+timestmap;
            StorageReference reference= FirebaseStorage.getInstance().getReference(filepathname);
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri=uriTask.getResult();
                    if(uriTask.isSuccessful()){
                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("title",ptitle);
                        hashMap.put("description",pdesc);
                        hashMap.put("productId",timestmap);
                        hashMap.put("Category",pcategory);
                        hashMap.put("Quantity",pquantity);
                        hashMap.put("productIcon",""+downloadUri);
                        hashMap.put("originalPrice",poroginalprice);
                        hashMap.put("discountprice",discountp);
                        hashMap.put("discountnote",discountnot);
                        hashMap.put("timestamp",timestmap);
                        hashMap.put("discountAvailable",""+discountavailable);
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(firebaseAuth.getUid()).child("Products").child(timestmap).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        startActivity(new Intent(AddProductActivity.this, MainSellerActivity.class));
                                        cleardata();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                startActivity(new Intent(AddProductActivity.this,MainSellerActivity.class));
                                finish();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    dialog.dismiss();
                    Toast.makeText(AddProductActivity.this,""+e.getMessage(), LENGTH_LONG).show();
                }
            });
        }
    }

    private void cleardata(){
        titles.setText("");
        productImage.setImageResource(R.drawable.ic_store);
        uri=null;
        des.setText("");
        category.setText("");
        quantity.setText("");
        price.setText("");
        discountprice.setText("");
        discountnote.setText("");
    }
    private void categoryDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String categories=Constants.options[which];
                        category.setText(categories);
                    }
                }).show();
    }

    private void showImagePicDialog() {
        String options[]={ "Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(AddProductActivity.this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }else if(which==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }

                }
            }
        });
        builder.create().show();
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST);
    }
    private Boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){

        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST);
    }
    private void pickFromCamera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Image Description");
        uri=this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent camerIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(camerIntent,IMAGE_PICKCAMERA_REQUEST);
    }
    private void pickFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST);
    }
    private Boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(AddProductActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST:{
                if(grantResults.length>0){
                    boolean camera_accepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(camera_accepted&&writeStorageaccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this,"Please Enable Camera and Storage Permissions",Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST:{
                if(grantResults.length>0){
                    boolean writeStorageaccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorageaccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this,"Please Enable Storage Permissions",Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode==IMAGEPICK_GALLERY_REQUEST){
                uri=data.getData();
                productImage.setImageURI(uri);
            }
            if(requestCode==IMAGE_PICKCAMERA_REQUEST){
                productImage.setImageURI(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
