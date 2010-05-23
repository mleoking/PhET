package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.content.DonatePanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutContactPanel extends PhetPanel {
    public AboutContactPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "about-contact-licensingText", "about.contact.licensingText", new Object[]{
                AboutLicensingPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "about-contact-correspondence", "about.contact.correspondence", new Object[]{
                Linkers.PHET_HELP_LINK,
                DonatePanel.getLinker().getHref( context, getPhetCycle() )
        } ) );
    }

    public static String getKey() {
        return "about.contact";
    }

    public static String getUrl() {
        return "about/contact";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, AboutContactPanel.class ) ) {
                    return "http://phet.colorado.edu/about/contact.php";
                }
                else {
                    return super.getRawUrl( context, cycle );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}