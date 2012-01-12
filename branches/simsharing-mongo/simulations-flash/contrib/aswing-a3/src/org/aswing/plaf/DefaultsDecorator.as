package org.aswing.plaf{
	
/**
 * The decorator that will use defaults properties.
 */
public interface DefaultsDecorator{
	
	/**
	 * Sets the defaults properties owner.
	 * Which the owner.getDefault() will be called.
	 */
	function setDefaultsOwner(owner:ComponentUI):void;
	
}
}