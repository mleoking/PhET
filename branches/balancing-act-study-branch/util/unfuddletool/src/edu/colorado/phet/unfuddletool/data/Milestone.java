package edu.colorado.phet.unfuddletool.data;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.util.Communication;

public class Milestone {
    //public boolean rawArchived;
    //public boolean rawCompleted;
    public DateTime rawCreatedAt;
    //public String rawDueOn;
    public int rawId;
    public int rawPersonResponsibleId;
    public int rawProjectId;
    public String rawTitle;
    public DateTime rawUpdatedAt;

    public Milestone( Element element ) {
        rawCreatedAt = Communication.getDateTimeField( element, "created-at" );
        //rawDueOn = Communication.getStringField( element, "due-on" );
        rawId = Communication.getIntField( element, "id" );
        rawPersonResponsibleId = Communication.getIntField( element, "person-responsible-id" );
        rawProjectId = Communication.getIntField( element, "project-id" );
        rawTitle = Communication.getStringField( element, "title" );
        rawUpdatedAt = Communication.getDateTimeField( element, "updated-at" );
    }

    public int getId() {
        return rawId;
    }

    public String getTitle() {
        if ( rawTitle == null ) {
            return "none";
        }
        return rawTitle;
    }

}
