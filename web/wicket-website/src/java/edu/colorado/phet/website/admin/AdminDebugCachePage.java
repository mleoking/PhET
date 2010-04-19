package edu.colorado.phet.website.admin;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.cache.IPanelCacheEntry;
import edu.colorado.phet.website.cache.PanelCache;

public class AdminDebugCachePage extends AdminPage {

    private static Logger logger = Logger.getLogger( AdminDebugCachePage.class.getName() );

    public AdminDebugCachePage( PageParameters parameters ) {
        super( parameters );

        // TODO: add ability to view cached content

        Set<IPanelCacheEntry> entrySet = PanelCache.get().getEntries();
        List<IPanelCacheEntry> entries = new LinkedList<IPanelCacheEntry>();

        entries.addAll( entrySet );

        Collections.sort( entries, new Comparator<IPanelCacheEntry>() {
            public int compare( IPanelCacheEntry a, IPanelCacheEntry b ) {
                int x = a.getPanelClass().getCanonicalName().compareTo( b.getPanelClass().getCanonicalName() );
                if ( x == 0 ) {
                    if ( a.getParentClass() != null && b.getParentClass() != null ) {
                        x = a.getParentClass().getCanonicalName().compareTo( b.getParentClass().getCanonicalName() );
                    }
                    if ( x == 0 ) {
                        x = a.getCacheId().compareTo( b.getCacheId() );
                    }
                }
                return x;
            }
        } );

        add( new ListView( "entries", entries ) {
            protected void populateItem( ListItem item ) {
                IPanelCacheEntry entry = (IPanelCacheEntry) item.getModel().getObject();
                item.add( new Label( "panel", entry.getPanelClass().getCanonicalName() ) );
                Class parentClass = entry.getParentClass();
                if ( parentClass == null ) {
                    item.add( new Label( "parent", "" ) );
                }
                else {
                    item.add( new Label( "parent", parentClass.getCanonicalName() ) );
                }
                item.add( new Label( "id", entry.getCacheId() ) );
            }
        } );

    }

}