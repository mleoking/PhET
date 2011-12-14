// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConfig;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that prompts the user for their sim-sharing id (an integer stored in String format.)
 * This dialog is modal, and blocks until the user enters and id, or quits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingIdDialog extends JDialog {

    // Strings used herein. No i18n needed now, but perhaps in the future.
    private static final String RESEARCH_STUDY = "Research Study";
    private static final String CONTINUE = "Continue";
    private static final String EXIT = "Exit";

    private final JTextField textField;
    private String id;

    public SimSharingIdDialog( String prompt ) {
        this( null, prompt );
        SwingUtils.centerWindowOnScreen( this );
    }

    public SimSharingIdDialog( Frame owner, String prompt ) {
        super( owner, true /* modal */ );
        setResizable( false );
        setUndecorated( true ); // so that the user can't use the close button. Also means that can't move the dialog.

        // banner
        JLabel phetLogo = new JLabel( new ImageIcon( PhetCommonResources.getImage( PhetCommonResources.IMAGE_PHET_LOGO ) ) ) {{
            setBorder( new LineBorder( Color.BLACK ) );
        }};
        JLabel studyLabel = new JLabel( RESEARCH_STUDY ) {{
            setFont( new PhetFont( Font.BOLD, 20 ) );
        }};
        JPanel bannerPanel = new JPanel();
        bannerPanel.add( phetLogo );
        bannerPanel.add( studyLabel );

        // input panel
        JLabel promptLabel = new JLabel( prompt ) {{
            setFont( new PhetFont( 16 ) );
        }};
        textField = new JTextField() {{
            setFont( new PhetFont( 16 ) );
            setColumns( 10 );
        }};
        JPanel inputPanel = new JPanel() {{
            setBorder( new EmptyBorder( 5, 10, 5, 10 ) );
        }};
        inputPanel.add( promptLabel );
        inputPanel.add( textField );

        // action panel
        final JButton continueButton = new JButton( CONTINUE ) {{
            setEnabled( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    doContinue();
                }
            } );
        }};
        final JButton exitButton = new JButton( EXIT ) {{
            addActionListener( new ActionListener() {
                // pressing the Exit button exits the application.
                public void actionPerformed( ActionEvent event ) {
                    System.exit( 0 );
                }
            } );
        }};
        JPanel actionPanel = new JPanel();
        actionPanel.add( continueButton );
        actionPanel.add( exitButton );

        textField.addKeyListener( new KeyAdapter() {

            // accept only keystrokes related to integer input
            @Override public void keyTyped( KeyEvent e ) {
                char c = e.getKeyChar();
                if ( !( ( c == KeyEvent.VK_BACK_SPACE ) || ( c == KeyEvent.VK_DELETE ) || ( c == KeyEvent.VK_ENTER ) || ( c == KeyEvent.VK_TAB ) || ( Character.isDigit( c ) ) ) ) {
                    e.consume();
                }
            }

            // enabled/disable the button based on whether the text field contains valid input
            @Override public void keyReleased( KeyEvent e ) {
                continueButton.setEnabled( isInputValid() );
            }
        } );
        textField.addActionListener( new ActionListener() {
            // pressing Enter is the same as pressing the Continue button
            public void actionPerformed( ActionEvent e ) {
                if ( isInputValid() ) {
                    doContinue();
                }
            }
        } );

        JPanel mainPanel = new VerticalLayoutPanel();
        mainPanel.add( bannerPanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( inputPanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( actionPanel );
        setContentPane( mainPanel );
        pack();
    }

    private boolean isInputValid() {
        return ( textField.getText() != null ) && ( textField.getText().length() != 0 );
    }

    private void doContinue() {
        id = textField.getText();
        dispose();
    }

    public String getId() {
        return id;
    }

    // test
    public static void main( String[] args ) {
        String prompt = SimSharingConfig.getConfig( "colorado" ).idPrompt;
        SimSharingIdDialog dialog = new SimSharingIdDialog( prompt );
        dialog.setVisible( true );
        System.out.println( "id = " + dialog.getId() );
        System.exit( 0 );
    }
}
