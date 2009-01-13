/*
 Copyright aswing.org, see the LICENCE.txt.
*/
  
/**
 * To successfully store and retrieve (key->value) mapping from a HashMap.
 * HashMap accept any type of object to be the key: number, string, Object etc... 
 * But it is only get fast accessing with string type keys. Others are slow.
 * <p>
 * ----------------------------------------------------------
 * This example creates a HashMap of friends. It uses the number of the friends as keys:
 * <pre>
 *     function person(name,age,sex){
 *         this.name=name;
 *         this.age=age;
 *         this.sex=sex;
 *     }
 *     var friends = new HashMap();
 *     friends.put("one", new person("iiley",21,"M"));
 *     friends.put("two", new person("gothic man",22,"M"));
 *     friends.put("three", new person("rock girl",19,"F"));
 * </pre>
 * <p>To retrieve a friends, use the following code:
 *
 * <pre>
 *     var thisperson = friends.get("two");
 *     if (thisperson != null) {
 *         trace("two name is "+thisperson.name);
 *         trace("two age is "+thisperson.age);
 *         trace("two sex is "+thisperson.sex);
 *     }else{
 *         trace("two is not in friends!");
 *     }
 * </pre>
 *
 * @author: iiley
 */
class org.aswing.util.HashMap{
    private var length:Number;
    private var content:Object;
    private var keyContent:Object;
	private var noneStringKeys:Array;
	private var noneStringMapedValues:Array;
	
	
 	public function HashMap(){
        length = 0;
        content = new Object();
        keyContent = new Object();
        noneStringKeys = new Array();
        noneStringMapedValues = new Array();
 	}

 	//-------------------public methods--------------------

 	/**
  	 * Returns the number of keys in this HashMap.
  	 */
 	public function size():Number{
  		return length;
 	}

 	/**
  	 * Returns if this HashMap maps no keys to values.
  	 */
 	public function isEmpty():Boolean{
  		return (length==0);
 	}

 	/**
  	 * Returns an Array of the keys in this HashMap.
  	 */
 	public function keys():Array{
  		var temp:Array = new Array(length);
  		var index:Number = 0;
  		for(var i:String in keyContent){
   			temp[index] = keyContent[i];
   			index++;
  		}
  		for(var i:Number=0; i<noneStringKeys.length; i++){
  			temp[index+i] = noneStringKeys[i];
  		}
  		return temp;
 	}
 	
 	/**
  	 * Returns an Array of the values in this HashMap.
  	 */
 	public function values():Array{
  		var temp:Array = new Array(length);
  		var index:Number = 0;
  		for(var i:String in content){
   			temp[index] = content[i];
   			index++;
  		}
  		for(var i:Number=0; i<noneStringMapedValues.length; i++){
  			temp[index+i] = noneStringMapedValues[i];
  		}
  		return temp;
 	}
 	
 	private function isStringKey(key):Boolean{
 		return (typeof(key) == "string" || key instanceof String);
 	}
 	 	
 	private function indexOfKey(key):Number{
 		for(var i:Number=0; i<noneStringKeys.length; i++){
 			if(key === noneStringKeys[i]){
 				return i;
 			}
 		}
 		return -1;
 	}
 	
 	private function indexOfValue(value):Number{
 		for(var i:Number=0; i<noneStringMapedValues.length; i++){
 			if(value === noneStringMapedValues[i]){
 				return i;
 			}
 		}
 		return -1; 		
 	}

 	/**
  	 * Tests if some key maps into the specified value in this HashMap. 
  	 * This operation is more expensive than the containsKey method.
  	 */
 	public function containsValue(value):Boolean{
  		for(var i:String in content){
   			if(content[i] === value){
    			return true;
   			}
  		}
  		if(indexOfValue(value) >= 0){
  			return true;
  		}
 		return false;
 	}

