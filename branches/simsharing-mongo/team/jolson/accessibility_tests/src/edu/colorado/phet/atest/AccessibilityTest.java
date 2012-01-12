package edu.colorado.phet.atest;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AccessibilityTest extends JFrame {

    private boolean isHighContrast() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Boolean ret = (Boolean) toolkit.getDesktopProperty( "win.highContrast.on" );
        if ( ret == null ) {
            return false;
        }
        return ret.booleanValue();
    }

    private Color accessibleColor( Color color ) {
        if ( isHighContrast() ) {
            return null;
        }
        return color;
    }

    private void setAccessibleFont( JComponent component ) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Font font = (Font) toolkit.getDesktopProperty( "win.messagebox.font" );
        if ( font != null && font.getSize() > 14 ) {
            component.setFont( font );
        }
    }


    public AccessibilityTest() throws HeadlessException {
        setTitle( "High Contrast / Font Size Test" );
        setSize( 700, 300 );

        JPanel content = new JPanel( new GridLayout( 1, 2 ) );

        JPanel regular = new JPanel();
        final JPanel custom = new JPanel();

        regular.setBorder( new TitledBorder( "Regular" ) );
        custom.setBorder( new TitledBorder( "Custom colors" ) );

        final JLabel regularLabel = new JLabel( "This is without color customization" );
        regular.add( regularLabel );


        final JLabel customLabel = new JLabel( "This is with color customization" );
        custom.add( customLabel );
        final JButton customButton = new JButton( "This button does nothing!" );
        custom.add( customButton );

        content.add( regular );
        content.add( custom );

        setContentPane( content );

        custom.setBackground( accessibleColor( Color.PINK ) );
        customLabel.setBackground( accessibleColor( Color.PINK ) );
        customLabel.setForeground( accessibleColor( Color.BLUE ) );
        customButton.setForeground( accessibleColor( Color.BLUE ) );

        setAccessibleFont( regular );
        setAccessibleFont( custom );
        setAccessibleFont( regularLabel );
        setAccessibleFont( customLabel );
        setAccessibleFont( customButton );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setVisible( true );
    }

    public static void main( String[] args ) {
        try {
            System.out.println( "Setting UI to WindowsLookAndFeel" );
            UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
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
        new AccessibilityTest();
    }
}
