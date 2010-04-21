package edu.colorado.phet.website.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.hibernate.Session;

import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.data.contribution.Contribution;

/**
 * Handles redirecting all of the old-site URLs to the new URLs. They will then be sent out with 301 (permanent)
 * redirections.
 */
public class RedirectionStrategy implements IRequestTargetUrlCodingStrategy {

    private static Logger logger = Logger.getLogger( RedirectionStrategy.class.getName() );

    private static Map<String, String> map = new HashMap<String, String>();

    private static Map<String, String> categoryMap = new HashMap<String, String>();

    private static final String VIEW_CONTRIBUTION = "/teacher_ideas/view-contribution.php";
    private static final String VIEW_CATEGORY = "/simulations/index.php";

    static {
        // initialize redirection mapping
        map.put( "/about/index.php", "/en/about" );
        map.put( "/contribute/index.php", "/en/contribute" );
        map.put( "/index.php", "/" );
        map.put( "/research/index.php", "/en/research" );
        map.put( "/simulations/translations.php", "/en/simulations/translated" );
        map.put( "/tech_support/index.php", "/en/troubleshooting" );
        map.put( "/tech_support/support-flash.php", "/en/troubleshooting/flash" );
        map.put( "/tech_support/support-java.php", "/en/troubleshooting/java" );
        map.put( "/tech_support/support-javascript.php", "/en/troubleshooting/javascript" );

//        map.put( "/simulations/sims.php?sim=Circuit_Construction_Kit_DC_Only", "/en/simulation/circuit-construction-kit/circuit-construction-kit-dc" );

        map.put( VIEW_CONTRIBUTION, null );
        map.put( VIEW_CATEGORY, null );


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
        

        // TODO: add all URLs
    }

    /**
     * @param path       A path to check redirections with
     * @param parameters
     * @return null if no direction is needed, otherwise the path to redirect to
     */
    private static String checkRedirect( String path, Map parameters ) {
        //http://phet.colorado.edu/teacher_ideas/view-contribution.php?contribution_id=690
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
            }
        }
        else if ( path.startsWith( VIEW_CATEGORY ) ) {
            String prefix = "/en/simulations/category/";
            if ( parameters.get( "cat" ) == null ) {
                return prefix + "featured";
            }
            String cat = ( (String[]) parameters.get( "cat" ) )[0];
            String newcat = categoryMap.get( cat );
            if ( newcat != null ) {
                return prefix + newcat;
            }

            // ignore old categories?
            return prefix + "featured";
        }
        return null;
    }

    private static String morphPath( String str ) {
        return "/" + str;
    }

    public String getMountPath() {
        return "";
    }

    public CharSequence encode( IRequestTarget requestTarget ) {
        // won't make links to this, so it shouldn't matter
        return null;
    }

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

    public boolean matches( String path ) {
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
}
