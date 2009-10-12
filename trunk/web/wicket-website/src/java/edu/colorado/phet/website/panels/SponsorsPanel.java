package edu.colorado.phet.website.panels;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.util.PageContext;

public class SponsorsPanel extends PhetPanel {
    public SponsorsPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/sponsors-v1.css" ) );

        add( AboutSponsorsPanel.getLinker().getLink( "sponsors-link", context ) );
    }
}
