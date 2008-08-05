/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

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
     * @param resourceName
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
     * @param locale       the locale
     * @return resource converted to java.util.Properties
     */
    public PhetProperties getProperties( String resourceName, final Locale locale ) {

        String baseName = getResourceBundleBaseName( resourceName );

        // Get the resource bundle.
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle( baseName, locale );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            rb = null;
        }

        // Load the resource bundle into Properties
        Properties properties = new Properties();
        if ( rb != null ) {
            for ( Enumeration keys = rb.getKeys(); keys.hasMoreElements(); ) {
                final String key = (String) keys.nextElement();
                final String value = rb.getString( key );
                properties.put( key, value );
            }
        }

        return new PhetProperties( properties );
    }
    
    /*
     * Gets the base name for a resource bundle.
     */
    private static String getResourceBundleBaseName( String resourceName ) {

        if ( resourceName == null ) {
            throw new IllegalArgumentException( "resourceName is null" );
        }

        // operate on a copy, not the original resourceName !
        // SRR 8-5-2008 Strings are immutable, this workaround is unnecessary
        String baseName = new String( resourceName );

        if ( baseName.startsWith( "/" ) ) {
            // strip off leading '/'
            baseName = baseName.substring( 1 );
        }
        if ( baseName.endsWith( PROPERTIES_SUFFIX ) ) {
            // strip off suffix
            baseName = baseName.substring( 0, baseName.length() - PROPERTIES_SUFFIX.length() );
        }
        baseName = baseName.replace( '/', '.' );
        return baseName;
    }

    /**
     * Gets an input stream for the specified resource.
     *
     * @param resourceName
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
     * @param resourceName
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
