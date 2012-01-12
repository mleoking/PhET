/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.event{

import flash.events.Event;

/**
 * For continusely clicks.
 */
public class ClickCountEvent extends AWEvent{
	
	/**
     *  The <code>ClickCountEvent.CLICK_COUNT</code> constant defines the value of the
     *  <code>type</code> property of the event object for a <code>clickCount</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>getCount()</code></td><td>the continuesly clicked count</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>comp.addEventListener()</code> to register an event listener,
     *       comp is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType clickCount
	 */
	public static const CLICK_COUNT:String = "clickCount";
	
	private var count:int;
	
	public function ClickCountEvent(type:String, count:int){
		super(type, false, false);
		this.count = count;
	}
	
	public function getCount():int{
		return count;
	}
	
	override public function clone():Event{
		return new ClickCountEvent(type, count);
	}
	
}
}