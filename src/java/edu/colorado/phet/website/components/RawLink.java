package edu.colorado.phet.website.components;

import org.apache.wicket.markup.html.link.Link;

/**
 * A raw URL link that can be inserted. Is stateless so it alone won't cause the page to be serialized and stored.
 */
public class RawLink extends Link {
    private String url;

    /**
     * Create a new raw link.
     *
     * @param id  The Wicket ID to use for the link
     * @param url The URL (relative or absolute) to put inside the link
     */
    public RawLink( String id, String url ) {
        super( id );
        this.url = url;
    }

    /**
     * Since this is stateless and we override the URL explicitly, this will never be called
     */
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
