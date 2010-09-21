package edu.colorado.phet.unfuddle;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:52:26 PM
 */
public class TicketCommentMessage implements IMessage {
    private XMLObject comment;
    private IUnfuddleAccount unfuddleAccount;
    private UnfuddleCurl curl;

    //cached for performance
    private String component;
    private XMLObject ticketXML;

    public TicketCommentMessage( XMLObject comment, IUnfuddleAccount unfuddleAccount, UnfuddleCurl curl ) {
        this.comment = comment;
        this.unfuddleAccount = unfuddleAccount;
        this.curl = curl;
    }

    public String getHashID() {
        return comment.getTextContent( "id" ).trim();
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
            catch( InterruptedException e ) {
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

        String person = getAuthorName();
        //TODO: this is a hack to use the "new ticket" header; header and footer should be in a base class, used by all ticket types
        final TicketNewMessage message = new TicketNewMessage( getTicketXML(), unfuddleAccount );
        return TicketNewMessage.getHeader( message.getTicketURL() ) +
               person + " said:\n" +
               "\n" +
               comment.getTextContent( "body" ) +
               TicketNewMessage.getFooter();
    }

    private String getAuthorName() {
        return getAuthor().getName();
    }

    private UnfuddlePerson getAuthor() {
        return unfuddleAccount.getPersonForID( comment.getTextContentAsInt( "author-id" ) );
    }

    public String getEmailSubject() {
        final TicketNewMessage message = new TicketNewMessage( getTicketXML(), unfuddleAccount );
        int number = message.getTicketNumber();
        return TicketNewMessage.toEmailSubject( getComponent(), number, message.getSummary(), "comment" );
    }

    public String getFromAddress() {
        return getAuthor().getEmail();
    }

    public String toString() {
        return TicketNewMessage.toString( this );
    }

}
