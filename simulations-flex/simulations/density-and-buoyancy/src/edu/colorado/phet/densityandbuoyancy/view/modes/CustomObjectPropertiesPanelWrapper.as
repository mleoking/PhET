package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

public class CustomObjectPropertiesPanelWrapper {
    private var customObjectPropertiesPanel: CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing: Boolean = false;
    private var canvas: AbstractDBCanvas;

    public function CustomObjectPropertiesPanelWrapper( block: DensityObject, canvas: AbstractDBCanvas, x: Number, y: Number ) {
        this.canvas = canvas;
        customObjectPropertiesPanel = new CustomObjectPropertiesPanel( block, canvas.units );
        customObjectPropertiesPanel.x = x;
        customObjectPropertiesPanel.y = y;
    }

    function teardown(): void {
        if ( customObjectPropertiesPanelShowing ) {
            canvas.container.removeChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = false;
        }
    }

    function init(): void {
        if ( !customObjectPropertiesPanelShowing ) {
            canvas.container.addChild( customObjectPropertiesPanel );
            customObjectPropertiesPanelShowing = true;
        }
    }

    public function get x(): Number {return customObjectPropertiesPanel.x;}

    function get width(): Number {return customObjectPropertiesPanel.width;}
}
}