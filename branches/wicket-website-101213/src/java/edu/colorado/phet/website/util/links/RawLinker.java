package edu.colorado.phet.website.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * Gives a linker for a raw URL. Can be absolute or relative
 */
public class RawLinker implements RawLinkable {

    private String url;

    public RawLinker( String url ) {
        this.url = url;
    }

    public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
        return url;
    }

    public String getHref( PageContext context, PhetRequestCycle cycle ) {
        return "href=\"" + url + "\"";
    }

    public String getDefaultRawUrl() {
        return url;
    }

    public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
        return new RawLink( id, url );
    }
}
