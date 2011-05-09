package edu.colorado.phet.website.components;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

//TODO: Get rid of this class and use generics instead
public class StringTextField extends TextField {
    public StringTextField( String id, IModel object ) {
        super( id, object );
        setType( String.class );
    }
}
