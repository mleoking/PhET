//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.components.CustomObjectPropertiesPanel;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyObject;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

/**
 * Control for customizing the properties of the buoyancy objects in BuoyancyPlayground mode.
 */
public class CustomObjectPropertiesPanelWrapper {
    public var customObjectPropertiesPanel: CustomObjectPropertiesPanel;
    private var customObjectPropertiesPanelShowing: Boolean = false;
    private var canvas: AbstractDBCanvas;

    public function CustomObjectPropertiesPanelWrapper( block: DensityAndBuoyancyObject, canvas: AbstractDBCanvas, x: Number, y: Number ) {
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

    public function reset(): void {
        customObjectPropertiesPanel.reset();
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