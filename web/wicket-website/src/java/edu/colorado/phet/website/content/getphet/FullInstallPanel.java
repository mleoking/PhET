package edu.colorado.phet.website.content.getphet;

import java.util.Date;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.cache.InstallerCache;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.content.about.AboutContactPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class FullInstallPanel extends PhetPanel {

    public static final String WINDOWS_INSTALLER_LOCATION = "/installer/PhET-Installer_windows.exe";
    public static final String MAC_INSTALLER_LOCATION = "/installer/PhET-Installer_osx.zip";
    public static final String LINUX_INSTALLER_LOCATION = "/installer/PhET-Installer_linux.bin";
    public static final String CD_INSTALLER_LOCATION = "/installer/PhET-Installer_cdrom.zip";

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

        // TODO: 246 constant?
        add( new LocalizedText( "step1", "get-phet.full-install.creatingInstallationCD.step1", new Object[]{
                "href=\"" + CD_INSTALLER_LOCATION + "\"",
                "246"
        } ) );

        add( new LocalizedText( "updatedFrequently", "get-phet.full-install.updatedFrequently", new Object[]{
                new Date( (long) InstallerCache.getTimestampMilliseconds() )
        } ) );

        add( new RawLink( "win-link", WINDOWS_INSTALLER_LOCATION ) );
        add( new RawLink( "mac-link", MAC_INSTALLER_LOCATION ) );
        add( new RawLink( "linux-link", LINUX_INSTALLER_LOCATION ) );

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