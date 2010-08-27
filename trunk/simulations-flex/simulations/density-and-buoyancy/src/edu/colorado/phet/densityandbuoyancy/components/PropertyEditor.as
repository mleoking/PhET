package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

import flash.display.DisplayObject;

import flash.display.Stage;
import flash.events.Event;

import flash.events.FocusEvent;
import flash.events.TextEvent;

import mx.containers.GridItem;
import mx.containers.GridRow;
import mx.controls.HSlider;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.events.FlexEvent;
import mx.events.SliderEvent;

public class PropertyEditor extends GridRow {
    private var property:NumericProperty;
    public static const SLIDER_WIDTH:Number = 250;
    private static const FONT_SIZE:Number = 12;

    public function PropertyEditor(property:NumericProperty, minimimum:Number, maximum:Number, unit:Unit) {
        super();
        this.property = property;

        var label:Label = new Label();
        label.text = property.name;
        label.setStyle(DensityConstants.FLEX_FONT_SIZE, FONT_SIZE);
        label.setStyle(DensityConstants.FLEX_FONT_WEIGHT, DensityConstants.FLEX_FONT_BOLD);
        addGridItem(label);

        addGridItem(createSlider(property, minimimum, maximum, unit));

        const textField:TextInput = new TextInput();
        textField.setStyle(DensityConstants.FLEX_FONT_SIZE, FONT_SIZE);
        textField.width = 50;//TODO: this is a magic number that just barely fits "00.00" with the above specified font
        textField.restrict = ".0-9";//TODO: does this handle languages that use a comma instead of a decimal place?
        function updateText():void {
            textField.text = unit.fromSI(property.value).toFixed(2);//TODO: make sure this doesn't fire an event
        }

        function updateModelFromTextField():void{
            property.value = unit.toSI(Number(textField.text));
        }
        
        updateText();
        const listener:Function = function myfunction():void {
            if ( focusManager != null ) { //Have to do a null check because it can be null if the component is not in the scene graph?
                if ( focusManager.getFocus() == textField ) {//Only update the model if the user is editing the text field, otherwise there are loops that cause errant behavior
                    updateModelFromTextField();
                }
            }
        };
        
        textField.addEventListener(FocusEvent.FOCUS_OUT,updateModelFromTextField);
        
        textField.addEventListener(FlexEvent.VALUE_COMMIT, listener);
        textField.addEventListener(FlexEvent.ENTER, listener);
        property.addListener(updateText);
        addGridItem(textField);

        var unitsLabel:Label = new Label();
        unitsLabel.setStyle(DensityConstants.FLEX_FONT_SIZE,FONT_SIZE);
        unitsLabel.text = unit.name;
        addGridItem(unitsLabel);
    }

    protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit):HSlider {
        const slider:HSlider = new HSlider();
        slider.sliderThumbClass=MySliderThumb;
        slider.width = SLIDER_WIDTH;
        slider.minimum = unit.fromSI(minimum);
        slider.maximum = unit.fromSI(maximum);
        slider.liveDragging = true;
        slider.thumbCount = 1;
        function sliderDragHandler(event:SliderEvent):void {
            property.value = unit.toSI(event.value);
        }

        slider.addEventListener(SliderEvent.THUMB_DRAG, sliderDragHandler);
        function updateSlider():void {
            const setValue:Number = unit.fromSI(property.value);
            slider.value = setValue;
            try {
                slider.getThumbAt(0).alpha = Math.max(0.25, //This is the minimum alpha that will be shown.  Beyond 0.25 is too hard to see anything.
                        Math.min(1, slider.maximum / setValue) //The more the value goes above the slider's maximum, make more transparent.  But keep alpha =1 if it is in the slider range.
                        );
            } catch(exception:Error) {

            }
        }

        updateSlider();
        property.addListener(updateSlider);
        return slider;
    }

    private function addGridItem(displayObject:DisplayObject):void {
        const item:GridItem = new GridItem();
        item.addChild(displayObject);
        addChild(item);
    }
}
}