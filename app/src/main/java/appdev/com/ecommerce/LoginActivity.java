package appdev.com.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import appdev.com.ecommerce.Model.Users;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingbar;

    private String parentDbname = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        loadingbar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
    }

    private void LoginUser() {
        String phone = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your phone number..", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password..", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please wait while we are checking the credentials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            AllowAccessToAccount(phone,password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbname).child(phone).exists()){
                    Users usersData = dataSnapshot.child(parentDbname).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)){
                        if (usersData.getPassword().equals(password)){
                            Toast.makeText(LoginActivity.this, "Logged in successfully..", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();

                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Password is incorrect..", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + " number does not exists..", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
