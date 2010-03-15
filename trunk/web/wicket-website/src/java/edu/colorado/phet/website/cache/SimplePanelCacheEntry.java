package edu.colorado.phet.website.cache;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public abstract class SimplePanelCacheEntry extends AbstractPanelCacheEntry {

    public SimplePanelCacheEntry( Class panelClass, Class parentClass, String parentCacheId ) {
        super( panelClass, parentClass, parentCacheId );
    }

    public final PhetPanel fabricate( String id, PageContext context ) {
        return new PhetPanel( id, context ) {
//            @Override
//            protected void onRender( MarkupStream markupStream ) {
//                super.onRender( markupStream );
//            }
        };
    }

    public abstract PhetPanel constructPanel( String id, PageContext context );

    public void addStylesheets( PhetPanel panel ) {
        // TODO: remove this or the main one in phetpanel
    }

    public final PhetPanel instantiate() {
        return null;
    }

}
