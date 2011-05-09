package edu.colorado.phet.website.util.links;

import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * Something that can be linked to directly by a URL.
 */
public interface RawLinkable extends Linkable {
    /**
     * @return A string that represents either an external URL or a relative URL to the site root.
     */
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

    /**
     * @return A string like getRawUrl(), but with the '/en/' prefix as an English default
     */
    public String getDefaultRawUrl();
}
