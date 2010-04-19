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

public class RedirectionStrategy implements IRequestTargetUrlCodingStrategy {

    private static Logger logger = Logger.getLogger( RedirectionStrategy.class.getName() );

    private static Map<String, String> map = new HashMap<String, String>();

    static {
        // initialize redirection mapping
        map.put( "/index.php", "/" );
        map.put( "/simulations/index.php", "/en/simulations/category/featured" );
        map.put( "/tech_support/index.php", "/en/troubleshooting" );
        map.put( "/tech_support/support-flash.php", "/en/troubleshooting/flash" );
        map.put( "/tech_support/support-java.php", "/en/troubleshooting/java" );
        map.put( "/tech_support/support-javascript.php", "/en/troubleshooting/javascript" );
        map.put( "/contribute/index.php", "/en/contribute" );
        map.put( "/research/index.php", "/en/research" );
        map.put( "/about/index.php", "/en/about" );

        map.put( "/simulations/sims.php?sim=Circuit_Construction_Kit_DC_Only", "/en/simulation/circuit-construction-kit/circuit-construction-kit-dc" );

        map.put( "/teacher_ideas/view-contribution.php", null );

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
        String cviewstr = "/teacher_ideas/view-contribution.php";
        if ( path.startsWith( cviewstr ) ) {
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
