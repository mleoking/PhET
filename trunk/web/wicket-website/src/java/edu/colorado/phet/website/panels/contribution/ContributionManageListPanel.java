package edu.colorado.phet.website.panels.contribution;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.hibernate.Session;

import edu.colorado.phet.website.content.contribution.ContributionEditPage;
import edu.colorado.phet.website.content.contribution.ContributionManagePage;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.contribution.ContributionSuccessPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * Shows a list of contributions, their authors, and a list of actions that can be taken
 * TODO: possibly get rid of actions for a more compact / informative version?
 */
public class ContributionManageListPanel extends PhetPanel {

    public ContributionManageListPanel( String id, final PageContext context, List<Contribution> contributions ) {
        super( id, context );

        // TODO: localize

        // don't show whatever this was contained in, so that the table rows are nested directly under the table tags in
        // the parent
        setRenderBodyOnly( true );

        add( new ListView<Contribution>( "contributions", contributions ) {
            protected void populateItem( ListItem<Contribution> item ) {
                final Contribution contribution = item.getModelObject();
                Link link = ContributionPage.getLinker( contribution ).getLink( "contribution-link", context, getPhetCycle() );
                link.add( new Label( "contribution-title", contribution.getTitle() ) );
                item.add( link );
                item.add( new Label( "contribution-authors", contribution.getAuthors() ) );
                item.add( ContributionEditPage.getLinker( contribution ).getLink( "edit-link", context, getPhetCycle() ) );
                item.add( new Link( "delete-link" ) {
                    @Override
                    public void onClick() {
                        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                            public boolean run( Session session ) {
                                Contribution contrib = (Contribution) session.load( Contribution.class, contribution.getId() );
                                contrib.deleteMe( session );
                                return true;
                            }
                        } );
                        if ( success ) {
                            setResponsePage( new RedirectPage( ContributionManagePage.getLinker().getRawUrl( context, getPhetCycle() ) ) );
                        }
                    }
                } );
            }
        } );
    }
}
