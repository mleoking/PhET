package edu.colorado.phet.unfuddletool.data;

import java.util.*;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.Configuration;
import edu.colorado.phet.unfuddletool.handlers.MilestoneHandler;
import edu.colorado.phet.unfuddletool.util.Communication;
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

    public List<TicketListener> listeners;

    public Ticket( Element element ) {
        listeners = new LinkedList<TicketListener>();

        initialize( element );
    }

    public void update() {
        Date beforeDate = lastUpdateTime();
        initialize( Communication.getTicketElementFromServer( getId() ) );
        refreshComments();
        Date afterDate = lastUpdateTime();
        if ( beforeDate.getTime() < afterDate.getTime() ) {
            notifyUpdatedTicket();
        }
        else {
            System.out.println( "Ticket " + this + " was already up to date" );
        }
    }

    private void initialize( Element element ) {
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

    public String getSummary() {
        return rawSummary;
    }

    public String externalLink() {
        return "http://" + Configuration.getAccountName() + ".unfuddle.com/projects/" + Configuration.getProjectIdString() + "/tickets/by_number/" + String.valueOf( getNumber() );
    }

    public String getPrettyPriority() {
        return getPrettyPriorityOf( getPriority() );
    }

    public static String getPrettyPriorityOf( int value ) {
        switch( value ) {
            case 1:
                return "Lowest";
            case 2:
                return "Low";
            case 3:
                return "Normal";
            case 4:
                return "High";
            case 5:
                return "Highest";
        }

        return "Unknown: " + value;
    }

    public String getAssigneeName() {
        return Person.getNameFromId( rawAssigneeId );
    }

    public String getReporterName() {
        return Person.getNameFromId( rawReporterId );
    }

    public int getMilestoneId() {
        return rawMilestoneId;
    }

    public Milestone getMilestone() {
        return MilestoneHandler.getMilestoneHandler().getMilestoneById( getMilestoneId() );
    }

    public String getMilestoneTitle() {
        return MilestoneHandler.getMilestoneHandler().getMilestoneTitleById( getMilestoneId() );
    }

    public String getHTMLHeader() {
        String ret = "<html><body bgcolor='#FFFFFF'>";

        ret += "<table width='100%'><tr><td align='left'>";

        ret += "<h3><a href='" + externalLink() + "'>#" + getNumber() + " " + getSummary() + "</a></h3>";

        ret += "</td><td align='right'>";

        ret += "<a href='update:" + getId() + "'>update</a>";

        ret += "</td></tr></table>";

        ret += "Component: <b>" + getComponentName() + "</b>&nbsp;&nbsp;&nbsp;&nbsp;";
        ret += "Assignee: <b>" + getAssigneeName() + "</b>&nbsp;&nbsp;&nbsp;&nbsp;";
        ret += "Reporter: <b>" + getReporterName() + "</b>\n";

        ret += "Created: " + DateUtils.compactDate( rawCreatedAt.getDate() ) + "&nbsp;&nbsp;&nbsp;&nbsp;";
        ret += "Updated: " + DateUtils.compactDate( rawUpdatedAt.getDate() ) + "&nbsp;&nbsp;&nbsp;&nbsp;";
        ret += "Milestone: " + getMilestoneTitle() + "\n";

        // TODO: milestone

        ret += "Priority: <b>" + getPrettyPriority() + "</b>&nbsp;&nbsp;&nbsp;&nbsp;";
        ret += "Status: <b>" + rawStatus + "</b>\n";

        // TODO: severity


        ret += "<hr/>" + Communication.HTMLPrepare( rawDescription );

        if ( rawResolution != null ) {
            ret += "<hr/>";
            ret += "Resolution: <b>" + rawResolution + "</b>\n";
            ret += rawResolutionDescription;
        }

        ret += "</body></html>";

        ret = ret.replaceAll( "\n", "<br/>" );

        return ret;
    }

    public String getHTMLComments() {

        String ret = "<html><body bgcolor='#FFFFFF'><br/>";

        setupComments();

        Iterator<Comment> iter = comments.iterator();

        int commentNumber = 1;

        while ( iter.hasNext() ) {
            Comment comment = iter.next();

            if ( commentNumber != 1 ) {
                ret += "<hr/>";
            }
            ret += "<b>" + String.valueOf( commentNumber++ ) + ". </b>";
            ret += comment.toString();

        }

        ret += "<hr/>";

        //ret += "<a href='" + externalLink() + "'>view in browser</a>";

        ret += "</body></html>";

        ret = ret.replaceAll( "\n", "<br/>" );

        return ret;
    }

    public String toHTMLString() {

        String ret = "<html><body bgcolor='#FFFFFF'><h3><a href='" + externalLink() + "'>";
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

        ret += "</body></html>";

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

    public static int compare( Ticket a, Ticket b ) {
        Date at = a.lastUpdateTime();
        Date bt = b.lastUpdateTime();
        long as = at.getTime();
        long bs = bt.getTime();

        if ( as == bs ) {
            return 0;
        }
        else if ( as < bs ) {
            return -1;
        }
        else {
            return 1;
        }
    }

    public synchronized void refreshComments() {


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

    private void notifyUpdatedTicket() {
        System.out.println( "Ticket updated: " + this.toString() );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                TicketListener[] listenerArray = listeners.toArray( new TicketListener[]{} );
                for ( int i = 0; i < listenerArray.length; i++ ) {
                    listenerArray[i].onTicketUpdate( Ticket.this );
                }
            }
        } );
    }

    public void addListener( TicketListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( TicketListener listener ) {
        listeners.remove( listener );
    }

    public interface TicketListener {
        public void onTicketUpdate( Ticket ticket );
    }

    public static class RecentTicketComparator implements Comparator<Ticket> {
        public int compare( Ticket a, Ticket b ) {
            return Ticket.compare( a, b );
        }
    }

    public static class RecentTicketReverseComparator implements Comparator<Ticket> {
        public int compare( Ticket a, Ticket b ) {
            return -Ticket.compare( a, b );
        }
    }

}
