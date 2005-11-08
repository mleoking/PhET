/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view.charts;

import ptolemy.plot.Plot;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Sodium;
import edu.colorado.phet.solublesalts.model.Chloride;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.dm.SGTPoint;
import gov.noaa.pmel.sgt.dm.SimplePoint;
import gov.noaa.pmel.sgt.CartesianGraph;
import gov.noaa.pmel.util.Range;
import gov.noaa.pmel.util.Domain;
import gov.noaa.pmel.util.Range2D;

/**
 * Concentrations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConcentrationsSgt implements ModelElement {
    private SolubleSaltsModel model;
    private double time;
    private JDialog dlg;
    private JPlotLayout plot;
    private double[] tVals;
    private double[] naVals;
    private double[] clVals;
    private SimpleLine naData;
    private SimpleLine clData;
    private int numPts;

    public ConcentrationsSgt( Frame owner, SolubleSaltsModel model ) {
        model.addModelElement( this );
        this.model = model;

        plot = makeGraph();

        dlg = new JDialog( owner, "Concentrations" );
        dlg.getContentPane().add( plot );
        dlg.pack();
    }

    public void setVisible( boolean isVisible ) {
        dlg.setVisible( isVisible);
    }

    void makeCartesianGraph() {
        CartesianGraph cg = new CartesianGraph( );
//        cg.set
    }

    JPlotLayout makeGraph() {
        numPts = 300;
        tVals = new double[numPts];
        naVals = new double[numPts];
        clVals = new double[numPts];
        for( int i = 0; i < numPts; i++ ) {
            tVals[i] = i * 1;
            naVals[i] = 0;
            clVals[i] = 0;
        }

//        final SGTMetaData xMetaData = new SGTMetaData( "time", "ticks", false, false );
//        final SGTMetaData yMetaData = new SGTMetaData( "concentration", "???", false, false );

        final JPlotLayout layout = new JPlotLayout( false, false, false, "Test", null, false );
        layout.setBatch( false );
        layout.setTitles( "", "", "" );
        layout.setTitleHeightP( .3, .2 );

        try {
            layout.setRange( new Domain(new Range2D( 0, 100), new Range2D(0,100)));
        }
        catch( PropertyVetoException e ) {
            e.printStackTrace();
        }
        SGTMetaData xMetaData = new SGTMetaData( "time", "ticks", false, false );
        SGTMetaData yMetaData = new SGTMetaData( "concentration", "???", false, false );
        naData = new SimpleLine( tVals, naVals, "Na" );
        clData = new SimpleLine( tVals, clVals, "Cl" );
        naData.setXMetaData( xMetaData );
        naData.setYMetaData( yMetaData );
        clData.setXMetaData( xMetaData );
        clData.setYMetaData( yMetaData );



//        naData = new SimpleLine( tVals, naVals, "x-y test" );
//        clData = new SimpleLine( tVals, clVals, "x-y test" );
//        clData.setXMetaData( xMetaData );
//        clData.setYMetaData( yMetaData );
//        layout.clear();
//        layout.addData( clData, clData.getTitle() );

//        Timer timer = new Timer( 20, new ActionListener() {
//            private int phase = 0;
//
//            public void actionPerformed( ActionEvent e ) {
//                phase++;
//                for( int i = 0; i < numPts; i++ ) {
//                    double x = ( (double)( i + phase ) ) / lambda * Math.PI * 2;
//                    double y = Math.sin( x );
//                    tVals[i] = x;
//                    naVals[i] = y;
//                }
//                SimpleLine naData = new SimpleLine( tVals, naVals, "x-y test" );
//                naData.setXMetaData( xMetaData );
//                naData.setYMetaData( yMetaData );
//                layout.clear();
//                layout.addData( naData, naData.getTitle() );
//            }
//        } );
//        timer.setRepeats( true );
//        timer.start();

        return layout;
    }

    int tickCnt = -1;
    public void stepInTime( double dt ) {


        tickCnt = ( tickCnt + 1 ) % numPts;
        time += dt;
//        tVals[tickCnt] = time;
        naVals[tickCnt] = model.getIonConcentration( Sodium.class );
        clVals[tickCnt] = model.getIonConcentration( Chloride.class );

        SGTMetaData xMetaData = new SGTMetaData( "time", "ticks", false, false );
        SGTMetaData yMetaData = new SGTMetaData( "concentration", "???", false, false );
        naData = new SimpleLine( tVals, naVals, "Na" );
        clData = new SimpleLine( tVals, clVals, "Cl" );
        naData.setXMetaData( xMetaData );
        naData.setYMetaData( yMetaData );
        clData.setXMetaData( xMetaData );
        clData.setYMetaData( yMetaData );


        plot.clear();
        plot.addData( clData, clData.getTitle() );
    }
}
