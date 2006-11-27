/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * ParticleCounter
 * <p>
 * A model element that counts the number of particles in a region of the model.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ParticleCounter extends SimpleObservable implements ModelElement {
    // The region within which to count particles
    private Rectangle2D region;
    private int cnt;
    private IdealGasModel model;

    public ParticleCounter( IdealGasModel model ) {
        this.model = model;
    }

    public void stepInTime( double dt ) {
        cnt = 0;
        List bodies = model.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Object o = bodies.get( i );
            if( o instanceof GasMolecule ) {
                GasMolecule molecule = (GasMolecule)o;
                if( region.contains( molecule.getPosition() ) ) {
                    cnt++;
                }
            }
        }
        notifyObservers();
    }

    public void setRegion( Rectangle2D region ) {
        this.region = region;
    }

    public int getCnt() {
        return cnt;
    }
}
