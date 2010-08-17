package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Substance;
import edu.colorado.phet.densityandbuoyancy.view.units.LinearUnit;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.MouseEvent;

import mx.containers.Grid;
import mx.containers.GridItem;
import mx.containers.GridRow;
import mx.containers.Panel;
import mx.containers.TitleWindow;
import mx.controls.Button;
import mx.controls.Label;
import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.managers.PopUpManager;

public class MysteryObjectsControlPanel extends Panel {

    public function MysteryObjectsControlPanel() {
        super();
        const button:Button = new Button();
        const myparent:Panel = this;

        button.addEventListener(MouseEvent.CLICK, function():void {

            const grid:Grid = new Grid();

            grid.addChild(toGridRow(FlexSimStrings.get("mysteryObject.substance", "Substance"), FlexSimStrings.get("mysteryObject.density", "Density (kg/L)"), DensityConstants.FLEX_UNDERLINE));
            for each (var substance:Substance in Substance.ALL) {
                const unit:Unit = new LinearUnit(FlexSimStrings.get("mysteryObject.densityUnits", "kg/L"), 0.001);
                grid.addChild(toGridRow(substance.name, unit.fromSI(substance.getDensity()).toFixed(2), DensityConstants.FLEX_NONE));
            }

            const titleWindow:TitleWindow = new TitleWindow();
            titleWindow.title = FlexSimStrings.get("mysteryObject.table.title", "Densities of Various Substances");
            titleWindow.setStyle(DensityConstants.FLEX_FONT_SIZE, 18);
            titleWindow.setStyle(DensityConstants.FLEX_FONT_WEIGHT, DensityConstants.FLEX_FONT_BOLD);
            titleWindow.showCloseButton = true;
            titleWindow.width = 400;
            titleWindow.height = 400;
            titleWindow.addEventListener(CloseEvent.CLOSE, function():void {
                PopUpManager.removePopUp(titleWindow);
            });
            titleWindow.addChild(grid);

            PopUpManager.addPopUp(titleWindow, myparent.parent, true);
            PopUpManager.centerPopUp(titleWindow);
        });
        button.label = FlexSimStrings.get("mysteryObject.table.showTable", "Show Table");
        addChild(button);
    }

    private function toGridRow(_name:String, density:String, textDecoration:String):GridRow {
        const gridRow:GridRow = new GridRow();
        const fontSize:Number = 18;
        const name:Label = new Label();
        name.setStyle(DensityConstants.FLEX_FONT_SIZE, fontSize);
        name.text = _name;
        name.setStyle(DensityConstants.FLEX_TEXT_DECORATION, textDecoration);
        function toGridItem(component:UIComponent):GridItem {
            const gridItem:GridItem = new GridItem();
            gridItem.addChild(component);
            return gridItem;
        }

        gridRow.addChild(toGridItem(name));
        const value:Label = new Label();
        value.text = density;
        value.setStyle(DensityConstants.FLEX_FONT_SIZE, fontSize);
        value.setStyle(DensityConstants.FLEX_TEXT_DECORATION, textDecoration);
        gridRow.addChild(toGridItem(value));
        return gridRow;
    }
}
}