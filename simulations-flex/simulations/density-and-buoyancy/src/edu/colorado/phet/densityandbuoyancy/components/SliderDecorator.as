//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.Material;

import flash.display.Sprite;
import flash.text.TextField;

import mx.controls.HSlider;
import mx.controls.sliderClasses.SliderThumb;
import mx.core.UIComponent;

/**
 * Slider control that adds tick marks, a data tip, a thumb offset, different rending for enabled/disabled.
 */
public class SliderDecorator extends UIComponent {
    private var slider: MyHSlider;

    private var tickMarkSet: Sprite;
    private var ticks: Array = new Array();
    private var sliderY: Number = 8;
    private const tickHeight: Number = 4;
    private var tickMarksEnabled: Boolean = false;
    public var isFluidDensitySlider: Boolean = false;

    public function SliderDecorator( dataTipClamp: Function/*Number=>Number*/, thumbOffset: Number ) {
        super();

        slider = new MyHSlider();
        slider.showDataTip = false;
        slider.setStyle( "thumbOffset", thumbOffset );

        slider.y = sliderY;
        addChild( slider );

        tickMarkSet = new Sprite();
        slider.addChild( tickMarkSet );

        updateTicks();
        this.height = slider.height + 20;

        enabled = true; // set the enabled style, and default to enabled
        slider.doCommitProperties();//fixes the problem that getSliderThumbCount = 1, but getSliderThumb(0) throws a nullpointerexception
    }

    public function enableTickmarks(): void {
        sliderY = 15;
        slider.y = sliderY;
        tickMarksEnabled = true;
        updateTicks();
    }

    override public function set enabled( enabled: Boolean ): void {
        super.enabled = enabled;
        if ( slider != null ) {
            slider.enabled = enabled;
            // this modifies the appearance in the current "Halo" Theme. Flex 4 will have a different theme
            if ( enabled ) {
                slider.setStyle( "fillAlphas", [ 0.60, 0.40 ] );
                slider.setStyle( "fillColors", [ 0xAAFFAA, 0x00FF00] );
            }
            else {
                slider.setStyle( "fillAlphas", [ 0.40, 0.20 ] ); // it seems like these alpha values are ignored?
                slider.setStyle( "fillColors", [ 0x666666, 0x333333] );
            }
            //TODO: should we gray out the slider?
            if ( enabled ) {
                slider.alpha = 1.0;
            }
            else {
                slider.setStyle( "trackSkin", MyTrackSkin );
            }
        }
    }

    private function modelToView( x: Number ): Number {
        // TODO: this part is what was giving us problems. temporary values added, but this should all be rewritten
//        return slider.mx_internal::getXFromValue(x);

        var modelRange: Number = slider.maximum - slider.minimum;
        var viewRange: Number = slider.width - (isFluidDensitySlider ? 16 : 10);//Width of the track only
        // working well for regular tick 12x6
        return (x - slider.minimum) * viewRange / modelRange + (isFluidDensitySlider ? 8 : 4);//note: can be off by a pixel sometimes, we are not sure why
    }

    private function updateTicks(): void {
        tickMarkSet.graphics.clear();
        for each ( var tick: Tick in ticks ) {
            drawTick( tick );
        }
        var tickPadding: Number = 5;
        var ok: Boolean = false;
        while ( !ok ) {
            ok = true;
            // loop through all pairs (order wasn't guaranteed)
            for ( var ix: * in ticks ) {
                var idx: Number = new Number( ix );
                for ( var jx: * in ticks ) {
                    var jdx: Number = new Number( jx );
                    if ( jdx != idx ) {
                        var i: Number, j: Number;

                        // make sure i.density < j.density
                        if ( ix < jx ) {
                            i = ix;
                            j = jx;
                        }
                        else {
                            i = jx;
                            j = ix;
                        }

                        var iText: TextField = ticks[i].textField;
                        var jText: TextField = ticks[j].textField;

                        // skip moving if the left one is styrofoam
                        if ( (ticks[i] as Tick).label == Material.STYROFOAM.name ) {
                            continue;
                        }
                        if ( jText.x < iText.x + iText.textWidth + tickPadding ) {
                            ok = false;
                            iText.x -= 1;
                            jText.x += 1;
                        }
                    }
                }
            }
        }
        for each( tick in ticks ) {
            drawTickLines( tick );
        }
    }

    private function drawTickLines( tick: Tick ): void {
        tickMarkSet.graphics.lineStyle( 1, tick.color );
        tickMarkSet.graphics.moveTo( modelToView( tick.value ), -sliderY + tickHeight + 7 );
        tickMarkSet.graphics.lineTo( modelToView( tick.value ), -sliderY + 7 );
        var topX: Number = modelToView( tick.value );
        var leftBound: Number = tick.textField.x + 3;
        var rightBound: Number = tick.textField.x + tick.textField.textWidth;
        if ( topX < leftBound ) {
            topX = leftBound;
        }
        if ( topX > rightBound ) {
            topX = rightBound;
        }
        tickMarkSet.graphics.lineTo( topX, -sliderY + 5 );
    }

    private function drawTick( tick: Tick ): void {
        tick.textField.x = modelToView( tick.value ) - tick.textField.textWidth / 2;
        tick.textField.y = -tick.textField.textHeight / 2 - 3;
        //TODO: Remove workaround and respect il8n
        //Temporary workaround to remove collision between styrofoam and wood
        if ( tick.label == Material.STYROFOAM.name ) {
            //            tick.textField.x = modelToView(tick.value) - tick.textField.textWidth;
            tick.textField.visible = false;
        }
    }

    public function set sliderThumbClass( sliderThumbClass: Class ): void {
        slider.sliderThumbClass = sliderThumbClass;
    }

    public function set minimum( minimum: Number ): void {
        slider.minimum = minimum;
    }

    public function set maximum( maximum: Number ): void {
        slider.maximum = maximum;
    }

    public function set liveDragging( liveDragging: Boolean ): void {
        slider.liveDragging = liveDragging;
    }

    public function get maximum(): Number {
        return slider.maximum;
    }

    public function get minimum(): Number {
        return slider.minimum;
    }


    public const changeListeners: Array = new Array();

    public function set value( value: Number ): void {
        slider.value = value;
        for each ( var listener: Function in changeListeners ) {
            listener();
        }
    }

    public function get value(): Number {
        return slider.value;
    }

    public function getThumbAt( i: int ): SliderThumb {
        return slider.getThumbAt( i );
    }

    public function getThumbCount(): Number {
        return slider.thumbCount;
    }

    public function addTick( value: Number, color: uint = 0x000000, label: String = null ): void {
        if ( !tickMarksEnabled ) {
            throw new Error( "tickMarksEnabled == false" );
        }
        var tick: Tick = new Tick( value, color, label );
        ticks.push( tick );
        addChild( tick.textField );
        updateTicks();
    }

    public function set sliderWidth( sliderWidth: Number ): void {
        slider.width = sliderWidth;
        this.width = sliderWidth;
        updateTicks();

    }

    public function addSliderEventListener( type: String, handler: Function ): void {
        slider.addEventListener( type, handler );
    }

    public function set sliderDataTipClass( sliderDataTipClass: Class ): void {
        slider.sliderDataTipClass = sliderDataTipClass;
    }

    public function get myslider(): HSlider {
        return slider;
    }
}
}