package edu.colorado.phet.densityflex.view {
import Box2D.Dynamics.b2DebugDraw;

import edu.colorado.phet.densityflex.DensityConstants;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.core.UIComponent;

public class DensityViewIntro extends DensityView {

    private var _densityCanvas:Canvas;

    private var customObjectMode:CustomObjectMode;
    private var sameMassMode:SameMassMode;
    private var sameVolumeMode:SameVolumeMode;
    private var sameDensityMode:SameDensityMode;
    private var mysteryObjectsMode:MysteryObjectsMode;
    private var mode:Mode;

    public function DensityViewIntro() {
        super();
    }

    public function doInit(densityCanvas:Canvas):void {
        this._densityCanvas = densityCanvas;
        customObjectMode = new CustomObjectMode(this);
        sameMassMode = new SameMassMode(this);
        sameVolumeMode = new SameVolumeMode(this);
        sameDensityMode = new SameDensityMode(this);
        mysteryObjectsMode = new MysteryObjectsMode(this);
        setMode(customObjectMode);

        // debug draw start
        var m_sprite:Sprite;
        m_sprite = new Sprite();
        m_sprite.x = 400;
        m_sprite.y = 400;
        var holder:UIComponent = new UIComponent();
        holder.addChild(m_sprite);

        _densityCanvas.addChild(holder);
        var dbgDraw:b2DebugDraw = new b2DebugDraw();
        var dbgSprite:Sprite = new Sprite();
        m_sprite.addChild(dbgSprite);
        dbgDraw.m_sprite = m_sprite;
        dbgDraw.m_drawScale = 150 * 5.0 / DensityConstants.SCALE_BOX2D;
        dbgDraw.m_alpha = 1;
        dbgDraw.m_fillAlpha = 0.5;
        dbgDraw.m_lineThickness = 1;
        dbgDraw.m_drawFlags = b2DebugDraw.e_shapeBit | b2DebugDraw.e_jointBit | b2DebugDraw.e_coreShapeBit | b2DebugDraw.e_aabbBit | b2DebugDraw.e_obbBit | b2DebugDraw.e_pairBit | b2DebugDraw.e_centerOfMassBit;
        getModel().getWorld().SetDebugDraw(dbgDraw);
    }

    override public function resetAll():void {
        super.resetAll();
        customObjectMode.reset();
        switchToCustomObject();
    }

    public function setMode(mode:Mode):void {
        if (this.mode != mode) {
            if (this.mode != null) {
                this.mode.teardown();
            }
            this.mode = mode;
            this.mode.init();
        }
    }

    public function switchToSameMass():void {
        setMode(sameMassMode);
    }

    public function switchToSameVolume():void {
        setMode(sameVolumeMode);
    }

    public function switchToSameDensity():void {
        setMode(sameDensityMode);
    }

    public function switchToCustomObject():void {
        setMode(customObjectMode);
    }

    public function switchToMysteryObjects():void {
        setMode(mysteryObjectsMode);
    }

    public function getDensityCanvas():Canvas {
        return _densityCanvas;
    }
}
}

import edu.colorado.phet.densityflex.DensityConstants;
import edu.colorado.phet.densityflex.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityflex.components.MysteryObjectsControlPanel;
import edu.colorado.phet.densityflex.model.Block;
import edu.colorado.phet.densityflex.model.DensityModel;
import edu.colorado.phet.densityflex.model.DensityObject;
import edu.colorado.phet.densityflex.model.MysteryBlock;
import edu.colorado.phet.densityflex.model.Substance;
import edu.colorado.phet.densityflex.view.DensityViewIntro;

import flash.geom.ColorTransform;

class Mode {
    protected var module:DensityViewIntro;

    public function Mode(module:DensityViewIntro) {
        this.module = module;
    }

    public function teardown():void {
        module.getModel().clearDensityObjects();
    }

    public function init():void {
    }

    public function reset():void {
    }
}
class CustomObjectMode extends Mode {
    private var customizableObject:DensityObject;
    private var customObjectPropertiesPanel:CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing:Boolean = false;

    public function CustomObjectMode(module:DensityViewIntro) {
        super(module);
        //Showing the blocks as partially floating allows easier visualization of densities
        customizableObject = Block.newBlockDensityMass(Substance.WOOD.getDensity(), DensityConstants.DEFAULT_BLOCK_MASS, 0, DensityConstants.POOL_HEIGHT_Y, new ColorTransform(0.5, 0.5, 0), module.getModel(), Substance.WOOD);
        //        customizableObject = Block.newBlockDensityMass(Substance.WOOD.getDensity(), DensityConstants.DEFAULT_BLOCK_MASS, -DensityConstants.POOL_WIDTH_X/2.0, DensityConstants.POOL_HEIGHT_Y, new ColorTransform(0.5, 0.5, 0), module.getModel(),Substance.WOOD);

        //        customizableObject = Block.newBlockDensityMass(Substance.WOOD.getDensity(), 5000, -8, 3, new ColorTransform(0.5, 0.5, 0), module.getModel(),Substance.WOOD);
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel(customizableObject);
    }

