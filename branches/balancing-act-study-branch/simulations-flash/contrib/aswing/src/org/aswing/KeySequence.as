/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.KeyStroke;
import org.aswing.KeyType;

/**
 * Key Sequence, defines a key sequence.
 * <p>
 * Thanks Romain for his Fever{@link http://fever.riaforge.org} accelerator framworks implementation, 
 * this is a simpler implementation study from his.
 * @author iiley
 */
class org.aswing.KeySequence implements KeyType {
	
	/** Constant definition for concatenation character. */
	public static var LIMITER : String = "+";
	
	private var codeString:String;
	private var codeSequence:Array;
	
	/**
	 * KeySequence(key1:KeyStroke, key2:KeyStroke, ...)<br>
	 * KeySequence(description:String, codeSequence:Array)<br>
	 * Create a key definition with keys.
	 */
	public function KeySequence(){
		if(arguments[0] instanceof KeyStroke){
			var key:KeyStroke = KeyStroke(arguments[0]);
			codeSequence = [key.getCode()];
			codeString = key.getDescription();
			for(var i:Number=1; i<arguments.length; i++){
				key = KeyStroke(arguments[i]);
				codeString += (LIMITER+key.getDescription());
				codeSequence.push(key.getCode());
			}
		}else{
			if(arguments[1] instanceof Array){
				codeString = arguments[0].toString();
				codeSequence = arguments[1].concat();
			}else{
				trace("/e/ KeySequence constructing error!!");
				throw new Error("KeySequence constructing error!!");
			}
		}
	}
	
	public function getDescription() : String {
		return codeString;
	}

	public function getCodeSequence() : Array {
		return codeSequence.concat();
	}
	
	public function toString():String{
		return "KeySequence[" + getDescription + "]";
	}
}