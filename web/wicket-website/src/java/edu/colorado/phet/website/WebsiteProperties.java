package edu.colorado.phet.website;

import java.io.File;

import javax.servlet.ServletContext;

public class WebsiteProperties {
    private ServletContext servletContext;

    // TODO: start caching parameters

    public static final String PHET_DOCUMENT_ROOT = "phet-document-root";
    public static final String PHET_DOWNLOAD_ROOT = "phet-download-root";
    public static final String PHET_DOWNLOAD_LOCATION = "phet-download-location";
    public static final String BUILD_LOCAL_PROPERTIES = "build-local-properties";
    public static final String PATH_TO_JAR_UTILITY = "path-to-jar-utility";
    public static final String SIM_STAGING_AREA = "sim-staging-area";

    public WebsiteProperties( ServletContext servletContext ) {
        this.servletContext = servletContext;
    }

    public File getPhetDocumentRoot() {
        return getFileFromLocation( servletContext.getInitParameter( PHET_DOCUMENT_ROOT ) );
    }

    public File getPhetDownloadRoot() {
        return getFileFromLocation( servletContext.getInitParameter( PHET_DOWNLOAD_ROOT ) );
    }

    public File getBuildLocalPropertiesFile() {
        return new File( servletContext.getInitParameter( BUILD_LOCAL_PROPERTIES ) );
    }

    public String getPhetDownloadLocation() {
        return servletContext.getInitParameter( PHET_DOWNLOAD_LOCATION );
    }

    public String getPathToJarUtility() {
        return servletContext.getInitParameter( PATH_TO_JAR_UTILITY );
    }

    /**
     * @return Returns the directory where simulations being deployed should be located. By convention, it should contain
     *         subdirectories named after their respective projects
     */
    public File getSimStagingArea() {
        return new File( servletContext.getInitParameter( SIM_STAGING_AREA ) );
    }

    private static File getFileFromLocation( String location ) {
        if ( location == null ) {
            return null;
        }
        File file = new File( location );
        if ( !file.exists() ) {
            return null;
        }
        return file;
    }
}
