package edu.colorado.phet.website.panels.contribution;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.behavior.HeaderContributor;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class ContributionManagePanel extends PhetPanel {

    private final List<Contribution> myContributions;
    private final List<Contribution> unapprovedContributions;
    private final List<Contribution> otherContributions;

    public ContributionManagePanel( String id, final PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

        myContributions = new LinkedList<Contribution>();
        unapprovedContributions = new LinkedList<Contribution>();
        otherContributions = new LinkedList<Contribution>();

        final PhetUser user = PhetSession.get().getUser();

        // TODO: add delete function

        // TODO: check success
        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List mys = session.createQuery( "select c from Contribution as c where c.phetUser = :user" ).setEntity( "user", user ).list();
                for ( Object my : mys ) {
                    myContributions.add( (Contribution) my );
                }
                if ( user.isTeamMember() ) {
                    List others = session.createQuery( "select c from Contribution as c where c.phetUser != :user" ).setEntity( "user", user ).list();
                    for ( Object o : others ) {
                        Contribution contribution = (Contribution) o;
                        if ( contribution.isApproved() ) {
                            otherContributions.add( contribution );
                        }
                        else {
                            unapprovedContributions.add( contribution );
                        }
                    }
                }
                return true;
            }
        } );

        // TODO: sort contributions? (and elsewhere)

        // TODO: localize

        add( new ContributionManageListPanel( "my-contributions", context, myContributions ) );

        if ( user.isTeamMember() ) {
            add( new ContributionManageListPanel( "other-contributions", context, otherContributions ) );
        }
        else {
            add( new InvisibleComponent( "other-contributions" ) );
        }

        if ( user.isTeamMember() && !unapprovedContributions.isEmpty() ) {
            add( new ContributionManageListPanel( "unapproved-contributions", context, unapprovedContributions ) );
        }
        else {
            add( new InvisibleComponent( "unapproved-contributions" ) );
        }

    }

}