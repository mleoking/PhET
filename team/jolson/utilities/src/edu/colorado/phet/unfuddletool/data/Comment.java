package edu.colorado.phet.unfuddletool.data;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.Communication;
import edu.colorado.phet.unfuddletool.PersonHandler;
import edu.colorado.phet.unfuddletool.TicketHandler;
import edu.colorado.phet.unfuddletool.util.DateUtils;

public class Comment extends Record {

    public int rawAuthorId;
    public String rawBody;
    public DateTime rawCreatedAt;
    public int rawId;
    public int rawParentId;
    public String rawParentType;
    public DateTime rawUpdatedAt;

    public int ticketNumber = -1;

    public Comment( Element element ) {
        rawAuthorId = Communication.getIntField( element, "author-id" );
        rawBody = Communication.getStringField( element, "body" );
        rawCreatedAt = Communication.getDateTimeField( element, "created-at" );
        rawId = Communication.getIntField( element, "id" );
        rawParentId = Communication.getIntField( element, "parent-id" );
        rawParentType = Communication.getStringField( element, "parent-type" );
        rawUpdatedAt = Communication.getDateTimeField( element, "updated-at" );

        if ( rawParentType.equals( "Ticket" ) ) {
            TicketHandler handler = TicketHandler.getTicketHandler();
            ticketNumber = handler.getTicketById( rawParentId ).getNumber();
        }
    }

    public int recordType() {
        return Record.RECORD_COMMENT;
    }

    public String toString() {
        String ret = "";

        Person person = PersonHandler.getPersonHandler().getPersonById( rawAuthorId );

        ret += "<b>" + person.getName() + "</b> said at " + DateUtils.compactDate( rawCreatedAt.getDate() ) + ":\n\n";

        String body = rawBody.replaceAll( "\n", "ABRACADABRAX" );

        body = Communication.HTMLPrepare( body );

        body = body.replaceAll( "ABRACADABRAX", "<br/>" );

        ret += body + "\n";

        if ( !rawCreatedAt.equals( rawUpdatedAt ) ) {
            ret += "[Updated at " + DateUtils.compactDate( rawUpdatedAt.getDate() ) + "]\n";
        }

        ret += "\n";

        return ret;
    }
}
