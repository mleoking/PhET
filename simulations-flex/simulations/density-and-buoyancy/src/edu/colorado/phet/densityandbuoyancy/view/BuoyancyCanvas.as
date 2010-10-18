package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ScaleNode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.densityandbuoyancy.view.modes.SameDensityMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.SameMassMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.SameVolumeMode;

public class BuoyancyCanvas extends AbstractDBCanvas {

    private var _container: BuoyancyContainer;

    private var customObjectMode: Mode;
    private var sameMassMode: SameMassMode;
    private var sameVolumeMode: SameVolumeMode;
    private var sameDensityMode: SameDensityMode;
    private var mode: Mode;

    private var gravityArrowsVisible: BooleanProperty = new BooleanProperty( false );
    private var buoyancyArrowsVisible: BooleanProperty = new BooleanProperty( true );//show only buoyancy by default
    private var contactArrowsVisible: BooleanProperty = new BooleanProperty( false );
    private var fluidDragArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const vectorValuesVisible: BooleanProperty = new BooleanProperty( true );

    public function BuoyancyCanvas() {
        super();

        _model.scalesMovableProperty.initialValue = true; // for now, do this early so that when scales are constructed they are initialized properly
    }

    override protected function createModel(): DensityModel {
        //TODO: dynamically compute the volume of the submerged scale
        return new DensityModel( DensityConstants.litersToMetersCubed( 100.0 - 2.46 ) );//this accounts for one submerged scale, so that the readout still reads 100.0 on init
    }

    public function doInit( container: BuoyancyContainer ): void {
        this._container = container;
        customObjectMode = container.createCustomObjectMode( this );
        sameMassMode = new SameMassMode( this );
        sameVolumeMode = new SameVolumeMode( this );
        sameDensityMode = new SameDensityMode( this );
        //If other modes are added, you may need to specify a call to the Mode.reset() in resetAll()
        setMode( customObjectMode );

        var box2DDebug: Box2DDebug = new Box2DDebug( model.getWorld() );
        //        _densityCanvas.addChild(box2DDebug.getSprite());
    }

    override public function resetAll(): void {
        super.resetAll();
        customObjectMode.reset();
        switchToCustomObject();
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

    public function switchToCustomObject(): void {
        setMode( customObjectMode );
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
            const gravityNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getGravityForceArrowModel(), 0x0000FF, gravityArrowsVisible );
            const buoyancyNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getBuoyancyForceArrowModel(), 0xFF00FF, buoyancyArrowsVisible );
            const contactForceNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getContactForceArrowModel(), 0xFF8800, contactArrowsVisible );
            const dragForceNode: ArrowNode = new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getDragForceArrowModel(), 0xFF0000, fluidDragArrowsVisible );

            const arrowList = [gravityNode, buoyancyNode, contactForceNode, dragForceNode];
            for each ( var arrowNode: ArrowNode in arrowList ) {
                addChild( new VectorValueNode( mainCamera, arrowNode, mainViewport, vectorValuesVisible ) );
                densityObjectNode.addArrowNode( arrowNode );
            }
        }
    }

    public function setGravityForceVisible( selected: Boolean ): void {
        gravityArrowsVisible.value = selected;
    }

    public function setBuoyancyForceVisible( selected: Boolean ): void {
        buoyancyArrowsVisible.value = selected;
    }

    public function get buoyantForceVisible(): Boolean {
        return buoyancyArrowsVisible.value;
    }

    public function setContactForceVisible( selected: Boolean ): void {
        contactArrowsVisible.value = selected;
    }

    public function setFluidDragForceVisible( selected: Boolean ): void {
        fluidDragArrowsVisible.value = selected;
    }
}
}

