package edu.colorado.phet.website;

import edu.colorado.phet.website.content.WorkshopsPanel;
import edu.colorado.phet.website.content.about.*;
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

    private static final Class[] yfPageRedirects = new Class[]{
            AboutContactPanel.class,
            AboutLicensingPanel.class,
            AboutMainPanel.class,
            AboutSourceCodePanel.class,
            AboutSponsorsPanel.class,
            AboutWhoWeArePanel.class,
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
}
