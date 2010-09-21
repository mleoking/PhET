/**
 * Class: Filter1D
 * Class: edu.colorado.phet.filter
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 4:51:43 PM
 */
package edu.colorado.phet.greenhouse.filter;

public abstract class Filter1D {

    public abstract boolean passes( double value );

    public boolean absorbs( double value ) {
        return !passes( value );
    }
}
