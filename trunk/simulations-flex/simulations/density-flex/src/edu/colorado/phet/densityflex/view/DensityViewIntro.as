package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityflex.model.Block;

import edu.colorado.phet.densityflex.model.DensityObject;

import edu.colorado.phet.densityflex.model.MysteryBlock;
import edu.colorado.phet.densityflex.model.Substance;

import flash.geom.ColorTransform;

import mx.containers.Canvas;

public class DensityViewIntro extends DensityView {
    private var _densityCanvas:Canvas;
    private var customObjectPropertiesPanel:CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing:Boolean=false;
    private var customizableObject:DensityObject; 

    public function DensityViewIntro() {
        super();
        //Showing the blocks as partially floating allows easier visualization of densities
        customizableObject = Block.newBlockDensityMass(Substance.WOOD.getDensity(), 5000, -8, 3, new ColorTransform(0.5, 0.5, 0), model);
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel(customizableObject);
    }

    override public function initObjects():void {
        super.initObjects();
        initializeCustomObject();
        //model.initializeTab1SameVolume();
    }

    public function initializeSameMass():void {
        model.addDensityObject(Block.newBlockSizeMass(3, 4.0*1000, -8, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockSizeMass(2, 4.0*1000, -8, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockSizeMass(1.5, 4.0*1000, 8, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockSizeMass(1, 4.0*1000, 8, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    public function initializeSameVolume():void {
        model.addDensityObject(Block.newBlockDensitySize(1.0 / 8.0*1000, 2, -8, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockDensitySize(0.5*1000, 2, -8, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockDensitySize(2*1000, 2, 8, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockDensitySize(4*1000, 2, 8, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    public function initializeSameDensity():void {
        var density:Number = 0.25*1000; //Showing the blocks as partially floating allows easier visualization of densities
        model.addDensityObject(Block.newBlockDensityMass(density, 7000, -8, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockDensityMass(density, 2000, -8, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockDensityMass(density, 1000, 8, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockDensityMass(density, 0.5*1000, 8, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    private function initializeCustomObject():void {
        customizableObject.updateBox2DModel();
        model.addDensityObject(customizableObject);
    }

    private function initializeMysteryObjects():void {
        model.addDensityObject(new MysteryBlock(Substance.GOLD.getDensity(), 1.3, -8, 0, new ColorTransform(0.5, 0.5, 0), model,"A"));
        model.addDensityObject(new MysteryBlock(Substance.APPLE.getDensity(), 1.4, -8, 0, new ColorTransform(0, 0, 1), model,"B"));
        model.addDensityObject(new MysteryBlock(Substance.GASOLINE_BALLOON.getDensity(), 1.5, 8, 0, new ColorTransform(0, 1, 0), model,"C"));
        model.addDensityObject(new MysteryBlock(Substance.ICE.getDensity(), 1.6, 8, 0, new ColorTransform(1, 0, 0), model,"D"));
        model.addDensityObject(new MysteryBlock(Substance.AIR_BALLOON.getDensity(), 1.7, 8, 0, new ColorTransform(1, 0, 0), model,"E"));
        addScales();
    }

    override public function resetAll():void {
        super.resetAll();
        customizableObject.reset();
        switchToCustomObject();
    }

    public function switchToSameMass():void {
        removeCustomPanel();
        model.clearDensityObjects();
        initializeSameMass();
    }

    public function switchToSameVolume():void {
        removeCustomPanel();
        model.clearDensityObjects();
        initializeSameVolume();
    }

    public function switchToSameDensity():void {
        removeCustomPanel();
        model.clearDensityObjects();
        initializeSameDensity();
    }

    public function switchToCustomObject():void {
        model.clearDensityObjects();
        initializeCustomObject();
        if (!customObjectPropertiesPanelShowing){
            _densityCanvas.addChild(customObjectPropertiesPanel);
            customObjectPropertiesPanelShowing = true;
        }
    }

    public function switchToMysteryObjects():void {
        removeCustomPanel();
        model.clearDensityObjects();
        initializeMysteryObjects();
    }

    //TODO: add a "onModeExit()" callback instead of having modes know about each other
    private function removeCustomPanel():void {
        if (customObjectPropertiesPanelShowing){
            _densityCanvas.removeChild(customObjectPropertiesPanel);
            customObjectPropertiesPanelShowing = false;
        }
    }

    public function set densityCanvas(densityCanvas:Canvas):void {
        this._densityCanvas = densityCanvas;
    }
}
}