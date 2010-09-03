package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.LinearUnit;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.FlexSimStrings;

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

public class MysteryObjectsControlPanel extends DensityVBox {
    private var firstTime:Boolean = true;
    private var titleWindow:TitleWindow;
    private var myparent:MysteryObjectsControlPanel;
    private const showTableButton:Button = new Button();
    private const hideTableButton:Button = new Button();

    public function MysteryObjectsControlPanel() {
        super();
        myparent = this;

        const grid:Grid = new Grid();

        grid.addChild(toGridRow(FlexSimStrings.get("mysteryObject.material", "Material"), FlexSimStrings.get("mysteryObject.density", "Density (kg/L)"), DensityConstants.FLEX_UNDERLINE));
        for each (var material:Material in Material.ALL) {
            const unit:Unit = new LinearUnit(FlexSimStrings.get("mysteryObject.densityUnits", "kg/L"), 0.001);
            grid.addChild(toGridRow(material.name, unit.fromSI(material.getDensity()).toFixed(DensityConstants.NUMBER_OF_DECIMAL_PLACES), DensityConstants.FLEX_NONE));
        }

        titleWindow = new TitleWindow();
        titleWindow.title = FlexSimStrings.get("mysteryObject.table.title", "Densities of Various Materials");
        titleWindow.setStyle(DensityConstants.FLEX_FONT_SIZE, 18);
        titleWindow.setStyle(DensityConstants.FLEX_FONT_WEIGHT, DensityConstants.FLEX_FONT_BOLD);
        titleWindow.showCloseButton = true;
        titleWindow.width = 400;
        titleWindow.height = 400;
        titleWindow.addEventListener(CloseEvent.CLOSE, function():void {
            setTableVisible(false);
        });
        titleWindow.addChild(grid);

        showTableButton.addEventListener(MouseEvent.CLICK, function():void {
            setTableVisible(true)
        });
        showTableButton.label = FlexSimStrings.get("mysteryObject.table.showTable", "Show Table");
        addChild(showTableButton);

        hideTableButton.label = FlexSimStrings.get("mysteryObject.table.hideTable", "Hide Table");
        hideTableButton.addEventListener(MouseEvent.CLICK, function():void {
            setTableVisible(false)
        });
        x = DensityConstants.CONTROL_INSET;
        y = DensityConstants.CONTROL_INSET;
    }

    private function setTableVisible(b:Boolean):void {
        if (b) {
            PopUpManager.addPopUp(titleWindow, myparent.parent, false);
            //Remember the dialog location in case the user wants to toggle it on and off in a specific (nondefault) location
            if (firstTime) {
                PopUpManager.centerPopUp(titleWindow);
                firstTime = false;
            }
            addChild(hideTableButton);
            removeChild(showTableButton);
        }
        else {
            PopUpManager.removePopUp(titleWindow);
            addChild(showTableButton);
            removeChild(hideTableButton);
        }
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