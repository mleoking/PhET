/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 5:09:23 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class WaveSideView extends PNode {
    private PPath path;
    private Lattice2D lattice2D;
    private int distBetweenPoints = 5;
    private double amplitudeScale = -150;

    public WaveSideView( Lattice2D lattice2D ) {
        this.lattice2D = lattice2D;
        path = new PPath();
        addChild( path );
        update();
    }

    protected PPath getPath() {
        return path;
    }

    public Lattice2D getLattice2D() {
        return lattice2D;
    }

    public void setStroke( Stroke stroke ) {
        path.setStroke( stroke );
    }

    public void setStrokePaint( Paint paint ) {
        path.setStrokePaint( paint );
    }

    public void update() {
        GeneralPath generalpath = getWavePath();
        path.setPathTo( generalpath );
    }

    protected GeneralPath getWavePath() {
        GeneralPath generalpath = new GeneralPath();
        int yValue = getYValue();
        for( int i = 0; i < lattice2D.getWidth(); i++ ) {
            float y = getY( i, yValue );
            int x = getX( i );
            if( i == 0 ) {
                generalpath.moveTo( x, y );
            }
            else {
                generalpath.lineTo( x, y );
            }
        }
        return generalpath;
    }

    protected int getYValue() {
        int yValue = lattice2D.getHeight() / 2;
        return yValue;
    }

    protected int getX( int i ) {
        return i * distBetweenPoints;
    }

    float getY( int index, int yValue ) {
        if( index >= 1 && index < lattice2D.getWidth() - 1 ) {
            double sum = getVerticalSlice( index, yValue ) + getVerticalSlice( index - 1, yValue ) + getVerticalSlice( index + 1, yValue );
            return (float)( sum / 3 );
        }
        else {
            return (float)getVerticalSlice( index, yValue );
        }
    }

    private double getVerticalSlice( int index, int yValue ) {
        double sum = getYORIG( index, yValue + 1 ) + getYORIG( index, yValue - 1 ) + getYORIG( index, yValue );
        return sum / 3;
    }

    private float getYORIG( int i, int yValue ) {
        float y = (float)( lattice2D.getValue( i, yValue ) * amplitudeScale );
        return y;
    }

    public double getDistBetweenCells() {
        return distBetweenPoints;
    }

    public void setSpaceBetweenCells( int dim ) {
        this.distBetweenPoints = dim;
    }
}
