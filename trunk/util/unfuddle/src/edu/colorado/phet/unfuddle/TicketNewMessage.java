package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:12:31 PM
 */
public class TicketNewMessage implements IMessage {
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
            return unfuddleAccount.getComponentForID( id );
        }
        catch( NumberFormatException nfe ) {
            return "";
        }
    }

    protected String getDescription() {
        return ticket.getTextContent( "description" );
    }

    protected String getReporter() {
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

    protected String getMessageType() {
        return "new";
    }

    public static String toEmailSubject( String component, int ticketNumber, String summary, String type ) {
        return component + " #" + ticketNumber + ": " + summary;
    }

    public String getEmailBody() {
        return getHeader( getTicketURL() ) +
               getMainEmailBodySection() +
               getFooter();

//        return "Ticket Created by: " + getReporter() + "\n" +
//               "Ticket Assigned to : " + getAssignee() + "\n" +
//               "Ticket Number: " + getTicketNumber() + "\n" +
//               "Ticket URL: " + getTicketURL() + "\n" +
//               "Ticket Description:\n" +
//               "" + getDescription() + "\n\n" +
//               getSuffix();
    }

    protected String getMainEmailBodySection() {
        return "Ticket Number: " + getTicketNumber() + "\n" +
               "Created by: " + getReporter() + "\n" +
               "Assigned to : " + getAssignee() + "\n" +
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

    protected String getAssignee() {
        final String s = ticket.getTextContent( "assignee-id" );
        if ( s == null || s.trim().length() == 0 ) {
            return "<not-assigned>";
        }
        else {
            return unfuddleAccount.getPersonForID( Integer.parseInt( s ) );
        }
    }

    public String getHashID() {
        return ticket.getTextContent( "id" ).trim();
    }

}
