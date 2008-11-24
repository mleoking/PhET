/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.KeyType;
import org.aswing.KeyMap;
import org.aswing.util.Delegate;
import org.aswing.util.Timer;
import org.aswing.util.Vector;

/**
 * KeyboardController controlls the key map for the action firing.
 * <p>
 * Thanks Romain for his Fever{@link http://fever.riaforge.org} accelerator framworks implementation, 
 * this is a simpler implementation study from his.
 * 
 * @see org.aswing.KeyMap
 * @see org.aswing.KeyType
 * @author iiley
 */
class org.aswing.KeyboardManager {
	
	private static var instance:KeyboardManager;
	
	private var keymaps:Vector;
	private var keySequence:Vector;
	private var selfKeyMap:KeyMap;
	private var checkMissedUpTimer:Timer;
	
	private function KeyboardManager(){
		keymaps = new Vector();
		keySequence = new Vector();
		selfKeyMap = new KeyMap();
		registerKeyMap(selfKeyMap);
		
		Key.addListener({
			onKeyDown:Delegate.create(this, ____onKeyDown), 
			onKeyUp:Delegate.create(this, ____onKeyUp)
		});
		checkMissedUpTimer = new Timer(100, true);
		checkMissedUpTimer.addActionListener(__checkMissedUp, this);
	}
	
	/**
	 * Returns the global keyboard controller.
	 */
	public static function getInstance():KeyboardManager{
		if(instance == null){
			instance = new KeyboardManager();
		}
		return instance;
	}
	
	/**
	 * Registers a key action map to the controller
	 */
	public function registerKeyMap(keyMap:KeyMap):Void{
		if(!keymaps.contains(keyMap)){
			keymaps.append(keyMap);
		}
	}
	
	/**
	 * Unregisters a key action map to the controller
	 */
	public function unregisterKeyMap(keyMap:KeyMap):Void{
		keymaps.remove(keyMap);
	}
	
	/**
	 * Registers a key action to the default key map of this controller.
	 */
	public function registerKeyAction(key:KeyType, func:Function, contex:Object):Void{
		selfKeyMap.registerKeyAction(key, func, contex);
	}
	
	/**
	 * Unregisters a key action to the default key map of this controller.
	 */
	public function unregisterKeyAction(key:KeyType):Void{
		selfKeyMap.unregisterKeyAction(key);
	}
	
	private function __onKeyDown() : Void {
		var code:Number = Key.getCode();
		if(!keySequence.contains(code)){
			keySequence.append(code);
		}
		var n:Number = keymaps.size();
		for(var i:Number=0; i<n; i++){
			var keymap:KeyMap = KeyMap(keymaps.get(i));
			keymap.fireKeyAction(keySequence.toArray());
		}
		if(!checkMissedUpTimer.isRunning()){
			checkMissedUpTimer.start();
		}
	}

	private function __onKeyUp() : Void {
		var code:Number = Key.getCode();
		keySequence.remove(code);
		if(keySequence.isEmpty()){
			checkMissedUpTimer.stop();
		}
	}
	
	private function __checkMissedUp():Void{
		for(var i:Number=keySequence.size()-1; i>=0; i--){
			if(!Key.isDown(keySequence.get(i))){
				keySequence.removeAt(i);
			}
		}
		if(keySequence.isEmpty()){
			checkMissedUpTimer.stop();
		}
	}
	
	//Don't override this method
	private function ____onKeyDown() : Void {
		__onKeyDown();
	}
	//Don't override this method
	private function ____onKeyUp() : Void {
		__onKeyUp();
	}
}