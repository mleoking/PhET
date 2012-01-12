package edu.colorado.phet.unfuddletool.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.Configuration;
import edu.colorado.phet.unfuddletool.data.Milestone;
import edu.colorado.phet.unfuddletool.util.Communication;

public class MilestoneHandler {

    public Set<Milestone> milestones;

    private MilestoneHandler() {
        milestones = new HashSet<Milestone>();

        refreshMilestones();
    }

    private static MilestoneHandler milestoneHandler;

    public static MilestoneHandler getMilestoneHandler() {
        if ( milestoneHandler == null ) {
            milestoneHandler = new MilestoneHandler();
        }

        return milestoneHandler;
    }

    public void refreshMilestones() {
        String xmlString = Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/milestones", Authentication.auth );
        try {
            milestones.clear();

            Element element = (Element) Communication.toDocument( xmlString ).getFirstChild();
            NodeList milestoneList = element.getElementsByTagName( "milestone" );

            for ( int i = 0; i < milestoneList.getLength(); i++ ) {
                Element milestoneElement = (Element) milestoneList.item( i );

                milestones.add( new Milestone( milestoneElement ) );
            }

        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
    }

    public Milestone getMilestoneById( int id ) {
        Iterator<Milestone> iter = milestones.iterator();

        while ( iter.hasNext() ) {
            Milestone milestone = iter.next();

            if ( milestone.getId() == id ) {
                return milestone;
            }
        }

        return null;
    }

    public String getMilestoneTitleById( int id ) {
        Milestone milestone = getMilestoneById( id );
        if ( milestone == null ) {
            return "";
        }
        return milestone.getTitle();
    }
}