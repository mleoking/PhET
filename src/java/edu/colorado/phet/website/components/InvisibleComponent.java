package edu.colorado.phet.website.components;

import org.apache.wicket.markup.html.WebComponent;

public class InvisibleComponent extends WebComponent {
    public InvisibleComponent( String id ) {
        super( id );

        setVisible( false );
    }
}
