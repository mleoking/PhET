/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 8/5/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon.controls {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;

/**
 * Replaces flex RadioButton, which cannot handle font size changes gracefully
 */
public class NiceRadioButton extends Sprite {


    private var label_str: String;
    private var _indexOfButton: int;
    private var _selected: Boolean;
    private var _group: NiceRadioButtonGroup;
    //graphics
    private var icon:Sprite;
    private var overIcon: Sprite;
    public var label: NiceLabel;
    private var buttonHitArea: Sprite;


    public function NiceRadioButton( label_str: String, selected:Boolean = false ) {
        this.label_str = label_str;
        this._selected = selected;
        this.icon = new Sprite();
        this.overIcon = new Sprite();
        this.label = new NiceLabel( 15, this.label_str );
        this.buttonHitArea = new Sprite();
        if( selected ){
            this.drawSelectedIcon();
        }else{
            this.drawDeselectedIcon();
        }
        this.drawOverIcon();
        this.overIcon.visible = false;
        this.drawHitArea();
        this.addChild( icon );
        this.addChild( overIcon );
        this.addChild( label );
        label.x = icon.width;
        label.y -= 3;
        this.addChild( buttonHitArea );
        this.activateButton();
    }



    public function set group( niceRadioButtonGroup: NiceRadioButtonGroup ):void{
        this._group = niceRadioButtonGroup;
        this._indexOfButton = niceRadioButtonGroup.numberOfButtons;
        this._group.addNiceRadioButton( this );
    }

    private function drawDeselectedIcon():void{
        var gIcon: Graphics = icon.graphics;
        var r: Number = 7;  //radius of circle
        with( gIcon ){
            clear();
            lineStyle( 3, 0x888888 );
            beginFill( 0x555555 );
            drawCircle( r, r, r );
            endFill();
        }
    }

    private function drawSelectedIcon():void{
        var gIcon: Graphics = icon.graphics;
        var r: Number = 7;  //radius of circle
        with(gIcon){
            clear();
            lineStyle( 3, 0x666666 );
            beginFill( 0xffffff );
            drawCircle( r, r, r );
            endFill();
        }
    }

    private function drawOverIcon():void{
        var gOverIcon: Graphics = overIcon.graphics;
        var r: Number = 7;  //radius of circle
        with(gOverIcon){
            clear();
            lineStyle( 3, 0xaaaa00 );
            beginFill( 0xffff00 );
            drawCircle( r, r, r );
            endFill();
        }
    }

    private function drawHitArea():void{
        var w: Number = icon.width + label.width;
        var h: Number = Math.max (icon.height,  label.height);
        var alpha: Number = 0;    //invisible hitArea
        var gHA:Graphics = this.buttonHitArea.graphics;
        with( gHA ){
            clear();
            lineStyle( 1, 0xffffff, alpha );
            beginFill( 0xffffff, alpha );
            drawRect( 0, 0, w,  h );
            endFill();
        }
    }

    private function activateButton():void{
        this.buttonHitArea.buttonMode = true;
        this.buttonHitArea.addEventListener( MouseEvent.MOUSE_DOWN, buttonBehave );
        this.buttonHitArea.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.buttonHitArea.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.buttonHitArea.addEventListener( MouseEvent.MOUSE_UP, buttonBehave );
        var localRef: Object = this;

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseDown" ) {
                localRef._group.selectButton( localRef );
                localRef.overIcon.visible = false;
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {
                localRef.label.setFontColor( 0xffff00 );
                localRef.label.setBold(true)
                localRef.overIcon.visible = true;
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);


            } else if ( evt.type == "mouseOut" ) {
                localRef.label.setFontColor( 0xffffff );
                localRef.overIcon.visible = false;
                if( !localRef._selected ) {
                    localRef.label.setBold( false );
                }
            }
    }
    }


    public function set selected( tOrF: Boolean ):void{
        this._selected = tOrF;
        if( _selected == true ){
            drawSelectedIcon();
            label.setBold( true );
        }else{
            drawDeselectedIcon();
            label.setBold( false );
        }
    }

//    private function selectThisRadioButton():void{
//        this._selected = true;
//        drawSelectedIcon();
//        _group.selectButton( this );
//    }

    public function get indexOfButton():int {
        return _indexOfButton;
    }
}
}
