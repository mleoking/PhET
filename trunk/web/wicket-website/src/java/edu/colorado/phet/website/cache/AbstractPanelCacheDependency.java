package edu.colorado.phet.website.cache;

public abstract class AbstractPanelCacheDependency implements IPanelCacheDependency {
    public Class[] getDependentClasses() {
        return new Class[0];
    }

    public String[] getDependentEvents() {
        return new String[0];
    }

    public boolean willInvalidateOnObject( Object object ) {
        return false;
    }
}
