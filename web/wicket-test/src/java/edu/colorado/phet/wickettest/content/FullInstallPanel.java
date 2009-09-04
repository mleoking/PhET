package edu.colorado.phet.wickettest.content;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class FullInstallPanel extends PhetPanel {
    public FullInstallPanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "get-phet.full-install";
    }

    public static String getUrl() {
        return "get-phet/full-install";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}