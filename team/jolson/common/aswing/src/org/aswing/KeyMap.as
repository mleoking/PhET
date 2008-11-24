/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.KeyType;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;

/**
 * KeyMap is a key definition -> action map.
 * @see org.aswing.KeyDefinition
 * @author iiley
 */
class org.aswing.KeyMap {
	
	private var map:HashMap;
	
	/**
	 * Creates a key map.
	 */
	public function KeyMap(){
		map = new HashMap();
	}
	
	/**
	 * registerKeyAction(key:KeyDefinition, func:Function, contex:Object)<br>
	 * registerKeyAction(key:KeyDefinition, func:Function)
	 * <p>
	 * Registers a key definition -> action pair to the map. If same key definition is already 
	 * in the map, it will be replaced with the new one.
	 * @param key the key definition.
	 * @param func the aciton function
	 * @param contex (Optional)the function contex
	 */
	public function registerKeyAction(key:KeyType, func:Function, contex:Object):Void{
		if(contex != undefined){
			func = Delegate.create(contex, func);
		}
		map.put(getCodec(key), {key:key, func:func});
	}
	
	/**
	 * Unregisters a key and its action value.
	 * @param key the key and its value to be unrigesterd.
	 */
	public function unregisterKeyAction(key:KeyType):Void{
		map.remove(getCodec(key));
	}
	
	/**
	 * Returns the action from the key defintion.
	 * @param key the key definition
	 * @return the action.
	 * @see #getCodec()
	 */
	public function getKeyAction(key:KeyType):Function{
		return getKeyActionWithCodec(getCodec(key));
	}
	
	private function getKeyActionWithCodec(codec:String):Function{
		return map.get(codec).func;
	}
	
	/**
	 * Fires a key action with key sequence.
	 */
	public function fireKeyAction(keySequence:Array):Void{
		var codec:String = getCodecWithKeySequence(keySequence);
		var action:Function = getKeyActionWithCodec(codec);
		action();
	}
	
	/**
	 * Returns whether the key definition is already registered.
	 * @param key the key definition
	 */
	public function containsKey(key:KeyType):Boolean{
		return map.containsKey(getCodec(key));
	}
	
	/**
	 * Returns the codec of a key definition, same codec means same key definitions.
	 * @param key the key definition
	 * @return the codec of specified key definition
	 */
	public static function getCodec(key:KeyType):String{
		return getCodecWithKeySequence(key.getCodeSequence());
	}
	
	/**
	 * Returns the codec of a key sequence.
	 * @param keySequence the key sequence
	 * @return the codec of specified key sequence
	 */
	public static function getCodecWithKeySequence(keySequence:Array):String{
		return keySequence.join("|");
	}
}