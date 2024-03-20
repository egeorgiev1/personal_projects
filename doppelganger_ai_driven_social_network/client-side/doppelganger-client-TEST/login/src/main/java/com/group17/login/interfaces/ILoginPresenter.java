package com.group17.login.interfaces;

public interface ILoginPresenter {
    public void attach(ILoginView loginView);
    public void detach();
    public void login(String email, String password);
    //public void recoverPassword(String email);
}
