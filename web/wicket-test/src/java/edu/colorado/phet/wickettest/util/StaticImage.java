package edu.colorado.phet.wickettest.util;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class StaticImage extends WebComponent {

    public StaticImage( String id, IModel model ) {
        super( id, model );
    }

    public StaticImage( String id, String url ) {
        this( id, new Model( url ) );
    }

    public StaticImage( String id ) {
        super( id );
    }

    protected void onComponentTag( ComponentTag tag ) {
        checkComponentTag( tag, "img" );
        super.onComponentTag( tag );
        tag.put( "src", getModelObjectAsString() );
    }

}