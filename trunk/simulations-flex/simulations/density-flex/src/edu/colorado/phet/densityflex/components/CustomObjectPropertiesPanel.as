package edu.colorado.phet.densityflex.components {
import mx.containers.Grid;
import mx.containers.Panel;

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
        grid.addChild(new PropertyEditor(densityObject.getDensity()));

        addChild(grid);
    }

}
}