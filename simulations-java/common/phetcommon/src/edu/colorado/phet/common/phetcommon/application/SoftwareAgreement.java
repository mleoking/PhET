package edu.colorado.phet.common.phetcommon.application;

import java.io.IOException;
import java.io.InputStream;

import edu.colorado.phet.common.phetcommon.resources.PhetProperties;

/**
 * Encapsulation of the "Software Use Agreement".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoftwareAgreement {
    
    private static final String RESOURCE_NAME = "software-agreement.properties";
    private static final String KEY_VERSION = "version";
    private static final String KEY_CONTENT = "content";
    private static final int DEFAULT_VERSION = -1;
    
    // singleton instance
    private static SoftwareAgreement instance;
    
    private final int version;
    private final String content;
    
    // singleton
    private SoftwareAgreement() {
        PhetProperties p = readProperties();
        version = p.getInt( KEY_VERSION, DEFAULT_VERSION );
        content = p.getProperty( KEY_CONTENT );
    }
    
    public static SoftwareAgreement getInstance() {
        if ( instance == null ) {
            instance = new SoftwareAgreement();
        }
        return instance;
    }
    
    /**
     * Version number is a monotonically-increasing integer.
     * @return
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Content of the agreement is an HTML string.
     * @return
     */
    public String getContent() {
       return content;
    }
    
    // TODO: use a standard resource loader
    private PhetProperties readProperties() {
        PhetProperties p = new PhetProperties();
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( RESOURCE_NAME );
        if ( inStream != null ) {
            try {
                p.load( inStream );
            }
            catch ( IOException e ) {
                System.err.println( "exception reading software agreement: " + RESOURCE_NAME );
                e.printStackTrace();
            }
        }
        else {
            System.err.println( "missing software agreement: " + RESOURCE_NAME );
        }
        return p;
    }
}
