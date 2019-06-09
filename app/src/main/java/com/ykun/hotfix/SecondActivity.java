package com.ykun.hotfix;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class SecondActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void calculate(View view) {
        BugClass bugClass = new BugClass();
        bugClass.startBug();
    }




}