    override public function teardown():void {
        super.teardown();
        removeCustomPanel();
    }

    //TODO: add a "onModeExit()" callback instead of having modes know about each other
    private function removeCustomPanel():void {
        if (customObjectPropertiesPanelShowing) {
            module.getDensityCanvas().removeChild(customObjectPropertiesPanel);
            customObjectPropertiesPanelShowing = false;
        }
    }

    override public function init():void {
        super.init();
        customizableObject.updateBox2DModel();

        if (!customObjectPropertiesPanelShowing) {
            module.getDensityCanvas().addChild(customObjectPropertiesPanel);
            customObjectPropertiesPanelShowing = true;
        }
        module.getModel().addDensityObject(customizableObject);
    }

    public override function reset():void {
        customizableObject.reset();
    }
}

class SameMassMode extends Mode {

    public function SameMassMode(module:DensityViewIntro) {
        super(module);
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();
        model.addDensityObject(Block.newBlockSizeMass(3, 4.0 * 1000, -8, 0, new ColorTransform(0.5, 0.5, 0), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockSizeMass(2, 4.0 * 1000, -8, 0, new ColorTransform(0, 0, 1), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockSizeMass(1.5, 4.0 * 1000, 8, 0, new ColorTransform(0, 1, 0), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockSizeMass(1, 4.0 * 1000, 8, 0, new ColorTransform(1, 0, 0), model, Substance.CUSTOM));
    }
}

class SameVolumeMode extends Mode {

    public function SameVolumeMode(module:DensityViewIntro) {
        super(module);
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();

        model.addDensityObject(Block.newBlockDensitySize(1.0 / 8.0 * 1000, 2, -8, 0, new ColorTransform(0.5, 0.5, 0), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockDensitySize(0.5 * 1000, 2, -8, 0, new ColorTransform(0, 0, 1), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockDensitySize(2 * 1000, 2, 8, 0, new ColorTransform(0, 1, 0), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockDensitySize(4 * 1000, 2, 8, 0, new ColorTransform(1, 0, 0), model, Substance.CUSTOM));
    }
}

class SameDensityMode extends Mode {

    public function SameDensityMode(module:DensityViewIntro) {
        super(module);
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();
        var density:Number = 0.25 * 1000; //Showing the blocks as partially floating allows easier visualization of densities
        model.addDensityObject(Block.newBlockDensityMass(density, 7000, -8, 0, new ColorTransform(0.5, 0.5, 0), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockDensityMass(density, 2000, -8, 0, new ColorTransform(0, 0, 1), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockDensityMass(density, 1000, 8, 0, new ColorTransform(0, 1, 0), model, Substance.CUSTOM));
        model.addDensityObject(Block.newBlockDensityMass(density, 0.5 * 1000, 8, 0, new ColorTransform(1, 0, 0), model, Substance.CUSTOM));
    }
}

class MysteryObjectsMode extends Mode {
    private var mysteryObjectsControlPanel:MysteryObjectsControlPanel;
    private var mysteryObjectsControlPanelShowing:Boolean = false;

    function MysteryObjectsMode(module:DensityViewIntro) {
        super(module);
        mysteryObjectsControlPanel = new MysteryObjectsControlPanel();
    }

    override public function teardown():void {
        super.teardown();
        removeMysteryObjectControlPanel();
    }

    private function removeMysteryObjectControlPanel():void {
        if (mysteryObjectsControlPanelShowing) {
            module.getDensityCanvas().removeChild(mysteryObjectsControlPanel);
            mysteryObjectsControlPanelShowing = false;
        }
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();

        model.addDensityObject(new MysteryBlock(Substance.GOLD.getDensity(), 1.3, -8, 0, new ColorTransform(0.5, 0.5, 0), model, "A"));
        model.addDensityObject(new MysteryBlock(Substance.APPLE.getDensity(), 1.4, -8, 0, new ColorTransform(0, 0, 1), model, "B"));
        model.addDensityObject(new MysteryBlock(Substance.GASOLINE_BALLOON.getDensity(), 1.5, 8, 0, new ColorTransform(0, 1, 0), model, "C"));
        model.addDensityObject(new MysteryBlock(Substance.ICE.getDensity(), 1.6, 8, 0, new ColorTransform(1, 0, 0), model, "D"));
        model.addDensityObject(new MysteryBlock(Substance.AIR_BALLOON.getDensity(), 1.7, 8, 0, new ColorTransform(1, 0, 0), model, "E"));
        module.addScales();

        if (!mysteryObjectsControlPanelShowing) {
            module.getDensityCanvas().addChild(mysteryObjectsControlPanel);
            mysteryObjectsControlPanelShowing = true;
        }
    }

    override public function reset():void {
        super.reset();
    }
}