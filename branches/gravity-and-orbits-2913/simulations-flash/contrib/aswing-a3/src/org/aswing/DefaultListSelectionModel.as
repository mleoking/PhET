/*
 Copyright aswing.org, see the LICENCE.txt.
*/


package org.aswing{

import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import org.aswing.event.SelectionEvent;

/**
 * Default data model for list selections.
 * @author iiley
 */
public class DefaultListSelectionModel extends EventDispatcher implements ListSelectionModel{

	/**
	 * Only can select one most item at a time.
	 */
	public static const SINGLE_SELECTION:int = 0;
	/**
	 * Can select any item at a time.
	 */
	public static const MULTIPLE_SELECTION:int = 1;
		
	private static var MIN:int = -1;
	private static var MAX:int = int.MAX_VALUE;
	
	private var value:Array;
	private var minIndex:int;
	private var maxIndex:int;
	private var anchorIndex:int;
	private var leadIndex:int;
	private var selectionMode:int;
	
	public function DefaultListSelectionModel(){
		value       = [];
		minIndex    = MAX;
		maxIndex    = MIN;
		anchorIndex = -1;
		leadIndex   = -1;
		selectionMode = MULTIPLE_SELECTION;
	}
	
	public function setSelectionInterval(index0 : int, index1 : int, programmatic:Boolean=true) : void {
		if (index0 < 0 || index1 < 0) {
			clearSelection(programmatic);
			return;
		}
		if (getSelectionMode() == SINGLE_SELECTION) {
			index0 = index1;
		}
		var i:int;
		updateLeadAnchorIndices(index0, index1);
		var min:int = Math.min(index0, index1);
		var max:int = Math.max(index0, index1);
		var changed:Boolean = false;
		if(min == minIndex && max == maxIndex){
			for(i=min; i<=max; i++){
				if(value[i] != true){
					changed = true;
					break;
				}
			}
		}else{
			changed = true;
		}
		if(changed){
			minIndex = min;
			maxIndex = max;
			clearSelectionImp();
			for(i=minIndex; i<=maxIndex; i++){
				value[i] = true;
			}
			fireListSelectionEvent(min, max, programmatic);
		}
	}

	public function addSelectionInterval(index0 : int, index1 : int, programmatic:Boolean=true) : void {
		if (index0 < 0 || index1 < 0) {
			return;
		}
		if (getSelectionMode() == SINGLE_SELECTION) {
			setSelectionInterval(index0, index1);
			return;
		}
		updateLeadAnchorIndices(index0, index1);
		var min:int = Math.min(index0, index1);
		var max:int = Math.max(index0, index1);
		var changed:Boolean = false;
		for(var i:int=min; i<=max; i++){
			if(value[i] != true){
				value[i] = true;
				changed = true;
			}
		}
		minIndex = Math.min(min, minIndex);
		maxIndex = Math.max(max, maxIndex);
		if(changed){
			fireListSelectionEvent(min, max, programmatic);
		}
	}

	public function removeSelectionInterval(index0 : int, index1 : int, programmatic:Boolean=true) : void {
		if (index0 < 0 || index1 < 0) {
			return;
		}		
		var min:int = Math.min(index0, index1);
		var max:int = Math.max(index0, index1);
		min = Math.max(min, minIndex);
		max = Math.min(max, maxIndex);
		if(min > max){
			return;
		}
		
		updateLeadAnchorIndices(index0, index1);
		
		if(min == minIndex && max == maxIndex){
			clearSelection();
			return;
		}else if(min > minIndex && max < maxIndex){
		}else if(min > minIndex && max == maxIndex){
			maxIndex = min - 1;
		}else{//min==minIndex && max<maxIndex
			minIndex = max + 1;
		}
		for(var i:int=min; i<=max; i++){
			value[i] = undefined;
		}
		fireListSelectionEvent(min, max, programmatic);
	}

	public function getMinSelectionIndex() : int {
		if(isSelectionEmpty()){
			return -1;
		}else{
			return minIndex;
		}
	}

	public function getMaxSelectionIndex() : int {
		return maxIndex;
	}

	public function isSelectedIndex(index : int) : Boolean {
		return value[index] == true;
	}
	
	private function updateLeadAnchorIndices(anchor:int, lead:int):void {
		anchorIndex = anchor;
		leadIndex   = lead;
	}

	public function getAnchorSelectionIndex() : int {
		return anchorIndex;
	}

	public function setAnchorSelectionIndex(index : int) : void {
		anchorIndex = index;
	}

	public function getLeadSelectionIndex() : int {
		return leadIndex;
	}

	public function setLeadSelectionIndex(index : int) : void {
		leadIndex = index;
	}

	public function clearSelection(programmatic:Boolean=true) : void {
		if(!isSelectionEmpty()){
			var max:int = maxIndex;
			minIndex	= MAX;
			maxIndex	= MIN;
			clearSelectionImp();
			fireListSelectionEvent(0, max, programmatic);
		}
	}
	
	private function clearSelectionImp():void{
		value = [];
	}

	public function isSelectionEmpty() : Boolean {
		return minIndex > maxIndex;
	}
	
	public function insertIndexInterval(index:int, length:int, before:Boolean, programmatic:Boolean=true):void{
		/* The first new index will appear at insMinIndex and the last
		 * one will appear at insMaxIndex
		 */
		var insMinIndex:int = (before) ? index : index + 1;
		var insMaxIndex:int = (insMinIndex + length) - 1;
	
		var needInstertArray:Boolean = false;
		
		if(isSelectionEmpty()){
			//need do nothing
		}else if(minIndex >= insMinIndex){
			minIndex += length;
			maxIndex += length;
			needInstertArray = true;
		}else if(maxIndex < insMinIndex){
			//do nothing
		}else if(insMinIndex > minIndex && insMinIndex <= maxIndex){
			maxIndex += length;
			needInstertArray = true;
		}
		
		if(needInstertArray){
			if(insMinIndex == 0){
				value = (new Array(length)).concat(value);
			}else{
				var right:Array = value.splice(insMinIndex);
				value = value.concat(new Array(length)).concat(right);
			}
		}
	
		var leadIn:int = leadIndex;
		if (leadIn > index || (before && leadIn == index)) {
			leadIn = leadIndex + length;
		}
		var anchorIn:int = anchorIndex;
		if (anchorIn > index || (before && anchorIn == index)) {
			anchorIn = anchorIndex + length;
		}
		if (leadIn != leadIndex || anchorIn != anchorIndex) {
			updateLeadAnchorIndices(anchorIn, leadIn);
		}
		
		if(needInstertArray){
			fireListSelectionEvent(insMinIndex, insMaxIndex+length, programmatic);
		}
	}

	public function removeIndexInterval(index0:int, index1:int, programmatic:Boolean=true):void{
		var rmMinIndex:int = Math.min(index0, index1);
		var rmMaxIndex:int = Math.max(index0, index1);
		var gapLength:int = (rmMaxIndex - rmMinIndex) + 1;

		var needFireEvent:Boolean = true;
		var i:int;
		
		if(isSelectionEmpty()){
			//need do nothing
			needFireEvent = false;
		}else if(minIndex >= rmMinIndex && maxIndex <= rmMaxIndex){
			minIndex	= MAX;
			maxIndex	= MIN;
			clearSelectionImp();
		}else if(maxIndex < rmMinIndex){
			value.splice(rmMinIndex, gapLength);
		}else if(minIndex > rmMaxIndex){
			value.splice(rmMinIndex, gapLength);
			minIndex -= gapLength;
			maxIndex -= gapLength;
		}else if(minIndex < rmMinIndex && maxIndex >= rmMinIndex && maxIndex <= rmMaxIndex){
			value.splice(rmMinIndex, gapLength);
			for(i = rmMinIndex-1; i>=minIndex; i--){
				maxIndex = i;
				if(value[i] == true){
					break;
				}
			}
		}else if(minIndex >= rmMinIndex && maxIndex > rmMaxIndex){
			value.splice(rmMinIndex, gapLength);
			maxIndex -= gapLength;
			for(i = rmMinIndex-1; i<=maxIndex; i++){
				minIndex = i;
				if(value[i] == true){
					break;
				}
			}
		}else if(minIndex < rmMinIndex && maxIndex > rmMaxIndex){
			value.splice(rmMinIndex, gapLength);
			maxIndex -= gapLength;
		}else{
			needFireEvent = false;
		}

		var leadIn:int = leadIndex;
		if (leadIn == 0 && rmMinIndex == 0) {
			// do nothing
		} else if (leadIn > rmMaxIndex) {
			leadIn = leadIndex - gapLength;
		} else if (leadIn >= rmMinIndex) {
			leadIn = rmMinIndex - 1;
		}

		var anchorIn:int = anchorIndex;
		if (anchorIn == 0 && rmMinIndex == 0) {
			// do nothing
		} else if (anchorIn > rmMaxIndex) {
			anchorIn = anchorIndex - gapLength;
		} else if (anchorIn >= rmMinIndex) {
			anchorIn = rmMinIndex - 1;
		}
		
		if (leadIn != leadIndex || anchorIn != anchorIndex) {
			updateLeadAnchorIndices(anchorIn, leadIn);
		}
		
		if(needFireEvent){
			fireListSelectionEvent(rmMinIndex, rmMaxIndex+gapLength, programmatic);
		}
	}	
	
	/**
	 * Sets the selection mode.  The default is
	 * MULTIPLE_SELECTION.
	 * @param selectionMode  one of three values:
	 * <ul>
	 * <li>SINGLE_SELECTION
	 * <li>MULTIPLE_SELECTION
	 * </ul>
	 */
	public function setSelectionMode(selectionMode : int) : void {
		this.selectionMode = selectionMode;
	}

	public function getSelectionMode() : int {
		return selectionMode;
	}

	public function addListSelectionListener(listener:Function):void{
		addEventListener(SelectionEvent.LIST_SELECTION_CHANGED, listener);
	}
	
	public function removeListSelectionListener(listener:Function):void{
		removeEventListener(SelectionEvent.LIST_SELECTION_CHANGED, listener);
	}
	
	protected function fireListSelectionEvent(firstIndex:int, lastIndex:int, programmatic:Boolean):void{
		dispatchEvent(new SelectionEvent(SelectionEvent.LIST_SELECTION_CHANGED, firstIndex, lastIndex, programmatic));
	}
	
	override public function toString():String{
		return "DefaultListSelectionModel[]";
	}
}
}