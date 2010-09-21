package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 21, 2008 at 1:12:31 PM
 */
//TODO: bad inheritance here, presumably for convenience. Move shared stuff into a base class.
public class TicketResolvedMessage extends TicketNewMessage {
    private int recordID;
    private UnfuddlePerson resolvedPerson;

    public TicketResolvedMessage( XMLObject ticket, IUnfuddleAccount unfuddleAccount, UnfuddlePerson resolvedPerson, int recordID ) {
        super( ticket, unfuddleAccount );
        this.resolvedPerson = resolvedPerson;
        this.recordID = recordID;
    }

    protected String getMessageType() {
        return "resolved";
    }

    protected String getMainEmailBodySection() {
        return "Ticket Number: " + getTicketNumber() + "\n" +
               "Resolved by: " + getResolvedBy() + "\n" +
               "Resolution Description:\n" +
               getResolutionDescription();
    }

    public String getHashID() {
        return recordID + "<record>";
        //todo: fix this awkward workaround
//        return recordID * 123 + 17;//it appears that "record-id" is not unique compared to "id", so this function tries to avoid hits
    }

    private String getResolvedBy() {
        return resolvedPerson.getName();
    }

    public String getFromAddress() {
        return resolvedPerson.getEmail();
    }

    private String getResolutionDescription() {
        return getTicket().getTextContent( "resolution-description" );
    }

}