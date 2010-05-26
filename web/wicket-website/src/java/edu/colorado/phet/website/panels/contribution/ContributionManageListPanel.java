package edu.colorado.phet.website.panels.contribution;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.content.contribution.ContributionEditPage;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * Shows a list of contributions, their authors, and a list of actions that can be taken
 * TODO: possibly get rid of actions for a more compact / informative version?
 */
public class ContributionManageListPanel extends PhetPanel {

    public ContributionManageListPanel( String id, final PageContext context, List<Contribution> contributions ) {
        super( id, context );

        // don't show whatever this was contained in, so that the table rows are nested directly under the table tags in
        // the parent
        setRenderBodyOnly( true );

        add( new ListView<Contribution>( "contributions", contributions ) {
            protected void populateItem( ListItem<Contribution> item ) {
                Contribution contribution = item.getModelObject();
                Link link = ContributionPage.getLinker( contribution ).getLink( "contribution-link", context, getPhetCycle() );
                link.add( new Label( "contribution-title", contribution.getTitle() ) );
                item.add( link );
                item.add( new Label( "contribution-authors", contribution.getAuthors() ) );
                item.add( ContributionEditPage.getLinker( contribution ).getLink( "edit-link", context, getPhetCycle() ) );
            }
        } );
    }
}
