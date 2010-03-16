package edu.colorado.phet.website.cache;

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

    public SimplePanelCacheEntry( Class panelClass, Class parentClass, String parentCacheId ) {
        super( panelClass, parentClass, parentCacheId );
    }

    public final PhetPanel fabricate( String id, PageContext context ) {
        return new SimplePanelCachePanel( id, context, header, body );
    }

    public SimplePanelCacheEntry ignoreParent() {
        parentClass = null;
        parentCacheId = null;
        return this;
    }

    public abstract PhetPanel constructPanel( String id, PageContext context );

    public final PhetPanel instantiate( String id, PageContext context ) {
        PanelCache cache = PanelCache.get();
        IPanelCacheEntry entry = cache.getMatching( this );

        if ( entry != null ) {
            logger.debug( "cached, delivering fabricated content" );
            return entry.fabricate( id, context );
        }
        else {
            logger.debug( "not cached, constructing original content" );
            PhetPanel panel = constructPanel( id, context );
            panel.setCacheEntry( this );
            return panel;
        }
    }

    public void setHeader( CharSequence header ) {
        this.header = header;

        logger.debug( "& header:\n" + header + "\nendheader" );
    }

    public void setBody( CharSequence body ) {
        this.body = body;

        logger.debug( "& body:\n" + body + "\nendbody" );
    }
}
