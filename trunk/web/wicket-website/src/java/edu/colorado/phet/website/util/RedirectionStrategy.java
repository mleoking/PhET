package edu.colorado.phet.website.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.search.SearchResultsPage;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.TeachersGuide;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionFile;

/**
 * Handles redirecting all of the old-site URLs to the new URLs. They will then be sent out with 301 (permanent)
 * redirections.
 * <p/>
 * It also handles current sim redirections for /services/sim-website-redirect queries
 */
public class RedirectionStrategy implements IRequestTargetUrlCodingStrategy {

    private static Logger logger = Logger.getLogger( RedirectionStrategy.class.getName() );

    /**
     * Maps relative URLs (minus query strings) to either a specific relative URL (string), or null. If null, the case
     * will have to be handled within checkRedirect()
     */
    private static Map<String, String> map = new HashMap<String, String>();

    /**
     * Direct map of older redirections with the /web-pages/ prefix
     */
    private static Map<String, String> oldMap = new HashMap<String, String>();

    /**
     * Mapping of category name redirections. Maps from category names to new category names
     */
    private static Map<String, String> categoryMap = new HashMap<String, String>();

    /**
     * Maps evil permutations of the expected category names that were linked in the past
     */
    private static Map<String, String> badCategoryMap = new HashMap<String, String>(); // for those ugly variations that aren't immediately caught

    /**
     * Mapping of escaped simulation names to partial URLs. see the prefix in redirectSimulations
     */
    private static Map<String, String> simMap = new HashMap<String, String>();

    /**
     * Maps evil permutations of the expected simulation names that were linked in the past
     */
    private static Map<String, String> badSimMap = new HashMap<String, String>();

    /*---------------------------------------------------------------------------*
    * scripts that we have to redirect to multiple locations
    *----------------------------------------------------------------------------*/
    private static final String VIEW_CONTRIBUTION = "/teacher_ideas/view-contribution.php";
    private static final String VIEW_CATEGORY = "/simulations/index.php";
    private static final String VIEW_SIM = "/simulations/sims.php";
    private static final String RUN_OFFLINE = "/admin/get-run-offline.php";
    private static final String DOWNLOAD_CONTRIBUTION_FILE = "/admin/get-contribution-file.php";
    private static final String DOWNLOAD_CONTRIBUTION_ARCHIVE = "/admin/download-archive.php";
    private static final String DOWNLOAD_TEACHERS_GUIDE = "/admin/get-teachers-guide.php";
    private static final String OLD_WEBSITE_PREFIX = "/web-pages";
    private static final String SIM_REDIRECTION = "/services/sim-website-redirect";
    private static final String SIM_REDIRECTION_PHP = "/services/sim-website-redirect.php";
    private static final String GET_MEMBER_FILE = "/admin/get-member-file.php";
    private static final String SIM_SEARCH = "/simulations/search.php";

    /*---------------------------------------------------------------------------*
    * base locations that should have everything underneath redirected
    *----------------------------------------------------------------------------*/
    private static final String OLD_PUBLICATIONS = "/phet-dist/publications/";
    private static final String OLD_WORKSHOPS = "/phet-dist/workshops/";
    private static final String OLD_INSTALLERS = "/phet-dist/installers/";
    private static final String OLD_NEW = "/new/";

    private static final String NOT_FOUND = "/error/404";

    // TODO: map all of these paths so if they are changed, the redirections are changed with them

