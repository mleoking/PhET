// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.view.UnitsControlPanel;

/**
 * @author Sam Reid
 */
public class FluidPressureControlPanel<T extends FluidPressureAndFlowModel> extends VerticalLayoutPanel {
    public static Color BACKGROUND = new Color( 232, 242, 152 );
    public static Color FOREGROUND = Color.black;
    public static final Font CONTROL_FONT = new PhetFont( 18, true );

    public FluidPressureControlPanel( final FluidPressureAndFlowModule<T> module ) {
        super();
        addControlFullWidth( new CheckBox( "Ruler", module.getRulerVisibleProperty() ) );
        addControlFullWidth( new UnitsControlPanel<T>( module ) );
        setBackground( BACKGROUND );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
