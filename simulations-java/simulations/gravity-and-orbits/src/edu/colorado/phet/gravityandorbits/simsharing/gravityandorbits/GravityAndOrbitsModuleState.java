package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModuleState implements Serializable {
    private boolean forcesProperty = false;
    private boolean tracesProperty = false;
    private boolean velocityProperty = false;
    private boolean showMassesProperty = false;
    private boolean moonProperty = false;
    private boolean toScaleProperty = false;
    private GravityAndOrbitsModelState modelState;

    public GravityAndOrbitsModuleState( GravityAndOrbitsModule module ) {
        forcesProperty = module.getForcesProperty().getValue();
        tracesProperty = module.getTracesProperty().getValue();
        velocityProperty = module.getVelocityProperty().getValue();
        showMassesProperty = module.getShowMassesProperty().getValue();
        moonProperty = module.getMoonProperty().getValue();
        toScaleProperty = module.getToScaleProperty().getValue();

        modelState = new GravityAndOrbitsModelState( module.getGravityAndOrbitsModel() );
    }

    public void apply( GravityAndOrbitsModule gravityAndOrbitsModule ) {
        gravityAndOrbitsModule.getForcesProperty().setValue( forcesProperty );
        gravityAndOrbitsModule.getTracesProperty().setValue( tracesProperty );
        gravityAndOrbitsModule.getVelocityProperty().setValue( velocityProperty );
        gravityAndOrbitsModule.getShowMassesProperty().setValue( showMassesProperty );
        gravityAndOrbitsModule.getMoonProperty().setValue( moonProperty );
        gravityAndOrbitsModule.getToScaleProperty().setValue( toScaleProperty );

        modelState.apply(gravityAndOrbitsModule.getGravityAndOrbitsModel());
    }
}