    static {

        /*---------------------------------------------------------------------------*
        * general page mappings
        *----------------------------------------------------------------------------*/

        // initialize redirection mapping. value of null indicates that it will be handled by custom code, usually for query parameters
        map.put( "/about/contact.php", "/en/about/contact" );
        map.put( "/about/index.php", "/en/about" );
        map.put( "/about/legend.php", "/en/for-teachers/legend" );
        map.put( "/about/licensing.php", "/en/about/licensing" );
        map.put( "/about/news.php", "/en/about/news" );
        map.put( "/about/source-code.php", "/en/about/source-code" );
        map.put( "/about/who-we-are.php", "/en/about" );
        map.put( "/contribute/donate.php", "/en/donate" );
        map.put( "/contribute/get-flash-common-strings.php", "/sims/flash-common-strings/flash-common-strings_en.jar" );
        map.put( "/contribute/get-java-common-strings.php", "/sims/java-common-strings/java-common-strings_en.jar" );
        map.put( "/contribute/index.php", "/en/for-translators" );
        map.put( "/contribute/translation-utility.php", "/en/for-translators/translation-utility" );
        map.put( "/get_phet/full_install.php", "/en/get-phet/full-install" );
        map.put( "/get_phet/index.php", "/en/get-phet" );
        map.put( "/get_phet/simlauncher.php", "/en/get-phet/one-at-a-time" );
        map.put( "/index.html", "/" );
        map.put( "/index.php", "/" );
        map.put( "/installers/PhET-windows-installer.exe", FullInstallPanel.WINDOWS_INSTALLER_LOCATION );
        map.put( "/research/index.php", "/en/research" );
        map.put( "/simulations", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        map.put( "/simulations/", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        map.put( "/simulations/cck/cck-ac.jnlp", "/sims/circuit-construction-kit/circuit-construction-kit-dc_en.jnlp" );
        map.put( "/simulations/stringwave/stringWave.swf", "/sims/wave-on-a-string/wave-on-a-string_en.html" );
        map.put( "/simulations/translations.php", "/en/simulations/translated" );
        map.put( "/sponsors/index.php", "/en/about/sponsors" );
        map.put( "/teacher_ideas/browse.php", "/en/for-teachers/browse-activities" );
        map.put( "/teacher_ideas/classroom-use.php", "/en/for-teachers/classroom-use" );
        map.put( "/teacher_ideas/contribute.php", "/en/for-teachers/submit-activity" );
        map.put( "/teacher_ideas/contribution-guidelines.php", "/en/for-teachers/activity-guide" );
        map.put( "/teacher_ideas/user-edit-profile.php", "/en/edit-profile" );
        map.put( "/teacher_ideas/index.php", "/en/for-teachers" );
        map.put( "/teacher_ideas/manage-contributions.php", "/en/for-teachers/manage-activities" );
        map.put( "/teacher_ideas/user-logout.php", "/en/sign-out" );
        map.put( "/teacher_ideas/user-edit-profile.php", "/en/edit-profile" );
        map.put( "/teacher_ideas/workshops.php", "/en/workshops" );
        map.put( "/teacher_ideas/workshop_uganda.php", "/en/for-teachers/workshops/uganda" );
        map.put( "/teacher_ideas/workshop_uganda_photos.php", "/en/for-teachers/workshops/uganda-photos" );
        map.put( "/tech_support/index.php", "/en/troubleshooting" );
        map.put( "/tech_support/support-flash.php", "/en/troubleshooting/flash" );
        map.put( "/tech_support/support-java.php", "/en/troubleshooting/java" );
        map.put( "/tech_support/support-javascript.php", "/en/troubleshooting/javascript" );

        map.put( VIEW_CONTRIBUTION, null );
        map.put( VIEW_CATEGORY, null );
        map.put( VIEW_SIM, null );
        map.put( RUN_OFFLINE, null );
        map.put( DOWNLOAD_CONTRIBUTION_FILE, null );
        map.put( DOWNLOAD_CONTRIBUTION_ARCHIVE, null );
        map.put( DOWNLOAD_TEACHERS_GUIDE, null );
        map.put( SIM_REDIRECTION, null );
        map.put( SIM_REDIRECTION_PHP, null );
        map.put( GET_MEMBER_FILE, null );
        map.put( SIM_SEARCH, null );

        /*---------------------------------------------------------------------------*
        * file page mappings
        *----------------------------------------------------------------------------*/

        map.put( "/favicon.gif", "/favicon.ico" );
        map.put( "/phet-dist/phet-updater/phet-updater.jar", "/files/phet-updater/phet-updater.jar" );
        map.put( "/phet-dist/newsletters/phet_newsletter_july16_2008.pdf", "/newsletters/phet_newsletter_july16_2008.pdf" );
        map.put( "/phet-dist/newsletters/phet_newsletter_sum09.pdf", "/newsletters/phet_newsletter_sum09.pdf" );
        map.put( "/simulations/favicon.ico", "/favicon.ico" );

        /*---------------------------------------------------------------------------*
        * really old page mappings
        *----------------------------------------------------------------------------*/

        oldMap.put( "/web-pages/about-phet.html", "/en/about" );
        oldMap.put( "/web-pages/contribute.htm", "/en/for-translators" );
        oldMap.put( "/web-pages/db", "/" );
        oldMap.put( "/web-pages/educator-resources.html", "/en/for-teachers" );
        oldMap.put( "/web-pages/javasupport.html", "/en/troubleshooting/java" );
        oldMap.put( "/web-pages/license.html", "/en/about/licensing" );
        oldMap.put( "/web-pages/misc-pages/publications.html", "/en/research" );
        //oldMap.put( "/web-pages/misc-pages/flash_detect_v7.html", "/tech_support/flash_detect_v7.html" );
        oldMap.put( "/web-pages/misc-pages/ratings-explanation.html", "/en/for-teachers/legend" );
        oldMap.put( "/web-pages/publications/index.html", "/en/research" );
        oldMap.put( "/web-pages/publications/phet-translation.htm", "/en/for-translators/translation-utility" );
        oldMap.put( "/web-pages/publications/phet-translation-deployment.htm", "/en/for-translators/translation-utility" );
        oldMap.put( "/web-pages/publications", "/publications" );
        oldMap.put( "/web-pages/misc-pages/ratings-explanation.html", "/en/for-teachers/legend" );
        oldMap.put( "/web-pages/research.html", "/en/research" );
        oldMap.put( "/web-pages/simulation-header.htm", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        oldMap.put( "/web-pages/simulation-header_es.htm", "/en/simulations/translated/es" );
        oldMap.put( "/web-pages/simulation-pages/chemistry-simulations.htm", "/en/simulations/category/chemistry" );
        oldMap.put( "/web-pages/simulation-pages/cuttingedge-simulations.htm", "/en/simulations/category/cutting-edge-research" );
        oldMap.put( "/web-pages/simulation-pages/electricity-simulations.htm", "/en/simulations/category/physics/electricity-magnets-and-circuits" );
        oldMap.put( "/web-pages/simulation-pages/heat-thermo-simulations.htm", "/en/simulations/category/physics/heat-and-thermodynamics" );
        oldMap.put( "/web-pages/simulation-pages/index.html", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        oldMap.put( "/web-pages/simulation-pages/light-radiation-simulations.htm", "/en/simulations/category/physics/light-and-radiation" );
        oldMap.put( "/web-pages/simulation-pages/math-tools.htm", "/en/simulations/category/math" );
        oldMap.put( "/web-pages/simulation-pages/motion-simulations.htm", "/en/simulations/category/physics/motion" );
        oldMap.put( "/web-pages/simulation-pages/new-simulations.htm", "/en/simulations/category/new" );
        oldMap.put( "/web-pages/simulation-pages/quantum-phenomena-simulations.htm", "/en/simulations/category/physics/quantum-phenomena" );
        oldMap.put( "/web-pages/simulation-pages/simulation-index.htm", "/en/simulations/index" );
        oldMap.put( "/web-pages/simulation-pages/sound-simulations.htm", "/en/simulations/category/physics/sound-and-waves" );
        oldMap.put( "/web-pages/simulation-pages/top-simulations.htm", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        oldMap.put( "/web-pages/simulation-pages/work-energy-simulations.htm", "/en/simulations/category/physics/work-energy-and-power" );
        oldMap.put( "/web-pages/simulations-base.html", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        oldMap.put( "/web-pages/simulations-base_es.html", "/en/simulations/translated/es" );
        oldMap.put( "/web-pages/support.html", "/en/troubleshooting" );
        oldMap.put( "/web-pages/whatsnew-archive.htm", "/en/about/news" );
        oldMap.put( "/web-pages/whatsnew.htm", "/en/about/news" );

        /*---------------------------------------------------------------------------*
        * category map
        *----------------------------------------------------------------------------*/

        categoryMap.put( "Featured_Sims", Category.getDefaultCategoryKey() );
        categoryMap.put( "New_Sims", "new" );
        categoryMap.put( "Physics", "physics" );
        categoryMap.put( "Motion", "physics/motion" );
        categoryMap.put( "Sound_and_Waves", "physics/sound-and-waves" );
        categoryMap.put( "Work_Energy_and_Power", "physics/work-energy-and-power" );
        categoryMap.put( "Heat_and_Thermo", "physics/heat-and-thermodynamics" );
        categoryMap.put( "Quantum_Phenomena", "physics/quantum-phenomena" );
        categoryMap.put( "Light_and_Radiation", "physics/light-and-radiation" );
        categoryMap.put( "Electricity_Magnets_and_Circuits", "physics/electricity-magnets-and-circuits" );
        categoryMap.put( "Biology", "biology" );
        categoryMap.put( "Chemistry", "chemistry" );
        categoryMap.put( "General_Chemistry", "chemistry/general" );
        categoryMap.put( "Quantum_Chemistry", "chemistry/quantum" );
        categoryMap.put( "Earth_Science", "earth-science" );
        categoryMap.put( "Math_", "math" );
        categoryMap.put( "Tools", "math/tools" );
        categoryMap.put( "Applications", "math/applications" );
        categoryMap.put( "All_Sims_by_Grade_Level_", "by-level" );
        categoryMap.put( "Elementary_School", "by-level/elementary-school" );
        categoryMap.put( "Middle_School_", "by-level/middle-school" );
        categoryMap.put( "High_School_", "by-level/high-school" );
        categoryMap.put( "University_", "by-level/university" );
        categoryMap.put( "Cutting_Edge_Research", "cutting-edge-research" );

        // grandfathered because they are still linked. all_sims is handled lower, to handle without the 'category' prefix
        categoryMap.put( "Top_Simulations", Category.getDefaultCategoryKey() );
        categoryMap.put( "Top_Simulation", Category.getDefaultCategoryKey() );
        categoryMap.put( "", Category.getDefaultCategoryKey() ); // yes, somehow there are a number with this blank

        for ( String key : categoryMap.keySet() ) {
            badCategoryMap.put( processCategoryName( key ), categoryMap.get( key ) );
        }

        /*---------------------------------------------------------------------------*
        * simulation mappings
        *----------------------------------------------------------------------------*/

        simMap.put( "Alpha_Decay", "nuclear-physics/alpha-decay" );
        simMap.put( "Arithmetic", "arithmetic" );
        simMap.put( "Atomic_Interactions", "states-of-matter/atomic-interactions" );
        simMap.put( "Balloons_and_Buoyancy", "ideal-gas/balloons-and-buoyancy" );
        simMap.put( "Balloons_and_Static_Electricity", "balloons" );
        simMap.put( "Band_Structure", "bound-states/band-structure" );
        simMap.put( "Battery_Voltage", "battery-voltage" );
        simMap.put( "BatteryResistor_Circuit", "battery-resistor-circuit" );
        simMap.put( "Beta_Decay", "nuclear-physics/beta-decay" );
        simMap.put( "Blackbody_Spectrum", "blackbody-spectrum" );
        simMap.put( "Calculus_Grapher", "calculus-grapher" );
        simMap.put( "Charges_and_Fields", "charges-and-fields" );
        simMap.put( "Circuit_Construction_Kit_ACDC", "circuit-construction-kit/circuit-construction-kit-ac" );
        simMap.put( "Circuit_Construction_Kit_DC_Only", "circuit-construction-kit/circuit-construction-kit-dc" );
        simMap.put( "Color_Vision", "color-vision" );
        simMap.put( "Conductivity", "conductivity" );
        simMap.put( "Curve_Fitting", "curve-fitting" );
        simMap.put( "DavissonGermer_Electron_Diffraction", "quantum-wave-interference/davisson-germer" );
        simMap.put( "Double_Wells_and_Covalent_Bonds", "bound-states/covalent-bonds" );
        simMap.put( "Eating_and_Exercise", "eating-and-exercise" );
        simMap.put( "Electric_Field_Hockey", "electric-hockey" );
        simMap.put( "Electric_Field_of_Dreams", "efield" );
        simMap.put( "Energy_Skate_Park", "energy-skate-park" );
        simMap.put( "Equation_Grapher", "equation-grapher" );
        simMap.put( "Estimation", "estimation" );
        simMap.put( "Faradays_Electromagnetic_Lab", "faraday" );
        simMap.put( "Faradays_Law", "faradays-law" );
        simMap.put( "Forces_in_1_Dimension", "forces-1d" );
        simMap.put( "Fourier_Making_Waves", "fourier" );
        simMap.put( "Friction", "friction" );
        simMap.put( "Gas_Properties", "ideal-gas/gas-properties" );
        simMap.put( "Generator", "faraday/generator" );
        simMap.put( "Geometric_Optics", "geometric-optics" );
        simMap.put( "Glaciers", "glaciers" );
        simMap.put( "Gravity_Force_Lab", "force-law-lab/gravity-force-lab" );
        simMap.put( "The_Greenhouse_Effect", "greenhouse" );
        simMap.put( "John_Travoltage", "travoltage" );
        simMap.put( "Ladybug_Motion_2D", "ladybug-motion-2d" );
        simMap.put( "Ladybug_Revolution", "rotation" );
        simMap.put( "Lasers", "lasers" );
        simMap.put( "Lunar_Lander", "lunar-lander" );
        simMap.put( "Magnet_and_Compass", "faraday/magnet-and-compass" );
        simMap.put( "Magnets_and_Electromagnets", "faraday/magnets-and-electromagnets" );
        simMap.put( "Masses_and_Springs", "mass-spring-lab" );
        simMap.put( "Maze_Game", "maze-game" );
        simMap.put( "Microwaves", "microwaves" );
        simMap.put( "Models_of_the_Hydrogen_Atom", "hydrogen-atom" );
        simMap.put( "Molecular_Motors", "optical-tweezers/molecular-motors" );
        simMap.put( "Motion_in_2D", "motion-2d" );
        simMap.put( "The_Moving_Man", "moving-man" );
        simMap.put( "My_Solar_System", "my-solar-system" );
        simMap.put( "Natural_Selection", "natural-selection" );
        simMap.put( "Neon_Lights_and_Other_Discharge_Lamps", "discharge-lamps" );
        simMap.put( "Nuclear_Fission", "nuclear-physics/nuclear-fission" );
        simMap.put( "Ohms_Law", "ohms-law" );
        simMap.put( "Optical_Quantum_Control", "optical-quantum-control" );
        simMap.put( "Optical_Tweezers_and_Applications", "optical-tweezers" );
        simMap.put( "Pendulum_Lab", "pendulum-lab" );
        simMap.put( "pH_Scale", "ph-scale" );
        simMap.put( "Photoelectric_Effect", "photoelectric" );
        simMap.put( "Plinko_Probability", "plinko-probability" );
        simMap.put( "Projectile_Motion", "projectile-motion" );
        simMap.put( "Quantum_Bound_States", "bound-states" );
        simMap.put( "Quantum_Tunneling_and_Wave_Packets", "quantum-tunneling" );
        simMap.put( "Quantum_Wave_Interference", "quantum-wave-interference" );
        simMap.put( "Radio_Waves_and_Electromagnetic_Fields", "radio-waves" );
        simMap.put( "Radioactive_Dating_Game", "nuclear-physics/radioactive-dating-game" );
        simMap.put( "The_Ramp", "the-ramp" );
        simMap.put( "Reactions_and_Rates", "reactions-and-rates" );
        simMap.put( "Resistance_in_a_Wire", "resistance-in-a-wire" );
        simMap.put( "Reversible_Reactions", "ideal-gas/reversible-reactions" );
        simMap.put( "Rutherford_Scattering", "rutherford-scattering" );
        simMap.put( "Salts_and_Solubility", "soluble-salts" );
        simMap.put( "SelfDriven_Particle_Model", "self-driven-particle-model" );
        simMap.put( "Semiconductors", "semiconductor" );
        simMap.put( "Signal_Circuit", "signal-circuit" );
        simMap.put( "Simplified_MRI", "mri" );
        simMap.put( "Sound", "sound" );
        simMap.put( "States_of_Matter", "states-of-matter" );
        simMap.put( "SternGerlach_Experiment", "stern-gerlach" );
        simMap.put( "Stretching_DNA", "optical-tweezers/stretching-dna" );
        simMap.put( "Torque", "rotation/torque" );
        simMap.put( "Vector_Addition", "vector-addition" );
        simMap.put( "Wave_Interference", "wave-interference" );
        simMap.put( "Wave_on_a_String", "wave-on-a-string" );

        for ( String key : simMap.keySet() ) {
            badSimMap.put( processSimName( key ), simMap.get( key ) );
        }

        // TODO: add all URLs (pretty much everything useful on tigercat has been mapped)
    }

    /**
     * @param path       A path to check redirections with
     * @param parameters Page parameters, which is basically a useful form of the query string.
     * @return null if no direction is needed, otherwise the path to redirect to
     */
    private static String checkRedirect( String path, Map parameters ) {
        // TODO: for performance, we could require equality on the path later
        if ( path.startsWith( "/phet-dist/workshops" ) ) {
            return path.substring( ( "/phet-dist" ).length() );
        }
        if ( map.containsKey( path ) ) {
            String ret = map.get( path );
            if ( ret != null ) {
                return ret;
            }
        }
        if ( path.length() == 3 ) {
            // redirect "/ar" to "/ar/" and others, to avoid those error pages
            // TODO: add support for this for locales w/ country code
            return path + "/";
        }
        if ( path.startsWith( VIEW_CONTRIBUTION ) ) {
            return redirectContributions( parameters );
        }
        else if ( path.startsWith( VIEW_CATEGORY ) ) {
            return redirectCategories( parameters );
        }
        else if ( path.startsWith( VIEW_SIM ) ) {
            return redirectSimulations( parameters );
        }
        else if ( path.startsWith( RUN_OFFLINE ) ) {
            return redirectRunOffline( parameters );
        }
        else if ( path.startsWith( SIM_REDIRECTION ) ) {
            return redirectSims( parameters );
        }
        else if ( path.startsWith( DOWNLOAD_CONTRIBUTION_FILE ) ) {
            return redirectDownloadContributionFile( parameters );
        }
        else if ( path.startsWith( DOWNLOAD_CONTRIBUTION_ARCHIVE ) ) {
            return redirectDownloadContributionArchive( parameters );
        }
        else if ( path.startsWith( DOWNLOAD_TEACHERS_GUIDE ) ) {
            return redirectDownloadTeachersGuide( parameters );
        }
        else if ( path.startsWith( OLD_WEBSITE_PREFIX ) ) {
            return redirectOldWeb( path );
        }
        else if ( path.startsWith( OLD_PUBLICATIONS ) ) {
            return path.substring( "/phet-dist".length() );
        }
        else if ( path.startsWith( OLD_WORKSHOPS ) ) {
            return path.substring( "/phet-dist".length() );
        }
        else if ( path.startsWith( GET_MEMBER_FILE ) ) {
            return redirectGetMemberFile( parameters );
        }
        else if ( path.startsWith( OLD_INSTALLERS ) ) {
            return path.substring( "/phet-dist".length() );
        }
        else if ( path.startsWith( OLD_NEW ) ) {
            return path.substring( "/new".length() );
        }
        else if ( path.startsWith( SIM_SEARCH ) ) {
            return redirectSimSearch( parameters );
        }
        return null;
    }

    /**
     * Decide which contribution to redirect to, depending on the parameters
     *
     * @param parameters Query string parameters
     * @return URL (relative) to redirect to
     */
    private static String redirectContributions( Map parameters ) {
        try {
            if ( !parameters.containsKey( "contribution_id" ) ) {
                return NOT_FOUND;
            }
            String idstr = ( (String[]) parameters.get( "contribution_id" ) )[0];
            final int id = Integer.parseInt( idstr );
            final String[] ret = new String[1];
            HibernateUtils.wrapSession( new HibernateTask() {
                public boolean run( Session session ) {
                    Contribution contribution = (Contribution) session.createQuery( "select c from Contribution as c where c.oldId = :id" )
                            .setInteger( "id", id ).uniqueResult();
                    if ( contribution != null ) {

                        // TODO: improve default linker to give convenience function that gives "default" redirectable result to English
                        ret[0] = ContributionPage.getLinker( contribution ).getDefaultRawUrl();
                        return true;
                    }
                    else {return false;}
                }
            } );
            return ret[0];
        }
        catch( RuntimeException e ) {
            logger.warn( "bad number X", e );
            return NOT_FOUND;
        }
    }

    /**
     * Decide which category to redirect to, depending on the parameters
     *
     * @param parameters Query string parameters
     * @return URL (relative) to redirect to
     */
    private static String redirectCategories( Map parameters ) {
        String prefix = "/en/simulations/category/";
        if ( parameters.get( "cat" ) == null ) {
            return prefix + Category.getDefaultCategoryKey();
        }
        String cat = ( (String[]) parameters.get( "cat" ) )[0];
        if ( cat.equals( "All_Sims" ) ) {
            return "/en/simulations/index";
        }
        String newcat = categoryMap.get( cat );
        if ( newcat == null ) {
            logger.debug( "didn't match category yet, trying badcat: " + processCategoryName( cat ) );
            // run it through the bad matcher
            newcat = badCategoryMap.get( processCategoryName( cat ) );
            if ( newcat != null ) {
                logger.debug( "matched category " + cat + " only with bad cat " + processCategoryName( cat ) );
            }
        }
        else {
            logger.debug( "matched category " + cat + " directly" );
        }
        if ( newcat != null ) {
            return prefix + newcat;
        }

        return NOT_FOUND;
    }

    /**
     * Decide which simulation to redirect to, depending on the parameters
     *
     * @param parameters Query string parameters
     * @return URL (relative) to redirect to
     */
    private static String redirectSimulations( Map parameters ) {
        String prefix = "/en/simulation/";
        if ( parameters.get( "sim" ) == null ) {
            return NOT_FOUND;
        }
        String sim = ( (String[]) parameters.get( "sim" ) )[0];
        String newsim = simMap.get( sim );
        if ( newsim == null ) {
            newsim = badSimMap.get( processSimName( sim ) );
        }
        if ( newsim != null ) {
            return prefix + newsim;
        }

        return NOT_FOUND;
    }

    /**
     * Map indirect-links to the translated JARs
     *
     * @param parameters Query string parameters
     * @return URL (relative) to redirect to
     */
    private static String redirectRunOffline( Map parameters ) {
        // http://phet.colorado.edu/admin/get-run-offline.php?sim_id=84&locale=en

        if ( !parameters.containsKey( "sim_id" ) || !parameters.containsKey( "locale" ) ) {
            return NOT_FOUND;
        }

        final int simId = Integer.parseInt( ( (String[]) parameters.get( "sim_id" ) )[0] );

        final Locale locale = parameters.get( "locale" ) == null ? PhetWicketApplication.getDefaultLocale() :
                              LocaleUtils.stringToLocale( ( (String[]) parameters.get( "locale" ) )[0] );
        final StringBuffer ret = new StringBuffer();
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                Simulation simulation = (Simulation) session.createQuery( "select s from Simulation as s where s.oldId = :oldid" ).setInteger( "oldid", simId ).uniqueResult();
                LocalizedSimulation lsim = simulation.getBestLocalizedSimulation( locale );
                ret.append( lsim.getDownloadUrl() );
                return true;
            }
        } );

        if ( success ) {
            return ret.toString();
        }

        return NOT_FOUND;
    }

    private static String redirectDownloadContributionFile( Map parameters ) {
        // http://phet.colorado.edu/admin/get-contribution-file.php?contribution_file_id=555

        if ( !parameters.containsKey( "contribution_file_id" ) ) {
            return NOT_FOUND;
        }

        final int contributionFileId = Integer.parseInt( ( (String[]) parameters.get( "contribution_file_id" ) )[0] );

        final StringBuffer ret = new StringBuffer();
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                ContributionFile file = (ContributionFile) session.createQuery( "select cf from ContributionFile as cf where cf.oldId = :oldid" ).setInteger( "oldid", contributionFileId ).uniqueResult();
                ret.append( file.getLinker().getDefaultRawUrl() );
                return true;
            }
        } );

        if ( success ) {
            return ret.toString();
        }

        return NOT_FOUND;
    }

