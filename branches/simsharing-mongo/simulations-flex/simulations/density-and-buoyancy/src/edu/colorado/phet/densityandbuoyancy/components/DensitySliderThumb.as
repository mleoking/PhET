//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;

import flash.display.DisplayObjectContainer;

import mx.controls.sliderClasses.SliderThumb;

/**
 * The density slider thumb is smaller than the regular thumb to help users know it is just a readout and not a control.
 * @author Sam Reid
 */
public class DensitySliderThumb extends SliderThumb {
    public static const SIZE: Number = 10;

    public function DensitySliderThumb() {
        super();
        this.width = SIZE;
        this.height = SIZE;
        setStyle( "thumbSkin", null );//we'll draw it ourselves

        graphics.beginFill( 0xFF0000 );
        var offsetX: Number = 4;//This is necessary to make the red triangle match up exactly with the desired thumb location.
        graphics.moveTo( 0 + offsetX, 0 );
        graphics.lineTo( +10 + offsetX, 10 );
        graphics.lineTo( -10 + offsetX, 10 );
        graphics.endFill();

        var dataTip: DensitySliderDataTip = new DensitySliderDataTip();
        addChild( dataTip );
        trace( "init.parent = " + parent );

        //When the simulation updates, synchronize the location of the slider thumb
        DensityAndBuoyancyModel.frameSteppedListener.push( function(): void {
            var p: PropertyEditor = getPropertyEditor( parent );
            dataTip.setDensity( p.property.value, p.unit );

            //Magic numbers to make sure the data tip appears in the right location
            dataTip.x = -dataTip.width / 2 - 5 + offsetX;
            dataTip.y = 10 + dataTip.height / 2;
        } );
    }

    //Recursively search up a component hierarchy for the PropertyEditor parent.
    private function getPropertyEditor( p: DisplayObjectContainer ): PropertyEditor {
        if ( p is PropertyEditor ) {
            return PropertyEditor( p );
        }
        else {
            return getPropertyEditor( p.parent );
        }
    }
}
}