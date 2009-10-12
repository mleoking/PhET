package edu.colorado.phet.website.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.util.PageContext;

public interface Linkable {
    public Link getLink( String id, PageContext context );
}
