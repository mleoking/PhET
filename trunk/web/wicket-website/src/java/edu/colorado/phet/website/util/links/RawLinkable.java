package edu.colorado.phet.website.util.links;

import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public interface RawLinkable extends Linkable {
    public String getRawUrl( PageContext context, PhetRequestCycle cycle );

    public String getHref( PageContext context, PhetRequestCycle cycle );
}
