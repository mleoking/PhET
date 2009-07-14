package edu.colorado.phet.atest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AccessibilityTest extends JFrame {


    public AccessibilityTest() throws HeadlessException {
        setTitle( "High Contrast Test" );
        setSize( 700, 300 );

        JPanel content = new JPanel( new GridLayout( 1, 2 ) );

        JPanel regular = new JPanel( new GridLayout( 3, 1 ) );
        final JPanel custom = new JPanel();

        regular.setBorder( new TitledBorder( "Regular" ) );
        custom.setBorder( new TitledBorder( "Custom colors" ) );

        final JLabel regularLabel = new JLabel( "This is without color customization" );
        regular.add( regularLabel );
        JButton regularButton = new JButton( "Check contrast / font" );
        regular.add( regularButton );
        JTextArea textArea = new JTextArea( "If you load this sim when high-contrast is enabled on windows and press the above button, " +
                                            "the right panel should switch back to normal colors. (and the above label should use the " +
                                            "correct windows system font" );
        textArea.setLineWrap( true );
        regular.add( textArea );


        final JLabel customLabel = new JLabel( "This is with color customization" );
        custom.add( customLabel );
        final JButton customButton = new JButton( "This button does nothing" );
        custom.add( customButton );

        content.add( regular );
        content.add( custom );

        setContentPane( content );

        custom.setBackground( Color.PINK );
        customLabel.setBackground( Color.PINK );
        customLabel.setForeground( Color.BLUE );
        customButton.setForeground( Color.BLUE );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setVisible( true );

        regularButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Boolean highContrast = (Boolean) toolkit.getDesktopProperty( "win.highContrast.on" );

                if ( highContrast != null && highContrast ) {
                    custom.setBackground( null );
                    customLabel.setBackground( null );
                    customLabel.setForeground( null );
                    customButton.setForeground( null );
                    regularLabel.setText( MessageFormat.format( "Win contrast: {0}, colors should be high-contrast on the right", highContrast ) );
                }
                else {
                    regularLabel.setText( MessageFormat.format( "Win contrast: {0}, colors unchanged", highContrast ) );
                }

                Font font = (Font) toolkit.getDesktopProperty( "win.messagebox.font" );

                if ( font != null ) {
                    regularLabel.setFont( font );                    
                }
            }
        } );
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
