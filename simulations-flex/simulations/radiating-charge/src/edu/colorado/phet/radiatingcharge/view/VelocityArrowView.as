/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 7/6/12
 * Time: 9:34 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.radiatingcharge.model.FieldModel;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.sampler.getSampleCount;

/**
 * View of an arrow representing velocity of charge. Velocity cannot exceed c, which corresponds to maxLength of arrow graphic.
 * Two different arrows
 * A green bar, whose length represents the current speed
 */
public class VelocityArrowView {
    private var mainVeiw: MainView;
    private var fieldModel: FieldModel;
    private var maxLength: Number;      //maximum length of arrow in pixels, corresponding to speed c
    private var _speedIndicatorContainer: Sprite; //container for static speed-of-light arrow and current speed indicator bar
    private var _speedOfLightArrow: Sprite;     //speed-of-light arrow graphic of fixed length = maxLength
    private var _currentSpeedIndicator: Sprite; //bar on static speed-of-light arrow, indicating current fraction of speed of light
    private var _velocityArrow: Sprite;         //current velocity arrow, length varies with speed
    private var cLabel: NiceLabel;
    private var c_str: String;

    public function VelocityArrowView( mainView: MainView, fieldModel: FieldModel ) {
        this.mainVeiw = mainView;
        this.fieldModel = fieldModel;
        fieldModel.registerView( this );
        _speedIndicatorContainer = new Sprite();
        _speedOfLightArrow = new Sprite();
        _currentSpeedIndicator = new Sprite();
        _velocityArrow = new Sprite();
        c_str = FlexSimStrings.get( "c", " c " );
        cLabel = new NiceLabel( 15, c_str );
        cLabel.setFontColor( 0xffffff );
        cLabel.setBackgroundColor( 0x000000 );
        _speedOfLightArrow.addChild( cLabel );
        maxLength = 120;
        drawSpeedOfLightArrow();
        drawVelocityArrow();
        speedIndicatorContainer.addChild( _currentSpeedIndicator );
        speedIndicatorContainer.addChild( _speedOfLightArrow );
    }

    /**
     * Static horizontal arrow sprite representing speed of light.
     * Arrow appears in Control Panel. Origin at tail of arrow.
     */
    private function drawSpeedOfLightArrow(): void {
        var gc: Graphics = _speedOfLightArrow.graphics;
        gc.clear();
        gc.lineStyle( 2, 0xffffff );        //white
        drawArrow( gc );
        //draw legend indicating that length of arrow is "c"
        var r: Number = maxLength / 30;      //radius of arrow shaft
        var y: Number = 5 * r;
        var w: Number = 0.8 * maxLength;      //length of arrow shaft
        with ( gc ) {
            moveTo( 0, y );
            lineTo( maxLength, y );
            moveTo( 0, y - 2*r );
            lineTo( 0, y + 2*r );
            moveTo( maxLength, y - 2*r );
            lineTo( maxLength, y + 2*r );

//            moveTo( 2*r, y - r );
//            lineTo( 0, y );
//            lineTo( 2*r, y + r );
//            moveTo( maxLength - 2*r, y - r );
//            lineTo( maxLength, y );
//            lineTo( maxLength - 2*r, y + r );
        }
        cLabel.y = y - cLabel.height/2;
        cLabel.x = maxLength/2 - cLabel.width/2;
    }

    /**
     * Horizontal green bar indicating current speed.
     * This is superimposed on static speed-of-light arrow
     */

    private function updateCurrentSpeedIndicator():void{
        var beta: Number = fieldModel.getBeta();
        var gS:Graphics = _currentSpeedIndicator.graphics;
        var h: Number =  4*maxLength/30;  //height of horizontal bar = height of speed-of-light arrow
        with(gS){
            clear();
            lineStyle( 2, 0x007700 );
            beginFill( 0x007700 );
            drawRect( 0, -h/2, beta*maxLength, h );
            endFill();
        }
    }

    private function drawArrow( g: Graphics ): void {
        var r: Number = maxLength / 30;      //radius of arrow shaft
        var w: Number = 0.8 * maxLength;     //length of arrow shaft
        with ( g ) {
            moveTo( 0, 0 );
            lineTo( 0, -r );
            lineTo( w, -r );
            lineTo( w, -2 * r );
            lineTo( maxLength, 0 );
            lineTo( w, 2 * r );
            lineTo( w, r );
            lineTo( 0, r );
            lineTo( 0, 0 );
        }
    }

    private function drawVelocityArrow(): void {
        var gv: Graphics = _velocityArrow.graphics;
        gv.clear();
        gv.lineStyle( 2, 0x00ff00 );     //green
        drawArrow( gv );
    }

    public function update(): void {
        updateCurrentSpeedIndicator();
        if( velocityArrow.visible ){
            var beta: Number = fieldModel.getBeta();
            var vX: Number = fieldModel.vX;
            var vY: Number = fieldModel.vY;
            var angle: Number = Math.atan2( -vY, vX ) * 180 / Math.PI;
            with ( velocityArrow ) {
                visible = false;
                rotation = 0;
                width = maxLength * beta;
                rotation = angle;
                visible = true;
            }
        }
    }

    public function get speedOfLightArrow(): Sprite {
        return _speedOfLightArrow;

    }

    public function get velocityArrow(): Sprite {
        //trace( "VelocityArrowView.getVelocityArrow called.  velocityArrow.visible  = " + this._velocityArrow.visible );
        return _velocityArrow;

    }

    public function setVisibilityOfVelocityArrow( tOrF:Boolean ):void{
        _velocityArrow.visible = tOrF;
        //trace( "VelocityArrowView.setVisibilityOfVelocityArrow called.  value = " + tOrF );
    }

    public function get speedIndicatorContainer(): Sprite {
        return _speedIndicatorContainer;
    }
}
}
