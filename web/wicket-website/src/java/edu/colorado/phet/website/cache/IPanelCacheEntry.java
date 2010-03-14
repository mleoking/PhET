package edu.colorado.phet.website.cache;

import java.util.List;

import org.apache.wicket.Component;

/**
 * Represents a cached panel
 */
public interface IPanelCacheEntry {
    
    public Class getPanelClass();

    public Class getParentClass();

    public String getParentCacheID();

    public Component fabricate();

    public List<IPanelCacheDependency> getDependencies();
}
