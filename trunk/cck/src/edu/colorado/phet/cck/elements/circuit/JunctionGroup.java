/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.circuit;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 5:03:30 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class JunctionGroup {
    Set set = new HashSet();
//    ArrayList junctions=new ArrayList();
    public JunctionGroup( Junction junction ) {
        set.add( junction );
        set.addAll( Arrays.asList( junction.getConnections() ) );
    }

    public void setSelected( boolean selected ) {
        Junction[] j2 = getJunctions();
        for( int i = 0; i < j2.length; i++ ) {
            Junction junction2 = j2[i];
            junction2.setSelected( selected );
        }
    }

    public boolean equals( Object obj ) {
        if( !( obj instanceof JunctionGroup ) ) {
            return false;
        }
        JunctionGroup jg = (JunctionGroup)obj;
        return jg.set.equals( set );
    }

    public boolean contains( Junction junction ) {
        return set.contains( junction );
    }

    public int hashCode() {
        return set.size();
    }

    public Junction[] getJunctions() {
        return (Junction[])set.toArray( new Junction[0] );
    }

    public Point2D.Double getLocation() {
        Junction j2 = getJunctions()[0];
        return j2.getLocation();
    }
}
