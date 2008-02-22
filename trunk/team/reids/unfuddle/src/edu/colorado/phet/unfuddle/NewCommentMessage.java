package edu.colorado.phet.unfuddle;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:52:26 PM
 */
public class NewCommentMessage implements Message {
    private XMLObject comment;
    private IUnfuddleAccount unfuddleAccount;
    private UnfuddleCurl curl;

    //cached for performance
    private String component;
    private XMLObject ticketXML;

    public NewCommentMessage( XMLObject comment, IUnfuddleAccount unfuddleAccount, UnfuddleCurl curl ) {
        this.comment = comment;
        this.unfuddleAccount = unfuddleAccount;
        this.curl = curl;
    }

    public int getID() {
        return Integer.parseInt( comment.getTextContent( "id" ) );
    }

    public XMLObject getTicketXML() {
        if ( ticketXML == null ) {
            int parentID = comment.getTextContentAsInt( "parent-id" );
            try {
                ticketXML = new XMLObject( curl.readString( "tickets/" + parentID ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            catch( SAXException e ) {
                e.printStackTrace();
            }
            catch( ParserConfigurationException e ) {
                e.printStackTrace();
            }
        }
        return ticketXML;
    }

    public String getComponent() {
        if ( component == null ) {
            int componentid = getTicketXML().getTextContentAsInt( "component-id" );
            component = unfuddleAccount.getComponentForID( componentid );
        }
        return component;
    }

    public String getEmailBody() {
//        String body = comment.getTextContent( "body" );
//
//        String person = unfuddleAccount.getPersonForID( comment.getTextContentAsInt( "author-id" ) );
//        return body + "\n\n" + "-" + person + "\n\n" + "Please navigate to " + new NewTicketMessage( getTicketXML(), unfuddleAccount ).getTicketURL() +
//               "\nto read other comments and add a comment.\n\n" + new NewTicketMessage( getTicketXML(), unfuddleAccount ).getSuffix();

        String person = unfuddleAccount.getPersonForID( comment.getTextContentAsInt( "author-id" ) );
        final NewTicketMessage message = new NewTicketMessage( getTicketXML(), unfuddleAccount );
        return NewTicketMessage.getHeader( message.getTicketURL() ) +
               person + " said:\n" +
               "\n" +
               comment.getTextContent( "body" ) +
               NewTicketMessage.getFooter();
    }

    public String getEmailSubject() {
        final NewTicketMessage message = new NewTicketMessage( getTicketXML(), unfuddleAccount );
        int number = message.getTicketNumber();
        return NewTicketMessage.toEmailSubject( getComponent(), number, message.getSummary(), "comment" );
//        return new NewTicketMessage( getTicketXML(), unfuddleAccount ).getEmailSubject();
    }

    public String toString() {
        return NewTicketMessage.toString( this );
    }

}
