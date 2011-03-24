package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.Not;
import edu.colorado.phet.densityandbuoyancy.view.BlockLabelNode;
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

        //TODO: See related workaround PropertyEditor
        function clampMass( mass: Number ): Number {
            //TODO: factor out this duplicated code
            if ( densityObject.material.equals( Material.STYROFOAM ) && mass > DensityConstants.STYROFOAM_MAX_MASS ) {
                return DensityConstants.STYROFOAM_MAX_MASS;
            }
            else {
                if ( densityObject.material.equals( Material.WOOD ) && mass > DensityConstants.WOOD_MAX_MASS ) {
                    return DensityConstants.WOOD_MAX_MASS;
                }
                else {
                    return mass;
                }
            }
        }

        grid.addChild( new PropertyEditor( densityObject.getMassProperty(), DensityConstants.MIN_MASS, DensityConstants.MAX_MASS, units.massUnit, clampMass, new MassBounds( densityObject ), sliderWidth ) );
        grid.addChild( new PropertyEditor( densityObject.getVolumeProperty(), DensityConstants.MIN_VOLUME, DensityConstants.MAX_VOLUME, units.volumeUnit, noClamp, new Unbounded(), sliderWidth ) );
        grid.addChild( createSpacerRow( 2 ) );
        grid.addChild( new DensityEditor( densityObject.getDensityProperty(), DensityConstants.MIN_DENSITY, DensityConstants.MAX_DENSITY, units.densityUnit, noClamp, new Unbounded(), sliderWidth ) );
        // this would make the background cover the data-tip
        //grid.addChild( createSpacerRow( 26 ) );

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

        const radioButtonPanel: HBox = new HBox();
        const myBlockName: String = FlexSimStrings.get( "customObject.custom", "My Block" );

        radioButtonPanel.addChild( new MyRadioButton( myBlockName, myBlockSelected ) );
        radioButtonPanel.addChild( new MyRadioButton( FlexSimStrings.get( "customObject.material", "Material" ), new Not( myBlockSelected ) ) );

        myBlockSelected.addListener( function(): void {
            if ( myBlockSelected.value ) {
                if ( !densityObject.material.isCustom() ) {
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

        var sprite: Sprite = new BlockLabelNode( densityObject.name, densityObject.nameVisibleProperty );
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