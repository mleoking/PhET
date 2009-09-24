/**
* @author Trevor McCauley
* @link www.senocular.com
*/
package org.papervision3d.core.utils.virtualmouse
{	
	import flash.display.InteractiveObject;
	import flash.events.MouseEvent;
	
	/**
	 * Wrapper for the MouseEvent class to let you check
	 * to see if an event originated from the user's mouse
	 * or a VirtualMouse instance.
	 */
	public class VirtualMouseMouseEvent extends MouseEvent implements IVirtualMouseEvent {
		public function VirtualMouseMouseEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false, localX:Number = NaN, localY:Number = NaN, relatedObject:InteractiveObject = null, ctrlKey:Boolean = false, altKey:Boolean = false, shiftKey:Boolean = false, buttonDown:Boolean = false, delta:int = 0){
			super(type, bubbles, cancelable, localX, localY, relatedObject, ctrlKey, altKey, shiftKey, buttonDown, delta);
		}
	}
}