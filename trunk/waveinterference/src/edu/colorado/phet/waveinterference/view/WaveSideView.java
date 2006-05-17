/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 5:09:23 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class WaveSideView extends AbstractWaveSideView {
    private PPath path;
    private Lattice2D lattice2D;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private double distBetweenPoints = 5;
//    private double amplitudeScale = -150;
//    private double amplitudeScale = -150/2.0;
    private double amplitudeScale = -150 / 1.8;
    private WaveSampler waveSampler;

    public WaveSideView( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.lattice2D = waveModel.getLattice();
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        path = new PPath();
        addChild( path );

        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
        this.waveSampler = new WaveSampler( waveModel, -150 / 1.8, 5 );
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
        setSpaceBetweenCells( latticeScreenCoordinates.getCellWidth() );
        //todo this is disabled because the RotationGraphic is managing the layout
        //todo we should provide support for both.
        path.setPathTo( getWavePath() );
    }

    protected GeneralPath getWavePath() {
        GeneralPath generalpath = new GeneralPath();
        Point2D[] samples = readValues();
        if( samples.length > 0 ) {
            generalpath.moveTo( (float)samples[0].getX(), (float)samples[0].getY() );
            for( int i = 1; i < samples.length; i++ ) {
                generalpath.lineTo( (float)samples[i].getX(), (float)samples[i].getY() );
            }
        }
        return generalpath;
    }

    protected Point2D[] readValues() {
        return waveSampler.readValues();
    }


    public double getDistBetweenCells() {
        return distBetweenPoints;
    }

    public void setSpaceBetweenCells( double dim ) {
        this.distBetweenPoints = dim;
        waveSampler.setDistanceBetweenCells( distBetweenPoints );
    }

    public void setStrokeColor( Color color ) {
        path.setStrokePaint( color );
    }

    public int getYValue() {
        return waveSampler.getYValue();
    }
}
