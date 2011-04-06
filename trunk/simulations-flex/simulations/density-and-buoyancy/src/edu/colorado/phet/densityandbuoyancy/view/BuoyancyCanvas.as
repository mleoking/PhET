//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.model.Vector2D;
import edu.colorado.phet.densityandbuoyancy.test.Box2DDebug;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ScaleNode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancyPlaygroundMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameDensityMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameMassMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameVolumeMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.NumericProperty;

/**
 * Contains the sim play area for Buoyancy (not including control panels)
 */
public class BuoyancyCanvas extends AbstractDBCanvas {

    private var _container: BuoyancyContainer;

    private var defaultMode: Mode;
    public var sameMassMode: BuoyancySameMassMode;
    private var sameVolumeMode: BuoyancySameVolumeMode;
    private var sameDensityMode: BuoyancySameDensityMode;

    public var playgroundModes: BuoyancyPlaygroundMode;
    private var mode: Mode;

    public const gravityArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const buoyancyArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const contactArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const vectorValuesVisible: BooleanProperty = new BooleanProperty( false );

    public function BuoyancyCanvas( container: BuoyancyContainer, extendedPool: Boolean, showExactLiquidColor: Boolean ) {
        super( extendedPool, showExactLiquidColor );
        this._container = container;

        _model.scalesMovableProperty.initialValue = true; // for now, do this early so that when scales are constructed they are initialized properly

        //Initialize the modes (for some reason cannot be done until application is fully loaded).
        const myThis: BuoyancyCanvas = this;
        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            sameMassMode = new BuoyancySameMassMode( myThis );
            sameVolumeMode = new BuoyancySameVolumeMode( myThis );
            sameDensityMode = new BuoyancySameDensityMode( myThis );
            playgroundModes = new BuoyancyPlaygroundMode( myThis );
            defaultMode = _container.getDefaultMode( myThis );
            //If other modes are added, you may need to specify a call to the Mode.reset() in resetAll()
            setMode( defaultMode );

            var box2DDebug: Box2DDebug = new Box2DDebug( model.getWorld() );
            //        addChild(box2DDebug.getSprite());
        } );
    }

    override protected function createModel( showExactLiquidColor: Boolean ): DensityAndBuoyancyModel {
        return new DensityAndBuoyancyModel( DensityAndBuoyancyConstants.litersToMetersCubed( 100.0 ) - Scale.SCALE_VOLUME, //this accounts for one submerged scale, so that the readout still reads 100.0 on init
                                            extendedPool, showExactLiquidColor );
    }

    override public function resetAll(): void {
        super.resetAll();
        sameMassMode.reset();
        sameVolumeMode.reset();
        sameDensityMode.reset();
        playgroundModes.reset();

        defaultMode.reset();
        switchToDefaultMode();
        vectorValuesVisible.reset();

        buoyancyArrowsVisible.reset();
        gravityArrowsVisible.reset();
        contactArrowsVisible.reset();
    }

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

    public function switchToDefaultMode(): void {
        setMode( defaultMode );
    }

    override public function get container(): AbstractDBContainer {
        return _container;
    }

    override public function showScales(): Boolean {
        return true;
    }

    override protected function createDensityObjectNode( densityObject: DensityObject ): DensityObjectNode {
        var densityObjectNode: DensityObjectNode = super.createDensityObjectNode( densityObject );
        addArrowNodes( densityObjectNode );
        return densityObjectNode;
    }

    private function addArrowNodes( densityObjectNode: DensityObjectNode ): void {
        if ( !(densityObjectNode is ScaleNode) ) {

            var densityObject: DensityObject = densityObjectNode.getDensityObject();
//            var offset: Number = 8;
            var offset: Number = 0;  //TODO: trial test of setting offsets to zero, maybe will revert
            const gravityNode: ArrowNode = new ArrowNode( densityObject, densityObject.getGravityForceArrowModel(), DensityAndBuoyancyConstants.GRAVITY_COLOR, gravityArrowsVisible, mainCamera, mainViewport, vectorValuesVisible,
                                                          createOffset( densityObject.getGravityForceArrowModel(), densityObject, 0 ), true );
            const buoyancyNode: ArrowNode = new ArrowNode( densityObject, densityObject.getBuoyancyForceArrowModel(), DensityAndBuoyancyConstants.BUOYANCY_COLOR, buoyancyArrowsVisible, mainCamera, mainViewport, vectorValuesVisible,
                                                           createOffset( densityObject.getBuoyancyForceArrowModel(), densityObject, 0 ), true );
            const contactForceNode: ArrowNode = new ArrowNode( densityObject, densityObject.getContactForceArrowModel(), DensityAndBuoyancyConstants.CONTACT_COLOR, contactArrowsVisible, mainCamera, mainViewport, vectorValuesVisible,
                                                               createOffset( densityObject.getContactForceArrowModel(), densityObject, offset ), false );

            const arrowList: Array = [gravityNode, buoyancyNode, contactForceNode];
            for each ( var arrowNode: ArrowNode in arrowList ) {
                densityObjectNode.addArrowNode( arrowNode );
            }
        }
    }

    private function createOffset( arrowModel: Vector2D, densityObject: DensityObject, dx: Number ): NumericProperty {
        var offsetX: NumericProperty = new NumericProperty( "offsetX", "pixels", dx );

        function tooMuchOverlap( y1: Number, y2: Number ): Boolean {
            return y1 * y2 > 100;
        }

        //Check to see if the arrowModel in question has the same sign as any other arrowModel
        function isTooMuchOverlap(): Boolean {
            for each ( var vector: Vector2D in densityObject.forceVectors ) {
                if ( vector != arrowModel ) {
                    if ( tooMuchOverlap( vector.y, arrowModel.y ) ) {
                        return true;
                    }
                }
            }
            return false;
        }

        function update(): void {
            if ( isTooMuchOverlap() ) {
                offsetX.value = dx;
            }
            else {
                offsetX.value = 0;
            }
        }

        densityObject.getGravityForceArrowModel().addListener( update );
        densityObject.getBuoyancyForceArrowModel().addListener( update );
        densityObject.getContactForceArrowModel().addListener( update );
        return offsetX;
    }

    public function switchToOneObject(): void {
        setMode( playgroundModes );
        playgroundModes.setOneObject();
    }

    public function switchToTwoObjects(): void {
        setMode( playgroundModes );
        playgroundModes.setTwoObjects();
    }
}
}

