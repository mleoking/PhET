package edu.colorado.phet.website.util.links;

import edu.colorado.phet.website.util.PageContext;

public interface RawLinkable extends Linkable {
    public String getRawUrl( PageContext context );

    public String getHref( PageContext context );
}
