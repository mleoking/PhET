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
    private String text;

    public Message() {
    }

    public Message( String text ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public void display() {
        JEditorPane textPane = new JEditorPane( "text/html", getText() );
        textPane.setPreferredSize( new Dimension( 400, 300 ) );
        JScrollPane jScrollPane = new JScrollPane( textPane );
        int response = JOptionPane.showConfirmDialog( null,
                                                      jScrollPane,
                                                      "Don't Panic!",
                                                      JOptionPane.OK_CANCEL_OPTION );
        if( response == JOptionPane.CANCEL_OPTION ) {
            int confirm = JOptionPane.showConfirmDialog( null,
                                                         "Are you sure you want to quit the game?",
                                                         "Confirm Exit",
                                                         JOptionPane.YES_NO_OPTION );
            if( confirm == JOptionPane.YES_OPTION ) {
                System.exit( 0 );
            }
        }
    }
}
