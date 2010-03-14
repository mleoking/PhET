package edu.colorado.phet.website.cache;

import java.util.List;

import org.apache.wicket.Component;

public class SimplePanelCacheEntry extends AbstractPanelCacheEntry {

    public SimplePanelCacheEntry( Class panelClass, Class parentClass, String parentCacheId ) {
        super( panelClass, parentClass, parentCacheId );
    }

    public Component fabricate() {
        return null;
    }

    public List<IPanelCacheDependency> getDependencies() {
        return null;
    }
}
