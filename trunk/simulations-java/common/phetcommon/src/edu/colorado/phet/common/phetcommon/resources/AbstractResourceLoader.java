/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

/**
 * AbstractResourceLoader provides a default implementation for
 * portions of IResourceLoader that are not PhET specific.
 */
/* package private */
abstract class AbstractResourceLoader implements IResourceLoader {

    private static final String PROPERTIES_SUFFIX = ".properties";

    /**
     * Determines if a named resource exists.
     *
     * @param resource
     * @return true or false
     */
    public boolean exists( String resource ) {
        ClassLoader cl = this.getClass().getClassLoader();
        URL url = cl.getResource( resource );

        //if (url == null) {
        //    new Exception("The resource " + resource + " could not be found").printStackTrace();
        //}

        return url != null;
    }

    /**
     * Gets properties by resource name.
     * <p/>
     * Looks up a resource named 'resourceName' in the classpath.
     * The resource must map to a file with .properties extension.
     * The name is a relative path in the data/ directory, with
     * "/" for package segment separation with an optional
     * leading "/" and optional ".properties" suffix. Thus, the
     * following names refer to the same resource:
     * <pre>
     * some/pkg/Resource
     * some/pkg/Resource.properties
     * /some/pkg/Resource
     * /some/pkg/Resource.properties
     * </pre>
     *
     * @param resourceName classpath resource name [may not be null]
     * @param locale       the locale
     * @return resource converted to java.util.Properties
     */
    public PhetProperties getProperties( String resourceName, final Locale locale ) {
        try {
            Properties properties = loadProperties( getFallbackPropertiesResourceName( resourceName ) );
            Properties requestedLanguageProperties=loadProperties( getLocalizedPropertiesResourceName( resourceName, locale ) );
            properties.putAll( requestedLanguageProperties );//overwrite fallback with requested language
            return new PhetProperties( properties );
        }
        catch( IOException e ) {
            e.printStackTrace(  );
            return new PhetProperties( new Properties( ) );
        }
    }

    private Properties loadProperties( String fallbackResourceName ) throws IOException {
        Properties properties = new Properties();
        // First load the fallback resource

        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( fallbackResourceName );
        if ( inStream != null ) {
            properties.load( inStream );
        }
        return properties;
    }

    private String getLocalizedPropertiesResourceName( String resourceName, Locale locale ) {
        String basename = stripPropertiesSuffix( resourceName );
        return basename + "_" + locale.getLanguage() + PROPERTIES_SUFFIX;
    }
    
    private String getFallbackPropertiesResourceName( String resourceName ) {
        String basename = stripPropertiesSuffix( resourceName );
        return basename + PROPERTIES_SUFFIX;
    }
    
    private String stripPropertiesSuffix( String s ) {
        if ( s.endsWith( PROPERTIES_SUFFIX ) ) {
            s = s.substring( 0, s.length() - PROPERTIES_SUFFIX.length() );
        }
        return s;
    }

    /**
     * Gets an input stream for the specified resource.
     *
     * @param resource
     * @return InputStream
     */
    public InputStream getResourceAsStream( String resource ) throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource );
        if ( stream == null ) {
            throw new IOException( "invalid resource: " + resource );
        }
        return stream;
    }

    /**
     * Gets a byte array for the specified resource.
     *
     * @param resource
     * @return byte[]
     */
    public byte[] getResource( String resource ) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream stream = getResourceAsStream( resource );
        try {
            byte[] buffer = new byte[1000];
            int bytesRead;
            while ( ( bytesRead = stream.read( buffer ) ) >= 0 ) {
                out.write( buffer, 0, bytesRead );
            }
            out.flush();
        }
        catch( Exception e ) {
            try {
                stream.close();
            }
            catch( IOException e1 ) {
                e1.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
