package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:12:31 PM
 */
public class TicketResolvedMessage extends NewTicketMessage {
    private String resolvedBy;

    public TicketResolvedMessage( XMLObject ticket, IUnfuddleAccount unfuddleAccount, String resolvedBy ) {
        super( ticket, unfuddleAccount );
        this.resolvedBy = resolvedBy;
    }

    protected String getMessageType() {
        return "resolved";
    }

    protected String getMainEmailBodySection() {
        return "Ticket Number: " + getTicketNumber() + "\n" +
               "Resolved by: " + getResolvedBy() + "\n" +
//               "Assigned to : " + getAssignee() + "\n" +
//               "Summary: " + getSummary() + "\n" +
"Resolution Description:\n" +
getResolutionDescription();
    }

    private String getResolvedBy() {
        return resolvedBy;
    }

    private String getResolutionDescription() {
        return getTicket().getTextContent( "resolution-description" );
    }

}