package edu.colorado.phet.website.panels.contribution;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public class ContributionSearchPanel extends PhetPanel {

    private static Logger logger = Logger.getLogger( ContributionSearchPanel.class.getName() );

    public ContributionSearchPanel( String id, final PageContext context, final PageParameters params ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/contribution-browse-v1.css" ) );

    }

}