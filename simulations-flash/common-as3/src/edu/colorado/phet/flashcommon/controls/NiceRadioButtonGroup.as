/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 8/5/12
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon.controls {
import flash.display.Sprite;

/**
 * Replaces Flex framework RadioButtonGroup.
 * Needed because the flex component RadioButton cannot handle font size changes gracefully.
 */
public class NiceRadioButtonGroup extends Sprite {
    private var radioButtons_arr: Array;   //array of NiceRadioButtons in this radioButtonGroup
    private var numberOfButtons: int;
    private var action: Function;          //function run when radio button is selected

    public function NiceRadioButtonGroup(){
        numberOfButtons = 0;
        radioButtons_arr = new Array();
    }

    public function addNiceRadioButton( niceRadioButton: NiceRadioButton ):void{
        numberOfButtons += 1;
        radioButtons_arr[ numberOfButtons - 1 ] = niceRadioButton;

    }
}
}
