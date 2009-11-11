package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutSourceCodePanel extends PhetPanel {
    public AboutSourceCodePanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "source-code-location", "about.source-code.location", new Object[]{
                "href=\"https://phet.unfuddle.com/projects/9404/repositories/23262/browse/head/trunk\"",
                "href=\"http://tortoisesvn.tigris.org/\"",
                "href=\"http://www.syntevo.com/smartsvn/download.jsp\""
        } ) );

    }

    public static String getKey() {
        return "about.source-code";
    }

    public static String getUrl() {
        return "about/source-code";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context ) {
                if ( DistributionHandler.redirectPageClassToProduction( context.getCycle(), AboutSourceCodePanel.class ) ) {
                    return "http://phet.colorado.edu/about/source-code.php";
                }
                else {
                    return super.getRawUrl( context );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}