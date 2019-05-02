package task.abhinav;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Info extends AppCompatActivity {

    static String[] deets = {"","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String id = intent.getStringExtra("user_id");


        deets = getContact(this, id);
        this.setTitle(deets[0]);
        TextView t1 = findViewById(R.id.ph);
        t1.setText("Phone : " + deets[1]);
        TextView t2 = findViewById(R.id.email);
        t2.setText("Email : " + getEmail(id));
        TextView t3 = findViewById(R.id.id);
        t3.setText("Contact id : " + deets[2]);



        if(deets[3] != null){
        ImageView cpt = findViewById(R.id.tyu);
        Bitmap my_btmp = BitmapFactory.decodeStream(openDisplayPhoto(Long.parseLong(deets[2])));
        cpt.setImageBitmap(my_btmp);
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + deets[1]));
                startActivity(callIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public static String[] getContact(Context context, String contactId) {
        String[] stuff = {"","","",""};

        String[] whereArgs = new String[] {contactId};

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone._ID + " = ? ", whereArgs, null);

        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    stuff[0] = cursor.getString(cursor
                            .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    stuff[1] = cursor.getString(cursor
                            .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    stuff[2] = cursor.getString(cursor
                            .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                }
            } finally {

                cursor.close();
            }

        }

        return stuff;
    }


    public String getEmail(String contactId){

        String email = "";
        Cursor emailCur = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactId}, null);
        while (emailCur.moveToNext()) {
            email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        return email;}






    public InputStream openDisplayPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

}
