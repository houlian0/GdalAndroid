package com.gisinfo.android.shp.test;

import com.gisinfo.android.core.base.AppData;
import com.gisinfo.android.lib.base.BaseApplication;

/**
 * @author Asen
 * @version v2.0
 * @email houlian@gisinfo.com
 * @date 2017/10/13 11:03
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppData.APP_PROJECT = "gisinfo_shp";
    }
}
