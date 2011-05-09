package edu.colorado.phet.website.services;

import java.io.File;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.FileResourceStream;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.Simulation;

/**
 * Replacement sim-jar-redirect service. Called from Java simulations.
 */
public class SimJarRedirectPage extends WebPage {

    private static final Logger logger = Logger.getLogger( SimJarRedirectPage.class.getName() );

    public SimJarRedirectPage( PageParameters parameters ) {
        super( parameters );

        int requestVersion = parameters.getInt( "request_version" );
        if ( requestVersion != 1 ) {
            logger.error( "Unsupported version: " + requestVersion );
            return;
        }

        String project = parameters.getString( "project" );
        String sim = parameters.getString( "sim" );
        String language = parameters.getString( "language" );
        String country = parameters.getString( "country" );

        if ( project == null ) {
            logger.warn( "Project not given" );
        }

        boolean findAllJar = sim == null || language == null;

        File docRoot = PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot();

        File jarFile;

        if ( findAllJar ) {
            jarFile = Simulation.getAllJar( docRoot, project );
        }
        else {
            Locale locale = country == null ? new Locale( language ) : new Locale( language, country );
            jarFile = Simulation.getLocalizedJar( docRoot, project, sim, locale );
            if ( !jarFile.exists() ) {
                if ( !( locale.getCountry() == null || locale.getCountry().equals( "" ) ) ) {
                    jarFile = Simulation.getLocalizedJar( docRoot, project, sim, new Locale( locale.getLanguage() ) );
                }
                if ( !jarFile.exists() ) {
                    jarFile = Simulation.getLocalizedJar( docRoot, project, sim, PhetWicketApplication.getDefaultLocale() );
                }
            }
        }

        if ( !jarFile.exists() ) {
            logger.warn( "jar file does not exist: " + jarFile.getAbsolutePath() );
        }
        else {
            setResponseAttachFile( jarFile );
        }

    }

    public void setResponseAttachFile( File file ) {
        getRequestCycle().setRequestTarget( new ResourceStreamRequestTarget( new FileResourceStream( file ) ) );

        ( (WebResponse) getResponse() ).setHeader( "Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" );
        ( (WebResponse) getResponse() ).setHeader( "Content-Description", file.getName() );
    }

}