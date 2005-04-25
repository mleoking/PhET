/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;

import smooth.basic.SmoothTitledBorder;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/**
 * PhetLookAndFeel describes the UI resources that need to be installed in
 * the UIDefaults database.  It provides methods for describing the values
 * for those resources, as well as methods for updating the UIDefaults database.
 * <p/>
 * Sample usage:
 * <code>
 * //Choose the default look and feel for your system, adding text antialias for windows.
 * //This must be done early in the application so no components get constructed with the wrong UI.
 * PhetLookAndFeel.setLookAndFeel();
 * //Create the usual PhetLookAndFeel (whatever we deem that to be)
 * PhetLookAndFeel lookAndFeel = new PhetLookAndFeel();
 * //customize it here for your own application.
 * lookAndFeel.setBackgroundColor( Color.blue );
 * lookAndFeel.setForegroundColor( Color.red );
 * lookAndFeel.setFont( new Font( "Lucida Sans", Font.BOLD, 20 ) );
 * //Apply the total look and feel (the usual PhetLookAndFeel + your changes)
 * // to your system defaults.
 * lookAndFeel.apply();
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
    
    // Operating Systems
    public static final int OS_WINDOWS = 0;
    public static final int OS_MACINTOSH = 1;
    public static final int OS_OTHER = 2;
    
    // These are the types (in alphabetical order) that will have their UIDefaults uniformly modified.
    private static final String[] types = new String[]{
        "Button", "CheckBox", "CheckBoxMenuItem", "ComboBox", "Dialog",
        "Label", "Menu", "MenuBar", "MenuItem", 
        "OptionPane", "Panel", 
        "RadioButton", "RadioButtonMenuItem", 
        "Slider", "Spinner",
        "TabbedPane", "TextArea", "TextField","TextPane"  
    };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int os; // the operating system
    private Font font;
    private Font titledBorderFont;
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
        os = getOperatingSystem();
        int fontSize = getFontSizeForScreen();
        font = new Font( "Lucida Sans", Font.PLAIN, fontSize );
        titledBorderFont = new Font( "Lucida Sans", Font.PLAIN, fontSize );
        foregroundColor = Color.BLACK;
        backgroundColor = new Color( 200, 240, 200 );  // light green
        textFieldBackgroundColor = Color.WHITE;
        insets = new Insets( 2, 2, 2, 2 );
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
     */
    public void apply() {
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
        FontUIResource fontResource = new FontUIResource( font );
        FontUIResource titledBorderFontResource = new FontUIResource( titledBorderFont );
        ColorUIResource backgroundResource = new ColorUIResource( backgroundColor );
        ColorUIResource foregroundResource = new ColorUIResource( foregroundColor );
        ColorUIResource textFieldBackgroundResource = new ColorUIResource( textFieldBackgroundColor );
        InsetsUIResource insetsResource = new InsetsUIResource( insets.top, insets.left, insets.bottom, insets.right );
        
        // Uniformly modify the resources for each of the types in the "types" list.
        ArrayList keyValuePairs = new ArrayList();
        for( int i = 0; i < types.length; i++ ) {
            String type = types[i];
            
            add( keyValuePairs, type, "font", fontResource );
            add( keyValuePairs, type, "foreground", foregroundResource );
            add( keyValuePairs, type, "background", backgroundResource );
            
            if( os != OS_MACINTOSH ) {
                add( keyValuePairs, type, "margin", insetsResource );
            }
        }
        
        // These types require some special modifications.
        add( keyValuePairs, "TitledBorder", "font", titledBorderFontResource );
        add( keyValuePairs, "TextField", "background", textFieldBackgroundResource );
        
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
    
    //----------------------------------------------------------------------------
    // Static utilities
    //----------------------------------------------------------------------------

    /**
     * Gets the operating system type.
     * 
     * @return OS_WINDOWS, OS_MACINTOSH, or OS_OTHER
     */
    public static int getOperatingSystem() {

        // Get the operating system name.
        String osName = "";
        try {
            osName = System.getProperty( "os.name" ).toLowerCase();
        }
        catch( Throwable t ) {
            t.printStackTrace();
        }
        
        // Convert to one of the operating system constants.
        int os = OS_OTHER;
        if ( osName.indexOf( "windows" ) >= 0 ) {
            os = OS_WINDOWS;
        }
        else if ( osName.indexOf( "mac" ) >= 0 ) {
            os = OS_MACINTOSH;
        }
        
        return os;
    }
    
    /**
     * Sets the look and feel based on the operating system.
     */
    public static void setLookAndFeel() {
        
        int os = getOperatingSystem();

        if( os == OS_WINDOWS ) {
            try {
//                UIManager.setLookAndFeel( new SmoothLookAndFeel() );//TODO fails on Carl & Kathy's machine.
                UIManager.setLookAndFeel( new WindowsLookAndFeel() );
            }
            catch( UnsupportedLookAndFeelException e ) {
                e.printStackTrace();
            }
        }
        else {
            try {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
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
    }

    /**
     * Gets the font size that corresponds to the screen size.
     * 
     * @return the font size
     */
    public static int getFontSizeForScreen() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int fontSize;
        if( d.width > 1024 ) {
            fontSize = 16;
        }
        else if( d.width <= 800 ) {
            fontSize = 8;
        }
        else {
            fontSize = 10;
        }
//        System.out.println( "PhetLookAndFeel.ScreenSizeHandler: screen size = " + d + " font size = " + fontSize );
        return fontSize;
    }
    
    /**
     * Creates a SmoothTitledBorder.
     * 
     * @param title
     * @return the titled border
     */
    public static Border createSmoothBorder( String title ) {
        return new SmoothTitledBorder( title );
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
        String[] keySet = (String[]) uidefs.keySet().toArray( new String[0] );
        
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