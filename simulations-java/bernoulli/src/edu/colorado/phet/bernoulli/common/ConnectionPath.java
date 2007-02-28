/**
 * Class: ConnectionPath
 * Package: edu.colorado.phet.bernoulli.common
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli.common;

import java.util.ArrayList;

public class ConnectionPath {
    ArrayList list = new ArrayList();

    void addPoint( double x, double y ) {
        list.add( new ConnectionPoint( x, y ) );
    }

}
