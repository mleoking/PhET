package edu.colorado.phet.website.content;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetPage;

public class BlankPage extends PhetPage {

    public BlankPage( PageParameters parameters ) {
        super( parameters );

        addTitle( "" );
    }
}
