package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.view.units.LinearUnit;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;

import mx.containers.Canvas;

public class DensityModule extends AbstractDensityModule {

    private var _densityCanvas:Canvas;

    private var customObjectMode:CustomObjectMode;
    private var sameMassMode:SameMassMode;
    private var sameVolumeMode:SameVolumeMode;
    private var sameDensityMode:SameDensityMode;
    private var mysteryObjectsMode:MysteryObjectsMode;
    private var mode:Mode;

    private var _units:Units = new Units("kg/L", new LinearUnit("kg", 1.0), new LinearUnit("L", 1000.0), new LinearUnit("kg/L", 1.0 / 1000.0));

    public function DensityModule() {
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

        var box2DDebug:Box2DDebug = new Box2DDebug(getModel().getWorld());
        //        _densityCanvas.addChild(box2DDebug.getSprite());
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

    public function get units():Units {
        return _units;
    }
}
}

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityandbuoyancy.components.MysteryObjectsControlPanel;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.MysteryBlock;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.DensityModule;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.geom.ColorTransform;

class Mode {
    protected var module:DensityModule;

    public function Mode(module:DensityModule) {
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
    private var customizableObject:Block;
    private var customObjectPropertiesPanel:CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing:Boolean = false;

    public function CustomObjectMode(module:DensityModule) {
        super(module);
        //Showing the blocks as partially floating allows easier visualization of densities
        const material:Material = Material.WOOD;
        const volume:Number = DensityConstants.DEFAULT_BLOCK_MASS / material.getDensity();
        const height:Number = Math.pow(volume, 1.0 / 3);
        customizableObject = Block.newBlockDensityMass(material.getDensity(), DensityConstants.DEFAULT_BLOCK_MASS, -DensityConstants.POOL_WIDTH_X / 2, height, new ColorTransform(0.5, 0.5, 0), module.getModel(), material);
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel(customizableObject, module.units);
    }

    override public function teardown():void {
        super.teardown();
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

        //        module.getModel().addDensityObject(new Scale(-DensityConstants.POOL_WIDTH_X/2-Scale.SCALE_WIDTH/2, 0.05, module.getModel(), 100));//For debugging the scale
    }

    public override function reset():void {
        customizableObject.reset();
    }
}

class SameMassMode extends Mode {

    public function SameMassMode(module:DensityModule) {
        super(module);
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();

        var block1:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(10), 5, 0, 0, new ColorTransform(0.5, 0.5, 0), model, Material.CUSTOM);
        block1.setPosition(-DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() / 2);
        model.addDensityObject(block1);

        var block2:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(5), 5, 0, 0, new ColorTransform(0, 0, 1), model, Material.CUSTOM);
        block2.setPosition(-DensityConstants.POOL_WIDTH_X / 2 - block1.getWidth(), block2.getHeight() / 2);
        model.addDensityObject(block2);

        var block3:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(3), 5, 0, 0, new ColorTransform(0, 1, 0), model, Material.CUSTOM);
        block3.setPosition(DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() / 2);
        model.addDensityObject(block3);

        var block4:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(1), 5, 0, 0, new ColorTransform(1, 0, 0), model, Material.CUSTOM);
        block4.setPosition(DensityConstants.POOL_WIDTH_X / 2 + block3.getWidth(), block4.getHeight() / 2);
        model.addDensityObject(block4);
    }
}

class SameVolumeMode extends Mode {

    public function SameVolumeMode(module:DensityModule) {
        super(module);
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();

        var block1:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(5), 5, 0, 0, new ColorTransform(0.5, 0.5, 0), model, Material.CUSTOM);
        block1.setPosition(-DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() / 2);
        model.addDensityObject(block1);

        var block2:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(5), 4, 0, 0, new ColorTransform(0, 0, 1), model, Material.CUSTOM);
        block2.setPosition(-DensityConstants.POOL_WIDTH_X / 2 - block1.getWidth(), block2.getHeight() / 2);
        model.addDensityObject(block2);

        var block3:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(5), 3, 0, 0, new ColorTransform(0, 1, 0), model, Material.CUSTOM);
        block3.setPosition(DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() / 2);
        model.addDensityObject(block3);

        var block4:Block = Block.newBlockVolumeMass(DensityConstants.litersToMetersCubed(5), 2, 0, 0, new ColorTransform(1, 0, 0), model, Material.CUSTOM);
        block4.setPosition(DensityConstants.POOL_WIDTH_X / 2 + block3.getWidth(), block4.getHeight() / 2);
        model.addDensityObject(block4);
    }
}

