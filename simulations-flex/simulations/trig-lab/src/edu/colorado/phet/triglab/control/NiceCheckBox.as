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
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Graphics;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.controls.CheckBox;

/*
* Dubson-built Checkbox Canvas component consisting of flex checkbox and Dubson-built NiceLabel.  Flex Checkbox component has
* a label property, but the label is unusable because any non-default font size results in the text trucated with ellipses (...)
* */
public class NiceCheckBox extends Canvas{
    private var container: HBox = new HBox();
    private var _checkBox: CheckBox = new CheckBox();
    private var _niceLabel: NiceLabel;
    private var label_str: String;
    private var fontSize:Number;
    private var checkBoxHitArea: Sprite = new Sprite();    //invisible hit area over label;

    public function NiceCheckBox( niceLabel: NiceLabel ):void {
        _checkBox.buttonMode = true;
        this._niceLabel = niceLabel;
        //this.label_str = label_str
        //this.fontSize = fontSize;
        //this._label_nlb.setText( label_str );
        //this._niceLabel.setFontSize( fontSize );
        createHitArea();
        //_checkBox.hitArea = new SpriteUIComponent( checkBoxHitArea );
        this.addChild( container );
        container.addChild( _checkBox );
        container.addChild( new SpriteUIComponent( _niceLabel ));
        //_checkBox.addChild( new SpriteUIComponent( checkBoxHitArea ) );

    }


    private function createHitArea():void{
        var width: Number = _checkBox.width + _niceLabel.width ;
        var height: Number = _niceLabel.height;
        with( checkBoxHitArea.graphics ){
            clear();
            lineStyle( 1, 0xffffff, 0.5 );
            beginFill( 0xffffff,0.5 );
            moveTo( 0, 0 );
            drawRect( 0, 0, width,  height );
            endFill();
        }
    }

    public function setFontColor( color:uint ):void{
        this._niceLabel.setFontColor( color );
    }

    public function setFontSize( size: Number ):void{
        this.fontSize = size;
        this._niceLabel.setFontSize( fontSize );
        createHitArea();
    }

    public function get checkBox():CheckBox{
        return _checkBox;
    }

    public function get niceLabel():NiceLabel{
        return _niceLabel;
    }
} //end class
} //end package
