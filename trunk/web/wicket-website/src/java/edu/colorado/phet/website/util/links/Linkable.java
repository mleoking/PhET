package edu.colorado.phet.website.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * An interface when we need to specify something to which we can link.
 * <p/>
 * NOTE: does not guarantee that there can be a URL present. Ajax (and other) links can be returned from a Linkable.
 */
public interface Linkable {
    /**
     * Get a Wicket link that can be added directly to a page
     *
     * @param id      The Wicket ID that the link should have
     * @param context The page context
     * @param cycle   The request cycle
     * @return A new link
     */
    public Link getLink( String id, PageContext context, PhetRequestCycle cycle );
}
