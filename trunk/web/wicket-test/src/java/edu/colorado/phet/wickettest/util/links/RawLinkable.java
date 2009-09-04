package edu.colorado.phet.wickettest.util.links;

import edu.colorado.phet.wickettest.util.PageContext;

public interface RawLinkable extends Linkable {
    public String getRawUrl( PageContext context );

    public String getHref( PageContext context );
}
