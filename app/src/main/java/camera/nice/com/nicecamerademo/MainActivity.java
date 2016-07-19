package camera.nice.com.nicecamerademo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import camera.nice.com.nicecamerademo.before.ActivityCapture;

public class MainActivity extends AppCompatActivity {


    private ImageView image;
    public static final String kPhotoPath = "photo_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.nice_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, CameraActivity.kCameraCode);
            }
        });

        findViewById(R.id.before_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityCapture.class);
                startActivityForResult(intent, ActivityCapture.kBeforeCameraCode);
            }
        });

        image = (ImageView) findViewById(R.id.image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraActivity.kCameraCode && resultCode == RESULT_OK) {
            String path = data.getStringExtra(kPhotoPath);
            if (!TextUtils.isEmpty(path)) ;
            image.setImageBitmap(BitmapFactory.decodeFile(path));
        }

        if (requestCode == ActivityCapture.kBeforeCameraCode && resultCode == RESULT_OK) {
            String path = data.getStringExtra(kPhotoPath);
            if (!TextUtils.isEmpty(path)) ;
            image.setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }
}
