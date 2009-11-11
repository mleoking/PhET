package edu.colorado.phet.website;

import edu.colorado.phet.website.content.ContributePanel;
import edu.colorado.phet.website.content.ResearchPanel;
import edu.colorado.phet.website.content.WorkshopsPanel;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class DistributionHandler {
    public static boolean displayJARLink( PhetRequestCycle cycle, LocalizedSimulation lsim ) {
        String localeString = lsim.getLocaleString();
        if ( cycle.isKsuRipperRequest() ) {
            return localeString.equals( "en" ) || localeString.equals( "ar" );
        }
        else {
            return true;
        }
    }

    public static boolean displayTranslationLinksPanel( PhetRequestCycle cycle ) {
        return !cycle.isYoungAndFreedmanRipperRequest();
    }

    public static boolean displayTranslationEditLink( PhetRequestCycle cycle ) {
        return !cycle.isYoungAndFreedmanRipperRequest();
    }

    public static boolean redirectEnglishLinkToPhetMain( PhetRequestCycle cycle ) {
        return cycle.isKsuRipperRequest();
    }

    /**
     * Pages which we want all links to them to point to phet.colorado.edu sub-sites (depend on the particular page)
     * Young & Freedman specific
     */
    private static final Class[] yfPageRedirects = new Class[]{
            AboutContactPanel.class,
            AboutLicensingPanel.class,
            AboutMainPanel.class,
            AboutSourceCodePanel.class,
            AboutSponsorsPanel.class,
            AboutWhoWeArePanel.class,
            ContributePanel.class,
            ResearchPanel.class,
            TroubleshootingFlashPanel.class,
            TroubleshootingJavaPanel.class,
            TroubleshootingJavascriptPanel.class,
            TroubleshootingMainPanel.class,
            WorkshopsPanel.class
    };

    public static boolean redirectPageClassToProduction( PhetRequestCycle cycle, Class pageClass ) {
        if ( cycle.isYoungAndFreedmanRipperRequest() ) {
            for ( Class c : yfPageRedirects ) {
                if ( pageClass == c ) {
                    return true;
                }
            }
            return false;
        }
        else {
            return false;
        }
    }

    public static boolean redirectActivities( PhetRequestCycle cycle ) {
        return cycle.isYoungAndFreedmanRipperRequest();
    }
}
