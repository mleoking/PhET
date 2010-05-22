package edu.colorado.phet.website.panels;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.util.PageContext;

public class SponsorsPanel extends PhetPanel {
    public SponsorsPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.SPONSORS ) );

        add( AboutSponsorsPanel.getLinker().getLink( "sponsors-link", context, getPhetCycle() ) );
    }
}
