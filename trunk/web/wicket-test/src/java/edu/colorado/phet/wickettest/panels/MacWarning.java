package edu.colorado.phet.wickettest.panels;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.wickettest.util.PageContext;

public class MacWarning extends PhetPanel {
    public MacWarning( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/warning-v1.css" ) );
    }
}
