package smooth;

import javax.swing.*;

/**
 * Factory for the appropriate smooth look and feel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class SmoothLookAndFeelFactory {

    protected static final String LNF_METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
    protected static final String LNF_WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    protected static final String SMOOTH_METAL = "smooth.metal.SmoothLookAndFeel";
    protected static final String SMOOTH_WINDOWS = "smooth.windows.SmoothLookAndFeel";

    protected SmoothLookAndFeelFactory() {
    }

    /**
     * Get the class name for the smooth version of the system look and feel (if available).
     *
     * @return a class name for the system look and feel, smoothed version if available.
     */
    public static final String getSystemLookAndFeelClassName() {

        String lnf = UIManager.getSystemLookAndFeelClassName();
        if( lnf == null || lnf.equals( LNF_METAL ) ) {
            lnf = SMOOTH_METAL;

        }
        else if( lnf.equals( LNF_WINDOWS ) ) {
            lnf = SMOOTH_WINDOWS;
        }

        return lnf;
    }

}
