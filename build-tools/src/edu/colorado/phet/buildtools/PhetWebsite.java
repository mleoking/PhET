package edu.colorado.phet.buildtools;

/**
 * Represents a Wicket-able website configuration that can have things deployed
 */
public abstract class PhetWebsite {
    /**
     * @return The hostname for SSH access
     */
    public abstract String getServerHost();

    /**
     * @return The hostname for HTTP(s) access
     */
    public abstract String getWebHost();

    /**
     * @return The location on the server side where the build-local.properties exists
     */
    public abstract String getBuildLocalPropertiesLocation();

    /**
     * @return SSH credentials
     */
    public abstract AuthenticationInfo getServerAuthenticationInfo( BuildLocalProperties properties );

    /**
     * @return Tomcat manager credentials
     */
    public abstract AuthenticationInfo getTomcatAuthenticationInfo( BuildLocalProperties properties );

    /**
     * @return The URL where the front page (base) of the website can be accessed
     */
    public String getWebBaseURL() {
        return "http://" + getWebHost();
    }

    public static PhetWebsite FIGARO = new PhetWebsite() {
        @Override
        public String getServerHost() {
            return "figaro.colorado.edu";
        }

        @Override
        public String getWebHost() {
            return "phetsims.colorado.edu";
        }

        @Override
        public String getBuildLocalPropertiesLocation() {
            return "/usr/local/tomcat/conf/build-local.properties";
        }

        @Override
        public AuthenticationInfo getServerAuthenticationInfo( BuildLocalProperties properties ) {
            return properties.getWebsiteProdAuthenticationInfo();
        }

        @Override
        public AuthenticationInfo getTomcatAuthenticationInfo( BuildLocalProperties properties ) {
            return properties.getWebsiteProdManagerAuthenticationInfo();
        }
    };
}
