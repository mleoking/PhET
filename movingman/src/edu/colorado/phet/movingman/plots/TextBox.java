package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.TimeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TextBox extends JPanel {
    boolean changedByUser;
    JTextField textField;
    JLabel label;
    static Font font = MMFontManager.getFontSet().getTextBoxFont();
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
                textField.setEditable( false );
            }

            public void recordingPaused() {
                textField.setEditable( true );
            }

            public void recordingFinished() {
                textField.setEditable( false );
            }

            public void playbackStarted() {
                textField.setEditable( false );
            }

            public void playbackPaused() {
                textField.setEditable( true );
            }

            public void playbackFinished() {
                textField.setEditable( false );
            }

            public void reset() {
                textField.setEditable( true );
            }

            public void rewind() {
                textField.setEditable( true );
            }
        } );
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

    public void setHorizontalAlignment( int right ) {
        textField.setHorizontalAlignment( right );
    }

    public String getText() {
        return textField.getText();
    }

    public void setText( String valueString ) {
        if( valueString.length() > textField.getColumns() ) {
            valueString = valueString.subSequence( 0, textField.getColumns() ) + "";
        }
        textField.setText( valueString );
    }
}
