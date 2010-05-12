package edu.colorado.phet.website.util.links;

import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public interface RawLinkable extends Linkable {
    public String getRawUrl( PageContext context, PhetRequestCycle cycle );

    /**
     * Returns a string that can be inserted into MessageFormat style strings like:
     * <code><pre>
     * <a {0}>link</a>
     * </pre></code>
     *
     * @param context The page context
     * @param cycle   The request cycle
     * @return A string that looks like href="...."
     */
    public String getHref( PageContext context, PhetRequestCycle cycle );

    public String getDefaultRawUrl();
}
