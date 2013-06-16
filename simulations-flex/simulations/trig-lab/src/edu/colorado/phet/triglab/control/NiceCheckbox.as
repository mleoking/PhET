/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/14/13
 * Time: 8:32 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.control {
import edu.colorado.phet.flashcommon.controls.NiceLabel;

import flash.display.Graphics;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.controls.CheckBox;

/*
* Dubson-built Checkbox Canvas component consisting of flex checkbox and Dubson-built NiceLabel.  Flex Checkbox component has
* a label property, but the label is unusable because any non-default font size results in the text trucated with ellipses (...)
* */
public class NiceCheckbox extends Canvas{
    private var container: HBox = new HBox();
    private var checkBox: CheckBox = new CheckBox();
    private var label_lbl: NiceLabel = new NiceLabel( );
    private var label_str: String;
    private var fontSize:Number;
    private var checkBoxHitAre
    a: Sprite = new Sprite();    //invisible hit area over label;
    public function NiceCheckbox( checkBox: CheckBox, label_lbl: NiceLabel, label_str: String, fontSize: Number = 25 ):void {
        this.checkBox = checkBox;
        this.label_lbl = label_lbl;
        this.label_str = label_str
        this.fontSize = fontSize;
        this.label_lbl.setText( label_str );
        this.label_lbl.setFontSize( fontSize );
        createHitArea();
        checkBox.hitArea = this.hitArea;
    }


    private function createHitArea():void{
        var width: Number = label_lbl.width + checkBox.width;
        var height: Number = label_lbl.width;
        with( checkBoxHitArea.graphics ){
            clear();
            lineGradientStyle( 1, 0xffffff, 0.5 );
            beginFill( 0xffffff, 0 );
            moveTo( 0, 0 );
            drawRect( 0, 0, width,  height );
            endFill( 0xffffff, 0.5);
        }
    }

    public function setFontColor( color:uint ):void{
        this.label_lbl.setFontColor( color );
    }

    public function setFontSize( size: Number ):void{
        this.fontSize = size;
        this.label_lbl.setFontSize( fontSize );
        createHitArea();
    }
} //end class
} //end package
