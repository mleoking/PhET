package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityflex.model.Block;

import flash.geom.ColorTransform;

import mx.containers.Canvas;

public class DensityViewIntro extends DensityView {
    private var _densityCanvas:Canvas;
    private var customObjectPropertiesPanel:CustomObjectPropertiesPanel;

    public function DensityViewIntro() {
        super();
         customObjectPropertiesPanel= new CustomObjectPropertiesPanel(new DefaultDensityObject());
    }

    override public function initObjects():void {
        super.initObjects();
        initializeSameMass();
        //model.initializeTab1SameVolume();
    }

    public function initializeSameMass():void {
        model.addDensityObject(Block.newBlockSizeMass(3, 4.0, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockSizeMass(2, 4.0, -1.5, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockSizeMass(1.5, 4.0, 1.5, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockSizeMass(1, 4.0, 4.5, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    public function initializeSameVolume():void {
        model.addDensityObject(Block.newBlockDensitySize(1.0 / 8.0, 2, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockDensitySize(0.5, 2, -1.5, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockDensitySize(2, 2, 1.5, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockDensitySize(4, 2, 4.5, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    public function initializeSameDensity():void {
        var density:Number = 0.25; //Showing the blocks as partially floating allows easier visualization of densities
        model.addDensityObject(Block.newBlockDensityMass(density, 7, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
        model.addDensityObject(Block.newBlockDensityMass(density, 2, -1.5, 0, new ColorTransform(0, 0, 1), model));
        model.addDensityObject(Block.newBlockDensityMass(density, 1, 1.5, 0, new ColorTransform(0, 1, 0), model));
        model.addDensityObject(Block.newBlockDensityMass(density, 0.5, 4.5, 0, new ColorTransform(1, 0, 0), model));
        addScales();
    }

    private function initializeCustomObject():void {
        var density:Number = 0.25; //Showing the blocks as partially floating allows easier visualization of densities
        model.addDensityObject(Block.newBlockDensityMass(density, 7, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
    }

    private function initializeMysteryObjects():void {
        var density:Number = 0.25; //Showing the blocks as partially floating allows easier visualization of densities
        model.addDensityObject(Block.newBlockDensityMass(density, 7, -4.5, 0, new ColorTransform(0.5, 0.5, 0), model));
        addScales();
    }

    public function switchToSameMass():void {
        model.clearDensityObjects();
        initializeSameMass();
    }

    override public function reset():void {
        super.reset();
        switchToSameMass();
    }

    public function switchToSameVolume():void {
        model.clearDensityObjects();
        initializeSameVolume();
    }

    public function switchToSameDensity():void {
        model.clearDensityObjects();
        initializeSameDensity();
    }

    public function switchToCustomObject():void {
        model.clearDensityObjects();
        initializeCustomObject();
        _densityCanvas.addChild(customObjectPropertiesPanel);
    }

    public function switchToMysteryObjects():void {
//        _densityCanvas.removeChild(customObjectPropertiesPanel);
        model.clearDensityObjects();
        initializeMysteryObjects();
    }

    public function set densityCanvas(densityCanvas:Canvas):void {
        this._densityCanvas= densityCanvas;
    }
}
}