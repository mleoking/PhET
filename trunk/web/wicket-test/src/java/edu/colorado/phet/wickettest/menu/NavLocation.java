package edu.colorado.phet.wickettest.menu;

import java.util.LinkedList;
import java.util.List;

public class NavLocation {

    private String key;
    private List<NavLocation> children = new LinkedList<NavLocation>();
    private NavLocation parent;

    public NavLocation( NavLocation parent, String key ) {
        this.parent = parent;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public List<NavLocation> getChildren() {
        return children;
    }

    public NavLocation getParent() {
        return parent;
    }

    public void addChild( NavLocation location ) {
        children.add( location );
    }
}
