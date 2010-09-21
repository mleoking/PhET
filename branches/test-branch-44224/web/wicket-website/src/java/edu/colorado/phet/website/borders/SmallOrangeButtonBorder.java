package edu.colorado.phet.website.borders;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.util.PageContext;

public class SmallOrangeButtonBorder extends PhetBorder {
    public SmallOrangeButtonBorder( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.BUTTONS ) );
    }
}