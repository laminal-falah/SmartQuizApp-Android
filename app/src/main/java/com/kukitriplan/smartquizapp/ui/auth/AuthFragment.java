package com.kukitriplan.smartquizapp.ui.auth;

import android.view.View;

public interface AuthFragment {
    void fragmentLogin();
    void fragemntRegister();
    void fragmentForget();

    void login(View view);
    void register(View view);
    void forget(View view);
}
