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
public class JavaVersion {
    
    private static JavaVersion instance;
    
    private int majorNumber, minorNumber, maintenanceNumber, updateNumber;
    private String identifier;
    
    public static JavaVersion getInstance() {
        if ( instance == null ) {
            instance = new JavaVersion();
        }
        return instance;
    }

    /* singleton */
    private JavaVersion() {
        parse( getRuntimeVersion() );
    }
    
    private void parse( String s ) {
        
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
    }
    
    public String getVersion() {
        return System.getProperty( "java.version" );
    }
    
    public String getRuntimeVersion() {
        return System.getProperty( "java.runtime.version" );
    }
    
    public String getSpecificationVersion() {
        return System.getProperty( "java.specification.version" );
    }
    
    public String getVMVersion() {
        return System.getProperty( "java.vm.version" );
    }
    
    public String getVMSpecificationVersion() {
        return System.getProperty( "java.vm.specification.version" );
    }
    
    public int getMajorNumber() {
        return majorNumber;
    }
    
    public int getMinorNumber() {
        return minorNumber;
    }
    
    /**
     * Gets the maintenance number.
     * A value > 0 indicates a Maintenance release.
     * 
     * @return
     */
    public int getMaintenanceNumber() {
        return maintenanceNumber;
    }
   
    /**
     * Gets the update number. This indicates an update to a Feature or Maintenance release.
     * The higher the number, the more recent the update.
     * A value of 0 indicates that there was no update number.
     * 
     * @return
     */
    public int getUpdateNumber() {
        return updateNumber;
    }
    
    /**
     * Gets the version identifier. For a GA (FCS) release, this will be null.
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
     * 
     * format: n.n.0<-identifier>
     * 
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
     * 
     * format: n.n.n<-identifier>
     *
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
     * 
     * format: n.n.n_nn<-identifier>
     *
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
     * @return
     */
    public boolean isGARelease() {
        return ( identifier == null );
    }
    
    /**
     * Is this is First Customer Ship (FCS) release?
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
        JavaVersion v = JavaVersion.getInstance();
        System.out.println( "toString=" + v.toString() );
        System.out.println( "java.runtime.version=" + v.getRuntimeVersion() );
    }
}
