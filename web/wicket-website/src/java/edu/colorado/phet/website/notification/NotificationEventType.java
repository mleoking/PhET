package edu.colorado.phet.website.notification;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionNomination;
import edu.colorado.phet.website.data.contribution.ContributionComment;
import edu.colorado.phet.website.data.NotificationEvent;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public enum NotificationEventType {
    NEW_CONTRIBUTION,
    UPDATED_CONTRIBUTION,
    NOMINATED_CONTRIBUTION,
    CONTRIBUTION_COMMENT;

    public String toString( String data ) {
        // store params as "a=1&b=1" etc.
        PageParameters params = new PageParameters( data );
        switch( this ) {
            case NEW_CONTRIBUTION:
                return "Contribution created: " + getContributionString( params.getInt( "contribution_id" ) );
            case UPDATED_CONTRIBUTION:
                return "Contribution updated: " + getContributionString( params.getInt( "contribution_id" ) );
            case NOMINATED_CONTRIBUTION:
                try {
                    return "Contribution nominated by " + URLDecoder.decode( params.getString( "email" ), "UTF-8" ) + ": " + getContributionString( params.getInt( "contribution_id" ) ) +
                           " with reason " + URLDecoder.decode( params.getString( "reason" ) == null ? "" : params.getString( "reason" ), "UTF-8" );
                }
                catch( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    return "Contribution nominated by Error decoding email: " + getContributionString( params.getInt( "contribution_id" ) );
                }
            case CONTRIBUTION_COMMENT:
                try {
                    return "User " + URLDecoder.decode( params.getString( "email" ), "UTF-8" ) + " commented on " + getContributionString( params.getInt( "contribution_id" ) ) + ": "
                           + URLDecoder.decode( params.getString( "text" ), "UTF-8" );
                }
                catch( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    return "Error encountered decoding comment";
                }
            default:
                return "Unidentified event";
        }
    }

    private String getContributionString( final int id ) {
        final String contribhref = "href=\"http://phetsims.colorado.edu" + ContributionPage.getLinker( id ).getDefaultRawUrl() + "\"";
        final String[] ret = new String[1];
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                Contribution cont = (Contribution) session.load( Contribution.class, id );
                ret[0] = "<a " + contribhref + ">" + cont.getTitle() + "</a> by user " + cont.getPhetUser().getEmail();
                return true;
            }
        } );
        if ( !success ) {
            ret[0] = "Contribution #" + id + " error, try <a href=\"" + contribhref + "\">this link</a>";
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