package com.example.drawingfun;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener{

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, cameraBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private static int TAKE_PICTURE = 1;
    private Bitmap bitMap;
    private ImageView image, img;
    private final static int MEDIA_TYPE_IMAGE = 2;

    private static final String TAG = "Main Activity";

    protected Uri mMediaUri;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);

        // email
        String email = user.get(SessionManager.KEY_EMAIL);

        /*
        // displaying user data
        lblName.setText(Html.fromHtml("Name: <b>" + name + "</b>"));
        lblEmail.setText(Html.fromHtml("Email: <b>" + email + "</b>"));


        /**
         * Logout button click event
         *
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });
        */

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawView = (DrawingView)findViewById(R.id.drawing);
        drawView.setOnClickListener(this);

        image = (ImageView)findViewById(R.id.image);
        image.setOnClickListener(this);


        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        cameraBtn = (ImageButton)findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (itemId == R.id.action_settings) {
            return true;
        }
        if (itemId == R.id.action_logout){
            session.logoutUser();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void paintClicked(View view){


        //use chosen color
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());

        // check that the user has clicked a paint color that is not the currently selected one
        if(view != currPaint){
            //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton)view;
        }
    }


    @Override
    public void onClick(View view){

        //respond to clicks
        drawView.setErase(true);


        if(view.getId() == R.id.draw_btn){
            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        else if(view.getId() == R.id.erase_btn){
            //switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        else if(view.getId() == R.id.new_btn){
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        else if(view.getId() == R.id.save_btn)
        {
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {

                Bitmap bitmap1 = ((BitmapDrawable) image.getDrawable()).getBitmap();
                //Bitmap bitmap2 = ((BitmapDrawable)drawView.getBackground()).getBitmap(); // crashed here!!!
                Bitmap bitmap2 = getBitmapFromView(drawView);

                Bitmap scaledBitmap1 = Bitmap.createScaledBitmap(bitmap1, 870, 700, true);
                Bitmap scaledBitmap2 = Bitmap.createScaledBitmap(bitmap2, 870, 700, true);

                Bitmap myBitmap = mergeImages(scaledBitmap1, scaledBitmap2);

                saveMyBitmap(myBitmap);
            }

                //原代码
                //save drawing
                    //}
                  //  else{
                  //      Toast unsavedToast = Toast.makeText(getApplicationContext(),
                   //             "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    //    unsavedToast.show();
                    //}

                //drawView.destroyDrawingCache();

              //  }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
        else if(view.getId() == R.id.camera_btn)
        {
            AlertDialog.Builder photoDialog = new AlertDialog.Builder(this);
            photoDialog.setTitle("Camera");
            photoDialog.setMessage("Where ?");

            photoDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, TAKE_PICTURE);
                }
            });// end Positive Button

            photoDialog.setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Does your device have a camera?
                    if (hasCamera()) {
                        // create intent with ACTION_IMAGE_CAPTURE action
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // start camera activity
                        startActivityForResult(intent, TAKE_PICTURE);
                    }
                    else {
                        messageCamera();
                    }
                }
            }); // end Negative Button
            photoDialog.show();
        }// end else if for camera button
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            //Log.d(TAG, "was == RESULT_OK");
            // add it to the Gallery
            //==================================
            //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //mediaScanIntent.setData(mMediaUri);
            //sendBroadcast(mediaScanIntent);
            //==================================
            bitMap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitMap);
        }
        //else {
            //Log.d(TAG, "was != RESULT_OK");
        //}
    }

    // method to check you have a Camera
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void messageCamera(){
        AlertDialog.Builder cameraDialog = new AlertDialog.Builder(this);
        cameraDialog.setTitle("No camera");
        cameraDialog.setMessage("Your phone do not have a Camera!!");
        cameraDialog.setPositiveButton("sure", null);
        cameraDialog.show();
    }

    /*
        Helper method
        MergeImages
     */
    public static Bitmap mergeImages(Bitmap bottomImage, Bitmap topImage) {
        try{
            int maxWidth = (bottomImage.getWidth() > topImage.getWidth() ? bottomImage.getWidth() : topImage.getWidth());
            int maxHeight = (bottomImage.getHeight() > topImage.getHeight() ? bottomImage.getHeight() : topImage.getHeight());
            final Bitmap bmOverlay = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bmOverlay);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);

            canvas.drawBitmap(bottomImage, 0, 0, paint);
            canvas.drawBitmap(topImage, 0, 0, paint);

            //Log.d(TAG, "mergeImages method worked");

            return bmOverlay;
        }
        catch (Exception e){
            //Log.d(TAG, "mergeImages method exception caught", e);
            return null;
        }

    }

    public void saveMyBitmap(Bitmap mBitmap){
        FileOutputStream out = null;

        // 1. Get the external storage directory
        String appName = MainActivity.this.getString(R.string.app_name);
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                appName);

        // 2. Create our subdirectory
        if (! mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdir()){
                //Log.e(TAG, "Failed to create directory");
                //return null;
            }
        }

        // 3. Create a file name
        // 4. Create the file
        File mediaFile;
        Date now = new Date();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(now);
        String path = mediaStorageDir.getPath() + File.separator;
        mediaFile = new File(path + "IMG_" + timestamp + ".jpg");

        //Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
        try {
            out = new FileOutputStream(mediaFile);
            if(mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)){
                //Log.d(TAG, "isSave is true line 385");
                Toast.makeText(getApplicationContext(), "Drawing saved to Gallery!", Toast.LENGTH_SHORT).show();
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

}
