/**
 * Class: WaveMedium
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.microwaves.model.waves;

import edu.colorado.phet.common_microwaves.model.ModelElement;
import edu.colorado.phet.microwaves.coreadditions.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This class represents a medium through which an arbitrary number of
 * Wavefronts progagate.
 * <p/>
 * TODO: Make this 2D. It is currently pnly 1D
 */
public class WaveMedium extends ModelElement {

    private ArrayList wavefronts = new ArrayList();

    public WaveMedium() {
        // NOP
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wave wavefront = (Wave)wavefronts.get( i );
            wavefront.stepInTime( dt );
        }
        setChanged();
        notifyObservers();
    }

    public void addWavefront( Wave wavefront ) {
        this.wavefronts.add( wavefront );
    }

    public void removeWavefront( Wave wavefront ) {
        this.wavefronts.remove( wavefront );
    }

    /**
     * Returns the length of the longest wavefront in the medium
     *
     * @return
     */
    public int getMaxX() {
        int maxX = Integer.MAX_VALUE;
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wave wavefront = (Wave)wavefronts.get( i );
            maxX = Math.min( maxX, wavefront.getAmplitude().length );
        }
        return maxX;
    }

    /**
     * Returns the combined amplitude of all wavefronts in the medium at
     * a specified point in the medium
     *
     * @param x
     * @return
     */
    public float getAmplitudeAt( float x, float y ) {
        float amplitude = 0;
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wave wavefront = (Wave)wavefronts.get( i );
            if( wavefront.isEnabled() ) {
                amplitude += wavefront.getAmplitude()[(int)x];
            }
        }
        amplitude /= wavefronts.size();
        return amplitude;
    }

    private Vector2D tempVector2D = new Vector2D();

    public Vector2D getFieldAtLocation( Point2D.Double latticePtLocation ) {
        tempVector2D.setX( 0 );
        tempVector2D.setY( 0 );
        float amplitude = 0;
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wave wave = (Wave)wavefronts.get( i );
            amplitude += wave.getAmplitude()[(int)latticePtLocation.getX()];
        }
        tempVector2D.setY( amplitude );
        return tempVector2D;
    }

    public void clear() {
        this.wavefronts.clear();
    }
}
