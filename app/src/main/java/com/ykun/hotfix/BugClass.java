package com.ykun.hotfix;

import android.widget.Toast;

class BugClass {
    public int startBug() {
//        System.out.println(20 / 0);
        return 20 / 1;
    }
}
