package edu.colorado.phet.website.panels.contribution;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * A simple panel that is shown when no contribution search parameters were used
 */
public class ContributionEmptyPanel extends PhetPanel {

    private static final Logger logger = Logger.getLogger( ContributionEmptyPanel.class.getName() );

    public ContributionEmptyPanel( String id, final PageContext context ) {
        super( id, context );
    }

}