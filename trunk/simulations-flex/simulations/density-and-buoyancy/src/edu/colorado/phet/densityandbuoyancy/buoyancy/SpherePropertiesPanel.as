package edu.colorado.phet.densityandbuoyancy.buoyancy {
import mx.controls.Button;

public class SpherePropertiesPanel extends DensityObjectPropertiesPanel {
    public function SpherePropertiesPanel() {
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