class SameDensityMode extends Mode {

    public function SameDensityMode(module:DensityModule) {
        super(module);
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();
        var density:Number = 800; //Showing the blocks as partially floating allows easier visualization of densities
        var block1:Block = Block.newBlockDensityMass(density, 3, 0, 0, new ColorTransform(0.5, 0.5, 0), model, Material.CUSTOM);
        block1.setPosition(-DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() / 2);
        model.addDensityObject(block1);

        var block2:Block = Block.newBlockDensityMass(density, 2, 0, 0, new ColorTransform(0, 0, 1), model, Material.CUSTOM);
        block2.setPosition(-DensityConstants.POOL_WIDTH_X / 2 - block1.getWidth(), block2.getHeight() / 2);
        model.addDensityObject(block2);

        var block3:Block = Block.newBlockDensityMass(density, 1, 0, 0, new ColorTransform(0, 1, 0), model, Material.CUSTOM);
        block3.setPosition(DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() / 2);
        model.addDensityObject(block3);

        var block4:Block = Block.newBlockDensityMass(density, 0.5, 0, 0, new ColorTransform(1, 0, 0), model, Material.CUSTOM);
        block4.setPosition(DensityConstants.POOL_WIDTH_X / 2 + block3.getWidth(), block4.getHeight() / 2);
        model.addDensityObject(block4);
    }
}

class MysteryObjectsMode extends Mode {
    private var mysteryObjectsControlPanel:MysteryObjectsControlPanel;
    private var mysteryObjectsControlPanelShowing:Boolean = false;

    function MysteryObjectsMode(module:DensityModule) {
        super(module);
        mysteryObjectsControlPanel = new MysteryObjectsControlPanel();
    }

    override public function teardown():void {
        super.teardown();
        if (mysteryObjectsControlPanelShowing) {
            module.getDensityCanvas().removeChild(mysteryObjectsControlPanel);
            mysteryObjectsControlPanelShowing = false;
        }
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.getModel();

        const block1:MysteryBlock = new MysteryBlock(Material.GOLD.getDensity(), 0.15, DensityConstants.POOL_WIDTH_X / 2, 0.15 / 2, new ColorTransform(0.5, 0.5, 0), model, FlexSimStrings.get("mode.mysteryObjects.A", "A"));
        model.addDensityObject(block1);
        const block2:MysteryBlock = new MysteryBlock(Material.APPLE.getDensity(), 0.1, DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() + block1.getY(), new ColorTransform(0, 0, 1), model, FlexSimStrings.get("mode.mysteryObjects.B", "B"));
        model.addDensityObject(block2);
        const block3:MysteryBlock = new MysteryBlock(Material.GASOLINE_BALLOON.getDensity(), 0.18, -DensityConstants.POOL_WIDTH_X / 2, 0.18 / 2, new ColorTransform(0, 1, 0), model, FlexSimStrings.get("mode.mysteryObjects.C", "C"));
        model.addDensityObject(block3);
        const block4:MysteryBlock = new MysteryBlock(Material.ICE.getDensity(), 0.15, -DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() + block3.getY(), new ColorTransform(1, 0, 0), model, FlexSimStrings.get("mode.mysteryObjects.D", "D"));
        model.addDensityObject(block4);
        const block5:MysteryBlock = new MysteryBlock(Material.DIAMOND.getDensity(), 0.1, -DensityConstants.POOL_WIDTH_X / 2, block4.getHeight() + block4.getY(), new ColorTransform(1, 0, 0), model, FlexSimStrings.get("mode.mysteryObjects.E", "E"));
        model.addDensityObject(block5);

        model.addDensityObject(new Scale(-DensityConstants.POOL_WIDTH_X / 2 - block3.getWidth() - Scale.SCALE_WIDTH / 2, 0.05, model, 100));

        if (!mysteryObjectsControlPanelShowing) {
            module.getDensityCanvas().addChild(mysteryObjectsControlPanel);
            mysteryObjectsControlPanelShowing = true;
        }
    }

    override public function reset():void {
        super.reset();
    }
}