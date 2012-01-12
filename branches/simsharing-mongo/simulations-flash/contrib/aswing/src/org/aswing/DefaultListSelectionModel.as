/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.EventDispatcher;
import org.aswing.ListSelectionModel;

/**
 * Default data model for list selections.
 * @author iiley
 */
class org.aswing.DefaultListSelectionModel extends EventDispatcher implements ListSelectionModel {
	
	/**
	 * Only can select one most item at a time.
	 */
	public static var SINGLE_SELECTION:Number = 0;
	/**
	 * Can select any item at a time.
	 */
	public static var MULTIPLE_SELECTION:Number = 1;

	
	public static var ON_SELECTION_CHANGED:String = "onSelectionChanged";
	
	private static var MIN:Number = -1;
	private static var MAX:Number = Number.MAX_VALUE;
	
	private var value:Array;
	private var minIndex:Number;
	private var maxIndex:Number;
	private var anchorIndex:Number;
	private var leadIndex:Number;
	private var selectionMode:Number;
	
	public function DefaultListSelectionModel(){
		value       = [];
		minIndex    = MAX;
		maxIndex    = MIN;
		anchorIndex = -1;
		leadIndex   = -1;
		selectionMode = MULTIPLE_SELECTION;
	}
	
	public function setSelectionInterval(index0 : Number, index1 : Number) : Void {
		if (index0 < 0 || index1 < 0) {
			return;
		}
		if (getSelectionMode() == SINGLE_SELECTION) {
			index0 = index1;
		}
		updateLeadAnchorIndices(index0, index1);
		var min:Number = Math.min(index0, index1);
		var max:Number = Math.max(index0, index1);
		var changed:Boolean = false;
		if(min == minIndex && max == maxIndex){
			for(var i:Number=min; i<=max; i++){
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
			for(var i:Number=minIndex; i<=maxIndex; i++){
				value[i] = true;
			}
			fireListSelectionEvent(min, max);
		}
	}

	public function addSelectionInterval(index0 : Number, index1 : Number) : Void {
		if (index0 < 0 || index1 < 0) {
			return;
		}
		if (getSelectionMode() == SINGLE_SELECTION) {
			setSelectionInterval(index0, index1);
			return;
		}
		updateLeadAnchorIndices(index0, index1);
		var min:Number = Math.min(index0, index1);
		var max:Number = Math.max(index0, index1);
		var changed:Boolean = false;
		for(var i:Number=min; i<=max; i++){
			if(value[i] != true){
				value[i] = true;
				changed = true;
			}
		}
		minIndex = Math.min(min, minIndex);
		maxIndex = Math.max(max, maxIndex);
		if(changed){
			fireListSelectionEvent(min, max);
		}
	}

	public function removeSelectionInterval(index0 : Number, index1 : Number) : Void {
		if (index0 < 0 || index1 < 0) {
			return;
		}		
		var min:Number = Math.min(index0, index1);
		var max:Number = Math.max(index0, index1);
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
		for(var i:Number=min; i<=max; i++){
			value[i] = undefined;
		}
		fireListSelectionEvent(min, max);
	}

	public function getMinSelectionIndex() : Number {
		if(isSelectionEmpty()){
			return -1;
		}else{
			return minIndex;
		}
	}

	public function getMaxSelectionIndex() : Number {
		return maxIndex;
	}

	public function isSelectedIndex(index : Number) : Boolean {
		return value[index] == true;
	}
	
	private function updateLeadAnchorIndices(anchor:Number, lead:Number):Void {
		anchorIndex = anchor;
		leadIndex   = lead;
	}

	public function getAnchorSelectionIndex() : Number {
		return anchorIndex;
	}

	public function setAnchorSelectionIndex(index : Number) : Void {
		anchorIndex = index;
	}

	public function getLeadSelectionIndex() : Number {
		return leadIndex;
	}

	public function setLeadSelectionIndex(index : Number) : Void {
		leadIndex = index;
	}

	public function clearSelection() : Void {
		if(!isSelectionEmpty()){
			var max:Number = maxIndex;
			minIndex	= MAX;
			maxIndex	= MIN;
			clearSelectionImp();
			fireListSelectionEvent(0, max);
		}
	}
	
	private function clearSelectionImp():Void{
		value = [];
	}

	public function isSelectionEmpty() : Boolean {
		return minIndex > maxIndex;
	}
	
	public function insertIndexInterval(index:Number, length:Number, before:Boolean):Void{
		/* The first new index will appear at insMinIndex and the last
		 * one will appear at insMaxIndex
		 */
		var insMinIndex:Number = (before) ? index : index + 1;
		var insMaxIndex:Number = (insMinIndex + length) - 1;
	
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
	
		var leadIn:Number = leadIndex;
		if (leadIn > index || (before && leadIn == index)) {
			leadIn = leadIndex + length;
		}
		var anchorIn:Number = anchorIndex;
		if (anchorIn > index || (before && anchorIn == index)) {
			anchorIn = anchorIndex + length;
		}
		if (leadIn != leadIndex || anchorIn != anchorIndex) {
			updateLeadAnchorIndices(anchorIn, leadIn);
		}
		
		if(needInstertArray){
			fireListSelectionEvent(insMinIndex, insMaxIndex+length);
		}
	}

	public function removeIndexInterval(index0:Number, index1:Number):Void{
		var rmMinIndex:Number = Math.min(index0, index1);
		var rmMaxIndex:Number = Math.max(index0, index1);
		var gapLength:Number = (rmMaxIndex - rmMinIndex) + 1;

		var needFireEvent:Boolean = true;
		
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
			for(var i:Number = rmMinIndex-1; i>=minIndex; i--){
				maxIndex = i;
				if(value[i] == true){
					break;
				}
			}
		}else if(minIndex >= rmMinIndex && maxIndex > rmMaxIndex){
			value.splice(rmMinIndex, gapLength);
			maxIndex -= gapLength;
			for(var i:Number = rmMinIndex-1; i<=maxIndex; i++){
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

		var leadIn:Number = leadIndex;
		if (leadIn == 0 && rmMinIndex == 0) {
			// do nothing
		} else if (leadIn > rmMaxIndex) {
			leadIn = leadIndex - gapLength;
		} else if (leadIn >= rmMinIndex) {
			leadIn = rmMinIndex - 1;
		}

		var anchorIn:Number = anchorIndex;
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
			fireListSelectionEvent(rmMinIndex, rmMaxIndex+gapLength);
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
	public function setSelectionMode(selectionMode : Number) : Void {
		this.selectionMode = selectionMode;
	}

	public function getSelectionMode() : Number {
		return selectionMode;
	}

	public function addListSelectionListener(func : Function, obj : Object) : Object {
		return addEventListener(ON_SELECTION_CHANGED, func, obj);
	}
	
	private function fireListSelectionEvent(firstIndex:Number, lastIndex:Number):Void{
		dispatchEvent(createEventObj(ON_SELECTION_CHANGED, firstIndex, lastIndex));
	}
	
	public function toString():String{
		return "DefaultListSelectionModel[]";
	}
}
