package edu.colorado.phet.wickettest.content.about;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class AboutSponsorsPanel extends PhetPanel {
    public AboutSponsorsPanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "sponsors";
    }

    public static String getUrl() {
        return "about/sponsors";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}
