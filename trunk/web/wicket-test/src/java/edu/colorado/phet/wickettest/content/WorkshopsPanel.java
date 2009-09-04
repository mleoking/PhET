package edu.colorado.phet.wickettest.content;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class WorkshopsPanel extends PhetPanel {
    public WorkshopsPanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "workshops";
    }

    public static String getUrl() {
        return "workshops";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}