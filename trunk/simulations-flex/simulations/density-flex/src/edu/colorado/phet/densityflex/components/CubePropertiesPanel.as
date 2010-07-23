package edu.colorado.phet.densityflex.components {
import mx.controls.Button;

public class CubePropertiesPanel extends DensityObjectPropertiesPanel {
    public function CubePropertiesPanel() {
        super();
    }

    public override function initialize():void {
        super.initialize();
        const button:Button = new Button();
        button.label = "radius";
        column2.addChild(button);
    }
}
}