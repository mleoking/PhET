package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.templates.PhetPage;

public class BlankPage extends PhetPage {

    public BlankPage( PageParameters parameters ) {
        super( parameters, true );

        addTitle( "" );
    }
}
