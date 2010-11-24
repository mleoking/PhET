package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModuleState implements Serializable {
    private boolean showGravityForce = false;
    private boolean showPaths = false;
    private boolean showVelocity = false;
    private boolean showMass = false;
    private boolean moonProperty = false;
    private GravityAndOrbitsModelState modelState;

    public GravityAndOrbitsModuleState( GravityAndOrbitsModule module ) {
        showGravityForce = module.getShowGravityForceProperty().getValue();
        showPaths = module.getShowPathProperty().getValue();
        showVelocity = module.getShowVelocityProperty().getValue();
        showMass = module.getShowMassProperty().getValue();
        moonProperty = module.getMoonProperty().getValue();

        modelState = new GravityAndOrbitsModelState( module.getGravityAndOrbitsModel() );
    }

    public void apply( GravityAndOrbitsModule gravityAndOrbitsModule ) {
        gravityAndOrbitsModule.getShowGravityForceProperty().setValue( showGravityForce );
        gravityAndOrbitsModule.getShowPathProperty().setValue( showPaths );
        gravityAndOrbitsModule.getShowVelocityProperty().setValue( showVelocity );
        gravityAndOrbitsModule.getShowMassProperty().setValue( showMass );
        gravityAndOrbitsModule.getMoonProperty().setValue( moonProperty );

        modelState.apply( gravityAndOrbitsModule.getGravityAndOrbitsModel() );
    }
}
