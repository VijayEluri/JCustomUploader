package uploader.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public final class Util {
    private Util() {}
    private static final Util singleton = new Util();

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static final ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = singleton.getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Configures the button's UI to a standard scheme used in this project. */
    public static final void prepButtonUI(JButton b, ImageIcon pressed, ImageIcon hover) {
        b.setPressedIcon(pressed);
        b.setRolloverIcon(hover);
        b.setFocusPainted(false);
        b.setMargin(new Insets(1,2,1,2));
        b.setForeground(Color.BLUE);

        // disable content area fill and border painting to make buttons look more like links
        //b.setContentAreaFilled(false);
        //b.setBorderPainted(false);
    }

    /** Sets c's min, max, and preferred sizes to d. */
    public static final void setSize(Component c, Dimension d) {
        c.setMinimumSize(d);
        c.setPreferredSize(d);
        c.setMaximumSize(d);
    }
}