package edu.colorado.phet.wickettest.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.util.PageContext;

public interface Linkable {
    public Link getLink( String id, PageContext context );
}
