package edu.colorado.phet.website.components;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class StringTextField extends TextField {
    public StringTextField( String id ) {
        super( id );
        setType( String.class );
    }

    public StringTextField( String id, Class type ) {
        super( id, type );
        setType( String.class );
    }

    public StringTextField( String id, IModel object ) {
        super( id, object );
        setType( String.class );
    }

    public StringTextField( String id, IModel model, Class type ) {
        super( id, model, type );
        setType( String.class );
    }
}
