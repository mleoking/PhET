/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.ConstantPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.model.WavePacket;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 10, 2006
 * Time: 2:52:04 PM
 * Copyright (c) Mar 10, 2006 by Sam Reid
 */

public class TestPropagator {

    public static void main( String[] args ) {
//        final BasicPlotter basicPlotter = new BasicPlotter();
        final FastPlotter basicPlotter = new FastPlotter();
        basicPlotter.setVisible( true );

        final WavePacket wavePacket = new WavePacket();
        wavePacket.setTotalEnergy( new TotalEnergy( 0.8 ) );
        wavePacket.setPotentialEnergy( new ConstantPotential( 0 ) );
        wavePacket.setEnabled( true );
        wavePacket.update( null, null );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                LightweightComplex[] psi = wavePacket.getWaveFunctionValues();
                ArrayList data = new ArrayList();
                for( int j = 0; j < psi.length; j++ ) {
                    LightweightComplex lightweightComplex = psi[j];
                    data.add( new Point2D.Double( j, lightweightComplex.getReal() ) );
                }

                basicPlotter.setData( (Point2D.Double[])data.toArray( new Point2D.Double[0] ) );
                wavePacket.propagate();
            }
        } );
        wavePacket.update( null, null );
        int n = 1000;
        wavePacket.setDx( QTConstants.POSITION_RANGE.getLength() / n );
        timer.start();
    }
}
