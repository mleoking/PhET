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
        textPane.setPreferredSize( new Dimension( 500, 400 ));
        JOptionPane.showMessageDialog( null, new JScrollPane( textPane ));
    }
}
