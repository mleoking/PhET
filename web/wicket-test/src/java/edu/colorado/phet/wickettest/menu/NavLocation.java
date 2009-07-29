package edu.colorado.phet.wickettest.menu;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class NavLocation {

    private String key;
    private List<NavLocation> children = new LinkedList<NavLocation>();
    private NavLocation parent;
    private Linkable linker;

    public NavLocation( NavLocation parent, String key, Linkable linker ) {
        this.parent = parent;
        this.key = key;
        this.linker = linker;
    }

    public String getBaseKey() {
        if ( getParent() == null ) {
            return getKey();
        }
        else {
            return getParent().getBaseKey();
        }
    }

    @Override
    public String toString() {
        return "NL: " + getKey();
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

    public Linkable getLinker() {
        return linker;
    }

    public Link getLink( String id, PageContext context ) {
        return linker.getLink( id, context );
    }

    public void addChild( NavLocation location ) {
        children.add( location );
    }

}
