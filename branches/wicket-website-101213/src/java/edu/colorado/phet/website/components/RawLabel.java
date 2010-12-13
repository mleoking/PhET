package edu.colorado.phet.website.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * A simple Wicket label that does not escape its strings. See Wicket Label for docs
 */
public class RawLabel extends Label {
    public RawLabel( String id ) {
        super( id );
        setEscapeModelStrings( false );
    }

    public RawLabel( String id, String label ) {
        super( id, label );
        setEscapeModelStrings( false );
    }

    public RawLabel( String id, IModel model ) {
        super( id, model );
        setEscapeModelStrings( false );
    }
}
