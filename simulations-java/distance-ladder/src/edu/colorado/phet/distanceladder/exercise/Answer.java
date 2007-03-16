/**
 * Class: Answer
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 9:26:16 PM
 */
package edu.colorado.phet.distanceladder.exercise;

public class Answer {

    private String id;
    private String text;

    public Answer( String id, String text ) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public String toString() {
        return new String( id + ": " + text );
    }
}
