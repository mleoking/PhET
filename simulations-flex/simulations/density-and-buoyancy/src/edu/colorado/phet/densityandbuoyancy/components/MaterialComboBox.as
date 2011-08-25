/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: 8/25/11
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;

import mx.controls.ComboBox;
import mx.events.ListEvent;

/**
 * Combo box that allows the user to change material types.
 */
public class MaterialComboBox extends ComboBox {
    public function MaterialComboBox( densityObject: DensityAndBuoyancyObject ) {
        const items: Array = Material.SELECTABLE_MATERIALS;
        dataProvider = items;
        rowCount = items.length;//Ensures there are no scroll bars in the combo box, see http://www.actionscript.org/forums/showthread.php3?t=218435
        labelField = "name";//uses the "name" get property on Material to identify the name
        function updateBlockBasedOnComboBoxSelection(): void {
            trace( "selectedItem=" + selectedItem );
            densityObject.material = Material( selectedItem );
        }

        selectedItem = densityObject.material;
        addEventListener( ListEvent.CHANGE, updateBlockBasedOnComboBoxSelection );

        densityObject.addMaterialListener( function f(): void {
            if ( densityObject.material.isCustom() ) {
                //Only update the combo box if material is non custom, because combo box should remember the last non-custom selection.
            }
            else {
                selectedItem = densityObject.material;
            }
        } );
    }
}
}
