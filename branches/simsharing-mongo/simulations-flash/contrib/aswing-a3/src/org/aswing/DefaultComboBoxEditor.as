/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.ui.Keyboard;

import org.aswing.event.AWEvent;

public class DefaultComboBoxEditor extends EventDispatcher implements ComboBoxEditor{

    private var textField:JTextField;
    private var lostingFocus:Boolean;
    protected var value:*;
    protected var valueText:String;
    	
	public function DefaultComboBoxEditor(){
		lostingFocus = false;
	}
	
	public function selectAll():void{
		if(getTextField().isEditable() && !lostingFocus){
			getTextField().selectAll();
		}
		//getTextField().makeFocus();
	}
	
	public function setValue(value:*):void{
		this.value = value;
		if(value == null){
			getTextField().setText("");
		}else{
			getTextField().setText(value+"");
		}
		valueText = getTextField().getText();
	}
	
	public function addActionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(AWEvent.ACT, listener, false,  priority, useWeakReference);
	}
	
	public function getValue():*{
		return value;
	}
	
	public function removeActionListener(listener:Function):void{
		removeEventListener(AWEvent.ACT, listener, false);
	}
	
	public function setEditable(b:Boolean):void{
        getTextField().setEditable(b);
        getTextField().setEnabled(b);
	}
	
	public function getEditorComponent():Component{
		return getTextField();
	}
	
	public function isEditable():Boolean{
		return getTextField().isEditable();
	}

    override public function toString():String{
        return "DefaultComboBoxEditor[]";
    }	
    
    //------------------------------------------------------
	
	protected function createTextField():JTextField{
		var tf:JTextField = new JTextField("", 1); //set rows 1 to ensure the JTextField has a perfer height when empty
		tf.setBorder(null);
        tf.setOpaque(false);
        tf.setFocusable(false);
        tf.setBackgroundDecorator(null);
        return tf;
	}
    
    protected function getTextField():JTextField{
        if(textField == null){
        	textField = createTextField();
            initHandler();
        }
        return textField;
    }

    private function initHandler():void{
        getTextField().getTextField().addEventListener(KeyboardEvent.KEY_DOWN, __textKeyDown);
        getTextField().getTextField().addEventListener(FocusEvent.FOCUS_OUT, __grapValueFormText);
    }
	
    private function __grapValueFormText(e:Event):void{
    	if(grapValueFormText()){
    		lostingFocus = true;
	        dispatchEvent(new AWEvent(AWEvent.ACT));
	        lostingFocus = false;
     	}
    }
    
    private function grapValueFormText():Boolean{
    	if(getTextField().isEditable() && valueText != getTextField().getText()){
    		value = getTextField().getText();
    		return true;
    	}
    	return false;
    }

    private function __textKeyDown(e:KeyboardEvent):void{
    	if(getTextField().isEditable() && e.keyCode == Keyboard.ENTER){
	        grapValueFormText();
	        dispatchEvent(new AWEvent(AWEvent.ACT));
     	}
    }   
}
}