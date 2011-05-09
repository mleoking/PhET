package edu.colorado.phet.website.components;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;

public class StringPasswordTextField extends PasswordTextField {
    public StringPasswordTextField( String id ) {
        super( id );
        setType( String.class );
    }

    public StringPasswordTextField( String id, IModel model ) {
        super( id, model );
        setType( String.class );
    }
}
