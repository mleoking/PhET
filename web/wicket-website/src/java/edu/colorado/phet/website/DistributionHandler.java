package edu.colorado.phet.website;

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
}
