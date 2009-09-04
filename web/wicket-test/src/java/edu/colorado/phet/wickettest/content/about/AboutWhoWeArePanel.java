package edu.colorado.phet.wickettest.content.about;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class AboutWhoWeArePanel extends PhetPanel {
    public AboutWhoWeArePanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "about.who-we-are";
    }

    public static String getUrl() {
        return "about/who-we-are";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}