package edu.colorado.phet.website.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * The common base for RawLinkable. This class assumes that the object that can be linked to is in a locale or context
 * specific area. The page that creates the link can then specify what prefix to add to the sub URL.
 * <p/>
 * For example, an object of this class could represent a sub URL of 'about/test'. Then separate other objects can
 * create links to '/en/about/test', '/ar_SA/about/test', or '/translation/5/about/test', with different page contexts
 */
public abstract class AbstractLinker implements RawLinkable {

    /**
     * For a page normally accessible in English at 'http://phet.colorado.edu/en/about/test', this should return
     * "about/test"
     *
     * @param context The page context
     * @return The URL relative to the active translation (or context), without the leading slash.
     */
    public abstract String getSubUrl( PageContext context );

    public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
        return context.getPrefix() + getSubUrl( context );
    }

    public String getHref( PageContext context, PhetRequestCycle cycle ) {
        return "href=\"" + getRawUrl( context, cycle ) + "\"";
    }

    public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
        return new RawLink( id, getRawUrl( context, cycle ) );
    }

    public String getDefaultRawUrl() {
        return getRawUrl( PageContext.getNewDefaultContext(), PhetRequestCycle.get() );
    }

}
