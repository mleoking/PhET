package edu.colorado.phet.unfuddletool.data;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.Communication;
import edu.colorado.phet.unfuddletool.Configuration;
import edu.colorado.phet.unfuddletool.util.DateUtils;

public class Ticket extends Record {

    public int rawAssigneeId;
    public int rawComponentId;
    public DateTime rawCreatedAt;
    public String rawDescription;
    // implement due-on
    // implement hours-estimate-current
    // implement hours-estimate-initial
    public int rawId;
    public int rawMilestoneId;
    public int rawNumber;
    public String rawPriority;
    public int rawProjectId;
    public int rawReporterId;
    public String rawResolution;
    public String rawResolutionDescription;
    public int rawSeverityId;
    public String rawStatus;
    public String rawSummary;
    public DateTime rawUpdatedAt;
    public int rawVersionId;

    public List<Comment> comments = null;

    public Ticket( Element element ) {
        rawAssigneeId = Communication.getOptionalIntField( element, "assignee-id" );
        rawComponentId = Communication.getOptionalIntField( element, "component-id" );
        rawCreatedAt = Communication.getDateTimeField( element, "created-at" );
        rawDescription = Communication.getStringField( element, "description" );
        rawId = Communication.getIntField( element, "id" );
        rawMilestoneId = Communication.getOptionalIntField( element, "milestone-id" );
        rawNumber = Communication.getIntField( element, "number" );
        rawPriority = Communication.getStringField( element, "priority" );
        rawProjectId = Communication.getIntField( element, "project-id" );
        rawReporterId = Communication.getIntField( element, "reporter-id" );
        rawResolution = Communication.getStringField( element, "resolution" );
        rawResolutionDescription = Communication.getStringField( element, "resolution-description" );
        rawSeverityId = Communication.getOptionalIntField( element, "severity-id" );
        rawStatus = Communication.getStringField( element, "status" );
        rawSummary = Communication.getStringField( element, "summary" );
        rawUpdatedAt = Communication.getDateTimeField( element, "updated-at" );
        rawVersionId = Communication.getOptionalIntField( element, "version-id" );
    }

    public int recordType() {
        return Record.RECORD_TICKET;
    }

    public String getComponentName() {
        return Component.getNameFromId( rawComponentId );
    }

    public String toString() {
        return getComponentName() + " #" + String.valueOf( rawNumber ) + " " + rawSummary;
    }

    public String externalLink() {
        return "http://" + Configuration.getAccountName() + ".unfuddle.com/projects/" + Configuration.getProjectIdString() + "/tickets/by_number/" + String.valueOf( getNumber() );
    }

    public String toHTMLString() {

        String ret = "<html><h3><a href='" + externalLink() + "'>";
        ret += toString() + "</a></h3>\n";

        ret += "Assignee: " + Person.getNameFromId( rawAssigneeId ) + "\n";
        ret += "Reporter: " + Person.getNameFromId( rawReporterId ) + "\n";
        // component
        ret += "Component: " + getComponentName() + "\n";
        ret += "Created: " + DateUtils.compactDate( rawCreatedAt.getDate() ) + "\n";
        ret += "Updated: " + DateUtils.compactDate( rawUpdatedAt.getDate() ) + "\n";
        // milestone
        ret += "Priority: " + rawPriority + "\n";
        // severity
        ret += "Status: " + rawStatus + "\n";

        ret += "Description:\n" + Communication.HTMLPrepare( rawDescription ) + "\n\n";

        if ( rawResolution != null ) {
            ret += "Resolution: " + rawResolution + "\n";
            ret += rawResolutionDescription + "\n\n";
        }

        setupComments();

        Iterator<Comment> iter = comments.iterator();

        int commentNumber = 1;

        while ( iter.hasNext() ) {
            Comment comment = iter.next();

            ret += "<hr/>";
            ret += "<b>" + String.valueOf( commentNumber++ ) + ". </b>";
            ret += comment.toString();

        }

        ret += "<hr/>";

        ret += "<a href='" + externalLink() + "'>view in browser</a>";

        ret += "</html>";

        ret = ret.replaceAll( "\n", "<br/>" );

        return ret;
    }

    public int getNumber() {
        return rawNumber;
    }

    public int getId() {
        return rawId;
    }

    public int getPriority() {
        return Integer.parseInt( rawPriority );
    }

    public String getStatus() {
        return rawStatus;
    }

    public Date getUpdatedAt() {
        return rawUpdatedAt.date;
    }

    public boolean equals( Ticket otherTicket ) {
        return getNumber() == otherTicket.getNumber();
    }

    public List<Comment> getComments() {
        setupComments();

        return comments;
    }

    public void setupComments() {
        if ( comments == null ) {
            refreshComments();
        }
    }

    public Comment lastComment() {
        setupComments();

        Iterator<Comment> iter = comments.iterator();

        Comment ret = null;

        while ( iter.hasNext() ) {
            ret = iter.next();
        }

        return ret;
    }

    public Date lastUpdateTime() {
        Date best = rawUpdatedAt.getDate();

        Comment comment = lastComment();

        if ( comment != null ) {
            Date commentDate = comment.rawCreatedAt.getDate();

            if ( commentDate.after( best ) ) {
                best = commentDate;
            }
        }

        return best;
    }

    public void refreshComments() {


        String xmlString = Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/tickets/" + String.valueOf( rawId ) + "/comments", Authentication.auth );
        try {
            if ( comments != null ) {
                comments.clear();
            }
            else {
                comments = new LinkedList<Comment>();
            }

            Element element = (Element) Communication.toDocument( xmlString ).getFirstChild();
            NodeList commentList = element.getElementsByTagName( "comment" );

            for ( int i = 0; i < commentList.getLength(); i++ ) {
                Element commentElement = (Element) commentList.item( i );

                comments.add( new Comment( commentElement ) );
            }

        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
    }
}
