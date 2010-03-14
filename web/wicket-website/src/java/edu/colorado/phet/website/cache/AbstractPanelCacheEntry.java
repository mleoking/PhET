package edu.colorado.phet.website.cache;

public abstract class AbstractPanelCacheEntry implements IPanelCacheEntry {

    private Class panelClass;
    private Class parentClass;
    private String parentCacheId;

    protected AbstractPanelCacheEntry( Class panelClass, Class parentClass, String parentCacheId ) {
        this.panelClass = panelClass;
        this.parentClass = parentClass;
        this.parentCacheId = parentCacheId;
    }

    public Class getPanelClass() {
        return panelClass;
    }

    public Class getParentClass() {
        return parentClass;
    }

    public String getParentCacheID() {
        return parentCacheId;
    }

    @Override
    public boolean equals( Object o ) {
        if ( o == null ) {
            return false;
        }

        /*
        * intuitively, they must have the same panel class. parent class must be equal (either both null or both the
        * same class), and if they specify the parent class, then the parent cache ID must be equal (either both null
        * or both the same string value)
        */
        if ( o instanceof IPanelCacheEntry ) {
            IPanelCacheEntry entry = (IPanelCacheEntry) o;
            return (
                    getPanelClass().equals( entry.getPanelClass() )
                    && (
                            getParentClass() == null
                            ? ( entry.getParentClass() == null )
                            : ( getParentClass().equals( entry.getParentClass() ) )
                    )
                    && (
                            getParentClass() == null
                            || (
                                    getParentCacheID() == null
                                    ? ( entry.getParentCacheID() == null )
                                    : ( getParentCacheID().equals( entry.getParentCacheID() ) )
                            )
                    )
            );
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int ret = getPanelClass().hashCode();
        ret *= 13;
        if ( getParentClass() != null ) {
            ret += getParentClass().hashCode();
            ret *= 17;
            if ( getParentCacheID() != null ) {
                ret += getParentCacheID().hashCode();
            }
        }
        return ret;
    }
}
