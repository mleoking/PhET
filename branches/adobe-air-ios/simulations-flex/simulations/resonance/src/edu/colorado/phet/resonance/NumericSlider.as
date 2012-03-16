package edu.colorado.phet.resonance {
import edu.colorado.phet.flexcommon.model.NumericProperty;

import flash.events.Event;

import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.HSlider;
import mx.controls.Label;
import mx.controls.TextInput;


public class NumericSlider extends VBox {

private var slider:HSlider;
    public function NumericSlider( property:NumericProperty ) {
        super();

        var hbox: HBox = new HBox();
        addChild( hbox );

        var nameLabel: PhetText = new PhetText( property.name );
        hbox.addChild( nameLabel );

        var textField:TextInput = new TextInput();
        textField.width = 50;
        hbox.addChild(textField);

        var unitsLabel: PhetText = new PhetText( property.units );
        hbox.addChild( unitsLabel );

        this.slider = new HSlider();
        this.formatSlider(this.slider);
        this.addChild(this.slider);
        slider.value = property.value;

        property.addListener( function():void{
            slider.value = property.value;
            textField.text = new String( property.value );
        });

        slider.addEventListener( Event.CHANGE, function():void{
            property.value = slider.value;
        } );
    }


    private function formatSlider(slider:HSlider):void{
        slider.buttonMode = true;
        slider.liveDragging = true;
        slider.percentWidth = 100;
        slider.showDataTip = false;
        //mySlider.setStyle( "labelOffset", 25 );
        slider.setStyle( "invertThumbDirection", true );
        //setStyle( "dataTipOffset", -50 );  //this does not work.  Why not?
        //slider.setStyle( "fontFamily", "Arial" );

    }
}
}