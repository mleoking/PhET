/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Sample
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Sample extends Particle {

    // A map that has lists of dipoles according to their x locations
    private HashMap dipolesByXLoc = new HashMap();

    public abstract Rectangle2D getBounds();

    public List getDipolesForXLoc( double xLoc ) {
        Integer key = new Integer( (int)xLoc );
        return (List)dipolesByXLoc.get( key );
    }

    public void addDipole( Dipole dipole ) {
        Integer xLoc = new Integer( (int)dipole.getPosition().getX() );
        List dipoleList = (List)dipolesByXLoc.get( xLoc );
        if( dipoleList == null ) {
            dipoleList = new ArrayList();
            dipolesByXLoc.put( xLoc, dipoleList );
        }
        dipoleList.add( dipole );
    }

    public void removeDipole( Dipole dipole ) {
        Collection xLocs = dipolesByXLoc.keySet();
        for( Iterator iterator = xLocs.iterator(); iterator.hasNext(); ) {
            Object key = iterator.next();
            List dipoleList = (List)dipolesByXLoc.get( key );
            if( dipoleList.contains( dipole ) ) {
                dipoleList.remove( dipole );
            }
        }
    }
}
