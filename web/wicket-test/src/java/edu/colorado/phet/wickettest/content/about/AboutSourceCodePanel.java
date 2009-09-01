package edu.colorado.phet.wickettest.content.about;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

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

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}