// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.Units;
import edu.colorado.phet.fluidpressureandflow.fluidflow.FluidFlowModule;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureControlPanel;

/**
 * @author Sam Reid
 */
public class UnitsControlPanel<T extends FluidPressureAndFlowModel> extends PhetTitledPanel {

    public UnitsControlPanel( FluidPressureAndFlowModule<T> module ) {
        super( FPAFStrings.UNITS );

        final T model = module.getFluidPressureAndFlowModel();
        add( new PropertyRadioButton<Units.Unit>( FPAFStrings.PRESSURE_ATM, model.pressureUnit, Units.ATMOSPHERE ) );
        add( new PropertyRadioButton<Units.Unit>( FPAFStrings.PASCALS_PA, model.pressureUnit, Units.PASCAL ) );
        add( new PropertyRadioButton<Units.Unit>( FPAFStrings.POUNDS_PER_SQUARE_INCH_PSI, model.pressureUnit, Units.PSI ) );
        add( new JSeparator() );
        add( new PropertyRadioButton<Units.Unit>( FPAFStrings.FEET_FT, model.distanceUnit, Units.FEET ) );
        add( new PropertyRadioButton<Units.Unit>( FPAFStrings.METERS_M, model.distanceUnit, Units.METERS ) );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        SwingUtils.setForegroundDeep( this, FluidPressureControlPanel.FOREGROUND );
    }

    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new UnitsControlPanel( new FluidFlowModule() ) );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            pack();
        }}.setVisible( true );
    }
}
