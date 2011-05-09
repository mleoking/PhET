package edu.colorado.phet.website.admin;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.cache.CacheItem;
import edu.colorado.phet.website.cache.IPanelCacheEntry;
import edu.colorado.phet.website.cache.PanelCache;

public class AdminDebugCachePage extends AdminPage {

    private static final Logger logger = Logger.getLogger( AdminDebugCachePage.class.getName() );

    public AdminDebugCachePage( PageParameters parameters ) {
        super( parameters );

        // TODO: (low) add ability to view cached content

        Set<CacheItem> entrySet = PanelCache.get().getEntries();
        List<CacheItem> entries = new LinkedList<CacheItem>();

        entries.addAll( entrySet );

        Collections.sort( entries, new Comparator<CacheItem>() {
            public int compare( CacheItem a, CacheItem b ) {
                int x = a.getEntry().getPanelClass().getCanonicalName().compareTo( b.getEntry().getPanelClass().getCanonicalName() );
                if ( x == 0 ) {
                    if ( a.getEntry().getParentClass() != null && b.getEntry().getParentClass() != null ) {
                        x = a.getEntry().getParentClass().getCanonicalName().compareTo( b.getEntry().getParentClass().getCanonicalName() );
                    }
                    if ( x == 0 ) {
                        x = a.getEntry().getCacheId().compareTo( b.getEntry().getCacheId() );
                    }
                }
                return x;
            }
        } );

        add( new ListView( "entries", entries ) {
            protected void populateItem( ListItem item ) {
                CacheItem entry = (CacheItem) item.getModel().getObject();
                IPanelCacheEntry panelEntry = entry.getEntry();
                item.add( new Label( "panel", entry.getEntry().getPanelClass().getCanonicalName() ) );
                Class parentClass = entry.getEntry().getParentClass();
                if ( parentClass == null ) {
                    item.add( new Label( "parent", "" ) );
                }
                else {
                    item.add( new Label( "parent", parentClass.getCanonicalName() ) );
                }
                item.add( new Label( "id", entry.getEntry().getCacheId() ) );
                item.add( new Label( "date", entry.getAddDate().toString() ) );
            }
        } );

    }

}