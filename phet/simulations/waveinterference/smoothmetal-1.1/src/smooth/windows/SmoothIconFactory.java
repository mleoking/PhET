package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsIconFactory;


/**
 * Smooth Icon Factory. These are a bit more difficult to handle, because
 * you can't subclass the factory methods in the MetalIconFactory. Some
 * genius thought they should be made private. Clever thinking dude!
 * This means we're duplicating some code here, which isn't that bad
 * since we want to draw stuff differently anyway.
 */
public class SmoothIconFactory extends WindowsIconFactory {
    // Don't do anything here

}
