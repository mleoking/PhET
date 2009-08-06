package edu.colorado.phet.wickettest.content;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.LocalizedLabel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetLink;

public class AboutPhetPanel extends PhetPanel {
    public AboutPhetPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedLabel( "about-p1", "about.p1", new String[]{"href=\"http://phet.colorado.edu/simulations/index.php\"", "href=\"http://phet.colorado.edu/research/index.php\""} ) );
        add( new LocalizedLabel( "about-p2", "about.p2" ) );
        add( new LocalizedLabel( "about-p3", "about.p3", new String[]{"href=\"http://phet.colorado.edu/about/legend.php\""} ) );
        add( new LocalizedLabel( "about-p4", "about.p4", new String[]{
                "href=\"http://phet.colorado.edu/index.php\"",
                "href=\"http://phet.colorado.edu/tech_support/support-java.php\"",
                "href=\"http://phet.colorado.edu/tech_support/support-flash.php\""
        } ) );
    }

    public static String getKey() {
        return "about";
    }

    public static String getUrl() {
        return "about";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}
