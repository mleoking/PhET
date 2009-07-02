package edu.colorado.phet.wickettest.util;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.border.Border;

public class PhetLink extends Border {

    private String url;

    public PhetLink( String id, String url ) {
        super( id );

        this.url = url;

    }

    @Override
    protected void onComponentTag( ComponentTag tag ) {
        checkComponentTag( tag, "a" );
        super.onComponentTag( tag );
        tag.put( "href", url );
    }
}
