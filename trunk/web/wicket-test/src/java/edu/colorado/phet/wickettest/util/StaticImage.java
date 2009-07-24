package edu.colorado.phet.wickettest.util;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;

public class StaticImage extends WebComponent {


    /*
    public StaticImage( String id, IModel model ) {
        super( id, model );
    }

    public StaticImage( String id, String url ) {
        this( id, new Model( url ) );
    }

    public StaticImage( String id ) {
        super( id );
    }
    */

    private String url;
    private String alt;

    public StaticImage( String id, String url, String alt ) {
        super( id );
        this.url = url;
        this.alt = alt;
    }

    protected void onComponentTag( ComponentTag tag ) {
        checkComponentTag( tag, "img" );
        super.onComponentTag( tag );
        tag.put( "src", url );
        if ( alt != null ) {
            tag.put( "alt", alt );
        }
    }

}