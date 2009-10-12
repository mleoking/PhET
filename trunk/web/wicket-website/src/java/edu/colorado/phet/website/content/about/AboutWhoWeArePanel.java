package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

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