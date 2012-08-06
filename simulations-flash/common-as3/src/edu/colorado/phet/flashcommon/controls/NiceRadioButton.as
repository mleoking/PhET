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
    private var label: NiceLabel;
    private var hitArea: Sprite;


    public function NiceRadioButton( label: String, selected = false ) {
        this.label_str = label;
        this._selected = selected;
        this.icon = new Sprite();
        this.label = new NiceLabel( 15, label_str );
        this.hitArea = new Sprite();
        this.drawDeselectedIcon();
        this.drawHitArea();
        this.addChild( icon );
        this.addChild( label );
        this.addChild( hitArea );
        this.activateButton();
    }



    public function set group( niceRadioButtonGroup: NiceRadioButtonGroup ):void{
        this._group = niceRadioButtonGroup;
        this._indexOfButton = niceRadioButtonGroup.numberOfButtons;
        this._group.addNiceRadioButton( this );
    }

    private function drawDeselectedIcon():void{
        var gIcon: Graphics = icon.graphics;
    }

    private function drawSelectedIcon():void{
        var gIcon: Graphics = icon.graphics;
    }

    private function drawHitArea():void{

    }

    private function activateButton():void{

    }


    public function set selected( tOrF: Boolean ):void{
        this._selected = tOrF;
        if( _selected == true ){
            this.selectThisRadioButton();
        }
    }

    private function selectThisRadioButton():void{
        _selected = true;
        drawSelectedIcon();
        _group.selectButton( this );
    }

    public function get indexOfButton():int {
        return _indexOfButton;
    }
}
}
