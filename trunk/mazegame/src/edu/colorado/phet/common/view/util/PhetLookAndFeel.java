/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * PhetLookAndFeel manages the Look and Feel for a PhetApplication.
 * It provides convenience methods for setting background color, fonts, etc.
 * <p/>
 * Sample subclass:
 * <code>
 * class SamplePhetLookAndFeel extends PhetLookAndFeel {
 * public TestPhetLookAndFeelExample() {
 * setBackgroundColor( Color.blue );
 * setForegroundColor( Color.white );
 * setFont( new Font( "Lucida Sans", Font.BOLD, 24 ) );
 * }
 * }
 * </code>
 * Sample usage:
 * <code>
 * phetApplication.setPhetLookAndFeel(new SamplePhetLookAndFeel());
 * </code>
 * * <p/>
 *
 * @author ?
 * @version $Revision$
 */
public class PhetLookAndFeel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // These are the types (in alphabetical order) that will have their UIDefaults uniformly modified.
    private static final String[] types = new String[]{
            "Button", "CheckBox", "CheckBoxMenuItem", "ComboBox", "Dialog",
            "Label", "Menu", "MenuBar", "MenuItem",
            "OptionPane", "Panel",
            "RadioButton", "RadioButtonMenuItem",
            "Slider", "Spinner",
            "TabbedPane", "TextArea", "TextField", "TextPane",
            "ScrollBar", "Viewport"
    };

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Font font;
    private Font titledBorderFont;
    private Font tabFont;
    private Color foregroundColor;
    private Color backgroundColor;
    private Color textFieldBackgroundColor;
    private Insets insets;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public PhetLookAndFeel() {
        setTabFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        //other defaults go here...
    }

    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------

    public Font getFont() {
        return font;
    }

    public void setFont( Font font ) {
        this.font = font;
    }

    public Font getTabFont() {
        return tabFont;
    }

    public void setTabFont( Font tabFont ) {
        this.tabFont = tabFont;
    }

    public Font getTitledBorderFont() {
        return titledBorderFont;
    }

    public void setTitledBorderFont( Font borderFont ) {
        this.titledBorderFont = borderFont;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor( Color foregroundColor ) {
        this.foregroundColor = foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor( Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
    }

    public Color getTextFieldBackgroundColor() {
        return textFieldBackgroundColor;
    }

    public void setTextFieldBackgroundColor( Color textFieldBackgroundColor ) {
        this.textFieldBackgroundColor = textFieldBackgroundColor;
    }

    public Insets getInsets() {
        return insets;
    }

    public void setInsets( Insets insets ) {
        this.insets = insets;
    }

    //----------------------------------------------------------------------------
    // UIDefaults modification
    //----------------------------------------------------------------------------

    /**
     * Applies this PhetLookAndFeel, effectively installing the resources it
     * describes in the UIDefaults database.
     *
     * @deprecated use updateDefaults()
     */
    public void apply() {
        updateDefaults();
    }

    /**
     * Adds the look and feel values specified in this look and feel into the UIManager's defaults.
     */
    public void updateDefaults() {
        UIDefaults defaults = UIManager.getDefaults();
        putDefaults( defaults );
    }

    /**
     * Constructs the UIDefaults that correspond to this PhetLookAndFeel,
     * and installs them in the UIDefaults database.
     */
    public void putDefaults( UIDefaults uiDefaults ) {
        Object[] keyValuePairs = constructDefaults();
        uiDefaults.putDefaults( keyValuePairs );
    }

    /**
     * Creates an array of key/value pairs that describes the desired UIDefaults
     * for this PhetLookAndFeel.
     *
     * @return an array of key/value pairs
     */
    private Object[] constructDefaults() {

        // UI resources
        FontUIResource fontResource = null;
        FontUIResource titledBorderFontResource = null;
        ColorUIResource backgroundResource = null;
        ColorUIResource foregroundResource = null;
        ColorUIResource textFieldBackgroundResource = null;
        InsetsUIResource insetsResource = null;
        ColorUIResource buttonBackgroundResource = null;

        // Construct UI resources
        if( font != null ) {
            fontResource = new FontUIResource( font );
        }
        if( titledBorderFont != null ) {
            titledBorderFontResource = new FontUIResource( titledBorderFont );
        }
        if( backgroundColor != null ) {
            backgroundResource = new ColorUIResource( backgroundColor );
        }
        if( foregroundColor != null ) {
            foregroundResource = new ColorUIResource( foregroundColor );
        }
        if( textFieldBackgroundColor != null ) {
            textFieldBackgroundResource = new ColorUIResource( textFieldBackgroundColor );
        }
        if( insets != null ) {
            insetsResource = new InsetsUIResource( insets.top, insets.left, insets.bottom, insets.right );
        }

        // Uniformly modify the resources for each of the types in the "types" list.
        ArrayList keyValuePairs = new ArrayList();
        for( int i = 0; i < types.length; i++ ) {
            String type = types[i];

            if( fontResource != null ) {
                add( keyValuePairs, type, "font", fontResource );
            }
            if( foregroundResource != null ) {
                add( keyValuePairs, type, "foreground", foregroundResource );
            }
            if( backgroundResource != null ) {
                add( keyValuePairs, type, "background", backgroundResource );
            }
            if( insetsResource != null ) {
                add( keyValuePairs, type, "margin", insetsResource );
            }
        }

        // These types require some special modifications.
        if( titledBorderFontResource != null ) {
            add( keyValuePairs, "TitledBorder", "font", titledBorderFontResource );
        }
        if( textFieldBackgroundResource != null ) {
            add( keyValuePairs, "TextField", "background", textFieldBackgroundResource );
        }

        if( buttonBackgroundResource != null ) {
            add( keyValuePairs, "Button", "background", buttonBackgroundResource );
        }
        if( tabFont != null ) {
            add( keyValuePairs, "TabbedPane", "font", new FontUIResource( tabFont ) );
        }

        return keyValuePairs.toArray();
    }

    /*
     * Adds a UIDefaults key/value pair to an array.
     * 
     * @param array
     * @param type
     * @param property
     * @param value
     */
    private void add( ArrayList array, String type, String property, Object value ) {
        array.add( type + "." + property ); // key
        array.add( value );
    }

    public void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel( getLookAndFeelClassName() );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        updateDefaults();
        refreshApp();
    }

    /**
     * Makes sure the component tree UI of all frames are updated.
     * Taken from http://mindprod.com/jgloss/laf.html
     */
    private void refreshApp() {
// refreshing the Look and Feel of the entire app
        Frame frames[] = Frame.getFrames();

// refresh all Frames in the app
        for( int i = 0; i < frames.length; i++ ) {
            SwingUtilities.updateComponentTreeUI( frames[i] );
            Window windows[] = frames[i].getOwnedWindows();

            // refresh all windows and dialogs of the frame
            for( int j = 0; j < windows.length; j++ ) {
                SwingUtilities.updateComponentTreeUI( windows[j] );
            }
        }
// It should not be necessary to revalidate or repaint on top of that.
    }

    /**
     * Sets the look and feel based on the operating system.
     *
     * @deprecated use initLookAndFeel
     */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel( new PhetLookAndFeel().getLookAndFeelClassName() );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Determines the look and feel class name that will be used by this PhetLookAndFeel.
     * The default behavior is to use the native look and feel for the platform.
     *
     * @return the class name for the look and feel.
     */
    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }

    /**
     * This method can be called in an override of getLookAndFeelClassName in case the simulation needs Ocean instead of Windows L&F
     * <p/>
     * The behavior is:
     * >> - Aqua on Mac
     * >> - Windows LAF for Window JDK 1.4.2
     * >> - default LAF (Ocean) for Windows with JDK 1.5
     * >> - default LAF for all other cases (eg, Linux)
     *
     * @return the class name for the look and feel.
     */
    protected String getLookAndFeelClassNamePreferOceanToWindows() {
        String javaVersion = System.getProperty( "java.version" );
        boolean oldJava = javaVersion.toLowerCase().startsWith( "1.4" ) || javaVersion.startsWith( "1.3" );
        String lafClassName = null;
        if( PhetUtilities.getOperatingSystem() == PhetUtilities.OS_WINDOWS ) {
            if( oldJava ) {
                lafClassName = WindowsLookAndFeel.class.getName();
            }
            else {
                lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
            }
        }
        else {
            lafClassName = UIManager.getSystemLookAndFeelClassName();
        }
        return lafClassName;
    }

    /**
     * Gets the font size that corresponds to the screen size.
     * Our minimum supported resolution for PhET simulations is 1024x768.
     * For that resolution and higher, we simply use the default font size.
     * For resolutions of 800x600 or lower, we scale the default font size by 800/1024.
     *
     * @return the font size
     * @deprecated use the instance based methods
     */
    public static int getFontSizeForScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JPanel panel = new JPanel();
        int fontSize = panel.getFont().getSize();
        if( screenSize.width <= 800 ) {
            fontSize = (int)( fontSize * ( 800.0 / 1024 ) );
        }
        return fontSize;
    }

    /**
     * Debugging routine that prints the UIDefault database key/value pairs.
     * The output is sorted lexographically by key.
     */
    public static void printUIDefaults() {

        // Get the currently installed look and feel
        UIDefaults uidefs = UIManager.getLookAndFeelDefaults();

        // Retrieve the keys. We can't use an iterator since the map
        // may be modified during the iteration. So retrieve all keys at once. 
        String[] keySet = (String[])uidefs.keySet().toArray( new String[0] );

        // Sort the keys.
        List keys = Arrays.asList( keySet );
        Collections.sort( keys );

        // Print out each key/value pair.
        for( int i = 0; i < keys.size(); i++ ) {
            Object key = keys.get( i );
            Object value = uidefs.get( key );
            System.out.println( key + ": " + value );
        }
    }
}