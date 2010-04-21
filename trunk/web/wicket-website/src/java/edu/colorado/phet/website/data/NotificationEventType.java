package edu.colorado.phet.website.data;

import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public enum NotificationEventType {
    NEW_CONTRIBUTION,
    UPDATED_CONTRIBUTION;

    public String toString( String data ) {
        // store params as "a=1&b=1" etc.
        PageParameters params = new PageParameters( data );
        switch( this ) {
            case NEW_CONTRIBUTION:
                return "Contribution created: " + getContributionString( params.getInt( "contribution_id" ) );
            case UPDATED_CONTRIBUTION:
                return "Contribution updated: " + getContributionString( params.getInt( "contribution_id" ) );
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

}