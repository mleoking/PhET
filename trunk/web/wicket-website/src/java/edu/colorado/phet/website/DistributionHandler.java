package edu.colorado.phet.website;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.content.DonatePanel;
import edu.colorado.phet.website.content.ResearchPanel;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.content.workshops.WorkshopsPanel;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * Consolidated spot for determining behaviors that might change based on distribution / ripping method. Thus we display
 * different information for the main website, the installers, website installers, etc.
 * <p/>
 * Most static methods take as input the PhetRequestCycle, easily obtainable with getPhetCycle() in subclasses of
 * PhetPage and PhetPanel. The cycle contains user-agent information that is used.
 */
public class DistributionHandler {

    private static final Logger logger = Logger.getLogger( DistributionHandler.class.getName() );

    private DistributionHandler() {
        // don't instantiate
        throw new AssertionError();
    }

    /**
     * Whether or not to show anything related to website translations on the website
     *
     * @param cycle
     * @return
     */
    public static boolean hideWebsiteTranslations( PhetRequestCycle cycle ) {
        if ( cycle.getUserAgent().equals( PhetRequestCycle.HIDE_TRANSLATIONS_USER_AGENT ) ) {
            return true;
        }
        if ( cycle.isOfflineInstaller() ) {
            return true;
        }
        return false; // we are now going to show translations
        //return cycle.isForProductionServer();
    }

    /**
     * @return Whether to skip straight to the rotator fallback (on the front page) instead of the SWF content
     */
    public static boolean showRotatorFallback( PhetRequestCycle cycle ) {
        return cycle.isOfflineInstaller() || cycle.isKsuRipperRequest();
    }

