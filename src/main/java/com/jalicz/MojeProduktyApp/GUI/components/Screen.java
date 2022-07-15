package com.jalicz.MojeProduktyApp.GUI.components;

import java.awt.*;

public class Screen {

    private static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public static final int
            width = screen.width,
            height = screen.height;

    //range 0.0 - 1,0
    public static int ofWidth(double d) {
        return (int) (width * d);
    }

    //range 0.0 - 1,0
    public static int ofHeight(double d) {
        return (int) (height * d);
    }
}
