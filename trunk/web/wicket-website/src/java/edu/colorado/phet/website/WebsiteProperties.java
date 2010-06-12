package edu.colorado.phet.website;

import java.io.File;

import javax.servlet.ServletContext;

public class WebsiteProperties {
    private ServletContext servletContext;

    public static final String PHET_DOCUMENT_ROOT = "phet-document-root";
    public static final String PHET_DOWNLOAD_ROOT = "phet-download-root";
    public static final String PHET_DOWNLOAD_LOCATION = "phet-download-location";
    public static final String BUILD_LOCAL_PROPERTIES = "build-local-properties";
    public static final String PATH_TO_JAR_UTILITY = "path-to-jar-utility";
    public static final String SIM_STAGING_AREA = "sim-staging-area";

    public static final String MAIL_HOST = "mail-host";
    public static final String MAIL_USER = "mail-user";
    public static final String MAIL_PASSWORD = "mail-password";

    public WebsiteProperties( ServletContext servletContext ) {
        this.servletContext = servletContext;
    }

    public File getPhetDocumentRoot() {
        return getFileFromLocation( getParameter( PHET_DOCUMENT_ROOT ) );
    }

    public File getPhetDownloadRoot() {
        return getFileFromLocation( getParameter( PHET_DOWNLOAD_ROOT ) );
    }

    public File getBuildLocalPropertiesFile() {
        return new File( getParameter( BUILD_LOCAL_PROPERTIES ) );
    }

    public String getPhetDownloadLocation() {
        return getParameter( PHET_DOWNLOAD_LOCATION );
    }

    public String getPathToJarUtility() {
        return getParameter( PATH_TO_JAR_UTILITY );
    }

    public boolean hasMailParameters() {
        return hasParameter( MAIL_HOST ) && hasParameter( MAIL_USER ) && hasParameter( MAIL_PASSWORD );
    }

    public String getMailHost() {
        return getParameter( MAIL_HOST );
    }

    public String getMailUser() {
        return getParameter( MAIL_USER );
    }

    public String getMailPassword() {
        return getParameter( MAIL_PASSWORD );
    }

    /**
     * @return Returns the directory where simulations being deployed should be located. By convention, it should contain
     *         subdirectories named after their respective projects
     */
    public File getSimStagingArea() {
        return new File( getParameter( SIM_STAGING_AREA ) );
    }

    private String getParameter( String paramName ) {
        return servletContext.getInitParameter( paramName );
    }

    private boolean hasParameter( String paramName ) {
        return getParameter( paramName ) != null;
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
