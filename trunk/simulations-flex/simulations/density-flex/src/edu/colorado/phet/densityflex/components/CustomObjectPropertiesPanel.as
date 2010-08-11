package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.DensityObject;

import edu.colorado.phet.densityflex.model.Substance;

import mx.containers.Grid;
import mx.containers.Panel;
import mx.controls.ComboBox;

public class CustomObjectPropertiesPanel extends Panel {
    private var grid:Grid = new Grid();
    private var iDensityObject:IDensityObject;
    private var densityObject:DensityObject;

    public function CustomObjectPropertiesPanel(densityObject:IDensityObject) {
        super();
        this.title = "Properties";
        this.iDensityObject = densityObject;
    }

    private var comboBox:ComboBox;

    public override function initialize():void {
        super.initialize();

        grid.addChild(new PropertyEditor(iDensityObject.getMass()));
        grid.addChild(new PropertyEditor(iDensityObject.getVolume()));
        grid.addChild(new DensityEditor(iDensityObject.getDensity()));

        comboBox = new ComboBox();
        comboBox.dataProvider = ["Styrofoam","Water Balloon","Lead","Custom"];
        function myListener():void{
            iDensityObject.getDensity().value = Substance.OBJECT_SUBSTANCES[comboBox.selectedItem];
        }
        comboBox.addEventListener("change",myListener);
        addChild(comboBox);

        addChild(grid);
    }


    public function setDensityObject(densityObject:DensityObject):void {
        //TODO: remove listeners from former density object
        this.densityObject = densityObject;

        iDensityObject.getMass().value = densityObject.getMass();
        iDensityObject.getVolume().value = densityObject.getVolume();
        iDensityObject.getDensity().value = densityObject.getDensity();
        iDensityObject.addSubstanceListener(function f():void {
            comboBox.selectedItem = iDensityObject.getSubstance().name; 
        });

        //TODO: connect mass values
        //        function massListener():void {densityObject.setMass(iDensityObject.getMass().value);}
        //        iDensityObject.getMass().addListener(massListener);
        function volumeListener():void {
            densityObject.setVolume(iDensityObject.getVolume().value);
        }

        iDensityObject.getVolume().addListener(volumeListener);
        function densityListener():void {
            densityObject.setDensity(iDensityObject.getDensity().value);
        }

        iDensityObject.getDensity().addListener(densityListener);
    }
}
}