package edu.colorado.phet.densityflex.components {
import flash.display.DisplayObject;

import mx.containers.GridItem;
import mx.containers.GridRow;
import mx.controls.HSlider;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.events.SliderEvent;

public class PropertyEditor extends GridRow {
    private var property:NumericProperty;
    public static const SLIDER_WIDTH:Number=250;
    public function PropertyEditor(property:NumericProperty) {
        super();
        this.property=property;
        
        var label:Label = new Label();
        label.text = property.name;
        addGridItem(label);
        
        addGridItem(createSlider(property));
        
        const textField:TextInput = new TextInput();
        textField.width = 100;
        function updateText():void{textField.text=property.value.toFixed(2)}
        updateText();
        property.addListener(updateText);
        addGridItem(textField);

        var unitsLabel:Label = new Label();
        unitsLabel.text = property.units;
        addGridItem(unitsLabel);
    }

    protected function createSlider(property:NumericProperty):HSlider {
        const slider:HSlider = new HSlider();
        slider.width=SLIDER_WIDTH;
        slider.liveDragging = true;
        function sliderDragHandler(event:SliderEvent):void {
            property.value = event.value;
        }

        slider.addEventListener(SliderEvent.CHANGE, sliderDragHandler);
        function updateSlider():void {
            slider.value = property.value;
        }

        updateSlider();
        property.addListener(updateSlider);
        return slider;
    }

    private function addGridItem(displayObject:DisplayObject):void {
        const item = new GridItem();
//        item.colSpan=2;
        item.addChild(displayObject);
        addChild(item);
    }
}
}