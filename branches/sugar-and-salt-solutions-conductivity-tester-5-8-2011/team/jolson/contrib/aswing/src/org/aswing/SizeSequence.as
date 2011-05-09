/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * A <code>SizeSequence</code> object
 * efficiently maintains an ordered list 
 * of sizes and corresponding positions. 
 * One situation for which <code>SizeSequence</code> 
 * might be appropriate is in a component
 * that displays multiple rows of unequal size.
 * In this case, a single <code>SizeSequence</code> 
 * object could be used to track the heights
 * and Y positions of all rows.
 * <p>
 * Another example would be a multi-column component,
 * such as a <code>JTable</code>,
 * in which the column sizes are not all equal.
 * The <code>JTable</code> might use a single
 * <code>SizeSequence</code> object 
 * to store the widths and X positions of all the columns.
 * The <code>JTable</code> could then use the
 * <code>SizeSequence</code> object
 * to find the column corresponding to a certain position.
 * The <code>JTable</code> could update the
 * <code>SizeSequence</code> object
 * whenever one or more column sizes changed.
 *
 * @author iiley
 */
class org.aswing.SizeSequence {
	
	private var a:Array;

	public function SizeSequence(numEntries:Number, value:Number){
		a = [];
		insertEntries(0, numEntries, value);
	}
	
    /**
     * Resets this <code>SizeSequence</code> object,
     * using the data in the <code>sizes</code> argument.
     * This method reinitializes this object so that it
     * contains as many entries as the <code>sizes</code> array.
     * Each entry's size is initialized to the value of the 
     * corresponding item in <code>sizes</code>.
     * 
     * @param sizes  the array of sizes to be contained in
     *		     this <code>SizeSequence</code>
     */	
	public function setSizes(sizes:Array):Void{
		if (a.length != sizes.length){
			a = new Array(sizes.length);
		}
		setSizesFromTo(0, a.length, sizes);
	}
	private function setSizesFromTo(from:Number, to:Number, sizes:Array):Number{
		if (to <= from){
			return 0;
		}
		var m:Number = Math.floor((from + to) / 2);
		a[m] = (sizes[m] + setSizesFromTo(from, m, sizes));
		return (a[m] + setSizesFromTo(m + 1, to, sizes));
	}
	
    /**
     * Returns the size of all entries.
     * 
     * @return  a new array containing the sizes in this object
     */	
	public function getSizes():Array{
		var n:Number = a.length;
		var sizes:Array = new Array(n);
		getSizesFromTo(0, n, sizes);
		return sizes;
	}
	private function getSizesFromTo(from:Number, to:Number, sizes:Array):Number{
		if (to <= from){
			return 0;
		}
		var m:Number = Math.floor((from + to) / 2);
		sizes[m] = (a[m] - getSizesFromTo(from, m, sizes));
		return (a[m] + getSizesFromTo(m + 1, to, sizes));
	}
	

    /**
     * Returns the start position for the specified entry.
     * For example, <code>getPosition(0)</code> returns 0,
     * <code>getPosition(1)</code> is equal to
     *   <code>getSize(0)</code>,
     * <code>getPosition(2)</code> is equal to
     *   <code>getSize(0)</code> + <code>getSize(1)</code>,
     * and so on.
     * <p>Note that if <code>index</code> is greater than
     * <code>length</code> the value returned may
     * be meaningless.
     * 
     * @param index  the index of the entry whose position is desired
     * @return       the starting position of the specified entry
     */	
	public function getPosition(index:Number):Number{
		return getPositionFromTo(0, a.length, index);
	}
	private function getPositionFromTo(from:Number, to:Number, index:Number):Number{
		if (to <= from){
			return 0;
		}
		var m:Number = Math.floor((from + to) / 2);
		if (index <= m){
			return getPositionFromTo(from, m, index);
		}else{
			return (a[m] + getPositionFromTo(m + 1, to, index));
		}
	}
	

