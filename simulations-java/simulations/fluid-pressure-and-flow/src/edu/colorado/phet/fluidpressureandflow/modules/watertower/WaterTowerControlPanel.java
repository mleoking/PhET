// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureControlPanel;
import edu.colorado.phet.fluidpressureandflow.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.view.UnitsControlPanel;

/**
 * @author Sam Reid
 */
public class WaterTowerControlPanel<T extends FluidPressureAndFlowModel> extends VerticalLayoutPanel {

    public WaterTowerControlPanel( final FluidPressureAndFlowModule<T> module ) {
        super();

        addControlFullWidth( new CheckBox( "Ruler", module.rulerVisible ) );
        addControlFullWidth( new CheckBox( "Measuring Tape", module.rulerVisible ) );
        addControlFullWidth( new UnitsControlPanel<T>( module ) );
        setBackground( FluidPressureControlPanel.BACKGROUND );
        addControlFullWidth( (JComponent) Box.createRigidArea( new Dimension( 40, 40 ) ) );
        addControlFullWidth( new JSeparator() );
        addControlFullWidth( new CheckBox( "Hose", module.hoseVisible ) );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
