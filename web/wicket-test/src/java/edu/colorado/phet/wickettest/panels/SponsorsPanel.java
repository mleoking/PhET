package edu.colorado.phet.wickettest.panels;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.content.about.AboutSponsors;

public class SponsorsPanel extends PhetPanel {
    public SponsorsPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/sponsors-v1.css" ) );

        add( AboutSponsors.getLinker().getLink( "sponsors-link", context ));
    }
}
