package edu.colorado.phet.reids.admin.jintellitype;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import java.awt.*;

//in a separate package so it can be easily excluded if necessary

//see http://melloware.com/products/jintellitype/quick-start.html
public class JIntellitypeSupport {
    public static void init(final Runnable work, final Runnable play) {
        JIntellitype.getInstance();
        // OPTIONAL: check to see if an instance of this application is already
        // running, use the name of the window title of this JFrame for checking
        if (JIntellitype.checkInstanceAlreadyRunning("MyApp")) {
            throw new RuntimeException("An instance of this application is already running");
        }

// Assign global hotkeys to Windows+A and ALT+SHIFT+B
        JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN + JIntellitype.MOD_SHIFT, (int) 'W');
        JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_WIN + JIntellitype.MOD_SHIFT, (int) 'H');

        //assign this class to be a HotKeyListener
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            public void onHotKey(int identifier) {
                if (identifier == 1) work.run();
                else if (identifier ==2) play.run();
            }
        });
    }

    public static void close() {
        JIntellitype.getInstance().cleanUp();
    }
}