    private static String redirectDownloadContributionArchive( Map parameters ) {
        // http://phet.colorado.edu/admin/download-archive.php?contribution_id=627

        if ( !parameters.containsKey( "contribution_id" ) ) {
            return NOT_FOUND;
        }

        final int contributionId = Integer.parseInt( ( (String[]) parameters.get( "contribution_id" ) )[0] );

        final StringBuffer ret = new StringBuffer();
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                Contribution contribution = (Contribution) session.createQuery( "select c from Contribution as c where c.oldId = :oldid" ).setInteger( "oldid", contributionId ).uniqueResult();
                ret.append( contribution.getZipLocation() );
                return true;
            }
        } );

        if ( success ) {
            return ret.toString();
        }

        return NOT_FOUND;
    }

    private static String redirectDownloadTeachersGuide( Map parameters ) {
        // http://phet.colorado.edu/admin/get-teachers-guide.php?teachers_guide_id=67

        if ( !parameters.containsKey( "teachers_guide_id" ) ) {
            return NOT_FOUND;
        }

        final int id = Integer.parseInt( ( (String[]) parameters.get( "teachers_guide_id" ) )[0] );

        final StringBuffer ret = new StringBuffer();
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                TeachersGuide guide = (TeachersGuide) session.createQuery( "select tg from TeachersGuide as tg where tg.oldId = :oldid" ).setInteger( "oldid", id ).uniqueResult();
                ret.append( guide.getLinker().getDefaultRawUrl() );
                return true;
            }
        } );

        if ( success ) {
            return ret.toString();
        }

        return NOT_FOUND;
    }

    /**
     * Replaces the sim-website-redirect service. Nice and quick!
     */
    private static String redirectSims( Map parameters ) {
        if ( !parameters.containsKey( "request_version" ) || !( (String[]) parameters.get( "request_version" ) )[0].equals( "1" ) ) {
            logger.warn( "invalid request version for sim-website-redirect" );
        }

        if ( !parameters.containsKey( "project" ) ) {
            return NOT_FOUND;
        }

        String project = ( (String[]) parameters.get( "project" ) )[0];

        String sim = parameters.containsKey( "sim" ) ? ( (String[]) parameters.get( "sim" ) )[0] : project;

        return SimulationPage.getLinker( project, sim ).getDefaultRawUrl();
    }

    /**
     * Handle an old way of linking to the windows installer
     */
    private static String redirectGetMemberFile( Map parameters ) {
        if ( !parameters.containsKey( "file" ) ) {
            return NOT_FOUND;
        }
        String filename = ( (String[]) parameters.get( "file" ) )[0];
        if ( filename.equals( "../phet-dist/installers/PhET-Installer_windows.exe" ) ||
             filename.equals( "..%2Fphet-dist%2Finstallers%2FPhET-Installer_windows.exe" ) ) {
            return FullInstallPanel.WINDOWS_INSTALLER_LOCATION;
        }
        else {
            return NOT_FOUND;
        }
    }

    private static String redirectSimSearch( Map parameters ) {
        // /simulations/search.php?search_for=sound
        if ( !parameters.containsKey( "search_for" ) ) {
            return SearchResultsPage.getLinker( "" ).getDefaultRawUrl();
        }
        String query = ( (String[]) parameters.get( "search_for" ) )[0];
        return SearchResultsPage.getLinker( query ).getDefaultRawUrl();
    }

    private static String redirectOldWeb( String path ) {
        if ( oldMap.containsKey( path ) ) {
            return oldMap.get( path );
        }
        else if ( path.startsWith( "/web-pages/publications" ) ) {
            return path.substring( "/web-pages/publications".length() );
        }
        else if ( path.startsWith( "/web-pages/simulation-pages" ) ) {
            return path.substring( "/web-pages/simulation-pages".length() );
        }
        else {
            return NOT_FOUND;
        }
    }

    private static String morphPath( String str ) {
        return "/" + str;
    }

    /**
     * @return Returns the prefix that these urls can be considered to be 'mounted' at. Since we want to be able to
     *         redirect ANY url, we effectively mount it at the root.
     */
    public String getMountPath() {
        return "";
    }

    public CharSequence encode( IRequestTarget requestTarget ) {
        // won't make links to this, so it shouldn't matter
        return null;
    }

    /**
     * Called by wicket to turn a request into the applicable request target (way of handling the request).
     * Here we use this to compute the URL it should be redirected to, and pass the PermanentRedirectRequestTarget.
     * <p/>
     * NOTE: this is only called when RedirectionStrategy.matches( String path ) returns true.
     *
     * @param requestParameters The request's parameters
     * @return Our redirection
     */
    public IRequestTarget decode( RequestParameters requestParameters ) {
        String requestPath = requestParameters.getPath();
        String path = morphPath( requestPath );

        String redirect = checkRedirect( path, requestParameters.getParameters() );
        if ( redirect != null ) {
            return new PermanentRedirectRequestTarget( redirect );
        }

        logger.error( "Did not find path: " + requestPath );

        throw new RuntimeException( "Did not find path: " + requestPath );
    }

    public boolean matches( IRequestTarget requestTarget ) {
        return requestTarget instanceof RedirectRequestTarget;
    }

    /**
     * Called by Wicket to initially test whether this URL strategy handles the particular path.
     *
     * @param path Path (without query strings, without leading slash)
     * @return Whether this URL should be redirected
     */
    public boolean matches( String path ) {
        if ( path.startsWith( "phet-dist/workshops/" ) ) {
            return true;
        }

        String morphedPath = morphPath( path );
        boolean inMap = map.containsKey( morphedPath );

        logger.debug( "testing: " + path );

        if ( inMap ) {
            return true;
        }

        if ( path.length() == 2 && path.indexOf( "." ) == -1 ) {
            return true;
        }

        // TODO: test equality for speed?
        if ( morphedPath.startsWith( OLD_WEBSITE_PREFIX ) ) {
            return true;
        }
        else if ( morphedPath.startsWith( OLD_PUBLICATIONS ) ) {
            return true;
        }
        else if ( morphedPath.startsWith( OLD_WORKSHOPS ) ) {
            return true;
        }
        else if ( morphedPath.startsWith( OLD_INSTALLERS ) ) {
            return true;
        }
        else if ( morphedPath.startsWith( OLD_NEW ) ) {
            return true;
        }

        // TODO: add in more complicated redirection matching here?

        return false;
    }

    /**
     * Hack away at a possible category name so we can get a generous match to handle old (bad) URLs
     *
     * @param name The presented name
     * @return The "cleaned" version
     */
    private static String processCategoryName( String name ) {
        return name.replace( "_", "" ).replace( " ", "" ).toLowerCase();
    }

    /**
     * Hack away at a possible simulation name so we can get a generous match to handle old (bad) URLs
     *
     * @param name The presented name
     * @return The "cleaned" version
     */
    private static String processSimName( String name ) {
        return name.replace( "_", "" ).replace( " ", "" ).toLowerCase();
    }
}
