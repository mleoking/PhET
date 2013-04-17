// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.module;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;
import edu.colorado.phet.workenergy.view.WorkEnergyCanvas;

/**
 * @author Sam Reid
 */
public class WorkEnergyModule<ModelType extends WorkEnergyModel> extends Module {
    private ModelType model;
    private Property<Boolean> showPieChartProperty = new Property<Boolean>( true );
    private Property<Boolean> showEnergyBarChartProperty = new Property<Boolean>( false );
    private Property<Boolean> showRulerProperty = new Property<Boolean>( false );

    static final int delay = 30;//milliseconds between clock ticks
    static final double delaySec = delay / 1000.0;

    public WorkEnergyModule( PhetFrame phetFrame, String title, final ModelType model ) {
        super( title, model.getClock() );
        this.model = model;
        WorkEnergyCanvas energyCanvas = new WorkEnergyCanvas( this, model );
        getModulePanel().setLogoPanel( null );

        setSimulationPanel( energyCanvas );

        //Handled in canvas
        setControlPanel( null );
        setClockControlPanel( null );

        getClock().addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                model.stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    public ModelType getWorkEnergyModel() {
        return model;
    }

    public void reset() {
        showPieChartProperty.reset();
        showEnergyBarChartProperty.reset();
        showRulerProperty.reset();
        model.resetAll();
    }

    public Property<Boolean> getShowPieChartProperty() {
        return showPieChartProperty;
    }

    public Property<Boolean> getShowEnergyBarChartProperty() {
        return showEnergyBarChartProperty;
    }

    public Property<Boolean> getShowRulerProperty() {
        return showRulerProperty;
    }
}
