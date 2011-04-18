//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.test.Box2DDebug;
import edu.colorado.phet.densityandbuoyancy.view.modes.DensityCustomObjectMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.DensityMysteryObjectsMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.DensitySameDensityMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.DensitySameMassMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.DensitySameVolumeMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;

/**
 * Contains the sim play area for Density (not including control panels)
 */
public class DensityPlayAreaComponent extends AbstractDensityAndBuoyancyPlayAreaComponent {

    private var _container: DensityCanvas;  //REVIEW why are we dealing with AS getter naming goofiness instead of making this public?

    private var customObjectMode: DensityCustomObjectMode;
    private var sameMassMode: DensitySameMassMode;
    private var sameVolumeMode: DensitySameVolumeMode;
    private var sameDensityMode: DensitySameDensityMode;
    private var mysteryObjectsMode: DensityMysteryObjectsMode;
    private var mode: Mode;

    public function DensityPlayAreaComponent( densityContainer: DensityCanvas ) {
        super( false );
        this._container = densityContainer;
        const myThis: DensityPlayAreaComponent = this;
        //REVIEW doc, why can't modes be created until applicationComplete event is dispatched? what's the dependency?
        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            customObjectMode = new DensityCustomObjectMode( myThis );
            sameMassMode = new DensitySameMassMode( myThis );
            sameVolumeMode = new DensitySameVolumeMode( myThis );
            sameDensityMode = new DensitySameDensityMode( myThis );
            mysteryObjectsMode = new DensityMysteryObjectsMode( myThis );
            //If other modes are added, you may need to specify a call to the Mode.reset() in resetAll()
            setMode( customObjectMode );

            //Box2D provides a sprite-oriented debugger for visualizing the box2d physics engine.  This can be turned on by uncommenting the addChild() line and will display the box2d shapes
            var box2DDebug: Box2DDebug = new Box2DDebug( model.getWorld() );
            //        _densityCanvas.addChild(box2DDebug.getSprite());
        } );
    }

    override public function resetAll(): void {
        super.resetAll();
        customObjectMode.reset();
        switchToCustomObject();
    }

    //REVIEW this can be private, switch* methods are used to publicly change mode
    public function setMode( mode: Mode ): void {
        if ( this.mode != mode ) {
            if ( this.mode != null ) {
                this.mode.teardown();
            }
            this.mode = mode;
            this.mode.init();
        }
    }

    public function switchToSameMass(): void {
        setMode( sameMassMode );
    }

    public function switchToSameVolume(): void {
        setMode( sameVolumeMode );
    }

    public function switchToSameDensity(): void {
        setMode( sameDensityMode );
    }

    public function switchToCustomObject(): void {
        setMode( customObjectMode );
    }

    public function switchToMysteryObjects(): void {
        setMode( mysteryObjectsMode );
    }

    //REVIEW what does this getter buy us? performance?
    override public function get container(): AbstractDensityAndBuoyancyCanvas {
        return _container;
    }

    protected override function createModel( showExactLiquidColor: Boolean ): DensityAndBuoyancyModel {
        return new DensityAndBuoyancyModel( DensityAndBuoyancyConstants.litersToMetersCubed( 100.0 ), extendedPool );
    }
}
}

