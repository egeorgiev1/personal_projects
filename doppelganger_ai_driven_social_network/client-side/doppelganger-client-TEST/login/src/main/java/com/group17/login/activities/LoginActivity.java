package com.group17.login.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.group17.home.activities.HomeActivity;
import com.group17.home.activities.HomeActivity_;
import com.group17.login.R;
import com.group17.login.R2;
import com.group17.login.interfaces.ILoginPresenter;
import com.group17.login.interfaces.ILoginView;
import com.group17.login.presenters.LoginPresenter;
import com.group17.registration.activities.RegistrationActivity;
import com.group17.registration.activities.RegistrationActivity_;
import com.group17.setup.activities.SetupActivity;
import com.group17.setup.activities.SetupActivity_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import us.feras.mdv.MarkdownView;

@EActivity
public class LoginActivity extends AppCompatActivity implements ILoginView {

    private ILoginPresenter loginPresenter;

    private ProgressDialog loadingDialog;
    //private AlertDialog forgotPasswordDialog;

    @SystemService
    InputMethodManager inputMethodManager;

    @ViewById(R2.id.email_input)
    TextInputEditText emailInput;

    @ViewById(R2.id.password_input)
    TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix layout scaling
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Configuration config = this.getResources().getConfiguration();
        config.densityDpi = 160;
        this.getResources().updateConfiguration(config, displayMetrics);

        setContentView(R.layout.activity_login);

        loginPresenter = new LoginPresenter(); // that's usually dependency injected

        // TODO: fix multiple successive clicks of a button, only a single handling of the click event

        // FOR TESTING PURPOSES:
//        emailInput.setText("emilxp4@gmail.com");
//        passwordInput.setText("12345");
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginPresenter.detach();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginPresenter.attach(this);
    }

    // Hide the keyboard when the user clicks outside the inputs: https://stackoverflow.com/a/19828165
    @FocusChange(R2.id.email_input)
    void emailInputFocusChangeHandler(View emailInputView) {
        // Hide the keyboard when leaving the input view
        inputMethodManager.hideSoftInputFromWindow(emailInputView.getWindowToken(), 0);
    }

    @FocusChange(R2.id.password_input)
    void passwordInputFocusChangeHandler(View passwordInputView) {
        // Hide the keyboard when leaving the input view
        inputMethodManager.hideSoftInputFromWindow(passwordInputView.getWindowToken(), 0);
    }

    @Click(R2.id.login_button)
    void loginClickHandler(View buttonView) {
        // Hide the keyboard when we click the login button
        inputMethodManager.hideSoftInputFromWindow(buttonView.getWindowToken(), 0);

        // Perform the login request
        loginPresenter.login(
                emailInput.getText().toString(),
                passwordInput.getText().toString()
        );
    }

    @Click(R2.id.sign_up_button)
    void signUpClickHandler() {
        openRegistrationActivity();
    }

//    @Click(R2.id.forgot_password_button)
//    void forgoPasswordClickHandler() {
//        showForgotPasswordDialog();
//    }

    public void showLoadingDialog(String message) {
        // Fix layout scaling
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Configuration config = this.getResources().getConfiguration();
        config.densityDpi = 213;
        this.getResources().updateConfiguration(config, displayMetrics);

        this.loadingDialog = ProgressDialog.show(
                LoginActivity.this, "",
                message, true
        );

        // Fix layout scaling
        config.densityDpi = 160;
        this.getResources().updateConfiguration(config, displayMetrics);
    }

    public void hideLoadingDialog(){
        this.loadingDialog.dismiss();
    }

    public void showMarkdownAlertDialog(String title, String assetFileName) {
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View termsOfServiceLayout = inflater.inflate(R.layout.terms_of_service, null);

        MarkdownView markdownView = termsOfServiceLayout.findViewById(R.id.terms_of_service_view);
        markdownView.loadMarkdownFile("file:///android_asset/" + assetFileName);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setView(termsOfServiceLayout);
        alertDialog.setNegativeButton("Close", null);
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

//    public void showForgotPasswordDialog() {
//        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
//        View forgotPasswordLayout = inflater.inflate(R.layout.forgot_password_input, null);
//        final EditText emailInput = (EditText) forgotPasswordLayout.findViewById(R.id.email_input);
//        emailInput.setText(this.emailInput.getText().toString());
//        emailInput.setSelection(emailInput.getText().length());
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
//        alertDialog.setTitle("Enter your email address");
//        alertDialog.setView(forgotPasswordLayout);
//
//        alertDialog.setNegativeButton("Close", null);
//
//        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                loginPresenter.recoverPassword(emailInput.getText().toString());
//            }
//        });
//
//        this.forgotPasswordDialog = alertDialog.create();
//        this.forgotPasswordDialog.show();
//    }

//    public void hideForgotPasswordDialog() {
//        this.forgotPasswordDialog.dismiss();
//    }

    public void openRegistrationActivity() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity_.class);
        startActivity(intent);
    }

    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    // TODO
    @Override
    public void openSetupActivity() {
        Intent intent = new Intent(this, SetupActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // TODO
    @Override
    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
