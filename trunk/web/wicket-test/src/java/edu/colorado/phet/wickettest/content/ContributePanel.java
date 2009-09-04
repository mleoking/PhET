package edu.colorado.phet.wickettest.content;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class ContributePanel extends PhetPanel {
    public ContributePanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "contribute-main", "contribute.main", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?Subject=Financial%20Contribution&amp;Body=Dear%20Sir%20or%20Madam:%20I%20would%20like%20to%20make%20a%20generous%20donation%20to%20PhET.\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "contribute-thanks", "contribute.thanks", new Object[]{
                "href=\"/about/sponsors\"",
                "href=\"http://www.royalinteractive.com/\""
        } ) );
    }

    public static String getKey() {
        return "contribute";
    }

    public static String getUrl() {
        return "contribute";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}