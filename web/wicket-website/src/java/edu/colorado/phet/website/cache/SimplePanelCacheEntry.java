package edu.colorado.phet.website.cache;

import java.util.Locale;

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
 * add(newSimplePanelCacheEntry( CacheTestPanel.class,WorkshopsPanel.class, "tester" ) {
 *     public PhetPanel constructPanel( String id, PageContext context ) {
 *         return new CacheTestPanel( id, context );
 *     }
 * }.instantiate( "test-panel", context ) );
 * }
 * </pre>
 */
public abstract class SimplePanelCacheEntry extends AbstractPanelCacheEntry {

    private CharSequence header;
    private CharSequence body;

    private static Logger logger = Logger.getLogger( SimplePanelCacheEntry.class.getName() );

    public SimplePanelCacheEntry( Class panelClass, Class parentClass, Locale locale, String parentCacheId ) {
        super( panelClass, parentClass, locale, parentCacheId );
    }

    public final PhetPanel fabricate( String id, PageContext context ) {
        return new CacheReplacementPanel( id, context, header, body );
    }

    public abstract PhetPanel constructPanel( String id, PageContext context );

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

    public void setHeader( CharSequence header ) {
        this.header = header;
    }

    public void setBody( CharSequence body ) {
        this.body = body;
    }

}
