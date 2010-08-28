package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import mx.containers.Grid;
import mx.containers.HBox;
import mx.controls.ComboBox;
import mx.controls.Label;
import mx.events.ListEvent;

public class CustomObjectPropertiesPanel extends DensityVBox {
    private var grid:Grid = new Grid();
    private var densityObject:DensityObject;
    private var comboBox:ComboBox;

    public function CustomObjectPropertiesPanel(densityObject:DensityObject, units:Units) {
        super();
        this.densityObject = densityObject;

        //TODO: remove listeners from former density object
        this.densityObject = densityObject;

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

        grid.addChild(new PropertyEditor(densityObject.getMassProperty(), DensityConstants.MIN_MASS, DensityConstants.MAX_MASS, units.massUnit));
        grid.addChild(new PropertyEditor(densityObject.getVolumeProperty(), DensityConstants.MIN_VOLUME, DensityConstants.MAX_VOLUME, units.volumeUnit));
        grid.addChild(new DensityEditor(densityObject.getDensityProperty(), DensityConstants.MIN_DENSITY, DensityConstants.MAX_DENSITY, units.densityUnit, densityObject));

        comboBox = new ComboBox();
        comboBox.dataProvider = Material.SELECTABLE_MATERIALS.concat([Material.CUSTOM]);

        comboBox.labelField = "name";//uses the "name" get property on Material to identify the name
        function myListener():void {
            trace("comboBox.selectedItem=" + comboBox.selectedItem);
            if (comboBox.selectedItem.isCustom()) {
                if (!densityObject.getMaterial().isCustom()) {
                    densityObject.material = new Material(FlexSimStrings.get("customObject.custom", "Custom"), densityObject.getDensity(), true);
                }
            }
            else {
                densityObject.material = Material(comboBox.selectedItem);
            }
        }

        comboBox.selectedItem = densityObject.getMaterial();

        comboBox.addEventListener(ListEvent.CHANGE, myListener);
        densityObject.addMaterialListener(function f():void {
            if (densityObject.getMaterial().isCustom()) {
                comboBox.selectedItem = Material.CUSTOM;
            }
            else {
                comboBox.selectedItem = densityObject.getMaterial();
            }
        });

        var label:Label = new Label();
        label.text = FlexSimStrings.get("customObject.material", "Material");
        label.setStyle(DensityConstants.FLEX_FONT_WEIGHT, DensityConstants.FLEX_FONT_BOLD);

        var comboBoxPanel:HBox = new HBox();
        comboBoxPanel.addChild(label);
        comboBoxPanel.addChild(comboBox);
        addChild(comboBoxPanel);

        addChild(grid);
    }

}
}