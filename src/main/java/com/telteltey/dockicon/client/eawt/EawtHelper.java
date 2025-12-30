package com.telteltey.dockicon.client.eawt;

import java.awt.Image;
import java.lang.reflect.Method;

public final class EawtHelper {
    private EawtHelper() {
    }

    public static void setDockIcon(Image image) throws Exception {
        Class<?> appClass = Class.forName("com.apple.eawt.Application");
        Method getApplication = appClass.getMethod("getApplication");
        Object application = getApplication.invoke(null);
        Method setDockIconImage = appClass.getMethod("setDockIconImage", Image.class);
        setDockIconImage.invoke(application, image);
    }
}
