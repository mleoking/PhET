package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:52:26 PM
 */
public class NewCommentMessage implements Message {
    private XMLObject comment;
    private IUnfuddleAccount unfuddleAccount;

    public NewCommentMessage( XMLObject comment, IUnfuddleAccount unfuddleAccount ) {
        this.comment = comment;
        this.unfuddleAccount = unfuddleAccount;
    }

    public int getID() {
        return Integer.parseInt( comment.getTextContent( "id" ) );
    }

    public String getComponent() {
        return "component not implemented for comments yet";
//        final int parentID = Integer.parseInt( comment.getTextContent( "parent-id" ) );
//        return unfuddleAccount.getComponentForID( parentID );
    }

    public String getEmailBody() {
        return "";
    }

    public String getEmailSubject() {
        return "";
    }

    public String toString() {
        return comment.getTextContent( "body" );
    }

}
