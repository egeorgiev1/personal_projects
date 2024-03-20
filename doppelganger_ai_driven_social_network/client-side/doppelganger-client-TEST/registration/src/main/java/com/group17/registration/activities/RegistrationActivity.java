package com.group17.registration.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.group17.model.helper.BaseActivity;
import com.group17.model.rest.ErrorHandlerCallback;
import com.group17.registration.R;
import com.group17.registration.R2;
import com.group17.setup.activities.SetupActivity;
import com.group17.setup.activities.SetupActivity_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Response;

@EActivity
public class RegistrationActivity extends BaseActivity {

    @ViewById(R2.id.email_input)
    TextInputEditText emailInput;

    @ViewById(R2.id.password_input)
    TextInputEditText passwordInput;

    @ViewById(R2.id.confirm_password_input)
    TextInputEditText confirmPasswordInput;

//    @ViewById(R2.id.register_button)
//    Button registrationButton;

//    @ViewById(R2.id.privacy_policy_button)
//    TextView privacyPolicyButton;

//    @ViewById(R2.id.terms_and_conditions_button)
//    TextView termsAndConditionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(RegistrationActivity.this, "test", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Click(R2.id.register_button)
    void registerClickHandler(View buttonView) {
        this.showLoadingDialog("Signing Up...");

        emailInput.setText(emailInput.getText().toString().toLowerCase());

        api.register(
                emailInput.getText().toString(),
                passwordInput.getText().toString(),
                confirmPasswordInput.getText().toString()
        ).enqueue(new ErrorHandlerCallback(this) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                super.onResponse(call, response);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegistrationActivity.this);

                if(response.isSuccessful()) {
                    // Add email to the preferences
                    preferences.edit().putString("email", emailInput.getText().toString().toLowerCase()).commit();
                    RegistrationActivity.this.openSetupActivity();
                } else {
                    preferences.edit().remove("email").remove("sessionId").commit();
                    RegistrationActivity.this.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit().remove("email").remove("sessionId").commit();
                RegistrationActivity.this.hideLoadingDialog();
            }
        });
    }

    @Click(R2.id.privacy_policy_button)
    void privacyPolicyClickHandler(View buttonView) {
        this.showMarkdownAlertDialog("Privacy Policy", "privacy_policy.md");
    }


    @Click(R2.id.terms_and_conditions_button)
    void termsAndConditionsClickHandler(View buttonView) {
        this.showMarkdownAlertDialog("Terms and Conditions", "terms_and_conditions.md");
    }

    public void openSetupActivity() {
        Intent intent = new Intent(this, SetupActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
