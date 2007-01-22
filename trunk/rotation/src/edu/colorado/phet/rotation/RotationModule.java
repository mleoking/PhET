package edu.colorado.phet.rotation;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 * Copyright (c) Dec 27, 2006 by Sam Reid
 */

public class RotationModule extends PiccoloModule {
    private RotationSimulationPanel rotationSimulationPanel;

    /*The Physical Model*/
    private RotationModel rotationModel;

    /*Other MVC model data structures*/
    private VectorViewModel vectorViewModel;

    public RotationModule() {
        super( "Rotation", createClock() );
        setModel( new BaseModel() );
        rotationModel = new RotationModel();
        vectorViewModel = new VectorViewModel();

        rotationSimulationPanel = new RotationSimulationPanel( this );
        setSimulationPanel( rotationSimulationPanel );

        setLogoPanel( null );
        setClockControlPanel( null );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                rotationModel.stepInTime( dt );
            }
        } );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1.0 );
    }

    public GraphSetModel getGraphSetModel() {
        return rotationSimulationPanel.getGraphSetModel();
    }

    public VectorViewModel getVectorViewModel() {
        return vectorViewModel;
    }

    public RotationModel getRotationModel() {
        return rotationModel;
    }

    public void setAngleUpdateStrategy() {
        rotationModel.setPositionDriven();
    }

    public void setAngle( double angle ) {
        rotationModel.getXVariable().setValue( angle );
    }

    public RotationSimulationPanel getRotationSimulationPanel() {
        return rotationSimulationPanel;
    }
}
