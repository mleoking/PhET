/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 8/5/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon.controls {
import flash.display.Sprite;

/**
 * Replaces flex RadioButton, which cannot handle font size changes gracefully
 */
public class NiceRadioButton extends Sprite {
    private var label_str: String;
    private var _selected: Boolean;
    private var _group: NiceRadioButtonGroup;
    public function NiceRadioButton( label: String) {
        this.label_str = label;
        this._selected = false;
    }

    public function set selected( tOrF: Boolean ):void{
        this._selected = tOrF;
    }

    public function set group( niceRadioButtonGroup: NiceRadioButtonGroup ):void{
        this._group = niceRadioButtonGroup;
        this._group.addNiceRadioButton( this );
    }
}
}
