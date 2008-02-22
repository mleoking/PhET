package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:12:31 PM
 */
public class NewTicketMessage implements Message {
    private XMLObject ticket;
    private IUnfuddleAccount unfuddleAccount;

    public NewTicketMessage( XMLObject ticket, IUnfuddleAccount unfuddleAccount ) {
        this.ticket = ticket;
        this.unfuddleAccount = unfuddleAccount;
    }

    public String toString() {
        return toString( this );
    }

    public static String toString( Message message ) {
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

    private String getDescription() {
        return ticket.getTextContent( "description" );
    }

    private String getReporter() {
        return unfuddleAccount.getPersonForID( Integer.parseInt( ticket.getTextContent( "reporter-id" ) ) );
    }

    public String getSummary() {
        return ticket.getTextContent( "summary" );
    }

    public int getTicketNumber() {
        return Integer.parseInt( ticket.getTextContent( "number" ) );
    }

    public String getEmailSubject() {
        return toEmailSubject( getComponent(), getTicketNumber(), getSummary(), "new" );
//        return "[ignore] PhET " + getComponent() + ": " + getSummary();
    }

    public static String toEmailSubject( String component, int ticketNumber, String summary, String type ) {
//        return "[ignore]" + "Unfuddle " + component + " [#" + ticketNumber + " " + type + "] : " + summary;
        return "Unfuddle " + component + " [#" + ticketNumber + " " + type + "] : " + summary;
    }

    public String getEmailBody() {
        return getHeader( getTicketURL() ) +
               "Ticket Number: " + getTicketNumber() + "\n" +
               "Created by: " + getReporter() + "\n" +
               "Assigned to : " + getAssignee() + "\n" +
               "Summary: " + getSummary() + "\n" +
               "Description:\n" +
               getDescription() +
               getFooter();

//        return "Ticket Created by: " + getReporter() + "\n" +
//               "Ticket Assigned to : " + getAssignee() + "\n" +
//               "Ticket Number: " + getTicketNumber() + "\n" +
//               "Ticket URL: " + getTicketURL() + "\n" +
//               "Ticket Description:\n" +
//               "" + getDescription() + "\n\n" +
//               getSuffix();
    }

    public static String getFooter() {
        return "\n" +
               "\n" +
               "-----------------------\n" +
               "You received this message because you are signed up on the list located at:\n" +
               "https://phet.unfuddle.com/p/unfuddled/notebooks/show/7161";
    }

    public static String getHeader( String ticketURL ) {
        return "Ticket URL: " + ticketURL + "\n" +
               "-----------------------\n" +
               "\n";
    }

    public String getSuffix() {
        return "-----------------------\n" +
               "You received this message because you are signed up on the list located at:\n" +
               "https://phet.unfuddle.com/p/unfuddled/notebooks/show/7161";
    }

    public String getTicketURL() {
        return "https://phet.unfuddle.com/p/unfuddled/tickets/show/" + getTicketNumber() + "/cycle";
    }

    private String getAssignee() {
        final String s = ticket.getTextContent( "assignee-id" );
        if ( s == null || s.trim().length() == 0 ) {
            return "<not-assigned>";
        }
        else {
            return unfuddleAccount.getPersonForID( Integer.parseInt( s ) );
        }
    }

    public int getID() {
        return Integer.parseInt( ticket.getTextContent( "id" ) );
    }

}