 	/**
  	 * Tests if the specified object is a key in this HashMap.
  	 * This operation is very fast if it is a string.
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
  	 */
 	public function containsKey(key):Boolean{
 		if(isStringKey(key)){
	 		if(content[key] != undefined){
	 			return true;
	 		}
 		}else{
	  		if(indexOfKey(key) >= 0){
	  			return true;
	  		}
 		}
  		return false;
 	}

 	/**
 	 * Returns the value to which the specified key is mapped in this HashMap.
 	 * Return null if the key is not mapped to any value in this HashMap.
  	 * This operation is very fast if the key is a string.
     * @param   key the key whose associated value is to be returned.
     * @return  the value to which this map maps the specified key, or
     *          <tt>null</tt> if the map contains no mapping for this key
     *           or it is null value originally.
 	 */
 	public function get(key){
 		if(isStringKey(key)){
	 		var value = content[key];
	 		if(value !== undefined){
	 			return value;
	 		}
 		}else{
	 		var index:Number = indexOfKey(key);
	 		if(index >= 0){
	 			return noneStringMapedValues[index];
	 		}
 		}
  		return null;
 	}

 	/**
 	 * Associates the specified value with the specified key in this map. 
 	 * If the map previously contained a mapping for this key, the old value is replaced. 
 	 * 
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the HashMap previously associated
     *	       <tt>null</tt> with the specified key.
  	 */
 	public function put(key, value){
  		if(key == undefined){
   			trace("cannot put a value with undefined or null key!");
   			return undefined;
  		}else{
  			var exist:Boolean = containsKey(key);
 			if(!exist){
   				length++;
 			}
 			var oldValue = this.get(key);
 			if(isStringKey(key)){
	   			content[key]=value;
	   			keyContent[key]=key;
 			}else{
 				if(!exist){
					noneStringKeys.push(key);
					noneStringMapedValues.push(value);
 				}else{
 					var index:Number = indexOfKey(key);
 					noneStringKeys[index] = key;
 					noneStringMapedValues[index] = value;
 				}
 			}
   			return oldValue;
  		}
 	}

 	/**
     * Removes the mapping for this key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key.
  	 */
 	public function remove(key){
 		var exist:Boolean = containsKey(key);
 		if(!exist){
 			return null;
 		}
  		var temp;
  		if(isStringKey(key)){
   			temp=content[key];
   			delete content[key];
   			delete keyContent[key];
  		}else{
 			var index:Number = indexOfKey(key);
 			temp = noneStringMapedValues[index];
 			noneStringKeys.splice(index, 1);
 			noneStringMapedValues.splice(index, 1);
  		}
   		length--;
  		return temp;
 	}
 	
 	/**
  	 * put a array's all element into the HashMap, 
  	 * key+index will be the key
  	 */
 	public function putArray(Arr:Array, key:String):Void{
  		for(var i:Number=0; i<Arr.length; i++){
   			put(key+i, Arr[i]);
  		}
 	}

 	/**
 	 * Clears this HashMap so that it contains no keys no values.
 	 */
 	public function clear():Void{
  		length = 0;
  		content = new Object();
  		keyContent = new Object();
        noneStringKeys = new Array();
        noneStringMapedValues = new Array();
 	}

 	/**
 	 * Return a same copy of HashMap object
 	 */
 	public function clone():HashMap{
  		var temp:HashMap = new HashMap();
  		for(var i:String in content){
   			temp.put(keyContent[i], content[i]);
  		}
  		for(var i:Number=0; i<noneStringKeys.length; i++){
  			temp.put(noneStringKeys[i], noneStringMapedValues[i]);
  		}
  		return temp;
 	}

 	public function toString():String{
  		var ks:Array = keys();
  		var vs:Array = values();
  		var temp:String = "HashMap Content:\n";
  		for(var i:Number=0; i<ks.length; i++){
   			temp += ks[i]+" -> "+vs[i] + "\n";
  		}
  		return temp;
 	}	
}
