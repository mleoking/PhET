package edu.colorado.phet.website.panels;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.behavior.HeaderContributor;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.content.ContributionEditPage;
import edu.colorado.phet.website.content.ContributionPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class ContributionManagePanel extends PhetPanel {

    private final List<Contribution> myContributions;
    private final List<Contribution> otherContributions;

    public ContributionManagePanel( String id, final PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        myContributions = new LinkedList<Contribution>();
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
                        otherContributions.add( (Contribution) o );
                    }
                }
                return true;
            }
        } );

        // TODO: sort contributions? (and elsewhere)

        // TODO: localize

        add( new ListView( "my-contributions", myContributions ) {
            protected void populateItem( ListItem item ) {
                Contribution contribution = (Contribution) item.getModel().getObject();
                Link link = ContributionPage.createLink( "contribution-link", context, contribution );
                link.add( new Label( "contribution-title", contribution.getTitle() ) );
                item.add( link );
                item.add( new Label( "contribution-authors", contribution.getAuthors() ) );
                item.add( ContributionEditPage.getLinker( contribution ).getLink( "edit-link", context, getPhetCycle() ) );
            }
        } );

        if ( user.isTeamMember() ) {
            add( new ListView( "other-contributions", otherContributions ) {
                protected void populateItem( ListItem item ) {
                    Contribution contribution = (Contribution) item.getModel().getObject();
                    Link link = ContributionPage.createLink( "contribution-link", context, contribution );
                    link.add( new Label( "contribution-title", contribution.getTitle() ) );
                    item.add( link );
                    item.add( new Label( "contribution-authors", contribution.getAuthors() ) );
                    item.add( ContributionEditPage.getLinker( contribution ).getLink( "edit-link", context, getPhetCycle() ) );
                }
            } );
        }
        else {
            add( new InvisibleComponent( "other-contributions" ) );
        }

    }

}