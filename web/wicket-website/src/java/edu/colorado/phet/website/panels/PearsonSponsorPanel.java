package edu.colorado.phet.website.panels;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.util.PageContext;

public class PearsonSponsorPanel extends PhetPanel {
    public PearsonSponsorPanel( String id, final PageContext context ) {
        super( id, context );

        PhetLink link = new PhetLink( "pearson-link", "http://www.pearson.com" );
        add( link );
        link.add( new StaticImage( "pearson-image", "/images/sponsors/pearson.png", "Pearson logo" ) );
    }
}