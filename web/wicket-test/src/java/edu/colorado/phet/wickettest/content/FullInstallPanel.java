package edu.colorado.phet.wickettest.content;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.content.about.AboutContactPanel;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class FullInstallPanel extends PhetPanel {
    public FullInstallPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "text-installer-description", "get-phet.full-install.installerDescription", new Object[]{
                "href=\"http://www.java.com/\"",
                "href=\"http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=shockwaveFlash\"",
                "href=\"http://www.mozilla.com/en-US/\"",
                "href=\"http://www.microsoft.com/windows/products/winfamily/ie/default.mspx\""
        } ) );

        add( new LocalizedText( "size-win", "get-phet.full-install.downloadInMB", new Object[]{"91"} ) );
        add( new LocalizedText( "size-mac", "get-phet.full-install.downloadInMB", new Object[]{"80"} ) );
        add( new LocalizedText( "size-lnx", "get-phet.full-install.downloadInMB", new Object[]{"75"} ) );

        add( new LocalizedText( "contact-for-cd-link", "get-phet.full-install.contactForCD", new Object[]{
                AboutContactPanel.getLinker().getHref( context )
        } ) );

        add( new LocalizedText( "troubleshooting-link", "get-phet.full-install.troubleshootingInfo", new Object[]{
                TroubleshootingMainPanel.getLinker().getHref( context )
        } ) );

        add( new LocalizedText( "requirements", "get-phet.full-install.requirements", new Object[]{
                TroubleshootingMainPanel.getLinker().getHref( context )
        } ) );

        add( new LocalizedText( "cd-intro", "get-phet.full-install.creatingInstallationCD.intro", new Object[]{
                AboutContactPanel.getLinker().getHref( context )
        } ) );

        add( new LocalizedText( "step1", "get-phet.full-install.creatingInstallationCD.step1", new Object[]{
                "href=\"../PhET-Installer_cdrom.zip\"",
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
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}