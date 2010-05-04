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
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionFile;

/**
 * Handles redirecting all of the old-site URLs to the new URLs. They will then be sent out with 301 (permanent)
 * redirections.
 */
public class RedirectionStrategy implements IRequestTargetUrlCodingStrategy {

    private static Logger logger = Logger.getLogger( RedirectionStrategy.class.getName() );

    private static Map<String, String> map = new HashMap<String, String>();

    private static Map<String, String> categoryMap = new HashMap<String, String>();
    private static Map<String, String> badCategoryMap = new HashMap<String, String>(); // for those ugly variations that aren't immediately caught

    private static Map<String, String> simMap = new HashMap<String, String>();
    private static Map<String, String> badSimMap = new HashMap<String, String>();

    private static final String VIEW_CONTRIBUTION = "/teacher_ideas/view-contribution.php";
    private static final String VIEW_CATEGORY = "/simulations/index.php";
    private static final String VIEW_SIM = "/simulations/sims.php";
    private static final String RUN_OFFLINE = "/admin/get-run-offline.php";
    private static final String DOWNLOAD_CONTRIBUTION_FILE = "/admin/get-contribution-file.php";

    private static final String NOT_FOUND = "/error/404";

    static {

        //----------------------------------------------------------------------------
        // general page map
        //----------------------------------------------------------------------------

        // initialize redirection mapping. value of null indicates that it will be handled by custom code, usually for query parameters
        map.put( "/about/contact.php", "/en/about/contact" );
        map.put( "/about/index.php", "/en/about" );
        map.put( "/about/legend.php", "/en/about/legend" );
        map.put( "/about/licensing.php", "/en/about/licensing" );
        map.put( "/about/news.php", "/en/about/news" );
        map.put( "/about/source-code.php", "/en/about/source-code" );
        map.put( "/about/who-we-are.php", "/en/about" );
        // TODO: add news redirect
        map.put( "/contribute/donate.php", "/en/donate" );
        map.put( "/contribute/index.php", "/en/for-translators" );
        map.put( "/contribute/translation-utility.php", "/en/for-translators/translation-utility" );
        map.put( "/get_phet/full_install.php", "/en/get-phet/full-install" );
        map.put( "/get_phet/index.php", "/en/get-phet" );
        map.put( "/get_phet/simlauncher.php", "/en/get-phet/one-at-a-time" );
        map.put( "/index.php", "/" );
        map.put( "/research/index.php", "/en/research" );
        map.put( "/simulations", "/en/simulations/category/featured" );
        map.put( "/simulations/", "/en/simulations/category/featured" );
        map.put( "/simulations/translations.php", "/en/simulations/translated" );
        map.put( "/sponsors/index.php", "/en/about/sponsors" );
        map.put( "/teacher_ideas/browse.php", "/en/contributions/browse" );
        map.put( "/teacher_ideas/contribute.php", "/en/contributions/submit" );
        map.put( "/teacher_ideas/contribution-guidelines.php", "/en/contributions/guide" );
        map.put( "/teacher_ideas/user-edit-profile.php", "/en/edit-profile" );
        map.put( "/teacher_ideas/index.php", "/en/contributions" );
        map.put( "/teacher_ideas/manage-contributions.php", "/en/contributions/manage" );
        map.put( "/teacher_ideas/workshops.php", "/en/workshops" );
        map.put( "/tech_support/index.php", "/en/troubleshooting" );
        map.put( "/tech_support/support-flash.php", "/en/troubleshooting/flash" );
        map.put( "/tech_support/support-java.php", "/en/troubleshooting/java" );
        map.put( "/tech_support/support-javascript.php", "/en/troubleshooting/javascript" );

//        map.put( "/simulations/sims.php?sim=Circuit_Construction_Kit_DC_Only", "/en/simulation/circuit-construction-kit/circuit-construction-kit-dc" );

        map.put( VIEW_CONTRIBUTION, null );
        map.put( VIEW_CATEGORY, null );
        map.put( VIEW_SIM, null );
        map.put( RUN_OFFLINE, null );
        map.put( DOWNLOAD_CONTRIBUTION_FILE, null );

        //----------------------------------------------------------------------------
        // category map
        //----------------------------------------------------------------------------

        categoryMap.put( "Featured_Sims", "featured" );
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
        categoryMap.put( "Top_Simulations", "featured" );
        categoryMap.put( "Top_Simulation", "featured" );
        categoryMap.put( "", "featured" ); // yes, somehow there are a number with this blank

        for ( String key : categoryMap.keySet() ) {
            badCategoryMap.put( processCategoryName( key ), categoryMap.get( key ) );
        }

        //----------------------------------------------------------------------------
        // simulation map
        //----------------------------------------------------------------------------

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

        // TODO: add all URLs
    }

    /**
     * @param path       A path to check redirections with
     * @param parameters
     * @return null if no direction is needed, otherwise the path to redirect to
     */
    private static String checkRedirect( String path, Map parameters ) {
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
        } else if( path.startsWith( DOWNLOAD_CONTRIBUTION_FILE ) ) {
            return redirectDownloadContributionFile( parameters );
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
            return prefix + "featured";
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

        if ( !parameters.containsKey( "sim_id" ) ) {
            return NOT_FOUND;
        }

        final int simId = Integer.parseInt( ( (String[]) parameters.get( "sim_id" ) )[0] );

        final Locale locale = parameters.get( "locale" ) == null ? PhetWicketApplication.getDefaultLocale() :
                              LocaleUtils.stringToLocale( ( (String[]) parameters.get( "locale" ) )[0] );
        final StringBuffer ret = new StringBuffer();
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                Simulation simulation = (Simulation) session.createQuery( "select s from Simulation as s where s.oldId = :oldid").setInteger( "oldid", simId ).uniqueResult();
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

    private static String redirectDownloadContributionFile ( Map parameters ) {
        // http://phet.colorado.edu/admin/get-contribution-file.php?contribution_file_id=555

        if ( !parameters.containsKey( "contribution_file_id" ) ) {
            return NOT_FOUND;
        }

        final int contributionFileId = Integer.parseInt( ( (String[]) parameters.get( "contribution_file_id" ) )[0] );

        final StringBuffer ret = new StringBuffer();
        boolean success = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                ContributionFile file = (ContributionFile) session.createQuery( "select cf from ContributionFile as cf where cf.oldId = :oldid").setInteger("oldid", contributionFileId ).uniqueResult();
                ret.append( file.getLinker().getDefaultRawUrl() );
                return true;
            }
        } );

        if ( success ) {
            return ret.toString();
        }

        return NOT_FOUND;
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

        boolean inMap = map.containsKey( morphPath( path ) );

        logger.debug( "testing: " + path );

        if ( inMap ) {
            return true;
        }

        if ( path.length() == 2 && path.indexOf( "." ) == -1 ) {
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
