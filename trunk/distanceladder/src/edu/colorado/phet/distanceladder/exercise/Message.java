/**
 * Class: Message
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 10:21:12 PM
 */
package edu.colorado.phet.distanceladder.exercise;

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
}
