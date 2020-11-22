package com.example.android.filter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.android.filter.utility.Helper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;

public class ControlActivity extends AppCompatActivity {


    String filename = "APHOTO";
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    Toolbar mControlToolbar;
    ImageView mTickImageView;
    ImageView mCenterImageView;
    Target mSmallTarget=new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Bitmap workingBitmap=Bitmap.createBitmap(bitmap);
                Bitmap mutableBitmap=workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
                Filter myFilter = new Filter();
                myFilter.addSubFilter(new BrightnessSubFilter(90));
                Bitmap outputImage = myFilter.processFilter(mutableBitmap);
                Helper.writeDataIntoExternalStorage(ControlActivity.this,filename,outputImage);
                Picasso.with(ControlActivity.this).load(Helper.getFileFromExternalStorage(ControlActivity.this,filename)).fit().centerInside().into(mFirstFilterPreviewImageView);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

    final static int PICK_IMAGE = 2;
    final static int MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION=3;
    private static final String TAG=ControlActivity.class.getSimpleName();
    ImageView mFirstFilterPreviewImageView;
    ImageView mSecondFilterPreviewImageView;
    ImageView mThirdFilterPreviewImageView;
    ImageView mFourthFilterPreviewImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        mControlToolbar=(Toolbar) findViewById(R.id.toolbar);
        mCenterImageView=findViewById(R.id.centerImageView);
        mControlToolbar.setTitle(getString(R.string.app_name));
        mControlToolbar.setNavigationIcon(R.drawable.icon);
        mControlToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        mFirstFilterPreviewImageView=(ImageView) findViewById(R.id.imageView4);
        mSecondFilterPreviewImageView=(ImageView) findViewById(R.id.imageView5);
        mThirdFilterPreviewImageView=(ImageView) findViewById(R.id.imageView6) ;
        mFourthFilterPreviewImageView=(ImageView)findViewById(R.id.imageView7);

        mTickImageView = (ImageView) findViewById(R.id.imageView3);
        mTickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControlActivity.this,ImagePreviewActivity.class);
                startActivity(intent);
            }

        });
        mCenterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermissions();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION:
                if(grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    new MaterialDialog.Builder(ControlActivity.this).title(R.string.permission_title)
                            .content(R.string.Permission_content)
                            .negativeText(R.string.Permission_cancel)
                            .positiveText(R.string.Permission_agree_settings)
                            .canceledOnTouchOutside(true)
                            .show();
                }
                else {
                    Log.d(TAG, "Permission denied");
                }


        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri=data.getData();
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mCenterImageView);

            Picasso.with(ControlActivity.this).load(R.drawable.center_image).fit().into(mSmallTarget);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mFirstFilterPreviewImageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mSecondFilterPreviewImageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mThirdFilterPreviewImageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mFourthFilterPreviewImageView);



        }
    }
    public void requestStoragePermissions(){
        if (ContextCompat.checkSelfPermission(ControlActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(ControlActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                new MaterialDialog.Builder(ControlActivity.this).title(R.string.permission_title)
                        .content(R.string.Permission_content)
                        .negativeText(R.string.Permission_cancel)
                        .positiveText(R.string.Permission_agree_settings)
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                            }
                        })
                        .show();
            } else{
                ActivityCompat.requestPermissions(ControlActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION);
            }
            return;
        }

    }

}
