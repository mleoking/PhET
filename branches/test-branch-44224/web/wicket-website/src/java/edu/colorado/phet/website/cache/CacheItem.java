package edu.colorado.phet.website.cache;

import java.util.Date;

/**
 * The immutable entry stored in the panel cache
 */
public class CacheItem {
    private IPanelCacheEntry entry;
    private Date addDate;

    CacheItem( IPanelCacheEntry entry ) {
        this.entry = entry;
        addDate = new Date();
    }

    public IPanelCacheEntry getEntry() {
        return entry;
    }

    public Date getAddDate() {
        return addDate;
    }
}
