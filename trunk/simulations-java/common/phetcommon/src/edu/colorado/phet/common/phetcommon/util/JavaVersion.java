/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.text.DecimalFormat;

/**
 * JavaVersion encapsulates information about the runtime Java version.
 * Uses the singleton pattern.
 *
 * For documentation on the version numbering format and release types, see
 * see http://java.sun.com/j2se/versioning_naming.html
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class JavaVersion {
    
    private String version;
    private int majorNumber, minorNumber, maintenanceNumber, updateNumber;
    private String identifier;
    
    /**
     * JREVersion is the version information for the JRE (Java Runtime Environment).
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     */
    public static class JREVersion extends JavaVersion {
        public JREVersion() {
            super( System.getProperty( "java.runtime.version" ) );
        }
    }
    
    /**
     * JVMVersion is the version information for the JVM (Java Virtual Machine).
     * The JVM is included as part of the JRE, and may have a different version
     * name than the JRE.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     */
    public static class JVMVersion extends JavaVersion {
        public JVMVersion() {
            super( System.getProperty( "java.vm.version" ) );
        }
    }
    
    private JavaVersion( String s ) {
        
        version = s;
        
        majorNumber = 0;
        minorNumber = 0;
        maintenanceNumber = 0;
        updateNumber = 0;
        identifier = null;
        
        String majorString = null;
        String minorString = null;
        String maintenanceString = null;
        String updateString = "0";
        
        int iPrev = 0;
        int i = 0;
            
        i = s.indexOf( '.' );
        majorString = s.substring( iPrev, i );

        iPrev = i + 1;
        i = s.indexOf( '.', iPrev );
        minorString = s.substring( iPrev, i );
        
        iPrev = i + 1;
        i = s.indexOf( '_', iPrev );
        if ( i != -1 ) {
            // n.n.n_nn
           maintenanceString = s.substring( iPrev, i );
           
           iPrev = i + 1;
           i = s.indexOf( '-', iPrev );
           if ( i != -1 ) {
               // n.n.n_nn-identifier
               updateString = s.substring( iPrev, i );
               iPrev = i + 1;
               identifier = s.substring( iPrev );
           }
           else {
                // n.n.n_nn
               updateString = s.substring( iPrev );
           }
        }
        else {
           // n.n.n
           i = s.indexOf( '-', iPrev );
           if ( i != -1 ) {
               // n.n.n-identifier
               maintenanceString = s.substring( iPrev, i );
               iPrev = i + 1;
               identifier = s.substring( iPrev );
           }
           else {
               // n.n.n
               maintenanceString = s.substring( iPrev );
           }
        }
        
        try {
            majorNumber = Integer.valueOf( majorString ).intValue();
            minorNumber = Integer.valueOf( minorString ).intValue();
            maintenanceNumber = Integer.valueOf( maintenanceString ).intValue();
            updateNumber = Integer.valueOf( updateString ).intValue();
        }
        catch ( NumberFormatException e ) {
            e.printStackTrace();
        }
        
        // if parsing worked correctly, the individual components can be reassembled into the original string
        assert( version.equals( toString() ) );
    }
    
    /**
     * Gets the version name.
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the major number for the release.
     * For example, 1 in 1.6.0.
     * @return
     */
    public int getMajorNumber() {
        return majorNumber;
    }
    
    /**
     * Gets the minor number for the release.
     * For example, 6 in 1.6.0.
     * @return
     */
    public int getMinorNumber() {
        return minorNumber;
    }
    
    /**
     * Gets the maintenance number.
     * For example, 2 in 1.3.2.
     * A value > 0 indicates a Maintenance release.
     * 
     * @return
     */
    public int getMaintenanceNumber() {
        return maintenanceNumber;
    }
   
    /**
     * Gets the update number. 
     * For example, 16 in 1.5.0_16.
     * 
     * This indicates an update to a Feature or Maintenance release.
     * The higher the number, the more recent the update.
     * <p>
     * The specification indicates that update numbers work like this example:
     *  1.3.0 < 1.3.0_01 < 1.3.1 < 1.3.1_01
     *  <p>
     * This seems to imply that the update number will never be 00.
     * So we'll use value of 0 to indicate that there was no update number.
     * 
     * @return
     */
    public int getUpdateNumber() {
        return updateNumber;
    }
    
    /**
     * Gets the build identifier. 
     * For example, "ea" in 1.5.0-ea.
     * 
     * For a GA (FCS) release, this will be null.
     * The identifier is often used to represent a particular milestone, for example:
     * ea (early access)
     * beta
     * rc1 (release candidate 1)
     * 
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Is this a Feature release?
     * A feature release has the format: n.n.0<-identifier>
     * The final digit is always a 0.
     * The -identifier is required for any non-GA (non-FCS) release.
     * A GA (FCS) release never has a -identifier.
     *
     * @return
     */
    public boolean isFeatureRelease() {
        return ( maintenanceNumber == 0 );
    }
    
    /**
     * Is this a Maintenance release?
     * A maintenance release has the format: n.n.n<-identifier>
     * The final digit is never a 0.
     * The -identifier is required for any non-GA (non-FCS) release.
     * A GA (FCS) release never has a -identifier.
     *
     * @return
     */
    public boolean isMaintenanceRelease() {
        return ( maintenanceNumber != 0 );
    }
    
    /**
     * Is this an Update release?
     * An update release has the format: n.n.n_nn<-identifier>
     * The first three digits are identical to those of the feature or maintenance release that is being updated.
     * The two digits following the underbar indicate the update number. The higher the number, the more recent the update.
     * The -identifier is required for any non-GA (non-FCS) release.
     * A GA (FCS) release never has a -identifier.
     *
     * @return
     */
    public boolean isUpdateRelease() {
        return ( updateNumber != 0 );
    }
    
    /**
     * Is this a General Availability (GA) release?
     * A GA release has no build identifier.
     * @return
     */
    public boolean isGARelease() {
        return ( identifier == null );
    }
    
    /**
     * Is this is First Customer Ship (FCS) release?
     * A FCS release is another common name for a GA release.
     * @return
     */
    public boolean isFCSRelease() {
        return isGARelease();
    }
    
    public String toString() {
        String s = majorNumber + "." + minorNumber + "." + maintenanceNumber;
        if ( updateNumber != 0 ) {
            s += "_" + new DecimalFormat( "00" ).format( updateNumber );
        }
        if ( identifier != null ) {
            s += "-" + identifier;
        }
        return s;
    }
    
    // test
    public static void main( String[] args ) {
        
        JREVersion jre = new JREVersion();
        System.out.println( "JREVersion " + jre.getVersion() + " -> " + jre.toString() );
        
        JVMVersion jvm = new JVMVersion();
        System.out.println( "JVMVersion " + jvm.getVersion() + " -> " + jvm.toString() );

        // parser tests in base class
        String[] tests = { "1.3.0", "1.3.0_01", "1.3.0-b24", "1.3.1-beta-b09", "1.3.1_05-ea-b01", "1.4.0_03-ea-b01" };
        for ( int i = 0; i < tests.length; i++ ) {
            JavaVersion jtest = new JavaVersion( tests[i] ) {};
            System.out.println( "parser " + tests[i] + " -> " + jtest.toString() );
        }
    }
}
