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

public class DGControlPanel extends VerticalLayoutPanel {
    private ModelSlider spacing;
    private ModelSlider radius;
    private DGModel dgModel;

    public DGControlPanel( final DGModel dgModel ) {
        this.dgModel = dgModel;
        spacing = new ModelSlider( "Spacing", "units", 0, 1, dgModel.getFractionalSpacing() );
        spacing.setModelTicks( new double[]{0, 0.5, 1.0} );
        spacing.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dgModel.setFractionalSpacing( spacing.getValue() );
            }
        } );

        radius = new ModelSlider( "Radius", "units", 0, 0.5, dgModel.getFractionalRadius() );
        radius.setModelTicks( new double[]{0, 0.25, 0.5} );
        radius.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dgModel.setFractionalRadius( radius.getValue() );
            }
        } );
        addFullWidth( spacing );
        addFullWidth( radius );
    }
}
