package edu.colorado.phet.website.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public abstract class AbstractLinker implements RawLinkable {

    public abstract String getSubUrl( PageContext context );

    public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
        return context.getPrefix() + getSubUrl( context );
    }

    public String getHref( PageContext context, PhetRequestCycle cycle ) {
        return "href=\"" + getRawUrl( context, cycle ) + "\"";
    }

    public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
        return new PhetLink( id, getRawUrl( context, cycle ) );
    }

}
