/**
 * Class: WaveMedium
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

public class WaveMedium extends SimpleObservable implements ModelElement {

    private ArrayList wavefronts = new ArrayList();
    private AttenuationFunction attenuationFunction = new AttenuationFunction() {
        public double getAttenuation( double x, double y ) {
            return 1;
        }
    };


    public void stepInTime( double dt ) {
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wavefront wavefront = (Wavefront)wavefronts.get( i );
            wavefront.stepInTime( dt, attenuationFunction );
        }
        notifyObservers();
    }

    /**
     * Returns the length of the longest wavefront in the medium
     *
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

    public void addWavefront( Wavefront wavefront ) {
        wavefronts.add( wavefront );
    }

    public void removeWavefront( Wavefront wavefront ) {
        wavefronts.remove( wavefront );
    }

    public ArrayList getWavefronts() {
        return wavefronts;
    }

    /**
     * Returns the combined amplitude of all wavefronts in the medium at
     * a specified point in the medium
     *
     * @param x
     * @return
     */
    public double getAmplitudeAt( double x ) {
        double amplitude = 0;
        int wavefrontCount = 0;
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wavefront wavefront = (Wavefront)wavefronts.get( i );
            if( wavefront.isEnabled() ) {
                wavefrontCount++;
                amplitude += wavefront.getAmplitude()[(int)x];
            }
        }
        amplitude /= wavefrontCount;
        return amplitude;
    }

    /**
     * Returns signal attentuation per unit distance. A value of 1 indicates
     * no attenuation.
     *
     * @param point
     * @return
     */
    public double getAttentuation( Point2D.Double point ) {
        return getAttentuation( point.x, point.y );
    }

    public double getAttentuation( double x, double y ) {
        return 1;
    }

    public void setAttenuationFunction( AttenuationFunction attenuationFunction ) {
        this.attenuationFunction = attenuationFunction;
    }

    public void clear() {

    }
}
