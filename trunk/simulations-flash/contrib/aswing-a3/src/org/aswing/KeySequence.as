/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * Key Sequence, defines a key sequence.
 * <p>
 * Thanks Romain for his Fever{@link http://fever.riaforge.org} accelerator framworks implementation, 
 * this is a simpler implementation study from his.
 * @author iiley
 */
public class KeySequence implements KeyType{
	
	/** Constant definition for concatenation character. */
	public static const LIMITER : String = "+";
	
	private var codeString:String;
	private var codeSequence:Array;
	
	/**
	 * KeySequence(key1:KeyStroke, key2:KeyStroke, ...)<br>
	 * KeySequence(description:String, codeSequence:Array)<br>
	 * Create a key definition with keys.
	 * @throws ArgumentError when arguments is not illegular.
	 */
	public function KeySequence(...arguments){
		if(arguments[0] is KeyStroke){
			var key:KeyStroke = KeyStroke(arguments[0]);
			codeSequence = [key.getCode()];
			codeString = key.getDescription();
			for(var i:Number=1; i<arguments.length; i++){
				key = KeyStroke(arguments[i]);
				codeString += (LIMITER+key.getDescription());
				codeSequence.push(key.getCode());
			}
		}else{
			if(arguments[1] is Array){
				codeString = arguments[0].toString();
				codeSequence = arguments[1].concat();
			}else{
				throw new ArgumentError("KeySequence constructing error!!");
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
}