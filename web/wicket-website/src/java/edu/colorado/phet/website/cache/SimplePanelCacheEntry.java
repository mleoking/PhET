package edu.colorado.phet.website.cache;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public abstract class SimplePanelCacheEntry extends AbstractPanelCacheEntry {

    private CharSequence header;
    private CharSequence body;

    private static Logger logger = Logger.getLogger( SimplePanelCacheEntry.class.getName() );

    public SimplePanelCacheEntry( Class panelClass, Class parentClass, String parentCacheId ) {
        super( panelClass, parentClass, parentCacheId );
    }

    public final PhetPanel fabricate( String id, PageContext context ) {
        return new PhetPanel( id, context ) {
            @Override
            public void renderHead( HtmlHeaderContainer container ) {
                RequestCycle cycle = getRequestCycle();
                Response response = cycle.getResponse();
                response.write( header );
            }

            @Override
            protected void onRender( MarkupStream markupStream ) {
                markupStream.skipComponent();
                RequestCycle cycle = getRequestCycle();
                Response response = cycle.getResponse();
                response.write( body );
            }
        };
    }

    public SimplePanelCacheEntry ignoreParent() {
        parentClass = null;
        parentCacheId = null;
        return this;
    }

    public abstract PhetPanel constructPanel( String id, PageContext context );

    public final PhetPanel instantiate( String id, PageContext context ) {
        PanelCache cache = PanelCache.get();

        if ( cache.contains( this ) ) {
            return fabricate( id, context );
        }
        else {
            PhetPanel panel = constructPanel( id, context );
            panel.setCacheEntry( this );
            return panel;
        }
    }

    public void setHeader( CharSequence header ) {
        this.header = header;

        logger.debug( "header:\n" + header + "\nendheader" );
    }

    public void setBody( CharSequence body ) {
        this.body = body;

        logger.debug( "body:\n" + body + "\nendbody" );
    }
}
