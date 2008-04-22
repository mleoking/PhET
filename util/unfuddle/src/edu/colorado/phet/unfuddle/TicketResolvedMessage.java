package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:12:31 PM
 */
public class TicketResolvedMessage extends TicketNewMessage {
    private String resolvedBy;
    private int recordID;

    public TicketResolvedMessage( XMLObject ticket, IUnfuddleAccount unfuddleAccount, String resolvedBy, int recordID ) {
        super( ticket, unfuddleAccount );
        this.resolvedBy = resolvedBy;
        this.recordID = recordID;
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

    public String getHashID() {
        return recordID+"<record>";
        //todo: fix this awkward workaround
//        return recordID * 123 + 17;//it appears that "record-id" is not unique compared to "id", so this function tries to avoid hits
    }

    private String getResolvedBy() {
        return resolvedBy;
    }

    private String getResolutionDescription() {
        return getTicket().getTextContent( "resolution-description" );
    }

}