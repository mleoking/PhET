package edu.colorado.phet.website.components;

import org.apache.wicket.model.IModel;

public class RawBodyLabel extends RawLabel {
    public RawBodyLabel( String id ) {
        super( id );
        setRenderBodyOnly( true );
    }

    public RawBodyLabel( String id, String label ) {
        super( id, label );
        setRenderBodyOnly( true );
    }

    public RawBodyLabel( String id, IModel model ) {
        super( id, model );
        setRenderBodyOnly( true );
    }
}
