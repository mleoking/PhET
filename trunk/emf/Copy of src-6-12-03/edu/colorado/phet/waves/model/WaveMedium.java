/**
 * Class: WaveMedium
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.waves.model;

import edu.colorado.phet.common.model.ModelElement;

import java.util.ArrayList;

/**
 * This class represents a medium through which an arbitrary number of
 * Wavefronts progagate.
 * <p>
 * TODO: Make this 2D. It is currently pnly 1D
 */
public class WaveMedium extends ModelElement {

    private ArrayList wavefronts = new ArrayList();

    public WaveMedium() {
        // NOP
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wavefront wavefront = (Wavefront)wavefronts.get( i );
            wavefront.stepInTime( dt );
        }
        setChanged();
        notifyObservers();
    }

    /**
     *
     * @param observer
     */
//    public WaveMedium( Observer observer ) {
//        super( observer );
//    }

    public void addWavefront( Wavefront wavefront ) {
        this.wavefronts.add( wavefront );
//        super.addModelElement( wavefront );
    }

    public void removeWavefront( Wavefront wavefront ) {
        this.wavefronts.remove( wavefront );
//        super.removeModelElement( wavefront );
    }

    /**
     * Returns the length of the longest wavefront in the medium
     * @return
     */
    public int getMaxX() {
        int maxX = Integer.MAX_VALUE;
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wavefront wavefront = (Wavefront)wavefronts.get( i );
            maxX = Math.min( maxX, wavefront.getAmplitude().length );
        }
        return maxX;
    }

    /**
     * Returns the combined amplitude of all wavefronts in the medium at
     * a specified point in the medium
     * @param x
     * @return
     */
    public float getAmplitudeAt( float x, float y ) {
        float amplitude = 0;
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wavefront wavefront = (Wavefront)wavefronts.get( i );
            if( wavefront.isEnabled() ) {
                amplitude += wavefront.getAmplitude()[ (int)x ];
            }
        }
        amplitude /= wavefronts.size();
        return amplitude;
    }
}
