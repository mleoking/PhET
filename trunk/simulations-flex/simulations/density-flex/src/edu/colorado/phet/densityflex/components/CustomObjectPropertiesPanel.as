package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.DensityObject;

import edu.colorado.phet.densityflex.model.Substance;

import mx.containers.Grid;
import mx.containers.Panel;
import mx.controls.ComboBox;

public class CustomObjectPropertiesPanel extends Panel {
    private var grid:Grid = new Grid();
    private var densityObject:DensityObject;

    public function CustomObjectPropertiesPanel(densityObject:DensityObject) {
        super();
        this.title = "Properties";
        this.densityObject=densityObject;
        
        //TODO: remove listeners from former density object
        this.densityObject = densityObject;

//        iDensityObject.getMass().value = densityObject.getMass();
//        iDensityObject.getVolume().value = densityObject.getVolume();
//        iDensityObject.getDensity().value = densityObject.getDensity();
        densityObject.addSubstanceListener(function f():void {
            comboBox.selectedItem = densityObject.getSubstance().name; 
        });

        //TODO: connect mass values
        //        function massListener():void {densityObject.setMass(iDensityObject.getMass().value);}
        //        iDensityObject.getMass().addListener(massListener);
        function volumeListener():void {
            densityObject.setVolume(densityObject.getVolume());
        }

        densityObject.getVolumeProperty().addListener(volumeListener);
        function densityListener():void {
            densityObject.setDensity(densityObject.getDensity());
        }

        densityObject.getDensityProperty().addListener(densityListener);
    }

    private var comboBox:ComboBox;

    public override function initialize():void {
        super.initialize();

        grid.addChild(new PropertyEditor(densityObject.getMassProperty()));
        grid.addChild(new PropertyEditor(densityObject.getVolumeProperty()));
        grid.addChild(new DensityEditor(densityObject.getDensityProperty()));

        comboBox = new ComboBox();
        comboBox.dataProvider = ["Styrofoam","Water Balloon","Lead","Custom"];
        function myListener():void{
            densityObject.setDensity(Substance.OBJECT_SUBSTANCES[comboBox.selectedItem]);
        }
        comboBox.addEventListener("change",myListener);
        addChild(comboBox);

        addChild(grid);
    }

}
}