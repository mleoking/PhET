// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.colorado.phet.fluidpressureandflow.modules.fluidflow.FluidFlowModule;

/**
 * @author Sam Reid
 */
public class UnitsControlPanel<T extends FluidPressureAndFlowModel> extends PhetTitledPanel {

    public UnitsControlPanel( FluidPressureAndFlowModule<T> module ) {
        super( "Units" );

        final T model = module.getFluidPressureAndFlowModel();
        add( new PropertyRadioButton<Units.Unit>( "atmospheres (atm)", model.getPressureUnitProperty(), Units.ATMOSPHERE ) );
        add( new PropertyRadioButton<Units.Unit>( "Pascals (Pa)", model.getPressureUnitProperty(), Units.PASCAL ) );
        add( new PropertyRadioButton<Units.Unit>( "<html>pounds per<br>square inch (psi)</html>", model.getPressureUnitProperty(), Units.PSI ) );
        add( new JSeparator() );
        add( new PropertyRadioButton<Units.Unit>( "feet (ft)", model.getDistanceUnitProperty(), Units.FEET ) );
        add( new PropertyRadioButton<Units.Unit>( "meters (m)", model.getDistanceUnitProperty(), Units.METERS ) );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        SwingUtils.setBackgroundDeep( this, FluidPressureControlPanel.BACKGROUND );
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
