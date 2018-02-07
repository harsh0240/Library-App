package com.example.user.librarytrial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class BarCodeActivity extends Activity implements ZBarScannerView.ResultHandler
{
    private ZBarScannerView mScannerView;

    public static final int PERMISSION_REQUEST_CAMERA = 1;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        changeStatusBarColor();

        if (!haveCameraPermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private boolean haveCameraPermission()
    {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (permissions.length == 0 || grantResults.length == 0)
            return;

        switch (requestCode)
        {
            case PERMISSION_REQUEST_CAMERA:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mScannerView.setAutoFocus(true);
                    startCamera();
                }
                else
                {
                    finish();
                }
            }
            break;
        }
    }

    public void startCamera()
    {
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(true);
        mScannerView.startCamera();
    }

    public void stopCamera()
    {
        mScannerView.stopCamera();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mScannerView.setAutoFocus(true);
        startCamera();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        stopCamera();
    }

    @Override
    public void handleResult(Result rawResult)
    {
        String LibraryID = rawResult.getContents();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String libId = LoginActivity.LibraryID;
        final String emailAddress = LoginActivity.EmailAddress;
        final String password = LoginActivity.Password;

        if(LibraryID.equals(libId))
        {
            auth.createUserWithEmailAndPassword(emailAddress, password)
                    .addOnCompleteListener(BarCodeActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(BarCodeActivity.this, "Account Created" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            LoginActivity.rprogressBar.setVisibility(View.GONE);

                            if (!task.isSuccessful()) {
                                startActivity(new Intent(BarCodeActivity.this, LoginActivity.class));
                                Toast.makeText(BarCodeActivity.this, "Authentication failed." +"\n"+ task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                generateUser(libId, emailAddress);
                                startActivity(new Intent(BarCodeActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "User ID Card Authentication FAILED!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BarCodeActivity.this, LoginActivity.class));
        }
    }

    public void generateUser(String username, String email)
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference();
        User user = new User(username, email);
        users.child("users").child(username).setValue(user);
    }
}

