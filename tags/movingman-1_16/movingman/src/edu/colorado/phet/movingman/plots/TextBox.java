package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.TimeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextBox extends JPanel {
    static Font font = MMFontManager.getFontSet().getTextBoxFont();

    private boolean changedByUser;
    private JTextField textField;
    private JLabel label;
    private MovingManModule module;

    public TextBox( MovingManModule module, int text, String labelText ) {
        this.module = module;
        textField = new JTextField( text );
        label = new JLabel( labelText );
        setLayout( new FlowLayout( FlowLayout.CENTER ) );
        textField.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( isEnabled() ) {
                    textField.selectAll();
                }
            }
        } );
        textField.addFocusListener( new FocusAdapter() {
            public void focusLost( FocusEvent e ) {
                textField.select( 0, 0 );
            }
        } );
        textField.addKeyListener( new KeyListener() {
            public void keyTyped( KeyEvent e ) {
                changedByUser = true;
            }

            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
            }
        } );
        label.setFont( font );
        textField.setFont( font );
        add( label );
        add( textField );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        module.addListener( new TimeListener() {
            public void recordingStarted() {
                setTextFieldEditable( false );
            }

            public void recordingPaused() {
                setTextFieldEditable( true );
            }

            public void recordingFinished() {
                setTextFieldEditable( false );
            }

            public void playbackStarted() {
                setTextFieldEditable( false );
            }

            public void playbackPaused() {
                setTextFieldEditable( true );
            }

            public void playbackFinished() {
                setTextFieldEditable( false );
            }

            public void reset() {
                setTextFieldEditable( true );
            }

            public void rewind() {
                setTextFieldEditable( true );
            }
        } );

        setText( "0.0" );
        textField.setHorizontalAlignment( JTextField.RIGHT );
    }

    public void setTextFieldEditable( boolean editable ) {
        textField.setEditable( editable );
    }

    public void clearChangedByUser() {
        changedByUser = false;
    }

    public boolean isChangedByUser() {
        return changedByUser;
    }

    public synchronized void addKeyListener( KeyListener l ) {
        textField.addKeyListener( l );
    }

    public void setEditable( boolean b ) {
        textField.setEditable( b );
    }

    public String getText() {
        return textField.getText();
    }

    public void setText( String valueString ) {
        if( !textField.getText().equals( valueString ) ) {
            textField.setText( valueString );
        }
//        if( valueString.length() > textField.getColumns() ) {
//            valueString = valueString.subSequence( 0, textField.getColumns() ) + "";
//        }
    }

    public void selectAll() {
        textField.selectAll();
    }

    public void deselectAll() {
        textField.select( 0, 0 );
    }

    public JTextField getTextField() {
        return textField;
    }
}
