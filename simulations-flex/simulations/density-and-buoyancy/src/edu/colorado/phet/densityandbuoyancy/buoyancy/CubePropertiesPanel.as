package edu.colorado.phet.densityandbuoyancy.buoyancy {
import edu.colorado.phet.densityandbuoyancy.components.*;

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