package edu.colorado.phet.website.panels;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.constants.Images;

public class PearsonSponsorPanel extends PhetPanel {
    public PearsonSponsorPanel( String id, final PageContext context ) {
        super( id, context );

        PhetLink link = new PhetLink( "pearson-link", "http://www.pearson.com" );
        add( link );
        link.add( new StaticImage( "pearson-image", Images.PEARSON_LOGO, "Pearson logo" ) );
    }
}