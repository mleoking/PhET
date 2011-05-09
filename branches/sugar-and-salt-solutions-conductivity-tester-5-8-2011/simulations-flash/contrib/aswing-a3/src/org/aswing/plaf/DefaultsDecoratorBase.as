package org.aswing.plaf{

import org.aswing.Component;	

public class DefaultsDecoratorBase implements DefaultsDecorator{
	
	protected var defaultsOwner:ComponentUI;
	
	public function DefaultsDecoratorBase(){
		
	}
	
	public function setDefaultsOwner(owner:ComponentUI):void{
		defaultsOwner = owner;
	}
	
	public function getDefaultsOwner(c:Component):ComponentUI{
		if(defaultsOwner){
			return defaultsOwner;
		}else{
			return c.getUI();
		}
	}
}
}