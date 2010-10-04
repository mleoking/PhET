package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import mx.containers.Grid;
import mx.containers.HBox;
import mx.controls.ComboBox;
import mx.controls.Label;
import mx.events.ListEvent;

public class CustomObjectPropertiesPanel extends DensityVBox {
    private var grid: Grid = new Grid();
    private var densityObject: DensityObject;
    private var comboBox: ComboBox;

    public function CustomObjectPropertiesPanel( densityObject: DensityObject, units: Units ) {
        super();
        this.densityObject = densityObject;

        function volumeListener(): void {
            densityObject.setVolume( densityObject.getVolume() );
        }

        densityObject.getVolumeProperty().addListener( volumeListener );
        function densityListener(): void {
            densityObject.setDensity( densityObject.getDensity() );
        }

        densityObject.getDensityProperty().addListener( densityListener );

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

        grid.addChild( new PropertyEditor( densityObject.getMassProperty(), DensityConstants.MIN_MASS, DensityConstants.MAX_MASS, units.massUnit, clampMass, new MassBounds( densityObject ) ) );
        grid.addChild( new PropertyEditor( densityObject.getVolumeProperty(), DensityConstants.MIN_VOLUME, DensityConstants.MAX_VOLUME, units.volumeUnit, noClamp, new Unbounded() ) );
        grid.addChild( new DensityEditor( densityObject.getDensityProperty(), DensityConstants.MIN_DENSITY, DensityConstants.MAX_DENSITY, units.densityUnit, noClamp, new Unbounded() ) );

        comboBox = new ComboBox();
        const items: Array = Material.SELECTABLE_MATERIALS.concat( [Material.CUSTOM] );
        comboBox.dataProvider = items;
        comboBox.rowCount = items.length;//Ensures there are no scroll bars in the combo box, see http://www.actionscript.org/forums/showthread.php3?t=218435

        comboBox.labelField = "name";//uses the "name" get property on Material to identify the name
        function listChangeListener(): void {
            trace( "comboBox.selectedItem=" + comboBox.selectedItem );
            if ( comboBox.selectedItem.isCustom() ) {
                if ( !densityObject.material.isCustom() ) {
                    // TODO: is customObject.cusom currently used? there is material.custom!
                    densityObject.material = new Material( FlexSimStrings.get( "customObject.custom", "My Object" ), densityObject.getDensity(), true );
                }
            }
            else {
                densityObject.material = Material( comboBox.selectedItem );
            }
        }

        comboBox.selectedItem = densityObject.material;

        comboBox.addEventListener( ListEvent.CHANGE, listChangeListener );
        densityObject.addMaterialListener( function f(): void {
            if ( densityObject.material.isCustom() ) {
                comboBox.selectedItem = Material.CUSTOM;
            }
            else {
                comboBox.selectedItem = densityObject.material;
            }
        } );

        var label: Label = new Label();
        label.text = FlexSimStrings.get( "customObject.material", "Material" );
        label.setStyle( DensityConstants.FLEX_FONT_WEIGHT, DensityConstants.FLEX_FONT_BOLD );

        var comboBoxPanel: HBox = new HBox();
        comboBoxPanel.addChild( label );
        comboBoxPanel.addChild( comboBox );
        addChild( comboBoxPanel );

        addChild( grid );
    }

}
}