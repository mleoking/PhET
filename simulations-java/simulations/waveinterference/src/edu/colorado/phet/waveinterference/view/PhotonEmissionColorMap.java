/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.Resettable;
import edu.colorado.phet.waveinterference.model.WaveModel;

import java.awt.*;

/**
 * Colors the wave area black if the wave hasn't propagated there yet.
 */
public class PhotonEmissionColorMap implements ColorMap, Resettable {
    private WaveModel lattice;
    private boolean[][] inited;
    private Color color;
    private BasicColorMap basicColorMap;

    public PhotonEmissionColorMap( final WaveModel waveModel, Color color ) {
        this.color = color;
        this.lattice = waveModel;
        inited = new boolean[waveModel.getWidth()][waveModel.getHeight()];
        waveModel.addListener( 0, new WaveModel.Listener() {//todo fix this workaround: has to get in front of WaveModelGraphic for notifications.

            public void sizeChanged() {
                System.out.println( "PhotonEmissionColorMap.sizeChanged" );
                inited = new boolean[waveModel.getWidth()][waveModel.getHeight()];
                debug();
            }
        } );
        this.basicColorMap = new BasicColorMap( waveModel.getLattice(), color );
    }

    private void debug() {
        System.out.println( "lattice.getWidth() = " + lattice.getWidth() + ", h=" + lattice.getHeight() );
        System.out.println( "inited.length = " + inited.length + ", inited[0].length=" + inited[0].length );
    }

    public Color getColor( int i, int k ) {
        //todo ensure wavefunction is the correct size (could have been resized).
        float value = lattice.getLattice().getValue( i, k );
        float epsilon = 0.025f;
        if( Math.abs( value ) < epsilon && !inited[i][k] ) {
            return Color.black;
        }
        else {
            inited[i][k] = true;
            return basicColorMap.getColor( i, k );
        }
    }

    public Color getRootColor() {
        return color;
    }

    public int getWidth() {
        return inited.length;
    }

    public int getHeight() {
        return inited[0].length;
    }

    public void setDark( int i, int k ) {
        inited[i][k] = false;
    }

    public void reset() {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int k = 0; k < getHeight(); k++ ) {
                setDark( i, k );
            }
        }
    }
}
