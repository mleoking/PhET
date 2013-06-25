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

    public function NiceCheckBox( label_str: String, fontSize: Number ):void {
        _checkBox.buttonMode = true;
        this.label_str = label_str;
        this._niceLabel = new NiceLabel( fontSize, label_str, false, _checkBox  );
        this.addChild( container );
        container.addChild( _checkBox );
        container.addChild( new SpriteUIComponent( _niceLabel, true ));
        //tweek label position
        this._niceLabel.y -= 0.15*this._niceLabel.height;
        container.setStyle( "horizontalGap", 2 ) ;
    }


    public function get checkBox():CheckBox{
        return _checkBox;
    }

    public function get niceLabel():NiceLabel{
        return _niceLabel;
    }
} //end class
} //end package
