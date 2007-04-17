/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * PropertiesLoader is used to load properties from a resource in the classpath.
 * Adapted from http://www.javaworld.com/javaworld/javaqa/2003-08/01-qa-0808-property.html.
 */
public abstract class PropertiesLoader {
    
    // Standard properties are listed here
    public static final String PROPERTY_VERSION_MAJOR = "version.major";
    public static final String PROPERTY_VERSION_MINOR = "version.minor";
    public static final String PROPERTY_VERSION_DEV = "version.dev";
    public static final String PROPERTY_VERSION_REVISION = "version.revision";
    public static final String PROPERTY_ABOUT_CREDITS = "about.credits";

    private static final String SUFFIX = ".properties";
    
    /* not intended for instantiation */
    private PropertiesLoader() {}
    
    /**
     * Looks up a resource named 'resourceName' in the classpath. 
     * The resource must map to a file with .properties extention. 
     * The name may be an absolute or relative path and can use either 
     * "/" or "." for package segment separation with an optional 
     * leading "/" and optional ".properties" suffix. Thus, the
     * following names refer to the same resource:
     * <pre>
     * some.pkg.Resource
     * some.pkg.Resource.properties
     * some/pkg/Resource
     * some/pkg/Resource.properties
     * /some/pkg/Resource
     * /some/pkg/Resource.properties
     * </pre>
     * 
     * @param resourceName classpath resource name [may not be null]
     * @param loader classloader through which to load the resource [null
     * is equivalent to the application loader]
     * @return resource converted to java.util.Properties
     */
    public static Properties loadProperties( String resourceName ) {
        
        if ( resourceName == null ) {
            throw new IllegalArgumentException( "name is null" );
        }

        // Process the resource name
        if ( resourceName.startsWith( "/" ) ) {
            resourceName = resourceName.substring( 1 );
        }
        if ( resourceName.endsWith( SUFFIX ) ) {
            resourceName = resourceName.substring( 0, resourceName.length() - SUFFIX.length() );
        }
        resourceName = resourceName.replace( '/', '.' );
        
        // Get the resource bundle
        ResourceBundle rb = null;
        try {
             rb = ResourceBundle.getBundle( resourceName );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            rb = null;
        }

        // Load the resource bundle into Properties
        Properties result = new Properties();
        if ( rb != null ) {
            for ( Enumeration keys = rb.getKeys(); keys.hasMoreElements(); ) {
                final String key = (String) keys.nextElement();
                final String value = rb.getString( key );

                result.put( key, value );
            }
        }

        return result;
    }
}
