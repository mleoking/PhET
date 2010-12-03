package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

public class CustomObjectPropertiesPanelWrapper {
    public var customObjectPropertiesPanel: CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing: Boolean = false;
    private var canvas: AbstractDBCanvas;

    public function CustomObjectPropertiesPanelWrapper( block: DensityObject, canvas: AbstractDBCanvas, x: Number, y: Number ) {
        this.canvas = canvas;
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel( block, canvas.units, 200 );
        customObjectPropertiesPanel.x = x;
        customObjectPropertiesPanel.y = y;
    }

    public function teardown(): void {
        if ( customObjectPropertiesPanelShowing ) {
            canvas.container.removeChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = false;
        }
    }

    public function init(): void {
        if ( !customObjectPropertiesPanelShowing ) {
            canvas.container.addChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = true;
        }
    }

    public function get x(): Number {
        return customObjectPropertiesPanel.x;
    }

    public function get width(): Number {
        return customObjectPropertiesPanel.width;
    }
}
}