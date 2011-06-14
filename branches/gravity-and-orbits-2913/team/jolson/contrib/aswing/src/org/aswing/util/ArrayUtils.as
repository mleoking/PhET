/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 

 
/**
 * Utils functions about Array.
 * @author iiley
 */
class org.aswing.util.ArrayUtils {
	/**
	 * Call the operation by pass each element of the array once.
	 * <p>
	 * for example:
	 * <pre>
	 * //hide all component in vector components
	 * ArrayUtils.each( 
	 *     components,
	 *     function(c:Component){
	 *         c.setVisible(false);
	 *     });
	 * <pre>
	 * @param arr the array for each element will be operated.
	 * @param the operation function for each element
	 * @see Vector#each
	 */
	public static function each(arr:Array, operation:Function):Void{
		for(var i:Number=0; i<arr.length; i++){
			operation(arr[i]);
		}
	}
	
	/**
	 * Sets the size of the array. If the new size is greater than the current size, 
	 * new undefined items are added to the end of the array. If the new size is less than 
	 * the current size, all components at index newSize and greater are removed.
	 * @param arr the array to resize
	 * @param size the new size of this vector 
	 */
	public static function setSize(arr:Array, size:Number):Void{
		//TODO test this method
		if(size < 0) size = 0;
		if(size == arr.length){
			return;
		}
		if(size > arr.length){
			arr[size - 1] = undefined;
		}else{
			arr.splice(size);
		}
	}
	
	/**
	 * Removes the object from the array and return the index.
	 * @return the index of the object, -1 if the object is not in the array
	 */
	public static function removeFromArray(arr:Array, obj:Object):Number{
		for(var i:Number=0; i<arr.length; i++){
			if(arr[i] == obj){
				arr.splice(i, 1);
				return i;
			}
		}
		return -1;
	}
	
	public static function removeAllFromArray(arr:Array, obj:Object):Void{
		for(var i:Number=0; i<arr.length; i++){
			if(arr[i] == obj){
				arr.splice(i, 1);
				i--;
			}
		}
	}
	
	public static function removeAllBehindSomeIndex(array:Array , index:Number):Void{
		if(index <= 0){
			array.splice(0, array.length);
			return;
		}
		var arrLen:Number = array.length;
		for(var i:Number=index+1 ; i<arrLen ; i++){
			array.pop();
		}
	}	
	
	public static function indexInArray(arr:Array, obj:Object):Number{
		for(var i:Number=0; i<arr.length; i++){
			if(arr[i] == obj){
				return i;
			}
		}
		return -1;
	}
	
	public static function cloneArray(arr:Array):Array{
		return arr.concat();
	}
}
