package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Material;

import flash.display.Sprite;
import flash.text.TextField;

import mx.controls.HSlider;
import mx.controls.sliderClasses.SliderThumb;
import mx.core.UIComponent;

public class SliderDecorator extends UIComponent {
    private var slider: HSlider;

    private var tickMarkSet: Sprite;
    private var ticks: Array = new Array();
    private const sliderY: Number = 15;
    private const tickHeight: Number = 4;

    public function SliderDecorator( dataTipClamp: Function/*Number=>Number*/ ) {
        super();

        slider = new HSlider();
        slider.showDataTip = false;
        slider.setStyle( "dataTipOffset", 0 );//Without this fix, data tips appear very far from the tip of the slider thumb, see http://blog.flexexamples.com/2007/11/03/customizing-a-slider-controls-data-tip/

        //The default NumberFormatter on slider uses Rounding.NONE, so to make sure it is compatible with the
        //numberformatter required in the rest of the sim, use full precision on the dataTipPrecision,
        //then pass the number through our own numberformatter
        //Otherwise, the readouts often differ by 0.01
        slider.setStyle( "dataTipPrecision", 10 );
        function doFormat( s: String ): String {
            var number: Number = Number( s );
            number = dataTipClamp( number );
            return DensityConstants.format( number );
        }

        slider.dataTipFormatFunction = doFormat;

        slider.y = sliderY;
        addChild( slider );

        tickMarkSet = new Sprite();
        slider.addChild( tickMarkSet );

        updateTicks();
        this.height = slider.height + 20;

        enabled = true; // set the enabled style, and default to enabled
    }

    override public function set enabled( value: Boolean ): void {
        super.enabled = value;
        if ( slider != null ) {
            slider.enabled = value;
            // this modifies the appearance in the current "Halo" Theme. Flex 4 will have a different theme
            if ( value ) {
                slider.setStyle( "fillAlphas", [ 0.60, 0.40 ] );
                slider.setStyle( "fillColors", [ 0xAAFFAA, 0x00FF00] );
            }
            else {
                slider.setStyle( "fillAlphas", [ 0.40, 0.20 ] ); // it seems like these alpha values are ignored?
                slider.setStyle( "fillColors", [ 0x666666, 0x333333] );
            }
        }
        if ( value ) {
            alpha = 1.0;
        }
        else {
            alpha = 0.38;
        }
    }

    private function modelToView( x: Number ): Number {
        var modelRange: Number = slider.maximum - slider.minimum;
        var viewRange: Number = slider.width - 8 * 2;//Width of the track only

        return (x - slider.minimum) * viewRange / modelRange + 8;//note: can be off by a pixel sometimes, we are not sure why
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

    public function set value( value: Number ): void {
        slider.value = value;
    }

    public function get value(): Number {
        return slider.value;
    }

    public function getThumbAt( i: int ): SliderThumb {
        return slider.getThumbAt( i );
    }

    public function addTick( value: Number, color: uint = 0x000000, label: String = null ): void {
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
}
}