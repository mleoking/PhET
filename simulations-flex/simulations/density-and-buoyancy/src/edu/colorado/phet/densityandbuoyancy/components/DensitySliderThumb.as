package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;

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
        graphics.moveTo( 0, 0 );
        graphics.lineTo( +10, 10 );
        graphics.lineTo( -10, 10 );
        graphics.endFill();

        var dataTip: DensitySliderDataTip = new DensitySliderDataTip();
        addChild( dataTip );
        trace( "init.parent = " + parent );

        DensityModel.frameListeners.push( function(): void {
            var p: PropertyEditor = getPropertyEditor( parent );
            dataTip.setDensity( p.property.value, p.unit );
            dataTip.x = -dataTip.width / 2 - 5;
            dataTip.y = 10 + dataTip.height / 2;
        } );
    }

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