package edu.colorado.phet.densityflex.components {
import mx.containers.Grid;
import mx.containers.Panel;
import mx.controls.ComboBox;

public class CustomObjectPropertiesPanel extends Panel {
    private var grid:Grid = new Grid();
    private var densityObject:IDensityObject;

    public function CustomObjectPropertiesPanel(densityObject:IDensityObject) {
        super();
        this.title = "Properties";
        this.densityObject=densityObject;
    }

    public override function initialize():void {
        super.initialize();

        grid.addChild(new PropertyEditor(densityObject.getMass()));
        grid.addChild(new PropertyEditor(densityObject.getVolume()));
        grid.addChild(new DensityEditor(densityObject.getDensity()));

        addChild(grid);
        const comboBox:ComboBox = new ComboBox();
        var values:Array = new Array();
        values.push("Syrofoam");
        values.push("Water");
        values.push("Lead");
        values.push("Custom");
        comboBox.dataProvider = values;
        addChild(comboBox);
    }

}
}