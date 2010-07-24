package edu.colorado.phet.densityflex.components {
import mx.containers.HBox;
import mx.containers.Panel;
import mx.containers.VBox;
import mx.controls.Label;

public class PropertiesPanel extends Panel {
    protected const column1:VBox = new VBox();
    protected const column2:VBox = new VBox();
    protected const column3:VBox = new VBox();

    public function PropertiesPanel() {
        super();
        title = "Properties";
    }

    public override function initialize():void {
        super.initialize();
        var hbox:HBox = new HBox();

        hbox.addChild(column1);
        hbox.addChild(column2);
        hbox.addChild(column3);

        const label:Label = new Label();
        label.text = "Volume:";
        column3.addChild(label);
        addChild(hbox);
    }
}
}