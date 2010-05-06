package edu.colorado.phet.website.borders;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.constants.CSS;

public class SmallOrangeButtonBorder extends PhetBorder {
    public SmallOrangeButtonBorder( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.BUTTONS ) );
    }
}