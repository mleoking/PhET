package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.Material;

import flash.display.Sprite;

import mx.controls.HSlider;
import mx.controls.sliderClasses.SliderThumb;
import mx.core.UIComponent;

public class SliderDecorator extends UIComponent {
    private var slider:HSlider;

    private var tickMarkSet:Sprite;
    private var ticks:Array = new Array();
    private const sliderY:Number = 15;
    private const tickHeight:Number = 4;

    public function SliderDecorator() {
        super();

        slider = new HSlider();
        slider.y = sliderY;
        addChild(slider);

        tickMarkSet = new Sprite();
        slider.addChild(tickMarkSet);

        updateTicks();
        this.height = slider.height + 20;
    }

    override public function set enabled(value:Boolean):void {
        super.enabled = value;
        if (slider != null)
            slider.enabled = value;
        if (value)
            alpha = 1.0;
        else
            alpha = 0.38;
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
        tickMarkSet.graphics.moveTo(modelToView(tick.value), -sliderY + 7);
        tickMarkSet.graphics.lineTo(modelToView(tick.value), -sliderY + tickHeight + 7);
        tick.textField.x = modelToView(tick.value) - tick.textField.textWidth / 2;
        tick.textField.y = -tick.textField.textHeight / 2 - 1;
        //TODO: Remove workaround and respect il8n
        //Temporary workaround to remove collision between styrofoam and wood
        if (tick.label == Material.STYROFOAM.name) {
            //            tick.textField.x = modelToView(tick.value) - tick.textField.textWidth;
            tick.textField.visible = false;
        }
    }

    public function set sliderThumbClass(sliderThumbClass:Class):void {
        slider.sliderThumbClass = sliderThumbClass;
    }

    public function set minimum(minimum:Number):void {
        slider.minimum = minimum;
    }

    public function set maximum(maximum:Number):void {
        slider.maximum = maximum;
    }

    public function set liveDragging(liveDragging:Boolean):void {
        slider.liveDragging = liveDragging;
    }

    public function get maximum():Number {
        return slider.maximum;
    }

    public function get minimum():Number {
        return slider.minimum;
    }

    public function set value(value:Number):void {
        slider.value = value;
    }

    public function getThumbAt(i:int):SliderThumb {
        return slider.getThumbAt(i);
    }

    public function addTick(value:Number, color:uint = 0x000000, label:String = null):void {
        var tick:Tick = new Tick(value, color, label);
        ticks.push(tick);
        addChild(tick.textField);
        updateTicks();
    }

    public function set sliderWidth(sliderWidth:Number):void {
        slider.width = sliderWidth;
        this.width = sliderWidth;
        updateTicks();

    }

    public function addSliderEventListener(type:String, handler:Function):void {
        slider.addEventListener(type, handler);
    }
}
}