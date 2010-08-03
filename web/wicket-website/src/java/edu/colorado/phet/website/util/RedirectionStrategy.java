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
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.search.SearchResultsPage;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.content.workshops.WorkshopsPanel;
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

    private static final Logger logger = Logger.getLogger( RedirectionStrategy.class.getName() );

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
     * Redirection map that takes effect after processing through the other redirections AND wicket.
     */
    private static Map<String, String> postWicketMap = new HashMap<String, String>();

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
    private static final String CHANGELOG = "/simulations/changelog.php";

    /*---------------------------------------------------------------------------*
    * base locations that should have everything underneath redirected
    *----------------------------------------------------------------------------*/
    private static final String OLD_PUBLICATIONS = "/phet-dist/publications/";
    private static final String OLD_WORKSHOPS = "/phet-dist/workshops/";
    private static final String OLD_INSTALLERS = "/phet-dist/installers/";
    private static final String OLD_TRANSLATION_UTIL = "/phet-dist/translation-utility/";

    private static final String NOT_FOUND = "/error/404";

    // TODO: map all of these paths so if they are changed, the redirections are changed with them

    static {

        /*---------------------------------------------------------------------------*
        * general page mappings
        *----------------------------------------------------------------------------*/

        // initialize redirection mapping. value of null indicates that it will be handled by custom code, usually for query parameters
        map.put( "/about/contact.php", "/en/about/contact" );
        map.put( "/about", "/en/about" );
        map.put( "/about/", "/en/about" );
        map.put( "/about/index.php", "/en/about" );
        map.put( "/about/legend.php", "/en/for-teachers/legend" );
        map.put( "/about/licensing.php", "/en/about/licensing" );
        map.put( "/about/news.php", "/en/about/news" );
        map.put( "/about/source-code.php", "/en/about/source-code" );
        map.put( "/about/who-we-are.php", "/en/about" );
        map.put( "/contribute/", "/en/for-translators" );
        map.put( "/contribute/donate.php", "/en/donate" );
        map.put( "/contribute/get-flash-common-strings.php", "/sims/flash-common-strings/flash-common-strings_en.jar" );
        map.put( "/contribute/get-java-common-strings.php", "/sims/java-common-strings/java-common-strings_en.jar" );
        map.put( "/contribute/index.php", "/en/for-translators" );
        map.put( "/contribute/translation-utility.php", "/en/for-translators/translation-utility" );
        map.put( "/get_phet/", "/en/get-phet" );
        map.put( "/get_phet/full_install.php", "/en/get-phet/full-install" );
        map.put( "/get_phet/index.php", "/en/get-phet" );
        map.put( "/get_phet/simlauncher.php", "/en/get-phet/one-at-a-time" );
        map.put( "/index.html", "/" );
        map.put( "/index.php", "/" );
        map.put( "/installers/PhET-windows-installer.exe", FullInstallPanel.WINDOWS_INSTALLER_LOCATION );
        map.put( "/phet-dist/installers/PhET-1.0-windows-installer.exe", FullInstallPanel.WINDOWS_INSTALLER_LOCATION );
        map.put( "/random-thumbnail.php", "/images/mass-spring-lab-animated-screenshot.gif" );
        map.put( "/research/", "/en/research" );
        map.put( "/research/index.php", "/en/research" );
        map.put( "/simulations", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        map.put( "/simulations/", "/en/simulations/category/" + Category.getDefaultCategoryKey() );
        map.put( "/simulations/cck/cck-ac.jnlp", "/sims/circuit-construction-kit/circuit-construction-kit-dc_en.jnlp" );
        map.put( "/simulations/faraday/faraday.jnlp", "/sims/faraday/faraday_en.jnlp" );
        map.put( "/simulations/stringwave/stringWave.swf", "/sims/wave-on-a-string/wave-on-a-string_en.html" );
        map.put( "/simulations/translations.php", "/en/simulations/translated" );
        map.put( "/sponsors/index.php", "/en/about/sponsors" );
        map.put( "/teacher_ideas/", "/en/for-teachers" );
        map.put( "/teacher_ideas/browse.php", "/en/for-teachers/browse-activities" );
        map.put( "/teacher_ideas/classroom-use.php", "/en/for-teachers/classroom-use" );
        map.put( "/teacher_ideas/contribute.php", "/en/for-teachers/submit-activity" );
        map.put( "/teacher_ideas/contribution-guidelines.php", "/en/for-teachers/activity-guide" );
        map.put( "/teacher_ideas/user-edit-profile.php", "/en/edit-profile" );
        map.put( "/teacher_ideas/index.php", "/en/for-teachers" );
        map.put( "/teacher_ideas/manage-contributions.php", "/en/for-teachers/manage-activities" );
        map.put( "/teacher_ideas/user-logout.php", "/en/sign-out" );
        map.put( "/teacher_ideas/user-edit-profile.php", "/en/edit-profile" );
        map.put( "/teacher_ideas/workshops.php", WorkshopsPanel.getLinker().getDefaultRawUrl() );
        map.put( "/teacher_ideas/workshop_uganda.php", "/en/for-teachers/workshops/uganda" );
        map.put( "/teacher_ideas/workshop_uganda_photos.php", "/en/for-teachers/workshops/uganda-photos" );
        map.put( "/tech_support/index.php", "/en/troubleshooting" );
        map.put( "/tech_support/support-flash.php", "/en/troubleshooting/flash" );
        map.put( "/tech_support/support-java.php", "/en/troubleshooting/java" );
        map.put( "/tech_support/support-javascript.php", "/en/troubleshooting/javascript" );
        map.put( "/web-pages/", "/" );

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
        map.put( CHANGELOG, null );

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
        oldMap.put( "/web-pages/index.html", "/" );
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

        simMap.put( "Alpha_Decay", "alpha-decay" );
        simMap.put( "Arithmetic", "arithmetic" );
        simMap.put( "Atomic_Interactions", "atomic-interactions" );
        simMap.put( "Balloons_and_Buoyancy", "balloons-and-buoyancy" );
        simMap.put( "Balloons_and_Static_Electricity", "balloons" );
        simMap.put( "Band_Structure", "band-structure" );
        simMap.put( "Battery_Voltage", "battery-voltage" );
        simMap.put( "BatteryResistor_Circuit", "battery-resistor-circuit" );
        simMap.put( "Beta_Decay", "beta-decay" );
        simMap.put( "Blackbody_Spectrum", "blackbody-spectrum" );
        simMap.put( "Calculus_Grapher", "calculus-grapher" );
        simMap.put( "Charges_and_Fields", "charges-and-fields" );
        simMap.put( "Circuit_Construction_Kit_ACDC", "circuit-construction-kit-ac" );
        simMap.put( "Circuit_Construction_Kit_DC_Only", "circuit-construction-kit-dc" );
        simMap.put( "Color_Vision", "color-vision" );
        simMap.put( "Conductivity", "conductivity" );
        simMap.put( "Curve_Fitting", "curve-fitting" );
        simMap.put( "DavissonGermer_Electron_Diffraction", "davisson-germer" );
        simMap.put( "Double_Wells_and_Covalent_Bonds", "covalent-bonds" );
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
        simMap.put( "Gas_Properties", "gas-properties" );
        simMap.put( "Gene_Machine_The_Lac_Operon", "gene-machine-lac-operon" );
        simMap.put( "Generator", "generator" );
        simMap.put( "Geometric_Optics", "geometric-optics" );
        simMap.put( "Glaciers", "glaciers" );
        simMap.put( "Gravity_Force_Lab", "gravity-force-lab" );
        simMap.put( "The_Greenhouse_Effect", "greenhouse" );
        simMap.put( "John_Travoltage", "travoltage" );
        simMap.put( "Ladybug_Motion_2D", "ladybug-motion-2d" );
        simMap.put( "Ladybug_Revolution", "rotation" );
        simMap.put( "Lasers", "lasers" );
        simMap.put( "Lunar_Lander", "lunar-lander" );
        simMap.put( "Magnet_and_Compass", "magnet-and-compass" );
        simMap.put( "Magnets_and_Electromagnets", "magnets-and-electromagnets" );
        simMap.put( "Masses_and_Springs", "mass-spring-lab" );
        simMap.put( "Maze_Game", "maze-game" );
        simMap.put( "Microwaves", "microwaves" );
        simMap.put( "Models_of_the_Hydrogen_Atom", "hydrogen-atom" );
        simMap.put( "Molecular_Motors", "molecular-motors" );
        simMap.put( "Motion_in_2D", "motion-2d" );
        simMap.put( "The_Moving_Man", "moving-man" );
        simMap.put( "My_Solar_System", "my-solar-system" );
        simMap.put( "Natural_Selection", "natural-selection" );
        simMap.put( "Neon_Lights_and_Other_Discharge_Lamps", "discharge-lamps" );
        simMap.put( "Nuclear_Fission", "nuclear-fission" );
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
        simMap.put( "Radioactive_Dating_Game", "radioactive-dating-game" );
        simMap.put( "The_Ramp", "the-ramp" );
        simMap.put( "Reactants_Products_and_Leftovers", "reactants-products-and-leftovers" );
        simMap.put( "Reactions_and_Rates", "reactions-and-rates" );
        simMap.put( "Resistance_in_a_Wire", "resistance-in-a-wire" );
        simMap.put( "Reversible_Reactions", "reversible-reactions" );
        simMap.put( "Rutherford_Scattering", "rutherford-scattering" );
        simMap.put( "Salts_and_Solubility", "soluble-salts" );
        simMap.put( "SelfDriven_Particle_Model", "self-driven-particle-model" );
        simMap.put( "Semiconductors", "semiconductor" );
        simMap.put( "Signal_Circuit", "signal-circuit" );
        simMap.put( "Simplified_MRI", "mri" );
        simMap.put( "Sound", "sound" );
        simMap.put( "States_of_Matter", "states-of-matter" );
        simMap.put( "SternGerlach_Experiment", "stern-gerlach" );
        simMap.put( "Stretching_DNA", "stretching-dna" );
        simMap.put( "Torque", "torque" );
        simMap.put( "Vector_Addition", "vector-addition" );
        simMap.put( "Wave_Interference", "wave-interference" );
        simMap.put( "Wave_on_a_String", "wave-on-a-string" );

        simMap.put( "Circuit_Construction_Kit_ACDC_Virtual_Lab_Version", "circuit-construction-kit-ac-virtual-lab" );
        simMap.put( "Circuit_Construction_Kit_Virtual_Lab_Version_DC_Only", "circuit-construction-kit-dc-virtual-lab" );

        for ( String key : simMap.keySet() ) {
            badSimMap.put( processSimName( key ), simMap.get( key ) );
        }

        /*---------------------------------------------------------------------------*
        * post-wicket map
        *----------------------------------------------------------------------------*/

        postWicketMap.put( "/simulations/arithmetic/Arithmetic.Max.swf", "/sims/arithmetic/arithmetic_en.html" );
        postWicketMap.put( "/simulations/arithmetic/Arithmetic.swf", "/sims/arithmetic/arithmetic_en.html" );
        postWicketMap.put( "/simulations/arithmetic/ArithmeticClockOnly.swf", "/sims/arithmetic/arithmetic_en.html" );
        postWicketMap.put( "/simulations/balloon/balloon_es.jnlp", "/sims/balloons/balloons_es.jnlp" );
        postWicketMap.put( "/simulations/balloon/balloons-save.jar", "/sims/balloons/balloons_en.jar" );
        postWicketMap.put( "/simulations/balloon/balloons.jar", "/sims/balloons/balloons_en.jar" );
        postWicketMap.put( "/simulations/balloon/webstart.jnlp", "/sims/balloons/balloons_en.jnlp" );
        postWicketMap.put( "/simulations/batteryvoltage/batteryVoltage.jar", "/sims/battery-voltage/battery-voltage_en.jar" );
        postWicketMap.put( "/simulations/batteryvoltage/batteryvoltage_sp.jnlp", "/sims/battery-voltage/battery-voltage_es.jnlp" );
        postWicketMap.put( "/simulations/batteryvoltage/webstart.jnlp", "/sims/battery-voltage/battery-voltage_en.jnlp" );
        postWicketMap.put( "/simulations/blackbody/blackbody.swf", "/sims/blackbody-spectrum/blackbody-spectrum_en.html" );
        postWicketMap.put( "/simulations/bound-states/band-structure.jnlp", "/sims/bound-states/band-structure_en.jnlp" );
        postWicketMap.put( "/simulations/bound-states/band-structure_es.jnlp", "/sims/bound-states/band-structure_es.jnlp" );
        postWicketMap.put( "/simulations/bound-states/bound-states.jnlp", "/sims/bound-states/bound-states_en.jnlp" );
        postWicketMap.put( "/simulations/bound-states/bound-states_es.jnlp", "/sims/bound-states/bound-states_es.jnlp" );
        postWicketMap.put( "/simulations/bound-states/boundstates.jar", "/sims/bound-states/bound-states_en.jar" );
        postWicketMap.put( "/simulations/bound-states/covalent-bonds.jnlp", "/sims/bound-states/covalent-bonds_en.jnlp" );
        postWicketMap.put( "/simulations/bound-states/covalent-bonds_es.jnlp", "/sims/bound-states/covalent-bonds_es.jnlp" );
        postWicketMap.put( "/simulations/cck/cck-ac-lab.jnlp", "/sims/cck/cck-ac.jnlp" );
        postWicketMap.put( "/simulations/cck/cck-ac-lab_sp.jnlp", "/sims/cck/cck-ac_es.jnlp" );
        postWicketMap.put( "/simulations/cck/cck-ac.jnlp", "/sims/cck/cck-ac.jnlp" );
        postWicketMap.put( "/simulations/cck/cck-ac_sp.jnlp", "/sims/cck/cck-ac_es.jnlp" );
        postWicketMap.put( "/simulations/cck/cck-grabbag.jnlp", "/sims/cck/cck-dc.jnlp" );
        postWicketMap.put( "/simulations/cck/cck-lab.jnlp", "/sims/cck/cck-dc.jnlp" );
        postWicketMap.put( "/simulations/cck/cck-pro.jar", "/sims/cck/cck-dc.jar" );
        postWicketMap.put( "/simulations/cck/cck.jar", "/sims/cck/cck.jar" );
        postWicketMap.put( "/simulations/cck/cck.jnlp", "/sims/cck/cck-dc.jnlp" );
        postWicketMap.put( "/simulations/cck/cck_sp.jnlp", "/sims/cck/cck-dc_es.jnlp" );
        postWicketMap.put( "/simulations/chargesandfields/ChargesAndFields.swf", "/sims/charges-and-fields/charges-and-fields_en.html" );
        postWicketMap.put( "/simulations/chargesandfields/ChargesAndFieldsOlder.swf", "/sims/charges-and-fields/charges-and-fields_en.html" );
        postWicketMap.put( "/simulations/colorvision3/colorvision3.jar", "/sims/color-vision/color-vision.jar" );
        postWicketMap.put( "/simulations/colorvision3/colorvision3.jnlp", "/sims/color-vision/color-vision.jnlp" );
        postWicketMap.put( "/simulations/colorvision3/colorvision3_es.jnlp", "/sims/color-vision/color-vision_es.jnlp" );
        postWicketMap.put( "/simulations/conductivity/conductivity-pro.jar", "/sims/conductivity/conductivity.jar" );
        postWicketMap.put( "/simulations/conductivity/conductivity.jnlp", "/sims/conductivity/conductivity.jnlp" );
        postWicketMap.put( "/simulations/conductivity/conductivity_sp.jnlp", "/sims/conductivity/conductivity_es.jnlp" );
        postWicketMap.put( "/simulations/dischargelamps/dischargelamps.jar", "/sims/discharge-lamps/discharge-lamps.jar" );
        postWicketMap.put( "/simulations/dischargelamps/dischargelamps.jnlp", "/sims/discharge-lamps/discharge-lamps.jnlp" );
        postWicketMap.put( "/simulations/electricfieldofdreams/efield.jar", "/sims/efield/efield.jar" );
        postWicketMap.put( "/simulations/electricfieldofdreams/electricfield_sp.jnlp", "/sims/efield/efield_es.jnlp" );
        postWicketMap.put( "/simulations/electricfieldofdreams/webstart.jnlp", "/sims/efield/efield.jnlp" );
        postWicketMap.put( "/simulations/electrichockey/ehockey.jar", "/sims/electric-hockey/electric-hockey.jar" );
        postWicketMap.put( "/simulations/electrichockey/ehockey_sp.jnlp", "/sims/electric-hockey/electric-hockey_es.jnlp" );
        postWicketMap.put( "/simulations/electrichockey/webstart.jnlp", "/sims/electric-hockey/electric-hockey.jnlp" );
        postWicketMap.put( "/simulations/emf/emf.jar", "/sims/radio-waves/radio-waves.jar" );
        postWicketMap.put( "/simulations/emf/emf.jnlp", "/sims/radio-waves/radio-waves.jnlp" );
        postWicketMap.put( "/simulations/emf/emf_es.jnlp", "/sims/radio-waves/radio-waves_es.jnlp" );
        postWicketMap.put( "/simulations/energyconservation/ec3-pro.jar", "/sims/energy-skate-park/energy-skate-park.jar" );
        postWicketMap.put( "/simulations/energyconservation/energy-skate-park.jar", "/sims/energy-skate-park/energy-skate-park.jar" );
        postWicketMap.put( "/simulations/energyconservation/energyconservation.jnlp", "/sims/energy-skate-park/energy-skate-park.jnlp" );
        postWicketMap.put( "/simulations/energyconservation/energyconservation_es.jnlp", "/sims/energy-skate-park/energy-skate-park_es.jnlp" );
        postWicketMap.put( "/simulations/energyconservation/energyconservation_sp.jnlp", "/sims/energy-skate-park/energy-skate-park_es.jnlp" );
        postWicketMap.put( "/simulations/equationgrapher/equationGrapher.swf", "/sims/equation-grapher/equation-grapher.swf" );
        postWicketMap.put( "/simulations/equationgrapher/equationGrapherOld.swf", "/sims/equation-grapher/equation-grapher.swf" );
        postWicketMap.put( "/simulations/estimation/estimation.swf", "/sims/estimation/estimation.swf" );
        postWicketMap.put( "/simulations/estimation/estimationOld.swf", "/sims/estimation/estimation.swf" );
        postWicketMap.put( "/simulations/faraday/faraday.jar", "/sims/faraday/faraday.jar" );
        postWicketMap.put( "/simulations/faraday/faraday.jnlp", "/sims/faraday/faraday.jnlp" );
        postWicketMap.put( "/simulations/faraday/faraday_es.jnlp", "/sims/faraday/faraday_es.jnlp" );
        postWicketMap.put( "/simulations/faraday/faraday_sp.jnlp", "/sims/faraday/faraday_es.jnlp" );
        postWicketMap.put( "/simulations/faradayFlash/faradayMX.swf", "/sims/faraday-mx/faraday-mx.swf" );
        postWicketMap.put( "/simulations/force1d/force1d.jar", "/sims/forces-1d/forces-1d.jar" );
        postWicketMap.put( "/simulations/force1d/force1d.jnlp", "/sims/forces-1d/forces-1d.jnlp" );
        postWicketMap.put( "/simulations/force1d/force1d_es.jnlp", "/sims/forces-1d/forces-1d_es.jnlp" );
        postWicketMap.put( "/simulations/fourier/fourier.jar", "/sims/fourier/fourier.jar" );
        postWicketMap.put( "/simulations/fourier/fourier.jnlp", "/sims/fourier/fourier.jnlp" );
        postWicketMap.put( "/simulations/fourier/fourier_es.jnlp", "/sims/fourier/fourier_es.jnlp" );
        postWicketMap.put( "/simulations/friction/friction.swf", "/sims/friction/friction.swf" );
        postWicketMap.put( "/simulations/gasses-buoyancy/buoyancy.jnlp", "/sims/ideal-gas/balloons-and-buoyancy.jnlp" );
        postWicketMap.put( "/simulations/gasses-buoyancy/buoyancy_es.jnlp", "/sims/ideal-gas/balloons-and-buoyancy_es.jnlp" );
        postWicketMap.put( "/simulations/gasses-buoyancy/diffusion.jnlp", "/sims/ideal-gas/diffusion.jnlp" );
        postWicketMap.put( "/simulations/gasses-buoyancy/ideal-gas.jar", "/sims/ideal-gas/gas-properties.jar" );
        postWicketMap.put( "/simulations/gasses-buoyancy/idealgas-save.jar", "/sims/ideal-gas/gas-properties.jar" );
        postWicketMap.put( "/simulations/gasses-buoyancy/idealgas.jar", "/sims/ideal-gas/gas-properties.jar" );
        postWicketMap.put( "/simulations/gasses-buoyancy/idealgas.jnlp", "/sims/ideal-gas/gas-properties.jnlp" );
        postWicketMap.put( "/simulations/gasses-buoyancy/idealgas_es.jnlp", "/sims/ideal-gas/gas-properties_es.jnlp" );
        postWicketMap.put( "/simulations/gasses-buoyancy/pchem.jnlp", "/sims/ideal-gas/reversible-reactions.jnlp" );
        postWicketMap.put( "/simulations/greenhouse/greenhouse-save.jar", "/sims/greenhouse/greenhouse.jar" );
        postWicketMap.put( "/simulations/greenhouse/greenhouse-save2.jar", "/sims/greenhouse/greenhouse.jar" );
        postWicketMap.put( "/simulations/greenhouse/greenhouse.jar", "/sims/greenhouse/greenhouse.jar" );
        postWicketMap.put( "/simulations/greenhouse/greenhouse.jnlp", "/sims/greenhouse/greenhouse.jnlp" );
        postWicketMap.put( "/simulations/greenhouse/greenhouse_es.jnlp", "/sims/greenhouse/greenhouse_es.jnlp" );
        postWicketMap.put( "/simulations/hydrogen-atom/hydrogen-atom.jar", "/sims/hydrogen-atom/hydrogen-atom.jar" );
        postWicketMap.put( "/simulations/hydrogen-atom/hydrogen-atom.jnlp", "/sims/hydrogen-atom/hydrogen-atom.jnlp" );
        postWicketMap.put( "/simulations/hydrogen-atom/hydrogen-atom_es.jnlp", "/sims/hydrogen-atom/hydrogen-atom_es.jnlp" );
        postWicketMap.put( "/simulations/lasers/lasers.jar", "/sims/lasers/lasers.jar" );
        postWicketMap.put( "/simulations/lasers/lasers.jnlp", "/sims/lasers/lasers.jnlp" );
        postWicketMap.put( "/simulations/lasers/lasers_es.jnlp", "/sims/lasers/lasers_es.jnlp" );
        postWicketMap.put( "/simulations/lens/lens.swf", "/sims/lens/lens.swf" );
        postWicketMap.put( "/simulations/lens/lensOld.swf", "/sims/lens/lens.swf" );
        postWicketMap.put( "/simulations/lunarLander/lunarlander.swf", "/sims/lunar-lander/lunar-lander.swf" );
        postWicketMap.put( "/simulations/lunarLander/lunarlanderOld.swf", "/sims/lunar-lander/lunar-lander.swf" );
        postWicketMap.put( "/simulations/massspringlab/MassSpringLab2.swf", "/sims/mass-spring-lab/mass-spring-lab.swf" );
        postWicketMap.put( "/simulations/mazegame/mazegame-pro.jar", "/sims/maze-game/maze-game.jar" );
        postWicketMap.put( "/simulations/mazegame/mazegame_es.jnlp", "/sims/maze-game/maze-game_es.jnlp" );
        postWicketMap.put( "/simulations/mazegame/webstart.jnlp", "/sims/maze-game/maze-game.jnlp" );
        postWicketMap.put( "/simulations/microwaves/microwaves-save.jar", "/sims/microwaves/microwaves.jar" );
        postWicketMap.put( "/simulations/microwaves/microwaves.jar", "/sims/microwaves/microwaves.jar" );
        postWicketMap.put( "/simulations/microwaves/microwaves.jnlp", "/sims/microwaves/microwaves.jnlp" );
        postWicketMap.put( "/simulations/microwaves/microwaves_es.jnlp", "/sims/microwaves/microwaves_es.jnlp" );
        postWicketMap.put( "/simulations/molecular-reactions/molecular-reactions.jar", "/sims/reactions-and-rates/reactions-and-rates.jar" );
        postWicketMap.put( "/simulations/molecular-reactions/molecular-reactions.jnlp", "/sims/reactions-and-rates/reactions-and-rates.jnlp" );
        postWicketMap.put( "/simulations/molecular-reactions/molecular-reactions_es.jnlp", "/sims/reactions-and-rates/reactions-and-rates.jnlp" );
        postWicketMap.put( "/simulations/molecular-reactions/mr-pro.jar", "/sims/reactions-and-rates/reactions-and-rates.jar" );
        postWicketMap.put( "/simulations/motion2d/motion2d-pro.jar", "/sims/motion-2d/motion-2d.jar" );
        postWicketMap.put( "/simulations/motion2d/motion2d.jnlp", "/sims/motion-2d/motion-2d.jnlp" );
        postWicketMap.put( "/simulations/motion2d/motion2d_es.jnlp", "/sims/motion-2d/motion-2d_es.jnlp" );
        postWicketMap.put( "/simulations/movingman-old/movingman.jar", "/sims/moving-man/moving-man.jar" );
        postWicketMap.put( "/simulations/movingman-old/movingman.jnlp", "/sims/moving-man/moving-man.jnlp" );
        postWicketMap.put( "/simulations/movingman/movingman-pro.jar", "/sims/moving-man/moving-man.jar" );
        postWicketMap.put( "/simulations/movingman/movingman.jnlp", "/sims/moving-man/moving-man.jnlp" );
        postWicketMap.put( "/simulations/movingman/movingman_es.jnlp", "/sims/moving-man/moving-man_es.jnlp" );
        postWicketMap.put( "/simulations/mri/mri.jar", "/sims/mri/mri.jar" );
        postWicketMap.put( "/simulations/mri/mri.jnlp", "/sims/mri/mri.jnlp" );
        postWicketMap.put( "/simulations/mri/mri_es.jnlp", "/sims/mri/mri_es.jnlp" );
        postWicketMap.put( "/simulations/nuclearphysics/nuclearphysics.jar", "/sims/nuclear-physics/nuclear-physics.jar" );
        postWicketMap.put( "/simulations/nuclearphysics/nukes.jnlp", "/sims/nuclear-physics/nuclear-physics.jnlp" );
        postWicketMap.put( "/simulations/nuclearphysics/nukes_es.jnlp", "/sims/nuclear-physics/nuclear-physics_es.jnlp" );
        postWicketMap.put( "/simulations/ohm1d/ohm1d.jar", "/sims/ohm-1d/ohm-1d.jar" );
        postWicketMap.put( "/simulations/ohm1d/ohm1d_es.jnlp", "/sims/ohm-1d/ohm-1d_es.jnlp" );
        postWicketMap.put( "/simulations/ohm1d/webstart.jnlp", "/sims/ohm-1d/ohm-1d.jnlp" );
        postWicketMap.put( "/simulations/orbits/orbits.swf", "/sims/my-solar-system/my-solar-system.swf" );
        postWicketMap.put( "/simulations/orbits/orbitsOld.swf", "/sims/my-solar-system/my-solar-system.swf" );
        postWicketMap.put( "/simulations/photoelectric/photoelectric.jar", "/sims/photoelectric/photoelectric.jar" );
        postWicketMap.put( "/simulations/photoelectric/photoelectric.jnlp", "/sims/photoelectric/photoelectric.jnlp" );
        postWicketMap.put( "/simulations/projectilemotion/projectile.swf", "/sims/projectile-motion/projectile-motion.swf" );
        postWicketMap.put( "/simulations/projectilemotion/projectileOlder.swf", "/sims/projectile-motion/projectile-motion.swf" );
        postWicketMap.put( "/simulations/projectilemotion/projectileOldest.swf", "/sims/projectile-motion/projectile-motion.swf" );
        postWicketMap.put( "/simulations/quantum-tunneling/quantum-tunneling.jnlp", "/sims/quantum-tunneling/quantum-tunneling.jnlp" );
        postWicketMap.put( "/simulations/quantum-tunneling/quantum-tunneling_es.jnlp", "/sims/quantum-tunneling/quantum-tunneling_es.jnlp" );
        postWicketMap.put( "/simulations/quantum-tunneling/quantumtunneling.jar", "/sims/quantum-tunneling/quantum-tunneling.jar" );
        postWicketMap.put( "/simulations/rhola/rRhoLA2.swf", "/sims/resistance-in-a-wire/resistance-in-a-wire.swf" );
        postWicketMap.put( "/simulations/rutherford-scattering/rutherford-scattering.jar", "/sims/rutherford-scattering/rutherford-scattering.jar" );
        postWicketMap.put( "/simulations/rutherford-scattering/rutherford-scattering.jnlp", "/sims/rutherford-scattering/rutherford-scattering.jnlp" );
        postWicketMap.put( "/simulations/rutherford-scattering/rutherford-scattering_es.jnlp", "/sims/rutherford-scattering/rutherford-scattering_es.jnlp" );
        postWicketMap.put( "/simulations/schrodinger/dg-pro.jar", "/sims/quantum-wave-interference/davisson-germer.jar" );
        postWicketMap.put( "/simulations/schrodinger/dg.jnlp", "/sims/quantum-wave-interference/davisson-germer.jnlp" );
        postWicketMap.put( "/simulations/schrodinger/dg_es.jnlp", "/sims/quantum-wave-interference/davisson-germer.jnlp" );
        postWicketMap.put( "/simulations/schrodinger/qwi-pro.jar", "/sims/quantum-wave-interference/quantum-wave-interference.jar" );
        postWicketMap.put( "/simulations/schrodinger/schrodinger.jnlp", "/sims/quantum-wave-interference/quantum-wave-interference.jnlp" );
        postWicketMap.put( "/simulations/schrodinger/schrodinger_es.jnlp", "/sims/quantum-wave-interference/quantum-wave-interference.jnlp" );
        postWicketMap.put( "/simulations/sdpm/particles.jar", "/sims/self-driven-particle-model/self-driven-particle-model.jar" );
        postWicketMap.put( "/simulations/sdpm/particles.jnlp", "/sims/self-driven-particle-model/self-driven-particle-model.jnlp" );
        postWicketMap.put( "/simulations/semiconductor/semiconductor.jar", "/sims/semiconductor/semiconductor.jar" );
        postWicketMap.put( "/simulations/semiconductor/semiconductor.jnlp", "/sims/semiconductor/semiconductor.jnlp" );
        postWicketMap.put( "/simulations/semiconductor/semiconductor_es.jnlp", "/sims/semiconductor/semiconductor_es.jnlp" );
        postWicketMap.put( "/simulations/shaper/shaper.jar", "/sims/optical-quantum-control/optical-quantum-control.jar" );
        postWicketMap.put( "/simulations/shaper/shaper.jnlp", "/sims/optical-quantum-control/optical-quantum-control.jnlp" );
        postWicketMap.put( "/simulations/shaper/shaper_es.jnlp", "/sims/optical-quantum-control/optical-quantum-control_es.jnlp" );
        postWicketMap.put( "/simulations/signalcircuit/signalCircuit.jar", "/sims/signal-circuit/signal-circuit.jar" );
        postWicketMap.put( "/simulations/signalcircuit/signalcircuit.jar", "/sims/signal-circuit/signal-circuit.jar" );
        postWicketMap.put( "/simulations/signalcircuit/signalcircuit.jnlp", "/sims/signal-circuit/signal-circuit.jnlp" );
        postWicketMap.put( "/simulations/signalcircuit/signalcircuit_es.jnlp", "/sims/signal-circuit/signal-circuit_es.jnlp" );
        postWicketMap.put( "/simulations/signalcircuit/webstart.jnlp", "/sims/signal-circuit/signal-circuit.jnlp" );
        postWicketMap.put( "/simulations/soluble-salts/soluble-salts.jnlp", "/sims/soluble-salts/soluble-salts.jnlp" );
        postWicketMap.put( "/simulations/soluble-salts/soluble-salts_es.jnlp", "/sims/soluble-salts/soluble-salts_es.jnlp" );
        postWicketMap.put( "/simulations/soluble-salts/solublesalts-save.jar", "/sims/soluble-salts/soluble-salts.jar" );
        postWicketMap.put( "/simulations/soluble-salts/solublesalts.jar", "/sims/soluble-salts/soluble-salts.jar" );
        postWicketMap.put( "/simulations/sound/sound.jar", "/sims/sound/sound.jar" );
        postWicketMap.put( "/simulations/sound/sound.jnlp", "/sims/sound/sound.jnlp" );
        postWicketMap.put( "/simulations/sound/sound_es.jnlp", "/sims/sound/sound_es.jnlp" );
        postWicketMap.put( "/simulations/sterngerlach/SG_1.swf", "/sims/stern-gerlacher/stern-gerlach.swf" );
        postWicketMap.put( "/simulations/stringwave/stringWave.swf", "/sims/string-wave/string-wave.swf" );
        postWicketMap.put( "/simulations/stringwave/stringWaveOld.swf", "/sims/string-wave/string-wave.swf" );
        postWicketMap.put( "/simulations/theramp/theramp-pro.jar", "/sims/the-ramp/the-ramp.jar" );
        postWicketMap.put( "/simulations/theramp/theramp.jnlp", "/sims/the-ramp/the-ramp.jnlp" );
        postWicketMap.put( "/simulations/travoltage/travoltage-pro.jar", "/sims/travoltage/travoltage.jar" );
        postWicketMap.put( "/simulations/travoltage/travoltage_es.jnlp", "/sims/travoltage/travoltage_es.jnlp" );
        postWicketMap.put( "/simulations/travoltage/webstart.jnlp", "/sims/travoltage/travoltage.jnlp" );
        postWicketMap.put( "/simulations/vectormath/vectorMath.swf", "/sims/vector-math/vector-math.swf" );
        postWicketMap.put( "/simulations/vectormath/vectorMathOlder.swf", "/sims/vector-math/vector-math.swf" );
        postWicketMap.put( "/simulations/vectormath/vectorMathOldest.swf", "/sims/vector-math/vector-math.swf" );
        postWicketMap.put( "/simulations/veqir/VeqIRColored.swf", "/sims/veqir/veqir.swf" );
        postWicketMap.put( "/simulations/waveinterference/waveinterference-pro.jar", "/sims/wave-interference/wave-interference.jar" );
        postWicketMap.put( "/simulations/waveinterference/waveinterference.jnlp", "/sims/wave-interference/wave-interference.jnlp" );
        postWicketMap.put( "/simulations/waveinterference/waveinterference_sp.jnlp", "/sims/wave-interference/wave-interference_es.jnlp" );

        postWicketMap.put( "/Design/Assets/images/Phet-Kavli-logo.jpg", "/publications/Design/Assets/images/Phet-Kavli-logo.jpg" );
        postWicketMap.put( "/teacher_ideas/contribution-guidelines.pdf", "/publications/activities-guide/contribution-guidelines.pdf" );
        postWicketMap.put( "/teacher_ideas/HighSchoolSampleUse.pdf", "/publications/activities-guide/HighSchoolSampleUse.pdf" );
        postWicketMap.put( "/teacher_ideas/ModernPhysicsSampleUse.pdf", "/publications/activities-guide/ModernPhysicsSampleUse.pdf" );
        postWicketMap.put( "/teacher_ideas/PhysicsOfEverydayLifeSampleUse.pdf", "/publications/activities-guide/PhysicsOfEverydayLifeSampleUse.pdf" );

        postWicketMap.put( "/sims", "/sims/" );
        postWicketMap.put( "/publications", "/publications/" );
        postWicketMap.put( "/workshops", "/workshops/" );
        postWicketMap.put( "/files", "/files/" );
        postWicketMap.put( "/installer", "/installer/" );
        postWicketMap.put( "/newsletters", "/newsletters/" );
        postWicketMap.put( "/statistics", "/statistics/" );

        postWicketMap.put( "/contribute/get-translation-utility.php", Linkers.PHET_TRANSLATION_UTILITY_JAR.getDefaultRawUrl() );

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
            return "/installer" + path.substring( "/phet-dist/installers".length() );
        }
        else if ( path.startsWith( OLD_TRANSLATION_UTIL ) ) {
            return "/files/translation-utility/" + path.substring( OLD_TRANSLATION_UTIL.length() );
        }
        else if ( path.startsWith( SIM_SEARCH ) ) {
            return redirectSimSearch( parameters );
        }
        else if ( path.startsWith( CHANGELOG ) ) {
            return redirectChangelog( parameters );
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
            if ( idstr.contains( "?" ) ) {
                idstr = idstr.substring( 0, idstr.indexOf( "?" ) );
            }
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
            if ( ret[0] == null ) {
                logger.info( "did not find old contribution value: " + id );
                return NOT_FOUND;
            }
            return ret[0];
        }
        catch( RuntimeException e ) {
            logger.info( "bad number X", e );
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
                if ( simulation == null ) {
                    return false;
                }
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
                if ( file == null ) {
                    return false;
                }
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
                if ( contribution == null ) {
                    return false;
                }
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
                if ( guide == null ) {
                    return false;
                }
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
            logger.warn( "BAD #1 for get-member-file: " + parameters );
            return NOT_FOUND;
        }
        String filename = ( (String[]) parameters.get( "file" ) )[0];
        if ( filename.equals( "../phet-dist/installers/PhET-Installer_windows.exe" ) ||
             filename.equals( "..%2Fphet-dist%2Finstallers%2FPhET-Installer_windows.exe" ) ) {
            return FullInstallPanel.WINDOWS_INSTALLER_LOCATION;
        }
        else {
            logger.warn( "BAD #2 for get-member-file: " + filename );
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

    private static String redirectChangelog( Map parameters ) {
        // /simulations/changelog.php?sim=Wave_Interference
        if ( parameters.get( "sim" ) == null ) {
            return NOT_FOUND;
        }
        String sim = ( (String[]) parameters.get( "sim" ) )[0];
        String newsim = simMap.get( sim );
        if ( newsim == null ) {
            newsim = badSimMap.get( processSimName( sim ) );
        }

        if ( newsim != null ) {
            return "/en/simulation/" + newsim + "/changelog";
        }

        return NOT_FOUND;
    }

    private static String redirectOldWeb( String path ) {
        if ( oldMap.containsKey( path ) ) {
            return oldMap.get( path );
        }
        else if ( path.startsWith( "/web-pages/publications" ) ) {
            return path.substring( "/web-pages".length() );
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

    /**
     * Called after both the above redirection strategy, Wicket, and Tomcat all cannot find a file. Here, return null
     * if not found, otherwise return a relative path that starts with "/". Do NOT include a query string in the final
     * result
     *
     * @param path        Relative path starting with "/" of a non-existant path or file
     * @param queryString The passed in query string
     * @return Null or a path starting with "/"
     */
    public static String redirectFile( String path, String queryString ) {
        if ( path.startsWith( "/images/" ) || path.startsWith( "/css/" ) || path.startsWith( "/js/" ) ) {
            return "/files/archive" + path;
        }
        else if ( postWicketMap.containsKey( path ) ) {
            return postWicketMap.get( path );
        }
        else if ( path.startsWith( "/new/" ) ) {
            return path.substring( "/new".length() );
        }
        else if ( path.startsWith( "/index.php/" ) ) {
            return path.substring( "/index.php".length() );
        }
        else if ( path.startsWith( "/simulations/images/" ) ) {
            return path.substring( "/simulations".length() );
        }
        else if ( path.startsWith( "/teacher_ideas/login-and-redirect.php" ) ) {
            if ( queryString.startsWith( "url=" ) ) {
                return queryString.substring( "url=".length() );
            }
            return queryString;
        }
        return null;
    }

    public boolean matches( IRequestTarget requestTarget ) {
        return requestTarget instanceof RedirectRequestTarget;
    }

    public boolean matches( String path, boolean caseSensitive ) {
        if ( caseSensitive ) {
            return matches( path );
        }
        else {
            logger.warn( "testing non-case-sensitive path: " + path );
            return matches( path.toLowerCase() );
        }
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
        else if ( morphedPath.startsWith( OLD_TRANSLATION_UTIL ) ) {
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
