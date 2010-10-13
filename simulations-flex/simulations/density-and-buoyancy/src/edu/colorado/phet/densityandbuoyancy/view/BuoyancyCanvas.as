package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
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
    private var buoyancyArrowsVisible: BooleanProperty = new BooleanProperty( false );
    private var contactArrowsVisible: BooleanProperty = new BooleanProperty( false );
    private var fluidDragArrowsVisible: BooleanProperty = new BooleanProperty( false );

    public function BuoyancyCanvas() {
        super();

        _model.scalesMovableProperty.initialValue = true; // for now, do this early so that when scales are constructed they are initialized properly
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
            densityObjectNode.addArrowNode( new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getGravityForceArrowModel(), 0x0000FF, gravityArrowsVisible ) );
            densityObjectNode.addArrowNode( new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getBuoyancyForceArrowModel(), 0xFF00FF, buoyancyArrowsVisible ) );
            densityObjectNode.addArrowNode( new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getContactForceArrowModel(), 0xFF8800, contactArrowsVisible ) );
            densityObjectNode.addArrowNode( new ArrowNode( densityObjectNode.getDensityObject(), densityObjectNode.getDensityObject().getDragForceArrowModel(), 0xFF0000, fluidDragArrowsVisible ) );
        }
    }

    public function setGravityForceVisible( selected: Boolean ): void {
        gravityArrowsVisible.value = selected;
    }

    public function setBuoyancyForceVisible( selected: Boolean ): void {
        buoyancyArrowsVisible.value = selected;
    }

    public function setContactForceVisible( selected: Boolean ): void {
        contactArrowsVisible.value = selected;
    }

    public function setFluidDragForceVisible( selected: Boolean ): void {
        fluidDragArrowsVisible.value = selected;
    }
}
}

