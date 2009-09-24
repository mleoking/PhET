/**
* @author Trevor McCauley
* @link www.senocular.com
*/
package org.papervision3d.core.utils.virtualmouse
{	
	import flash.events.Event;
	
	/**
	 * Wrapper for the Event class to let you check to
	 * see if an event originated from the user's mouse
	 * or a VirtualMouse instance.
	 */
	public class VirtualMouseEvent extends Event implements IVirtualMouseEvent {
		public function VirtualMouseEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false){
			super(type, bubbles, cancelable);
		}
	}
}