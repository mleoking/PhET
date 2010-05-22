package edu.colorado.phet.website.panels;

import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.util.PageContext;

public class PearsonSponsorPanel extends PhetPanel {
    public PearsonSponsorPanel( String id, final PageContext context ) {
        super( id, context );

        RawLink link = new RawLink( "pearson-link", "http://www.pearson.com" );
        add( link );
        link.add( new StaticImage( "pearson-image", Images.PEARSON_LOGO, "Pearson logo" ) );
    }
}