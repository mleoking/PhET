/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:41:18 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class AtomLatticeControlPanel extends VerticalLayoutPanel {
    private ModelSlider spacing;
    private ModelSlider radius;
    private ModelSlider y0;
    private DGModel dgModel;

    /*
        •	 “Atom separation.”  Range should be from 0.4nm to 1.2nm, in increments of 0.1nm.  Default value should be 0.6nm  
        (note that at low resolution, 1 grid point = 0.1nm)
        •	 “Atom radius.”  Range should be from 0.05nm to 0.25nm, in increments of 0.05nm.  Default value should be 0.1nm.
     */
    public AtomLatticeControlPanel( final DGModel dgModel ) {
        this.dgModel = dgModel;
        final double scale = dgModel.getWavefunction().getWidth() / 10.0;
        spacing = new ModelSlider( "Atom Separation (D)", "nm", 0.4, 1.2, dgModel.getFractionalSpacing() * scale, new DecimalFormat( "0.0" ) );
        spacing.setModelTicks( new double[]{0.4, ( 1.2 + 0.4 ) / 2, 1.2} );
        spacing.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dgModel.setFractionalSpacing( spacing.getValue() / scale );
            }
        } );

        radius = new ModelSlider( "Atom Radius", "nm", 0.05, 0.25, dgModel.getFractionalRadius() * scale );
        radius.setModelTicks( new double[]{0.05, ( 0.5 + 0.25 ) / 2.0, 0.25} );
        radius.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dgModel.setFractionalRadius( radius.getValue() / scale );
            }
        } );

        y0 = new ModelSlider( "Vertical Position", "units", 0, 1.0, dgModel.getFractionalY0() );
        y0.setModelTicks( new double[]{0, 0.5, 1.0} );
        y0.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dgModel.setFractionalY0( y0.getValue() );
            }
        } );
        addFullWidth( spacing );
        addFullWidth( radius );
//        addFullWidth( y0 );
    }
}
