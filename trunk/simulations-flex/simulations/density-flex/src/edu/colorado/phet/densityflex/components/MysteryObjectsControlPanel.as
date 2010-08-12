package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.Substance;

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

            grid.addChild(toGridRow("Substance", "Density","underline"));
            for each (var substance:Substance in Substance.ALL) {
                grid.addChild(toGridRow(substance.name, substance.getDensity().toFixed(1),"none"));
            }

            const titleWindow:TitleWindow = new TitleWindow();
            titleWindow.title = "Densities of Various Substances";
            titleWindow.showCloseButton = true;
            titleWindow.width = 240;
            titleWindow.height = 180;
            titleWindow.addEventListener(CloseEvent.CLOSE, function():void {
                PopUpManager.removePopUp(titleWindow);
            });
            titleWindow.addChild(grid);

            PopUpManager.addPopUp(titleWindow, myparent.parent, true);
            PopUpManager.centerPopUp(titleWindow);
        });
        button.label = "Show Table";
        addChild(button);
    }

    private function toGridRow(_name:String, density:String,textDecoration:String):GridRow {
        const gridRow:GridRow = new GridRow();
        const name:Label = new Label();
        name.text = _name;
        name.setStyle("textDecoration",textDecoration);
        function toGridItem(component:UIComponent):GridItem {
            const gridItem:GridItem = new GridItem();
            gridItem.addChild(component);
            return gridItem;
        }

        gridRow.addChild(toGridItem(name));
        const value:Label = new Label();
        value.text = density;
        value.setStyle("textDecoration",textDecoration);
        gridRow.addChild(toGridItem(value));
        return gridRow;
    }
}
}