    /**
     * Whether or not to display links for non-English (or non-Arabic for KSU) JARs. Thus we don't have all of the
     * translation JARs ripped in certain instances
     *
     * @param cycle Request cycle
     * @param lsim  The translated simulation in question (with the specific Locale)
     * @return Whether or not to display a link to the JAR
     */
    public static boolean displayJARLink( PhetRequestCycle cycle, LocalizedSimulation lsim ) {
        String localeString = lsim.getLocaleString();
        if ( cycle.isKsuRipperRequest() ) {
            // we want the arabic rip to get all of the localized JARs now --- cancelled: size increase breaks httrack
            return localeString.equals( "en" ) || localeString.equals( "ar" );
            // return true;
        }
        else if ( cycle.isYoungAndFreedmanRipperRequest() ) {
            return localeString.equals( "en" );
        }
        else if ( cycle.isOfflineInstaller() ) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Whether or not to display links to other translations
     *
     * @param cycle Request cycle
     * @return Whether or not to display links to other translations
     */
    public static boolean displayTranslationLinksPanel( PhetRequestCycle cycle ) {
        if ( hideWebsiteTranslations( cycle ) ) {
            return false;
        }
        return !cycle.isYoungAndFreedmanRipperRequest() && !cycle.isOfflineInstaller();
    }

    /**
     * Whether or not to display the link to edit translations.
     *
     * @param cycle Request cycle
     * @return Whether or not to display the link to edit translations
     */
    public static boolean displayTranslationEditLink( PhetRequestCycle cycle ) {
        if ( hideWebsiteTranslations( cycle ) ) {
            return false;
        }
        return !cycle.isInstaller();
    }

    /**
     * Whether or not to redirect links to English pages to the main PhET website phet.colorado.edu
     *
     * @param cycle Request cycle
     * @return Whether or not to redirect links to English pages to the main PhET website phet.colorado.edu
     */
    public static boolean redirectEnglishLinkToPhetMain( PhetRequestCycle cycle ) {
        //return cycle.isKsuRipperRequest();
        return false;
    }

    /**
     * Whether or not to display a login link in the upper-right corner. Log-out links are irrelevant, since rippers
     * shouldn't be able to rip those pages.
     *
     * @param cycle Request cycle
     * @return Whether or not to display a login link in the upper-right corner
     */
    public static boolean displayLogin( PhetRequestCycle cycle ) {
        return !cycle.isInstaller();
    }

    /**
     * Whether or not to display things under Teacher Ideas & Activities. Shouldn't be displayed on sites which don't
     * display activities
     *
     * @param cycle Request cycle
     * @return Whether or not to display pages under Teacher Ideas & Activities
     */
    public static boolean displayContributions( PhetRequestCycle cycle ) {
        return cycle.isOfflineInstaller() || !cycle.isInstaller();
    }

    /**
     * Pages which we want all links to them to point to phet.colorado.edu sub-sites (depend on the particular page)
     * Young & Freedman specific
     */
    private static final Class[] yfPageRedirects = new Class[] {
            AboutContactPanel.class,
            AboutLicensingPanel.class,
            AboutMainPanel.class,
            AboutSourceCodePanel.class,
            AboutSponsorsPanel.class,
            DonatePanel.class,
            FullInstallPanel.class,
            ResearchPanel.class,
            TroubleshootingFlashPanel.class,
            TroubleshootingJavaPanel.class,
            TroubleshootingJavascriptPanel.class,
            TroubleshootingMainPanel.class,
            WorkshopsPanel.class
    };

    /**
     * Whether or not to change links to pages to point to the phet.colorado.edu versions. Should only be applicable
     * for installers
     *
     * @param cycle     Request cycle
     * @param pageClass The page class in question
     * @return Whether or not to change links to pages to point to the phet.colorado.edu versions
     */
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

    /**
     * Whether or not to redirect the activities link to the main PhET site
     *
     * @param cycle Request cycle
     * @return Whether or not to redirect the activities link to the main PhET site
     */
    public static boolean redirectActivities( PhetRequestCycle cycle ) {
        return cycle.isYoungAndFreedmanRipperRequest();
    }

    /**
     * Whether or not to redirect the header (PhET logo on the top of the page) to the PhET production website
     *
     * @param cycle Request cycle
     * @return Whether or not to redirect the header (PhET logo on the top of the page) to the PhET production website
     */
    public static boolean redirectHeaderToProduction( PhetRequestCycle cycle ) {
        return cycle.isYoungAndFreedmanRipperRequest();
    }

    public static boolean showSimSponsor( PhetRequestCycle cycle ) {
        return !cycle.isForProductionServer();
    }

    /**
     * Whether caching should be enabled for this distribution.
     *
     * @param cycle
     * @return
     */
    public static boolean allowCaching( PhetRequestCycle cycle ) {
        //return !cycle.isInstaller() && !cycle.getUserAgent().equals( PhetRequestCycle.HIDE_TRANSLATIONS_USER_AGENT );
        return !cycle.isKsuRipperRequest() && !cycle.isKsuRipperRequest();
    }

    /**
     * Returns a unique identifier that represents what distribution is being used. This should have the contract that
     * given two separate PhetRequestCycles, if this function returns the same value, all other DistributionHandler
     * functions should return the same value.
     *
     * @param cycle The request cycle
     * @return
     */
    public static String getDistributionCacheKey( PhetRequestCycle cycle ) {
        if ( cycle.isInstaller() || cycle.getUserAgent().equals( PhetRequestCycle.HIDE_TRANSLATIONS_USER_AGENT ) ) {
            return cycle.getUserAgent();
        }
        else if ( cycle.isForProductionServer() ) {
            return "production";
        }
        else {
            return "regular";
        }
    }

    public static enum SearchBoxVisibility {
        NONE, OFFLINE_INSTALLER, NORMAL
    }

    public static SearchBoxVisibility getSearchBoxVisibility( PhetRequestCycle cycle ) {
        if ( cycle.isKsuRipperRequest() ) {
            // just flat-out hide the search box for the KSU mirror
            return SearchBoxVisibility.NONE;
        }
        else if ( cycle.isOfflineInstaller() ) {
            return SearchBoxVisibility.OFFLINE_INSTALLER;
        }
        else {
            return SearchBoxVisibility.NORMAL;
        }
    }

}
