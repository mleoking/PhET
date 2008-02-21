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
        return "subject:\n" + getEmailSubject() + "\nbody:\n" + getEmailBody();
    }

    public String getComponent() {
        return unfuddleAccount.getComponentForID( Integer.parseInt( ticket.getTextContent( "component-id" ) ) );
    }

    private String getDescription() {
        return ticket.getTextContent( "description" );
    }

    private String getReporter() {
        return unfuddleAccount.getPersonForID( Integer.parseInt( ticket.getTextContent( "reporter-id" ) ) );
    }

    private String getSummary() {
        return ticket.getTextContent( "summary" );
    }

    private int getTicketNumber() {
        return Integer.parseInt( ticket.getTextContent( "number" ) );
    }

    public String getEmailSubject() {
        return "PhET/" + getComponent() + ": " + getSummary();
    }

    public String getEmailBody() {
        return "Ticket Created by: " + getReporter() + "\n" +
               "Ticket Assigned to : " + getAssignee() + "\n" +
               "Ticket Number: " + getTicketNumber() + "\n" +
               "Ticket URL: " + getTicketURL() + "\n" +
               "Ticket Description:\n" +
               "" + getDescription();
    }

    private String getTicketURL() {
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
