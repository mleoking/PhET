package edu.colorado.phet.wickettest.components;

import org.apache.wicket.markup.html.link.Link;

public class PhetLink extends Link {
    private String url;

    public PhetLink( String id, String url ) {
        super( id );
        this.url = url;
    }

    public void onClick() {

    }

    @Override
    protected CharSequence getURL() {
        return url;
    }


    @Override
    protected boolean getStatelessHint() {
        return true;
    }
}