    /**
     * Returns the index of the entry
     * that corresponds to the specified position.
     * For example, <code>getIndex(0)</code> is 0,
     * since the first entry always starts at position 0.
     * 
     * @param position  the position of the entry
     * @return  the index of the entry that occupies the specified position
     */	
	public function getIndex(position:Number):Number{
		return getIndexFromTo(0, a.length, position);
	}
	private function getIndexFromTo(from:Number, to:Number, position:Number):Number{
		if (to <= from){
			return from;
		}
		var m:Number = Math.floor((from + to) / 2);
		var pivot:Number = a[m];
		if (position < pivot){
			return getIndexFromTo(from, m, position);
		}else{
			return getIndexFromTo(m + 1, to, position - pivot);
		}
	}
	

    /**
     * Returns the size of the specified entry.
     * If <code>index</code> is out of the range 
     * <code>(0 <= index < getSizes().length)</code>
     * the behavior is unspecified.
     * 
     * @param index  the index corresponding to the entry
     * @return  the size of the entry
     */	
	public function getSize(index:Number):Number{
		return (getPosition(index + 1) - getPosition(index));
	}
	
    /**
     * Sets the size of the specified entry.
     * Note that if the value of <code>index</code>
     * does not fall in the range:
     * <code>(0 <= index < getSizes().length)</code>
     * the behavior is unspecified.
     * 
     * @param index  the index corresponding to the entry
     * @param size   the size of the entry
     */
	public function setSize(index:Number, size:Number):Void{
		changeSize(0, a.length, index, size - getSize(index));
	}
	private function changeSize(from:Number, to:Number, index:Number, delta:Number):Void{
		if (to <= from){
			return ;
		}
		var m:Number = Math.floor((from + to) / 2);
		if (index <= m){
			a[m] += delta;
			changeSize(from, m, index, delta);
		}else{
			changeSize(m + 1, to, index, delta);
		}
	}

    /**
     * Adds a contiguous group of entries to this <code>SizeSequence</code>.
     * Note that the values of <code>start</code> and
     * <code>length</code> must satisfy the following
     * conditions:  <code>(0 <= start <= getSizes().length)
     * AND (length >= 0)</code>.  If these conditions are
     * not met, nothing will be done except a trace.
     * 
     * @param start   the index to be assigned to the first entry
     * 		      in the group
     * @param length  the number of entries in the group
     * @param value   the size to be assigned to each new entry
     */	
	public function insertEntries(start:Number, length:Number, value:Number):Void{
		var sizes:Array = getSizes();
		if(!(start >= 0 && start <= sizes.length && length >= 0)){
			trace("ArrayIndexOutOfBoundsException, the method call return with nothing done");
			trace("start = " + start);
			trace("sizes = " + sizes);
			trace("length = " + length);
			return;
		}
		var end:Number = (start + length);
		var n:Number = (a.length + length);
		a = new Array(n);
		for (var i:Number = 0; i < start; i++){
			a[i] = sizes[i];
		}
		for (var i:Number = start; i < end; i++){
			a[i] = value;
		}
		for (var i:Number = end; i < n; i++){
			a[i] = sizes[(i - length)];
		}
		setSizes(a);
	}

    /**
     * Removes a contiguous group of entries
     * from this <code>SizeSequence</code>.
     * Note that the values of <code>start</code> and
     * <code>length</code> must satisfy the following
     * conditions:  <code>(0 <= start <= getSizes().length)
     * AND (length >= 0)</code>.  If these conditions are
     * not met, nothing will be done except a trace.
     * 
     * @param start   the index of the first entry to be removed
     * @param length  the number of entries to be removed
     */	
	public function removeEntries(start:Number, length:Number):Void{
		var sizes:Array = getSizes();
		if(!(start >= 0 && start <= sizes.length && length >= 0)){
			trace("ArrayIndexOutOfBoundsException, the method call return with nothing done");
			trace("start = " + start);
			trace("sizes = " + sizes);
			trace("length = " + length);			
			return;
		}
		var end:Number = (start + length);
		var n:Number = (a.length - length);
		a = new Array(n);
		for (var i:Number = 0; i < start; i++){
			a[i] = sizes[i];
		}
		for (var i:Number = start; i < n; i++){
			a[i] = sizes[(i + length)];
		}
		setSizes(a);
	}
}