package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ScaleNode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancyPlaygroundMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameDensityMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameMassMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameVolumeMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;

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
    public const fluidDragArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const vectorValuesVisible: BooleanProperty = new BooleanProperty( false );

    public function BuoyancyCanvas( container: BuoyancyContainer, extendedPool: Boolean, showExactLiquidColor: Boolean ) {
        super( extendedPool, showExactLiquidColor );
        this._container = container;

        _model.scalesMovableProperty.initialValue = true; // for now, do this early so that when scales are constructed they are initialized properly
    }

    override protected function createModel( showExactLiquidColor: Boolean ): DensityModel {
        //TODO: dynamically compute the volume of the submerged scale
        return new DensityModel( DensityConstants.litersToMetersCubed( 100.0 - 2.46 ), extendedPool, showExactLiquidColor );//this accounts for one submerged scale, so that the readout still reads 100.0 on init
    }

    override public function init(): void {
        super.init();
        sameMassMode = new BuoyancySameMassMode( this );
        sameVolumeMode = new BuoyancySameVolumeMode( this );
        sameDensityMode = new BuoyancySameDensityMode( this );
        playgroundModes = new BuoyancyPlaygroundMode( this );
        defaultMode = _container.getDefaultMode( this );
        //If other modes are added, you may need to specify a call to the Mode.reset() in resetAll()
        setMode( defaultMode );

        var box2DDebug: Box2DDebug = new Box2DDebug( model.getWorld() );
        //        addChild(box2DDebug.getSprite());
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
        fluidDragArrowsVisible.reset();
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
            const gravityNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getGravityForceArrowModel(), DensityConstants.GRAVITY_COLOR, gravityArrowsVisible, mainCamera, mainViewport, vectorValuesVisible );
            const buoyancyNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getBuoyancyForceArrowModel(), DensityConstants.BUOYANCY_COLOR, buoyancyArrowsVisible, mainCamera, mainViewport, vectorValuesVisible );
            const contactForceNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getContactForceArrowModel(), DensityConstants.CONTACT_COLOR, contactArrowsVisible, mainCamera, mainViewport, vectorValuesVisible );
            const dragForceNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getDragForceArrowModel(), DensityConstants.FLUID_DRAG_COLOR, fluidDragArrowsVisible, mainCamera, mainViewport, vectorValuesVisible );

            const arrowList: Array = [gravityNode, buoyancyNode, contactForceNode, dragForceNode];
            for each ( var arrowNode: ArrowNode in arrowList ) {
                densityObjectNode.addArrowNode( arrowNode );
            }
        }
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

