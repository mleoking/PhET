/**
 * Class: WaveMedium
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.model.ModelElement;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class WaveMedium extends SimpleObservable implements ModelElement {

    private ArrayList wavefronts = new ArrayList();

    public WaveMedium() {
        System.out.println( "" );
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wavefront wavefront = (Wavefront)wavefronts.get( i );
            wavefront.stepInTime( dt );
        }
        notifyObservers();
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
               amplitude += wavefront.getAmplitude()[ (int)x ];
            }
        }
        amplitude /= wavefrontCount;
        return amplitude;
    }
}
