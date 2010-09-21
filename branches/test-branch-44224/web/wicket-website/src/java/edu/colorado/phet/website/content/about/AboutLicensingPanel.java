package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutLicensingPanel extends PhetPanel {
    public AboutLicensingPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "about-licensing-contact-phet", "about.licensing.contactPhet", new Object[]{
                Linkers.PHET_HELP_LINK
        } ) );

        add( new LocalizedText( "about-licensing-cca-intro", "about.licensing.cca.intro", new Object[]{
                "href=\"http://phet.colorado.edu\"",
                "href=\"http://creativecommons.org/licenses/by/3.0/us/\""
        } ) );

        add( new LocalizedText( "about-licensing-gpl-sims", "about.licensing.gplSims" ) );

        add( new LocalizedText( "about-licensing-cca-fulltext", "about.licensing.ccaFulltext", new Object[]{
                "href=\"http://creativecommons.org/licenses/by/3.0/us/legalcode\""
        } ) );

        add( new LocalizedText( "about-licensing-gpl-intro", "about.licensing.gplIntro", new Object[]{
                "href=\"http://phet.colorado.edu\"",
                "href=\"http://creativecommons.org/licenses/GPL/2.0/\""
                //"href=\"http://creativecommons.org/licenses/by/3.0/us/\""
        } ) );

        add( new LocalizedText( "about-licensing-gpl-fulltext", "about.licensing.gpl.fullText", new Object[]{
                "href=\"http://www.gnu.org/licenses/old-licenses/gpl-2.0.html\""
        } ) );

        add( new LocalizedText( "about-licensing-gpl-source-code", "about.licensing.gpl.sourceCode", new Object[]{
                AboutSourceCodePanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "about-licensing-alternative-license-options", "about.licensing.alternativeLicenseOptions", new Object[]{
                Linkers.PHET_HELP_LINK
        } ) );

        add( new LocalizedText( "about-licensing-source-code-link", "about.licensing.sourceCodeLink", new Object[]{
                AboutSourceCodePanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "about-licensing-agreement-full-text", "about.licensing.softwareAgreementFullText", new Object[]{
                Linkers.SOFTWARE_AGREEMENT.getHref( context, getPhetCycle() )
        } ) );

    }

    public static String getKey() {
        return "about.licensing";
    }

    public static String getUrl() {
        return "about/licensing";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, AboutLicensingPanel.class ) ) {
                    return "http://phet.colorado.edu/about/licensing.php";
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