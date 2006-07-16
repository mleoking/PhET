/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

    public AtomLatticeControlPanel( final DGModel dgModel ) {
        this.dgModel = dgModel;
        spacing = new ModelSlider( "Spacing", "units", 0, 1, dgModel.getFractionalSpacing() );
        spacing.setModelTicks( new double[]{0, 0.5, 1.0} );
        spacing.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dgModel.setFractionalSpacing( spacing.getValue() );
            }
        } );

        radius = new ModelSlider( "Radius", "units", 0, 0.1, dgModel.getFractionalRadius() );
        radius.setModelTicks( new double[]{0, 0.05, 0.1} );
        radius.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dgModel.setFractionalRadius( radius.getValue() );
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
