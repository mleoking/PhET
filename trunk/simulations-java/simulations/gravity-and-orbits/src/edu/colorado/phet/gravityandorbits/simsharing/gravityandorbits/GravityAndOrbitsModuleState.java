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
        forcesProperty = module.getShowGravityForceProperty().getValue();
        tracesProperty = module.getShowPathProperty().getValue();
        velocityProperty = module.getShowVelocityProperty().getValue();
        showMassesProperty = module.getShowMassProperty().getValue();
        moonProperty = module.getMoonProperty().getValue();
        toScaleProperty = module.getToScaleProperty().getValue();

        modelState = new GravityAndOrbitsModelState( module.getGravityAndOrbitsModel() );
    }

    public void apply( GravityAndOrbitsModule gravityAndOrbitsModule ) {
        gravityAndOrbitsModule.getShowGravityForceProperty().setValue( forcesProperty );
        gravityAndOrbitsModule.getShowPathProperty().setValue( tracesProperty );
        gravityAndOrbitsModule.getShowVelocityProperty().setValue( velocityProperty );
        gravityAndOrbitsModule.getShowMassProperty().setValue( showMassesProperty );
        gravityAndOrbitsModule.getMoonProperty().setValue( moonProperty );
        gravityAndOrbitsModule.getToScaleProperty().setValue( toScaleProperty );

        modelState.apply(gravityAndOrbitsModule.getGravityAndOrbitsModel());
    }
}
