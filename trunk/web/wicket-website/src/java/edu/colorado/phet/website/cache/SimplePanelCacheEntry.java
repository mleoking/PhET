package edu.colorado.phet.website.cache;

import java.util.Locale;
import java.util.Map;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * Cacheable panel entry. Upon the first non-cached request, it will deliver the panel specified in
 * {@see constructPanel(String,PageContext)} with modifications that will cause it to be put in the cache when rendered.
 * Afterwards (when the panel is in the cache), a placeholder panel will be inserted with the prerendered version.
 * <p/>
 * <pre>
 * {@code
 * add(newSimplePanelCacheEntry( SponsorsPanel.class,null,getPageContext().getLocale(), "tester" ) {
 * public PhetPanel constructPanel( String id, PageContext context ) {
 * return new SponsorsPanel( id, context );
 * }
 * }.instantiate( "sponsors-panel", getPageContext() ) );
 * }
 * </pre>
 * <p/>
 * After creation, instantiate() can be called to create the necessary panel. It will
 * automatically create either the normal panel (implemented in constructPanel()) or a cached version of that panel.
 * <p/>
 * Additionally, parameters can be stored within the map to be used later.
 */
public abstract class SimplePanelCacheEntry extends AbstractPanelCacheEntry implements Serializable {

    /**
     * Represents the cached header string (everything inserted into the head tag)
     */
    private CharSequence header;

    /**
     * Represents the main body of the cached panel
     */
    private CharSequence body;

    private Map<Object, Object> map;

    private static final Logger logger = Logger.getLogger( SimplePanelCacheEntry.class.getName() );

    /**
     * @param panelClass    The class of the panel to be cached
     * @param parentClass   The (optionally null) class of the component where the panel will be placed.
     * @param locale        The locale being used
     * @param parentCacheId A parent-specific string to identify this particular construction of the panel
     */
    public SimplePanelCacheEntry( Class panelClass, Class parentClass, Locale locale, String parentCacheId ) {
        super( panelClass, parentClass, locale, parentCacheId );
    }

    public final PhetPanel fabricate( String id, PageContext context ) {
        CacheReplacementPanel panel = new CacheReplacementPanel( id, context, header, body );

        // make the cache map accessible to the next panel
        panel.setCacheMap( map );
        return panel;
    }

    /**
     * Override this to create the "regular" non-cached version of the panel
     *
     * @param id      The Wicket id to use
     * @param context The page context
     * @return A regular PhetPanel
     */
    public abstract PhetPanel constructPanel( String id, PageContext context );

    /**
     * Get a copy of a PhetPanel to add to our component
     *
     * @param id      The Wicket id to use
     * @param context The page context
     * @return A (possibly cached) PhetPanel
     */
    public final PhetPanel instantiate( String id, PageContext context ) {
        PanelCache cache = PanelCache.get();
        IPanelCacheEntry entry = cache.getMatching( this );

        if ( entry == null || !context.isCacheable() ) {
            if ( entry == null ) {
                logger.debug( "not cached, constructing original content for " + this );
            }
            else {
                logger.debug( "not cacheable, constructing original content for " + this );
            }
            PhetPanel panel = constructPanel( id, context );
            panel.setCacheEntry( this );
            return panel;
        }
        else {
            logger.debug( "cached, delivering " + entry );
            return entry.fabricate( id, context );
        }
    }

    /**
     * Sets the header string. Should only be called during the rendering of the original panel
     *
     * @param header
     */
    public void setHeader( CharSequence header ) {
        this.header = header;

        //logger.debug( "set header: " + header );
    }

    /**
     * Sets the main body string. Should only be called during the rendering of the original panel
     *
     * @param body
     */
    public void setBody( CharSequence body ) {
        this.body = body;

        //logger.debug( "set body: " + body );
    }

    /**
     * Sets the map, useful for storing data pulled from the DB when the pre-cached version is constructed
     *
     * @param map
     */
    public void setMap( Map<Object, Object> map ) {
        this.map = map;
    }
}
