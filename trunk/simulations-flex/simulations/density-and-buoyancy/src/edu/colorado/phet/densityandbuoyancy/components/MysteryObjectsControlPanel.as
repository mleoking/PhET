//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.LinearUnit;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Grid;
import mx.containers.GridItem;
import mx.containers.GridRow;
import mx.containers.TitleWindow;
import mx.controls.Button;
import mx.controls.Label;
import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.managers.PopUpManager;

/**
 * The MysteryObjectsControlPanel is used in the MysteryObjectsMode of the Density Simulation, allowing the user to show a table with various densities.
 * It could be refactored so that the table and parent window are a separate class.
 */
public class MysteryObjectsControlPanel extends DensityVBox {
    private var firstTime: Boolean = true;
    private var titleWindow: TitleWindow;
    private const tableButton: Button = new Button();
    private var titleWindowVisible: BooleanProperty = new BooleanProperty( false );

    public function MysteryObjectsControlPanel() {
        super();

        //Create the grid that shows the table of known substances and their densities
        const grid: Grid = new Grid();
        grid.addChild( toGridRow( FlexSimStrings.get( "mysteryObject.material", "Material" ),
                                  FlexSimStrings.get( "mysteryObject.densityColumnHeader", "Density ({0})", [FlexSimStrings.get( "mysteryObject.densityUnitsKgL", "kg/L" )] ),
                                  DensityAndBuoyancyConstants.FLEX_UNDERLINE ) );
        for each ( var material: Material in Material.ALL ) {
            const unit: Unit = new LinearUnit( FlexSimStrings.get( "mysteryObject.densityUnitsKgL", "kg/L" ), 0.001 );
            var density: Number = unit.fromSI( material.getDensity() );
            grid.addChild( toGridRow( material.name, DensityAndBuoyancyConstants.formatWithPrecision( density, density >= 10 ? 1 : 2 ), DensityAndBuoyancyConstants.FLEX_NONE ) );
        }

        //Create the window that shows the table of known substances and their densities
        titleWindow = new TitleWindow();
        titleWindow.title = FlexSimStrings.get( "mysteryObject.table.title", "Densities of Various Materials" );
        titleWindow.setStyle( DensityAndBuoyancyConstants.FLEX_FONT_SIZE, 18 );
        titleWindow.setStyle( DensityAndBuoyancyConstants.FLEX_FONT_WEIGHT, DensityAndBuoyancyConstants.FLEX_FONT_BOLD );
        titleWindow.showCloseButton = true;
        titleWindow.width = 400;
        titleWindow.height = 400;
        titleWindow.addEventListener( CloseEvent.CLOSE, function( evt: Event ): void {
            titleWindowVisible.value = false;
        } );
        titleWindow.addChild( grid );

        //Add the button that shows/hides the table
        tableButton.addEventListener( MouseEvent.CLICK, function( evt: MouseEvent ): void {
            titleWindowVisible.value = !titleWindowVisible.value;
        } );
        addChild( tableButton );

        x = DensityAndBuoyancyConstants.CONTROL_INSET;
        y = DensityAndBuoyancyConstants.CONTROL_INSET;

        //Handle visibility of the popup window
        var visibilityChangeListener: Function = function(): void {
            if ( titleWindowVisible.value ) {
                PopUpManager.addPopUp( titleWindow, parent, false );
                //Remember the dialog location in case the user wants to toggle it on and off in a specific (nondefault) location
                if ( firstTime ) {
                    PopUpManager.centerPopUp( titleWindow );
                    firstTime = false;
                }
            }
            else {
                PopUpManager.removePopUp( titleWindow );
            }
            if ( titleWindowVisible.value ) {
                // change to hide table
                tableButton.label = FlexSimStrings.get( "mysteryObject.table.hideTable", "Hide Table" );
                tableButton.setStyle( "fillColors", [0xFFFF00,0xFF0000] );
            }
            else {
                // change to show table
                tableButton.label = FlexSimStrings.get( "mysteryObject.table.showTable", "Show Table" );
                tableButton.setStyle( "fillColors", [0x00FFFF,0x00FF00] );
            }
        };
        titleWindowVisible.addListener( visibilityChangeListener );
        visibilityChangeListener();
    }

    public function teardown(): void {
        titleWindowVisible.value = false;
    }

    private function toGridRow( _name: String, density: String, textDecoration: String ): GridRow {
        const gridRow: GridRow = new GridRow();
        const fontSize: Number = 18;
        const name: Label = new Label();
        name.setStyle( DensityAndBuoyancyConstants.FLEX_FONT_SIZE, fontSize );
        name.text = _name;
        name.setStyle( DensityAndBuoyancyConstants.FLEX_TEXT_DECORATION, textDecoration );
        function toGridItem( component: UIComponent ): GridItem {
            const gridItem: GridItem = new GridItem();
            gridItem.addChild( component );
            return gridItem;
        }

        gridRow.addChild( toGridItem( name ) );
        const value: Label = new Label();
        value.text = density;
        value.setStyle( DensityAndBuoyancyConstants.FLEX_FONT_SIZE, fontSize );
        value.setStyle( DensityAndBuoyancyConstants.FLEX_TEXT_DECORATION, textDecoration );
        gridRow.addChild( toGridItem( value ) );
        return gridRow;
    }

}
}