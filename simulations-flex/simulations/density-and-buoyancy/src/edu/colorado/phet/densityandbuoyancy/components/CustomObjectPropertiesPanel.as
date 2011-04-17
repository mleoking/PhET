//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.Not;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.display.Sprite;

import mx.containers.Grid;
import mx.containers.GridItem;
import mx.containers.GridRow;
import mx.containers.HBox;
import mx.controls.ComboBox;
import mx.controls.Spacer;
import mx.core.UIComponent;
import mx.events.ListEvent;

/**
 * The CustomObjectPropertiesPanel shows controls for mass, volume, density and material of an object, and appears in:
 * -the Buoyancy Playground tab of Buoyancy
 * -the Custom Object mode of Density
 */
public class CustomObjectPropertiesPanel extends DensityVBox {
    private var grid: Grid = new Grid();
    private var densityObject: DensityObject;
    private var comboBox: ComboBox;
    public const myBlockSelected: BooleanProperty = new BooleanProperty( false );

    public function CustomObjectPropertiesPanel( densityObject: DensityObject, units: Units, sliderWidth: Number = 280 ) {
        super();
        this.densityObject = densityObject;

        function noClamp( n: Number ): Number {
            return n;
        }

        var massBounds = new MassBounds( densityObject );

        //Add the PropertyEditors for Mass, Volume and Density
        grid.addChild( new PropertyEditor( densityObject.getMassProperty(), DensityAndBuoyancyConstants.MIN_MASS, DensityAndBuoyancyConstants.MAX_MASS, units.massUnit, massBounds.clamp, new MassBounds( densityObject ), sliderWidth ) );
        grid.addChild( new PropertyEditor( densityObject.getVolumeProperty(), DensityAndBuoyancyConstants.MIN_VOLUME, DensityAndBuoyancyConstants.MAX_VOLUME, units.volumeUnit, noClamp, new Unbounded(), sliderWidth ) );
        grid.addChild( createSpacerRow( 2 ) );
        grid.addChild( new DensityEditor( densityObject.getDensityProperty(), DensityAndBuoyancyConstants.MIN_DENSITY, DensityAndBuoyancyConstants.MAX_DENSITY, units.densityUnit, noClamp, new Unbounded(), sliderWidth ) );

        //Configure and add the ComboBox
        comboBox = new ComboBox();
        const items: Array = Material.SELECTABLE_MATERIALS;
        comboBox.dataProvider = items;
        comboBox.rowCount = items.length;//Ensures there are no scroll bars in the combo box, see http://www.actionscript.org/forums/showthread.php3?t=218435
        comboBox.labelField = "name";//uses the "name" get property on Material to identify the name
        function updateBlockBasedOnComboBoxSelection(): void {
            trace( "comboBox.selectedItem=" + comboBox.selectedItem );
            densityObject.material = Material( comboBox.selectedItem );
        }

        comboBox.selectedItem = densityObject.material;
        comboBox.addEventListener( ListEvent.CHANGE, updateBlockBasedOnComboBoxSelection );
        densityObject.addMaterialListener( function f(): void {
            if ( densityObject.material.isCustom() ) {
                //Only update the combo box if material is non custom, because combo box should remember the last non-custom selection.
            }
            else {
                comboBox.selectedItem = densityObject.material;
            }
        } );

        //Set up the radio buttons for selecting different materials, e.g. "my block", "wood", etc.
        const radioButtonPanel: HBox = new HBox();
        const myBlockName: String = FlexSimStrings.get( "customObject.custom", "My Block" );
        radioButtonPanel.addChild( new PropertyRadioButton( myBlockName, myBlockSelected ) );
        radioButtonPanel.addChild( new PropertyRadioButton( FlexSimStrings.get( "customObject.material", "Material" ), new Not( myBlockSelected ) ) );
        myBlockSelected.addListener( function(): void {
            if ( myBlockSelected.value ) {
                if ( !densityObject.material.isCustom() ) {
                    //REVIEW has this been addressed?
                    // TODO: is customObject.custom currently used? there is material.custom!
                    densityObject.material = new Material( myBlockName, densityObject.density, true );
                }
            }
            else {
                updateBlockBasedOnComboBoxSelection();
            }
        } );
        addChild( radioButtonPanel );

        myBlockSelected.addListener( function(): void {
            comboBox.visible = !myBlockSelected.value;
        } );
        radioButtonPanel.addChild( comboBox );

        //Show the name of the block using the same code used in the play area
        var sprite: Sprite = new BlockLabel( densityObject.name, densityObject.nameVisibleProperty );
        var uiComponent: UIComponent = new UIComponent();
        uiComponent.width = sprite.width;
        uiComponent.addChild( sprite );
        var spacer: Spacer = new Spacer();
        spacer.percentWidth = 100;
        radioButtonPanel.percentWidth = 100;
        radioButtonPanel.addChild( spacer );
        radioButtonPanel.addChild( uiComponent );

        addChild( grid );
    }

    //REVIEW private?
    //Helper function to create a GridRow, to put some vertical spacing between mass,volume and the density readout.
    public function createSpacerRow( height: int ): GridRow {
        var spacerRow: GridRow = new GridRow();
        var spacerItem: GridItem = new GridItem();
        var spacerElement: Spacer = new Spacer();
        spacerElement.height = height;
        spacerItem.addChild( spacerElement );
        spacerRow.addChild( spacerItem );
        return spacerRow;
    }

    public function reset(): void {
        myBlockSelected.reset();
    }
}
}