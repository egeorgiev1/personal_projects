package com.group17.login.interfaces;

public interface ILoginView {
    public void showLoadingDialog(String message);
    public void hideLoadingDialog();
//    public void showForgotPasswordDialog();
//    public void hideForgotPasswordDialog();
    public void showError(String error);
    public void openSetupActivity();
    public void openHomeActivity();
}
