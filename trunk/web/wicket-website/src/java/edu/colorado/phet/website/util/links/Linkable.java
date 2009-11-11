package edu.colorado.phet.website.util.links;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public interface Linkable {
    public Link getLink( String id, PageContext context, PhetRequestCycle cycle );
}
