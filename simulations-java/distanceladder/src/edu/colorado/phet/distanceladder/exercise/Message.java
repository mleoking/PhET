/**
 * Class: Message
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 10:21:12 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import javax.swing.*;
import java.awt.*;

public class Message {

    public final static int NEXT = 0;
    public final static int GO_TO_GAME = 1;

    private String text;
    private Container parent;

    public Message( Container parent ) {
        this.parent = parent;
    }

    public Message( Container parent, String text ) {
        this( parent );
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }


    public int display() {
        JEditorPane textPane = new JEditorPane( "text/html", getText() );
        textPane.setPreferredSize( new Dimension( 400, 300 ) );
        int response = 0;
        do {
            JScrollPane jScrollPane = new JScrollPane( textPane );
            jScrollPane.getVerticalScrollBar().setValue( 0 );
            response = JOptionPane.showOptionDialog( parent,
                                                     jScrollPane,
                                                     "Don't Panic!",
                                                     JOptionPane.DEFAULT_OPTION,
                                                     JOptionPane.INFORMATION_MESSAGE,
                                                     null,
                                                     new Object[]{"Next", "Go to Game", "Quit"},
                                                     "Next" );
            if( response == JOptionPane.CANCEL_OPTION ) {
                int confirm = JOptionPane.showConfirmDialog( parent,
                                                             "Are you sure you want to quit the game?",
                                                             "Confirm Exit",
                                                             JOptionPane.YES_NO_OPTION );
                if( confirm == JOptionPane.YES_OPTION ) {
                    System.exit( 0 );
                }
            }
        } while( response != 0 && response != 1 );
        if( response == 0 ) {
            return NEXT;
        }
        else {
            return GO_TO_GAME;
        }
    }
}
