package edu.colorado.phet.unfuddle;

import java.util.logging.Logger;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:12:31 PM
 */
public class TicketNewMessage implements IMessage {
    private final static Logger LOGGER = UnfuddleLogger.getLogger( TicketNewMessage.class );
    private XMLObject ticket;
    private IUnfuddleAccount unfuddleAccount;

    public TicketNewMessage( XMLObject ticket, IUnfuddleAccount unfuddleAccount ) {
        this.ticket = ticket;
        this.unfuddleAccount = unfuddleAccount;
    }

    protected XMLObject getTicket() {
        return ticket;
    }

    public String toString() {
        return toString( this );
    }

    public static String toString( IMessage message ) {
        return "subject:\n" + message.getEmailSubject() + "\nbody:\n" + message.getEmailBody();
    }

    public String getComponent() {
        try {
            final int id = Integer.parseInt( ticket.getTextContent( "component-id" ) );
            if ( id == -1 ) {
                //Don't use toString in the warning or it will be a stackoverflow error
                LOGGER.warning( "ID was -1 for component-id in ticket: " + ticket );
                return "";
            }
            return unfuddleAccount.getComponentForID( id );
        }
        catch ( NumberFormatException nfe ) {
            LOGGER.warning( "Number format exception for component-id in ticket: " + ticket );
            return "";
        }
    }

    protected String getDescription() {
        return ticket.getTextContent( "description" );
    }

    protected IUnfuddlePerson getReporter() {
        return unfuddleAccount.getPersonForID( Integer.parseInt( ticket.getTextContent( "reporter-id" ) ) );
    }

    public String getSummary() {
        return ticket.getTextContent( "summary" );
    }

    public int getTicketNumber() {
        return Integer.parseInt( ticket.getTextContent( "number" ) );
    }

    public String getEmailSubject() {
        return toEmailSubject( getComponent(), getTicketNumber(), getSummary(), getMessageType() );
//        return "[ignore] PhET " + getComponent() + ": " + getSummary();
    }

    public String getFromAddress() {
        return getReporter().getEmail();
    }

    protected String getMessageType() {
        return "new";
    }

    public static String getEmailSubjectPrefix() {
        return "uf:";
    }

    public static String toEmailSubject( String component, int ticketNumber, String summary, String type ) {
        return getEmailSubjectPrefix() + " " + component + " #" + ticketNumber + ": " + summary;
    }

    public String getEmailBody() {
        return getHeader( getTicketURL() ) +
               getMainEmailBodySection() +
               getFooter();
    }

    protected String getMainEmailBodySection() {
        return "Ticket Number: " + getTicketNumber() + "\n" +
               "Created by: " + getReporter().getName() + "\n" +
               "Assigned to : " + getAssigneeName() + "\n" +
               "Summary: " + getSummary() + "\n" +
               "Description:\n" +
               getDescription();
    }

    public static String getFooter() {
        return "\n" +
               "\n" +
               "-----------------------\n" +
               "You received this message because you are signed up on the list located at:\n" +
               UnfuddleNotifierConstants.PHET_PROJECT_URL + "/notebooks/7161";
    }

    public static String getHeader( String ticketURL ) {
        return "Ticket URL: " + ticketURL + "\n" +
               "-----------------------\n" +
               "\n";
    }

    public String getTicketURL() {
        //Unfuddle link structure changed sometime around 4-10-2008
        //please see the forum at:http://unfuddle.com/community/forums/6/topics/223
        //if it is still located there

//        return "https://phet.unfuddle.com/p/unfuddled/tickets/show/" + getTicketNumber() + "/cycle";
        return UnfuddleNotifierConstants.PHET_PROJECT_URL + "/tickets/by_number/" + getTicketNumber();
    }

    protected String getAssigneeName() {
        final String s = ticket.getTextContent( "assignee-id" );
        if ( s == null || s.trim().length() == 0 ) {
            return "<not-assigned>";
        }
        else {
            if ( unfuddleAccount == null ) {
                throw new RuntimeException( "Unfuddle account was null" );
            }
            final int id = Integer.parseInt( s );
            if ( unfuddleAccount.getPersonForID( id ) == null ) {
                System.err.println( "Person not found for ID: " + id + ", perhaps the dump.xml needs to be updated?" );
                return "<PhET Team Member ID:" + id + " (name not found, perhaps dump.xml needs to be updated?)>";//Return the ID and a message if the team member name cannot be found
            }
            return unfuddleAccount.getPersonForID( id ).getName();
        }
    }

    public String getHashID() {
        return ticket.getTextContent( "id" ).trim();
    }

}
