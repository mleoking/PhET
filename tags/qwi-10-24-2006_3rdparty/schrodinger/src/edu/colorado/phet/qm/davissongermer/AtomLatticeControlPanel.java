/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:41:18 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class AtomLatticeControlPanel extends VerticalLayoutPanel {
    private JComponent spacing;
    private JComponent radius;
    private DGModel dgModel;

    /*
        •	 “Atom separation.”  Range should be from 0.4nm to 1.2nm, in increments of 0.1nm.  Default value should be 0.6nm  
        (note that at low resolution, 1 grid point = 0.1nm)
        •	 “Atom radius.”  Range should be from 0.05nm to 0.25nm, in increments of 0.05nm.  Default value should be 0.1nm.
     */
    public AtomLatticeControlPanel( final DGModel dgModel ) {
        this.dgModel = dgModel;
        final double scale = dgModel.getWavefunction().getWidth() / 10.0;

        //        spacing = new SpacingModelSlider( dgModel, scale );
        spacing = new SpacingControl( dgModel );
//        radius = new RadiusModelSlider( dgModel, scale );
        radius = new RadiusControl( dgModel );

//        y0 = new ModelSlider( "Vertical Position", "units", 0, 1.0, dgModel.getFractionalY0() );
//        y0.setModelTicks( new double[]{0, 0.5, 1.0} );
//        y0.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                dgModel.setFractionalY0( y0.getValue() );
//            }
//        } );
        addFullWidth( spacing );
        addFullWidth( radius );
//        addFullWidth( y0 );
    }
}
