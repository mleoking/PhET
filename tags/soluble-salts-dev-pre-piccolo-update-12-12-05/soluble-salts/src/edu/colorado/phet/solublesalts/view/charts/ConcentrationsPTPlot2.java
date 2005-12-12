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
import edu.colorado.phet.solublesalts.model.ion.Sodium;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Sodium;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.awt.*;

/**
 * Concentrations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConcentrationsPTPlot2 implements ModelElement {
    private SolubleSaltsModel model;
    private Plot plot;
    private double time;
    private JDialog dlg;

    private int numPts = 300;
    private double[] tData = new double[numPts];
    private double[] naData = new double[numPts];
    private double[] clData = new double[numPts];

    public ConcentrationsPTPlot2( Frame owner, SolubleSaltsModel model ) {
        model.addModelElement( this );
        this.model = model;

        plot = new Plot();
        plot.setBounds( 0, 0, 200, 100 );
        plot.setXRange( 0, 100 );
        plot.setYRange( 0, 50 );
        plot.setColors( new Color[]{Color.green, Color.blue} );
        plot.setPointsPersistence( 0 );
        plot.setWrap( true );

        dlg = new JDialog( owner, "Concentrations" );
        dlg.getContentPane().add( plot );
        dlg.pack();
    }

    public void setVisible( boolean isVisible ) {
        dlg.setVisible( isVisible);
    }

    int eraseIdx;
    int dataCnt;
    public void stepInTime( double dt ) {
        time += dt;
        double concNa = model.getIonConcentration( Sodium.class );
        double concCl = model.getIonConcentration( Chlorine.class );

        dataCnt = Math.min( dataCnt + 1, numPts);
        if( dataCnt < numPts ) {
            tData[dataCnt-1] = time;
            naData[dataCnt-1] = concNa;
            clData[dataCnt-1] = concCl;
        }
        else {
            for( int i = 0; i < numPts - 1; i++ ) {
                tData[i] = tData[i+1];
                naData[i] = naData[i+1];
                clData[i] = clData[i+1];
            }
            tData[numPts-1] = tData[numPts - 2];
            naData[numPts-1] = concNa;
            clData[numPts-1] = concCl;
        }

        plot.clear( 0 );
        plot.clear( 1 );
        plot.clear(true);
        for( int i = 0; i < dataCnt; i++ ) {
            plot.addPoint( 0, tData[i], naData[i], true );
            plot.addPoint( 1, tData[i], clData[i], true );
        }
    }
}
