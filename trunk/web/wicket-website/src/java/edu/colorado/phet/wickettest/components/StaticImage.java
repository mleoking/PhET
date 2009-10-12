package edu.colorado.phet.wickettest.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

public class StaticImage extends WebComponent {

    private String url = null;
    private String alt = null;
    private IModel altModel = null;

    public StaticImage( String id, String url, String alt ) {
        super( id );
        this.url = url;
        this.alt = alt;
    }

    public StaticImage( String id, String url, String alt, IModel altModel ) {
        // TODO: improve calls to this
        super( id );
        this.url = url;
        this.alt = alt;
        this.altModel = altModel;
    }

    protected void onComponentTag( ComponentTag tag ) {
        checkComponentTag( tag, "img" );
        super.onComponentTag( tag );
        tag.put( "src", url );
        if ( alt != null ) {
            tag.put( "alt", alt );
        }
        if ( altModel != null ) {
            tag.put( "alt", altModel.getObject().toString() );
        }
    }

    @Override
    protected boolean getStatelessHint() {
        return true;
    }
}