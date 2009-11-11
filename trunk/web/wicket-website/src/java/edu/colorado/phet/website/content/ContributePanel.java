package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributePanel extends PhetPanel {
    public ContributePanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "contribute-main", "contribute.main", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?Subject=Financial%20Contribution&amp;Body=Dear%20Sir%20or%20Madam:%20I%20would%20like%20to%20make%20a%20generous%20donation%20to%20PhET.\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "contribute-thanks", "contribute.thanks", new Object[]{
                AboutSponsorsPanel.getLinker().getHref( context ),
                "href=\"http://www.royalinteractive.com/\""
        } ) );
    }

    public static String getKey() {
        return "contribute";
    }

    public static String getUrl() {
        return "contribute";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context ) {
                if ( DistributionHandler.redirectPageClassToProduction( context.getCycle(), ContributePanel.class ) ) {
                    return "http://phet.colorado.edu/contribute/index.php";
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