/**
 * Created by ${PRODUCT_NAME}.
 * User: General User
 * Date: 12/12/10
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance {

import flash.display.Sprite;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.Button;
import mx.core.Container;
import mx.core.UIComponent;

public class PlayPauseButtons extends Canvas{
     private var playPauseButton:Button;

    public function PlayPauseButtons() {
        this.playPauseButton = new Button();
        this.addChild(this.playPauseButton);
        this.playPauseButton.buttonMode = true;
        this.playPauseButton.label = "Play";
        this.playPauseButton.addEventListener(MouseEvent.CLICK, onMouseClick)
    }  //end of constructor

    private function onMouseClick(evt:MouseEvent):void{
        if(this.playPauseButton.label == "Play"){
         this.playPauseButton.label = "Pause";
        }else{
          this.playPauseButton.label = "Play";
        }
    }
} //end of class
} //end of package
