package edu.colorado.phet.website.notification;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.wicket.util.value.ValueMap;
import org.hibernate.Session;

import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.data.NotificationEvent;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionComment;
import edu.colorado.phet.website.data.contribution.ContributionNomination;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.HtmlUtils;

public enum NotificationEventType {
    NEW_CONTRIBUTION,
    UPDATED_CONTRIBUTION,
    NOMINATED_CONTRIBUTION,
    CONTRIBUTION_COMMENT;

    public String toString( String data ) {
        // store params as "a=1,b=1" etc.
        ValueMap params = new ValueMap( data );
        switch( this ) {
            case NEW_CONTRIBUTION:
                return "Contribution created: " + getContributionString( params.getInt( "contribution_id" ) );
            case UPDATED_CONTRIBUTION:
                return "Contribution updated: " + getContributionString( params.getInt( "contribution_id" ) );
            case NOMINATED_CONTRIBUTION:
                return "Contribution nominated by " + decodeToDisplay( params.getString( "email" ) ) + ": " + getContributionString( params.getInt( "contribution_id" ) ) +
                       " with reason: <em>" + decodeToDisplay( params.getString( "reason" ) ) + "</em>";
            case CONTRIBUTION_COMMENT:
                return "User " + decodeToDisplay( params.getString( "email" ) ) + " commented on " + getContributionString( params.getInt( "contribution_id" ) ) + ": <em>"
                       + decodeToDisplay( params.getString( "text" ) ) + "</em>";
            default:
                return "Unidentified event";
        }
    }

    private static String decodeToDisplay( String string ) {
        if ( string == null ) {
            return "";
        }
        try {
            return HtmlUtils.encode( URLDecoder.decode( string, "UTF-8" ) );
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            return string;
        }
    }

    private String getContributionString( final int id ) {
        final String contribhref = "href=\"http://" + WebsiteConstants.WEB_SERVER + ContributionPage.getLinker( id ).getDefaultRawUrl() + "\"";
        final String[] ret = new String[1];
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                Contribution cont = (Contribution) session.load( Contribution.class, id );
                if ( cont == null ) {
                    return false;
                }
                ret[0] = "<a " + contribhref + ">" + cont.getTitle() + "</a> by user " + cont.getPhetUser().getEmail();
                return true;
            }
        } );
        if ( !success ) {
            ret[0] = "Contribution #" + id + " does not exist anymore, try <a href=\"" + contribhref + "\">this link</a>";
        }
        return ret[0];
    }

    public static void onNewContribution( final Contribution contribution ) {
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                NotificationEvent event = new NotificationEvent();
                event.setCreatedAt( new Date() );
                event.setType( NEW_CONTRIBUTION );
                event.setData( "contribution_id=" + contribution.getId() );
                session.save( event );
                return true;
            }
        } );
    }

    public static void onUpdatedContribution( final Contribution contribution ) {
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                NotificationEvent event = new NotificationEvent();
                event.setCreatedAt( new Date() );
                event.setType( UPDATED_CONTRIBUTION );
                event.setData( "contribution_id=" + contribution.getId() );
                session.save( event );
                return true;
            }
        } );
    }

    public static void onNominatedContribution( final ContributionNomination nomination ) {
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                NotificationEvent event = new NotificationEvent();
                event.setCreatedAt( new Date() );
                event.setType( NOMINATED_CONTRIBUTION );
                try {
                    event.setData( "contribution_id=" + nomination.getContribution().getId() + ",email=" + URLEncoder.encode( nomination.getPhetUser().getEmail(), "UTF-8" )
                                   + ",reason=" + URLEncoder.encode( nomination.getReason(), "UTF-8" ) );
                }
                catch( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    return false;
                }
                session.save( event );
                return true;
            }
        } );
    }

    public static void onContributionComment( final ContributionComment comment ) {
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                NotificationEvent event = new NotificationEvent();
                event.setCreatedAt( new Date() );
                event.setType( CONTRIBUTION_COMMENT );
                try {
                    event.setData( "contribution_id=" + comment.getContribution().getId() + ",email=" + URLEncoder.encode( comment.getPhetUser().getEmail(), "UTF-8" )
                                   + ",text=" + URLEncoder.encode( comment.getText(), "UTF-8" ) );
                }
                catch( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    return false;
                }
                session.save( event );
                return true;
            }
        } );
    }
}