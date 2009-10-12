package edu.colorado.phet.website.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.util.PageContext;

public abstract class AbstractLinker implements RawLinkable {

    public abstract String getSubUrl( PageContext context );

    public String getRawUrl( PageContext context ) {
        return context.getPrefix() + getSubUrl( context );
    }

    public String getHref( PageContext context ) {
        return "href=\"" + getRawUrl( context ) + "\"";
    }

    public Link getLink( String id, PageContext context ) {
        return new PhetLink( id, getRawUrl( context ) );
    }

}
