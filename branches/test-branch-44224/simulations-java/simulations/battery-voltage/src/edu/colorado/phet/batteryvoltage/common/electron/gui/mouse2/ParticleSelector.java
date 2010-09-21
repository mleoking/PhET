package edu.colorado.phet.batteryvoltage.common.electron.gui.mouse2;

import java.awt.*;
import java.util.Arrays;
import java.util.Vector;

import edu.colorado.phet.batteryvoltage.common.electron.paint.particle.ParticlePainterAdapter;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;

public class ParticleSelector {
    Vector v = new Vector();

    public ParticleSelector() {
    }

    public void addAll( ParticlePainterAdapter[] pp ) {
        v.addAll( Arrays.asList( pp ) );
    }

    public Particle selectClosestTo( Point p ) {
        DoublePoint px = new DoublePoint( p.x, p.y );
        Particle best = null;
        double bestDist = Double.MAX_VALUE;
        for ( int i = 0; i < v.size(); i++ ) {
            ParticlePainterAdapter painter = (ParticlePainterAdapter) v.get( i );
            Particle party = painter.getParticle();
            if ( party.getPosition().distance( px ) < bestDist ) {
                bestDist = party.getPosition().distance( px );
                best = party;
            }
        }
        return best;
    }

    /**
     * Grab the topmost edu.colorado.phet.electron under the mouse, if any.
     */
    public Particle selectAt( Point pt ) {
        for ( int i = 0; i < v.size(); i++ ) {
            ParticlePainterAdapter painter = (ParticlePainterAdapter) v.get( i );
            if ( painter.contains( pt ) ) {
                return painter.getParticle();
            }
        }
        return null;
    }
}




