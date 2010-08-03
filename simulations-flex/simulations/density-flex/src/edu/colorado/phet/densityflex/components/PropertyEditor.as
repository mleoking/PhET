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
    public function PropertyEditor(property:NumericProperty) {
        super();
        this.property=property;
        const slider:HSlider = new HSlider();
        slider.liveDragging = true;
        slider.addEventListener(SliderEvent.CHANGE, sliderDragHandler);
        function updateSlider():void{slider.value=property.value;}
        updateSlider();
        property.addListener(updateSlider);
        addGridItem(slider);
        
        var label:Label = new Label();
        label.text = property.name;
        addGridItem(label);

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

    private function sliderDragHandler(event:SliderEvent):void {
        property.value = event.value;
    }


    private function addGridItem(displayObject:DisplayObject):void {
        const item = new GridItem();
        item.addChild(displayObject);
        addChild(item);
    }
}
}