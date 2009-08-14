package edu.colorado.phet.wickettest.util;

import org.apache.wicket.markup.html.link.Link;

public interface Linkable {
    public Link getLink( String id, PageContext context );
}
