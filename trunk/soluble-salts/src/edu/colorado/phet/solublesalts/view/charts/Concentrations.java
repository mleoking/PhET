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

/**
 * Concentrations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Concentrations implements ModelElement {
    private SolubleSaltsModel model;
    private Plot plot;
    private double time;
    private JDialog dlg;

    public Concentrations( Frame owner, SolubleSaltsModel model ) {
        model.addModelElement( this );
        this.model = model;

        plot = new Plot();
        plot.setBounds( 0, 0, 300, 200 );
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
    public void stepInTime( double dt ) {
        time += dt;
        double concNa = model.getIonConcentration( Sodium.class );
        double concCl = model.getIonConcentration( Chloride.class );

        plot.addPoint( 0, time, concNa, true );
        plot.addPoint( 1, time, concCl, true );
    }
}
