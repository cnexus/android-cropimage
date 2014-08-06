package com.android.moar.cropper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.android.camera.CropImageIntentBuilder;
import com.android.camera.example.R;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {
    private static int REQUEST_PICTURE = 1;

    private Button button;
    private Button button2;

    private boolean isStatusBarCrop;

    private int mStatusBarHeight;
    private Point mDimensions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mStatusBarHeight = getStatusBarHeight();
        mDimensions = getResolution();

        int width = mDimensions.x;
        int height = isStatusBarCrop ? mStatusBarHeight : mDimensions.y;

        Log.d(com.android.camera.CropImage.TAG, "Using dimensions: " + width + "x" + height);

        if (requestCode == REQUEST_PICTURE && resultCode == RESULT_OK) {
            File output = new File("/sdcard/moar/" + (isStatusBarCrop ? "statusbar.png" : "pulldown.png"));
            new File("/sdcard/moar").mkdir();

            Uri uri = Uri.fromFile(output);

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(width, height, uri);
            cropImage.setSourceImage(data.getData()).setOutputFormat("PNG");

            startActivity(cropImage.getIntent(this));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(button))
            isStatusBarCrop = true;
        else if(view.equals(button2))
            isStatusBarCrop = false;

        startActivityForResult(MediaStoreUtils.getPickImageIntent(this), REQUEST_PICTURE);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = getResources().getDimensionPixelSize(resourceId);

        return result;
    }

    private Point getResolution(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }
}
