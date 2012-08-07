/*
 * Copyright 2002-2012, University of Colorado
 */

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
public class NiceRadioButtonGroup {
    private var radioButtons_arr: Array;   //array of NiceRadioButtons in this radioButtonGroup
    private var _numberOfButtons: int;
    private var _selectedIndex: int;
    private var action: Function;          //function run when radio button is selected
    private var controlPanel: Object;

    public function NiceRadioButtonGroup( ){
        _numberOfButtons = 0;
        radioButtons_arr = new Array();
    }

    public function addNiceRadioButton( niceRadioButton: NiceRadioButton ):void{
        _numberOfButtons += 1;
        radioButtons_arr[ _numberOfButtons - 1 ] = niceRadioButton;

    }

    public function setListener( controlPanel: Object ):void{
        this.controlPanel = controlPanel;
    }

    public function selectButton( selectedButton: NiceRadioButton ):void{
        for( var i:int; i < numberOfButtons; i++ ){
            radioButtons_arr[i].selected = false;
        }
        selectedButton.selected = true;
        _selectedIndex = selectedButton.indexOfButton;
        this.controlPanel.niceRadioGroupListener( _selectedIndex );
        //trace( "NiceRadioButtonGroup.selectButton  selectedIndex is " + _selectedIndex ) ;
    }

    public function selectButtonWithoutAction( selectedButton: NiceRadioButton ):void{
        for( var i:int; i < numberOfButtons; i++ ){
            radioButtons_arr[i].selected = false;
        }
        selectedButton.selected = true;
        _selectedIndex = selectedButton.indexOfButton;
        //trace( "NiceRadioButtonGroup.selectButton  selectedIndex is " + _selectedIndex ) ;
    }

    public function get numberOfButtons():int {
        return _numberOfButtons;
    }

    public function get selectedIndex(): int {
        return _selectedIndex;
    }
} //end class
} //end package
