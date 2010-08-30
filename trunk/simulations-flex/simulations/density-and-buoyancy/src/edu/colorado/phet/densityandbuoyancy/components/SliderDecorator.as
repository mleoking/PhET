package edu.colorado.phet.densityandbuoyancy.components {
import flash.display.Sprite;

import mx.controls.HSlider;
import mx.controls.sliderClasses.SliderThumb;
import mx.core.UIComponent;

public class SliderDecorator extends UIComponent {
    var slider:HSlider;

    private var tickMarkSet:Sprite;
    private var ticks:Array = new Array();

    public function SliderDecorator() {
        super();

        slider = new HSlider();
        slider.y = 10;
        addChild(slider);

        tickMarkSet = new Sprite();
        slider.addChild(tickMarkSet);

        updateTicks();
    }

    private function modelToView(x:Number):Number {
        var modelRange:Number = slider.maximum - slider.minimum;
        var viewRange:Number = slider.width - 8 * 2;//Width of the track only

        return (x - slider.minimum) * viewRange / modelRange + 8;//note: can be off by a pixel sometimes, we are not sure why
    }

    private function updateTicks():void {
        tickMarkSet.graphics.clear();
        for each (var tick:Tick in ticks) {
            drawTick(tick);
        }
    }

    private function drawTick(tick:Tick):void {
        tickMarkSet.graphics.lineStyle(2, tick.color);
        tickMarkSet.graphics.moveTo(modelToView(tick.value), -10);
        tickMarkSet.graphics.lineTo(modelToView(tick.value), -4);
    }

    function set sliderThumbClass(sliderThumbClass:Class):void {
        slider.sliderThumbClass = sliderThumbClass;
    }

    function set minimum(minimum:Number):void {
        slider.minimum = minimum;
    }

    function set maximum(maximum:Number):void {
        slider.maximum = maximum;
    }

    function set liveDragging(liveDragging:Boolean):void {
        slider.liveDragging = liveDragging;
    }

    function get maximum():Number {
        return slider.maximum;
    }

    function get minimum():Number {
        return slider.minimum;
    }

    function set value(value:Number):void {
        slider.value = value;
    }

    function getThumbAt(i:int):SliderThumb {
        return slider.getThumbAt(i);
    }

    function addTick(value:Number, color:uint = 0x000000, label:String = null):void {
        ticks.push(new Tick(value, color, label));
        updateTicks();
    }

    function set sliderWidth(sliderWidth:Number):void {
        slider.width = sliderWidth;
        this.width = sliderWidth;
        updateTicks();

    }
}
}