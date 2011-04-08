// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

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
     *
     * Unlike Java's ResourceBundle, this implementation requires an exact match on Locale
     * in order to find the resource.
     * </pre>
     *
     * @param resourceName classpath resource name [may not be null]
     * @param locale       the locale
     * @return resource converted to java.util.Properties
     */
    public PhetProperties getProperties( String resourceName, final Locale locale ) {
        try {
            Properties properties = loadProperties( getFallbackPropertiesResourceName( resourceName ) );
            Properties requestedLanguageProperties = loadProperties( getLocalizedPropertiesResourceName( resourceName, locale ) );
            properties.putAll( requestedLanguageProperties ); //overwrite fallback with requested language
            return new PhetProperties( properties );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            return new PhetProperties( new Properties() );
        }
    }

    private Properties loadProperties( String resourceName ) throws IOException {
        Properties properties = new Properties();
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resourceName );
        if ( inStream != null ) {
            properties.load( inStream );
        }
        return properties;
    }

    private String getLocalizedPropertiesResourceName( String resourceName, Locale locale ) {
        return stripPropertiesSuffix( resourceName ) + LocaleUtils.getTranslationFileSuffix( locale ) + PROPERTIES_SUFFIX;
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
     * Returns the contents of a resource as a String.
     *
     * @param resourceName
     * @return String
     */
    public String getResourceAsString( String resourceName ) throws IOException {
        InputStream in = getResourceAsStream( resourceName );
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for ( int n; ( n = in.read( b ) ) != -1; ) {
            out.append( new String( b, 0, n ) );
        }
        return out.toString();
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
        catch ( Exception e ) {
            try {
                stream.close();
            }
            catch ( IOException e1 ) {
                e1.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
