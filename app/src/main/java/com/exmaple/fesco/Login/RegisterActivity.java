package com.exmaple.fesco.Login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.fesco.Activities.MainActivity;
import com.example.fesco.R;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPhone;
    private EditText inputAddress;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private ScrollView scrollView;


    Bitmap bitmap;
    RequestQueue requestQueue;
    Button SelectImageGallery;
    ImageView profileImageView;

    final static int CAMERA = 2;
    final static int GALLERY = 1;


    String name;
    String email;
    String password;
    String phone;
    String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestPermissions();


        inputFullName = (EditText) findViewById(R.id.username_edit_text_register);
        inputEmail = (EditText) findViewById(R.id.email_edit_text_register);
        inputPassword = (EditText) findViewById(R.id.password_edit_text_register);
        inputPhone= (EditText) findViewById(R.id.phone_edit_text_register);
        inputAddress = (EditText) findViewById(R.id.address_edit_text_register);
        Button btnRegister = (Button) findViewById(R.id.sign_up_btn);
        TextView btnLinkToLogin = findViewById(R.id.LinkToLogin);
        scrollView=findViewById(R.id.scroll_view);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Registering");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                name = inputFullName.getText().toString().trim();
                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                phone=inputPhone.getText().toString().trim();
                address=inputAddress.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    if (password.length() >= 4) {
                        if (isEmailValid(email)) {
                            uploadImage( name, email, password,phone,address);
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter valid Email", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Your password must contain at least 4 characters.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please complete all details!", Toast.LENGTH_LONG).show();
                }
            }
        });


        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

   /*
    private void registerUser(final String username, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    Log.e("anyText",response);
                    JSONObject jObj = new JSONObject(response);

                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("username");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);

                         Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();


                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        //System.out.println(strReq);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    profileImageView.setImageBitmap(getCroppedBitmap(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }else if(requestCode == CAMERA){
            bitmap = (Bitmap) data.getExtras().get("data");
            profileImageView.setImageBitmap(getCroppedBitmap(bitmap));
        }
    }

    private void setCircleProfileImage(String url, ImageView imageView) {
        Glide.with(this)
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.avatar)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(),
                                Bitmap.createScaledBitmap(resource, 100, 100, false));
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void uploadImage(String name, String email, String password,String phone,String address) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ImageInterface.RegisterURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();

        ImageInterface api = retrofit.create(ImageInterface.class);


        pDialog.show();

        Call<String> call = api.getUser(phone,address, name, email, password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                assert response.body() != null;
                Log.i("Response", response.body());
                System.out.println(response);
                pDialog.dismiss();
                //Toast.makeText()
                // Inserting row in users table

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        try {
                            JSONObject reader = new JSONObject(response.body());
                            String error = reader.getString("error");

                            if (error.equals("false")) {
                                // Printing uploading success message coming from server on android app.

                                // Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_LONG).show();
                                Snackbar.make(scrollView, "Registered successfully.You can Login now", Snackbar.LENGTH_INDEFINITE).
                                        setAction("Login", v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class))).show();
                            } else {
                                String errorMessage = reader.getString("error_msg");
                                // Toast.makeText(RegisterActivity.this, "Error happened! Try again\n"+error_msg , Toast.LENGTH_LONG).show();
                                Snackbar.make(scrollView, "Something went wrong\n" + errorMessage, Snackbar.LENGTH_INDEFINITE).
                                        setAction("RETRY", v -> {
                                            //retry register here
                                        }).setBackgroundTint(ContextCompat.getColor(RegisterActivity.this, R.color.red)).show();
                                //  }
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Toast.makeText(RegisterActivity.this, "Registered Successfully!!", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");
                        Toast.makeText(RegisterActivity.this, "Nothing returned", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });
    }


    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {

                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

}