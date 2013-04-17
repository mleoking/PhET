// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 8:34:20 PM
 */

public class WaveSampler {
    private WaveModel waveModel;
    private double distBetweenPoints;
    private double amplitudeScale;

    public WaveSampler( WaveModel waveModel, double amplitudeScale, double distBetweenPoints ) {
        this.waveModel = waveModel;
        this.amplitudeScale = amplitudeScale;
        this.distBetweenPoints = distBetweenPoints;
    }

    //todo is this expensive?
    public Point2D[] readValues( int crossSectionY ) {
        Lattice2D lattice2D = getLattice();
        ArrayList pts = new ArrayList();
        for ( int i = 0; i < lattice2D.getWidth(); i++ ) {
            float y = getY( i, crossSectionY );
            float x = getX( i );
            pts.add( new Point2D.Float( x, y ) );
        }
        return (Point2D[]) pts.toArray( new Point2D.Float[0] );
    }

    private Lattice2D getLattice() {
        return waveModel.getLattice();
    }

    protected float getX( int i ) {
        return (float) ( i * distBetweenPoints );
    }

    protected float getY( int index, int yValue ) {
        if ( index >= 1 && index < getLattice().getWidth() - 1 ) {
            double sum = getVerticalSlice( index, yValue ) + getVerticalSlice( index - 1, yValue ) + getVerticalSlice( index + 1, yValue );
            return (float) ( sum / 3 );
        }
        else {
            return (float) getVerticalSlice( index, yValue );
        }
    }

    private double getVerticalSlice( int index, int yValue ) {
        if ( yValue >= 1 && yValue < getLattice().getHeight() - 1 ) {
            double sum = getYORIG( index, yValue + 1 ) + getYORIG( index, yValue - 1 ) + getYORIG( index, yValue );
            return sum / 3;
        }
        else {
            return getYORIG( index, yValue );
        }

    }

    private float getYORIG( int i, int yValue ) {
        return (float) ( getLattice().getValue( i, yValue ) * amplitudeScale );
    }

    public void setDistanceBetweenCells( double distBetweenPoints ) {
        this.distBetweenPoints = distBetweenPoints;
    }
}
