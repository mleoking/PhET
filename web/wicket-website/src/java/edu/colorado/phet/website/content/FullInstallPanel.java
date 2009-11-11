package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.about.AboutContactPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class FullInstallPanel extends PhetPanel {
    public FullInstallPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "text-installer-description", "get-phet.full-install.installerDescription", new Object[]{
                "href=\"http://www.java.com/\"",
                "href=\"http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=shockwaveFlash\"",
                "href=\"http://www.mozilla.com/en-US/\"",
                "href=\"http://www.microsoft.com/windows/products/winfamily/ie/default.mspx\""
        } ) );

//        add( new LocalizedText( "size-win", "get-phet.full-install.downloadInMB", new Object[]{"91"} ) );
//        add( new LocalizedText( "size-mac", "get-phet.full-install.downloadInMB", new Object[]{"80"} ) );
//        add( new LocalizedText( "size-lnx", "get-phet.full-install.downloadInMB", new Object[]{"75"} ) );

        add( new LocalizedText( "contact-for-cd-link", "get-phet.full-install.contactForCD", new Object[]{
                AboutContactPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-link", "get-phet.full-install.troubleshootingInfo", new Object[]{
                TroubleshootingMainPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "requirements", "get-phet.full-install.requirements", new Object[]{
                TroubleshootingMainPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "cd-intro", "get-phet.full-install.creatingInstallationCD.intro", new Object[]{
                AboutContactPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "step1", "get-phet.full-install.creatingInstallationCD.step1", new Object[]{
                "href=\"/installer/PhET-Installer_cdrom.zip\"",
                "246"
        } ) );

    }

    public static String getKey() {
        return "get-phet.full-install";
    }

    public static String getUrl() {
        return "get-phet/full-install";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, FullInstallPanel.class ) ) {
                    return "http://phet.colorado.edu/get_phet/full_install.php";
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