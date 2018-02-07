package com.example.user.librarytrial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean isSigninScreen = true;
    public static String LibraryID;
    public static String EmailAddress;
    public static String Password;
    private TextView tvSignupInvoker;
    private LinearLayout llSignup;
    private TextView tvSigninInvoker;
    private LinearLayout llSignin;
    private Button btnSignup;
    private EditText linputEmail,rinputEmail;
    private Button btnSignin;
    private EditText linputPassword, remailAddress,rinputPassword, crinputPassword;
    private Button btnResetPassword;
    public static ProgressBar lprogressBar,rprogressBar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);


        llSignin = (LinearLayout) findViewById(R.id.llSignin);
        llSignin.setOnClickListener(this);
        llSignup =(LinearLayout)findViewById(R.id.llSignup);
        llSignup.setOnClickListener(this);

        tvSignupInvoker = (TextView) findViewById(R.id.tvSignupInvoker);
        tvSigninInvoker = (TextView) findViewById(R.id.tvSigninInvoker);

        btnSignup= (Button) findViewById(R.id.btnRegister);
        btnSignin= (Button) findViewById(R.id.btnLogin);


        linputEmail = (EditText) findViewById(R.id.lidsi);
        rinputEmail= (EditText) findViewById(R.id.lidr);
        linputPassword = (EditText) findViewById(R.id.sipassword);
        lprogressBar = (ProgressBar) findViewById(R.id.siprogressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        if(isSigninScreen)
        {
            auth = FirebaseAuth.getInstance();

            btnResetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                }
            });

            btnSignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String username = linputEmail.getText().toString();
                    final String password = linputPassword.getText().toString();

                    if (TextUtils.isEmpty(username)) {
                        Toast.makeText(getApplicationContext(), "Enter Library ID!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lprogressBar.setVisibility(View.VISIBLE);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    databaseReference.child("users").child(username)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot!=null) {
                                        User user = dataSnapshot.getValue(User.class);
                                        String emailAddress;
                                        if(user != null)
                                        {
                                            emailAddress = user.getEmail();
                                            auth.signInWithEmailAndPassword(emailAddress, password)
                                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            lprogressBar.setVisibility(View.GONE);
                                                            if (!task.isSuccessful()) {
                                                                // there was an error
                                                                if (password.length() < 6) {
                                                                    linputPassword.setError(getString(R.string.minimum_password));
                                                                } else {
                                                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                                                }
                                                            } else {
                                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            lprogressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "User do not exist!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    lprogressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            });
        }

        tvSignupInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSigninScreen = false;
                showSignupForm();
                auth = FirebaseAuth.getInstance();


                rinputEmail = (EditText) findViewById(R.id.lidr);
                remailAddress = (EditText) findViewById(R.id.remail);
                rinputPassword = (EditText) findViewById(R.id.rpassword);
                rprogressBar = (ProgressBar) findViewById(R.id.rprogressBar);
                crinputPassword = (EditText) findViewById(R.id.crpassword);

                btnSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String libId,emailAddress,password;
                        libId = rinputEmail.getText().toString().trim();
                        LibraryID = libId;
                        emailAddress = remailAddress.getText().toString().trim();
                        EmailAddress = emailAddress;
                        password = rinputPassword.getText().toString().trim();
                        Password = password;
                        final String confPassword = crinputPassword.getText().toString().trim();

                        if (TextUtils.isEmpty(libId)) {
                            Toast.makeText(getApplicationContext(), "Enter Library ID!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(emailAddress)) {
                            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(confPassword)) {
                            Toast.makeText(getApplicationContext(), "Confirm your password!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (password.length() < 6) {
                            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (password.compareTo(confPassword) != 0) {
                            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        rprogressBar.setVisibility(View.VISIBLE);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        databaseReference.child("users").child(LibraryID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            User user = dataSnapshot.getValue(User.class);
                                            if (user != null) {
                                                rprogressBar.setVisibility(View.GONE);
                                                Toast.makeText(LoginActivity.this, "User Already Registered!", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                rprogressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(LoginActivity.this,BarCodeActivity.class));
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        LoginActivity.lprogressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "iudcvk", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });

        tvSigninInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSigninScreen = true;
                showSigninForm();

            }
        });
        showSigninForm();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
                if(isSigninScreen)
                    btnSignup.startAnimation(clockwise);
            }
        });


    }

    private void showSignupForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.15f;
        llSignin.requestLayout();


        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llSignup.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.85f;
        llSignup.requestLayout();

        tvSignupInvoker.setVisibility(View.GONE);
        tvSigninInvoker.setVisibility(View.VISIBLE);
        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_right_to_left);
        llSignup.startAnimation(translate);

        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
        btnSignup.startAnimation(clockwise);

    }


    private void showSigninForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.85f;
        llSignin.requestLayout();


        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llSignup.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.15f;
        llSignup.requestLayout();

        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_to_right);
        llSignin.startAnimation(translate);

        tvSignupInvoker.setVisibility(View.VISIBLE);
        tvSigninInvoker.setVisibility(View.GONE);
        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_left_to_right);
        btnSignin.startAnimation(clockwise);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.llSignin || v.getId() == R.id.llSignup){
            InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

        }

    }



}
