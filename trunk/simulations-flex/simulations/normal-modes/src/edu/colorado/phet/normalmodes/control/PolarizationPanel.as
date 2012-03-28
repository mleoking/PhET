/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 7/14/11
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.normalmodes.NiceComponents.NiceLabel;
import edu.colorado.phet.normalmodes.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.events.Event;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.core.UIComponent;

public class PolarizationPanel extends Canvas {
    private var myMainView:MainView;
    private var myModel:Object;     //either Model1 or Model2

    //Polarization radio buttons
    private var innerBckgrnd: VBox;
    private var polarizationLabel: NiceLabel;
    private var modeTypeHBox: HBox;
    private var directionOfMode_rbg: RadioButtonGroup;
    private var horizPolarizationButton: RadioButton;
    private var vertPolarizationButton: RadioButton;
    private var horizArrow: TwoHeadedArrow;
    private var vertArrow: TwoHeadedArrow;

    public var polarization_str: String;

    public function PolarizationPanel( myMainView: MainView, myModel: Object ) {
        super();
        this.myMainView = myMainView;
        this.myModel = myModel;
        this.init();
    }//end constructor

    private function init():void{
        this.polarization_str = FlexSimStrings.get( "polarization:", "Polarization Control:");

        this.innerBckgrnd = new VBox();
        with ( this.innerBckgrnd ) {
            setStyle( "backgroundColor", 0x88ff88 );   //0xdddd00
            percentWidth = 100;
            setStyle( "borderStyle", "solid" );
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 6 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 0 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 3 );
            setStyle( "paddingLeft", 8 );
            setStyle( "verticalGap", 0 );
            setStyle( "horizontalAlign" , "center" );
        }

        //Set up polarization radio button box
        this.polarizationLabel = new NiceLabel( 12, polarization_str );
        this.modeTypeHBox = new HBox();
        this.directionOfMode_rbg = new RadioButtonGroup();
        this.horizPolarizationButton = new RadioButton();
        this.vertPolarizationButton = new RadioButton();
        this.horizArrow = new TwoHeadedArrow();
        this.horizArrow.height = 10;
        this.horizArrow.width = 20;
        this.horizArrow.y = -0.5*this.horizArrow.height;   //I don't understand why this must be negative.
        this.horizArrow.x = 5;                              //and why this is positive
        this.vertArrow = new TwoHeadedArrow();
        this.vertArrow.height = 10;
        this.vertArrow.width = 20;
        this.vertArrow.rotation = -90;
        this.vertArrow.x = 5;
        this.horizPolarizationButton.group = directionOfMode_rbg;
        this.vertPolarizationButton.group = directionOfMode_rbg;
        this.horizPolarizationButton.value = 1;
        this.vertPolarizationButton.value = 0;
        this.horizPolarizationButton.selected = false;
        this.vertPolarizationButton.selected = true;
        this.directionOfMode_rbg.addEventListener( Event.CHANGE, setPolarization );

        //Polarization type radio buttons
        this.addChild( this.innerBckgrnd );
        this.innerBckgrnd.addChild( new SpriteUIComponent( this.polarizationLabel ));
        this.innerBckgrnd.addChild( this.modeTypeHBox );
        this.modeTypeHBox.addChild( this.horizPolarizationButton );
        this.modeTypeHBox.addChild( new SpriteUIComponent( this.horizArrow, true) );
        this.modeTypeHBox.addChild( this.vertPolarizationButton );
        this.modeTypeHBox.addChild( new SpriteUIComponent( this.vertArrow, true) );
    }//end init()

    public function setModel( currentModel: Object ):void{
        this.myModel = currentModel;
    }

    private function setPolarization( evt: Event ): void {
        var val: Object = this.directionOfMode_rbg.selectedValue;
        if ( val == 1 ) {
            this.myModel.xModes = true;
            //this.myMainView.myButtonArrayPanel.showVerticalPolarization( false );
        }
        else {
            this.myModel.xModes =  false;
            //this.myMainView.myButtonArrayPanel.showVerticalPolarization( true );
        }
    }//end setPolarization();


}//end class
}//end package
