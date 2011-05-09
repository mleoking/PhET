/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.event{

import flash.events.Event;

/**
 * The event for table cell editing.
 * @author iiley
 */
public class TableCellEditEvent extends AWEvent{

	/**
     *  The <code>TableCellEditEvent.EDITING_STARTED</code> constant defines the value of the
     *  <code>type</code> property of the event object for a <code>tableCellEditingStarted</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>getRow()</code></td><td>the row be edit</td></tr>
     *     <tr><td><code>getColumn()</code></td><td>the column be edit</td></tr>
     *     <tr><td><code>getOldValue()</code></td><td>the old value</td></tr>
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
     *  @eventType tableCellEditingStarted
	 */
	public static const EDITING_STARTED:String = "tableCellEditingStarted";	
	
	/**
     *  The <code>TableCellEditEvent.EDITING_CANCELED</code> constant defines the value of the
     *  <code>type</code> property of the event object for a <code>tableCellEditingCanceled</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>getRow()</code></td><td>the row be edit</td></tr>
     *     <tr><td><code>getColumn()</code></td><td>the column be edit</td></tr>
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
     *  @eventType tableCellEditingCanceled
	 */
	public static const EDITING_CANCELED:String = "tableCellEditingCanceled";
	
	/**
     *  The <code>TableCellEditEvent.EDITING_STOPPED</code> constant defines the value of the
     *  <code>type</code> property of the event object for a <code>tableCellEditingStopped</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>getRow()</code></td><td>the row be edit</td></tr>
     *     <tr><td><code>getColumn()</code></td><td>the column be edit</td></tr>
     *     <tr><td><code>getOldValue()</code></td><td>the old value</td></tr>
     *     <tr><td><code>getNewValue()</code></td><td>the new value edited</td></tr>
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
     *  @eventType tableCellEditingStopped
	 */
	public static const EDITING_STOPPED:String = "tableCellEditingStopped";
	
	private var row:int;
	private var column:int;
	private var oldValue:*;
	private var newValue:*;
	
	/**
	 * Create a cell edit event.
	 * @param type the type
	 * @param row the edit row
	 * @param column the edit column
	 * @param oldValue the old value
	 * @param newValue the edited new value
	 */
	public function TableCellEditEvent(type:String, row:int, column:int, oldValue:*=null, newValue:*=null){
		super(type, false, false);
		this.row = row;
		this.column = column;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public function getRow():int{
		return row;
	}
	
	public function getColumn():int{
		return column;
	}
	
	public function getOldValue():*{
		return oldValue;
	}
	
	public function getNewValue():*{
		return newValue;
	}
	
	override public function clone():Event{
		return new TableCellEditEvent(type, row, column, oldValue, newValue);
	}
}
}