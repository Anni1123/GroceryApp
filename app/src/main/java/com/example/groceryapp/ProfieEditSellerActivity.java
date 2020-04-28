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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.groceryapp.Seller.MainSellerActivity;
import com.example.groceryapp.Seller.RegisterSellerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

public class ProfieEditSellerActivity extends AppCompatActivity implements LocationListener {

    ImageButton back,gps;
    ImageView profile;
    Button update;
    SwitchCompat compat;
    EditText name, phone, country, state, city, adress,shop,delivery;

    private static final int LOCATION_REQUEST=100;
    private static final int CAMERA_REQUEST = 200;
    private static final int STORAGE_REQUEST = 500;
    String cameraPermission[];
    String storagePermission[];
    private String[] locationpermission;
    private Uri uri;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;

    private FirebaseAuth firebaseAuth;
    ProgressDialog dialog;
    LocationManager manager;
    private double latitude=0.0,longitude=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profie_edit_seller);
        back=findViewById(R.id.backbtnuser);
        profile = findViewById(R.id.profiletvuser);
        name = findViewById(R.id.nameuser);
        shop=findViewById(R.id.shop);
        delivery=findViewById(R.id.delivery);
        gps=findViewById(R.id.gpsbtn);
        phone = findViewById(R.id.phoneuser);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        compat=findViewById(R.id.shopopen);
        adress = findViewById(R.id.addressuser);
        update=findViewById(R.id.update);
        locationpermission=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseAuth=FirebaseAuth.getInstance();
        checkUSer();
        dialog=new ProgressDialog(this);
        dialog.setTitle("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkLocationPermission()){
                    detectlocation();
                }
                else {
                    requestlocationpermission();
                }
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });
    }

    String names,phonenumber,mycountry,mystate,mycity,myadress,mydelivery,myshop;

    private void inputData() {
        names=name.getText().toString().trim();
        phonenumber=phone.getText().toString().trim();
        mycountry=country.getText().toString().trim();
        mystate=state.getText().toString().trim();
        myadress=adress.getText().toString().trim();
        mycity=city.getText().toString().trim();
        mydelivery=delivery.getText().toString().trim();
        myshop=shop.getText().toString().trim();
        if(TextUtils.isEmpty(names)){
            Toast.makeText(ProfieEditSellerActivity.this,"Name Cant be Empty...", LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phonenumber)){
            Toast.makeText(ProfieEditSellerActivity.this,"Phone Number Cant be Empty...", LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mydelivery)){
            Toast.makeText(ProfieEditSellerActivity.this,"Delivery Cant be Empty...", LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(myshop)){
            Toast.makeText(ProfieEditSellerActivity.this,"Shop Cant be Empty...", LENGTH_LONG).show();
            return;
        }
      updateInfo();

    }

    private void updateInfo() {
        dialog.setMessage("Updating Profile...");
        dialog.show();
        if(uri==null){
            HashMap<String ,Object> hashMap=new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("name",""+names);
            hashMap.put("shopName",""+myshop);
            hashMap.put("phone",phonenumber);
            hashMap.put("delivery",mydelivery);
            hashMap.put("country",mycountry);
            hashMap.put("state",mystate);
            hashMap.put("city",mycity);
            hashMap.put("address",myadress);
            hashMap.put("shopOpen","true");
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            startActivity(new Intent(ProfieEditSellerActivity.this, MainSellerActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    dialog.dismiss();
                    startActivity(new Intent(ProfieEditSellerActivity.this,MainSellerActivity.class));
                    finish();
                }
            });
        }
        else
        {
            String filepathname="profileImage/" + ""+firebaseAuth.getUid();
            StorageReference reference= FirebaseStorage.getInstance().getReference(filepathname);
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri=uriTask.getResult();
                    if(uriTask.isSuccessful()){
                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("uid",""+firebaseAuth.getUid());
                        hashMap.put("name",""+names);
                        hashMap.put("shopName",""+myshop);
                        hashMap.put("phone",phonenumber);
                        hashMap.put("delivery",mydelivery);
                        hashMap.put("country",mycountry);
                        hashMap.put("state",mystate);
                        hashMap.put("city",mycity);
                        hashMap.put("address",myadress);
                        hashMap.put("latitude",latitude);
                        hashMap.put("longitude",longitude);
                        hashMap.put("accountType","seller");
                        hashMap.put("online","true");
                        hashMap.put("shopOpen","true");
                        hashMap.put("profileImage",""+downloadUri);
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        startActivity(new Intent(ProfieEditSellerActivity.this,MainSellerActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                startActivity(new Intent(ProfieEditSellerActivity.this,MainSellerActivity.class));
                                finish();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    dialog.dismiss();
                    Toast.makeText(ProfieEditSellerActivity.this,""+e.getMessage(), LENGTH_LONG).show();
                }
            });
        }
    }

    private void checkUSer() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(ProfieEditSellerActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String names=""+dataSnapshot1.child("name").getValue();
                    String accountType=""+dataSnapshot1.child("accountType").getValue();
                    String addresss=""+dataSnapshot1.child("address").getValue();
                    String cityy=""+dataSnapshot1.child("city").getValue();
                    String statee=""+dataSnapshot1.child("state").getValue();
                    String countryy=""+dataSnapshot1.child("country").getValue();
                    String shopNamee=""+dataSnapshot1.child("shopName").getValue();
                    String phone1=""+dataSnapshot1.child("phone").getValue();
                    String deliveryy=""+dataSnapshot1.child("delivery").getValue();
                    String timestamp1=""+dataSnapshot1.child("timestamp").getValue();
                    String onlinee=""+dataSnapshot1.child("online").getValue();
                    String profileI=""+dataSnapshot1.child("profileImage").getValue();
                    String shopOpen=""+dataSnapshot1.child("shopOpen").getValue();
                    name.setText(names );
                    phone.setText(phone1);
                    state.setText(statee);
                    city.setText(cityy);
                    country.setText(countryy);
                    adress.setText(addresss);
                    shop.setText(shopNamee);
                    delivery.setText(deliveryy);
                    if(shopOpen.equals("true")){
                        compat.setChecked(true);
                    }
                    else {
                        compat.setChecked(false);
                    }
                    try{
                        Picasso.get().load(profileI).into(profile);
                    }
                    catch (Exception e){

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showImagePicDialog() {
        String options[]={ "Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(ProfieEditSellerActivity.this);
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
        boolean result= ContextCompat.checkSelfPermission(ProfieEditSellerActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private boolean checkLocationPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestlocationpermission(){
        ActivityCompat.requestPermissions(this,locationpermission,LOCATION_REQUEST);
    }
    private void detectlocation() {

        Toast.makeText(ProfieEditSellerActivity.this,"Please Wait", LENGTH_LONG).show();
        manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }
    private void findAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());
        try {
            addresses=geocoder.getFromLocation(latitude,longitude,1);
            String addresss=addresses.get(0).getAddressLine(0);
            String cityy=addresses.get(0).getLocality();
            String statee=addresses.get(0).getAdminArea();
            String countryy=addresses.get(0).getCountryName();
            country.setText(countryy);
            state.setText(statee);
            city.setText(cityy);
            adress.setText(addresss);
        }
        catch (Exception e){
            Toast.makeText(ProfieEditSellerActivity.this,""+e.getMessage(), LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        findAddress();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(ProfieEditSellerActivity.this,"Please Turn On Gps...", LENGTH_LONG).show();
    }


    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(ProfieEditSellerActivity.this,"Location is Disabled", LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
            {
                if(grantResults.length>0){
                    boolean locationaccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(locationaccepted){
                        detectlocation();
                    }
                    else {
                        Toast.makeText(ProfieEditSellerActivity.this,"LocationPermission Required", LENGTH_LONG).show();
                    }
                }
            }
            break;
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
                profile.setImageURI(uri);
            }
            if(requestCode==IMAGE_PICKCAMERA_REQUEST){
                profile.setImageURI(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
