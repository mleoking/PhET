// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.view.UnitsControlPanel;

import static edu.colorado.phet.fluidpressureandflow.FPAFStrings.*;

/**
 * @author Sam Reid
 */
public class WaterTowerControlPanel<T extends FluidPressureAndFlowModel> extends VerticalLayoutPanel {

    public WaterTowerControlPanel( final FluidPressureAndFlowModule<T> module ) {
        super();

        add( new CheckBox( RULER, module.rulerVisible ) );
        add( new CheckBox( MEASURING_TAPE, module.measuringTapeVisible ) );
        add( new UnitsControlPanel<T>( module ) );
        add( Box.createRigidArea( new Dimension( 5, 5 ) ) );//separate the "hose" control a bit from the other controls so it is easier to parse visually
        add( new JSeparator() );
        add( new CheckBox( HOSE, module.hoseVisible ) );
    }
}