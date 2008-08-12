/* Copyright 2007, University of Colorado */
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
        // Load the resource bundle into Properties
        Properties properties = new Properties();
        try {
            InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( getPropertiesResourceName( resourceName, locale ) );
            if ( inStream == null ) { //need to fall back for sim property files such as the version properties file
                inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( getPropertiesResourceName( resourceName, new Locale( "en" ) ) );
            }
            properties.load( inStream );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return new PhetProperties( properties );
    }

    private String getPropertiesResourceName( String resourceName, Locale locale ) {
        // strip off suffix, if it exists
        if ( resourceName.endsWith( PROPERTIES_SUFFIX ) ) {
            resourceName = resourceName.substring( 0, resourceName.length() - PROPERTIES_SUFFIX.length() );
        }

        //append locale if necessary
        //later we may change all files to have suffix, instead of suppressing it in English case
        if ( !locale.getLanguage().equals( "en" ) ) {
            resourceName += "_" + locale.getLanguage();
        }

        return resourceName + ".properties";
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
