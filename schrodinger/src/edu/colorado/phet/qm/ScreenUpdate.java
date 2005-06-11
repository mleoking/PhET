/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 7:02:29 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class ScreenUpdate implements DiscreteModel.Listener {
//    Complex[][] wavefunction;
    public ColorGrid colorGrid;
    public ImageDebugFrame frame;
    private DiscreteModel discreteModel;
    private int numIterationsBetwenScreenUpdate = 1;
    public ColorMap colorMap;

    public ScreenUpdate( final DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;

        colorGrid = new ColorGrid( 600, 600, discreteModel.getXMesh(), discreteModel.getYMesh() );
        colorMap = new ColorMap() {
            public Paint getPaint( int i, int k ) {
                Complex[][] wavefunction = discreteModel.getWavefunction();
                double h = Math.abs( wavefunction[i][k].getReal() );
                double s = Math.abs( wavefunction[i][k].getImaginary() );
//                double h = 0.7;
//                double s = 0.5;
                double b = 1 - wavefunction[i][k].abs();
                Color color = new Color( Color.HSBtoRGB( (float)h, (float)s, (float)b ) );
                double potval = discreteModel.getPotential().getPotential( i, k, 0 );
                if( potval > 0 ) {
                    color = new Color( 100, color.getGreen(), color.getBlue() );
                }
//                if( barrierRect.contains( i, k ) ) {
//                    color = new Color( color.getRed(), color.getGreen(), 30 );
//                }
                return color;
            }
        };
        colorGrid.colorize( colorMap );

        frame = new ImageDebugFrame( colorGrid.getBufferedImage() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

    public void finishedTimeStep( DiscreteModel model ) {
        if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 ) {
            colorGrid.colorize( colorMap );
            frame.setImage( colorGrid.getBufferedImage() );
        }
    }
}
