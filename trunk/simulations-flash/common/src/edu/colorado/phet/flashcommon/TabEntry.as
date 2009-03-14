// TabEntry.as
//
// Used in TabHandler. Stores all of the information
// necessary for an object in the tab order.
//
// Author: Jonathan Olson

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.TabEntry {
	
	// the actual object in the tab order (which is a movieclip, textfield, etc)
	public var control : Object;
	
	// whether to highlight the border, and if so, what method to use
	// should be a value of either TabHandler.HIGHLIGHT_NONE,
	// TabHandler.HIGHLIGHT_GLOBAL or TabHandler.HIGHLIGHT_LOCAL
	public var highlight : String;
	
	// which keypresses to handle and pass to callbacks. this is stored
	// by creating attributes (properties) in the keys object with the
	// name equal to the Key code (Key.getCode()), and the value equal to
	// the callback which should be called
	public var keys : Object;
	
	// stores whether this tab entry should act like a button. this connects
	// enter/space to certain events that are used with movieclips acting like
	// a button
	public var buttonlike : Boolean;
	
	// constructor
	public function TabEntry(obj : Object, high : String) {
		control = obj;
		highlight = high;
		if(highlight == undefined) {
			highlight = TabHandler.HIGHLIGHT_GLOBAL;
		}
		keys = new Object();
		buttonlike = false;
	}
	
	public function toString() : String {
		var str : String = "";
		str += "\tTabEntry\n";
		str += "\t\t" + String(control) + "\n";
		str += "\t\t" + (highlight == TabHandler.HIGHLIGHT_NONE ? "no highlight" : (highlight == TabHandler.HIGHLIGHT_GLOBAL ? "global highlight" : "local highlight")) + "\n";
		str += "\t\t";
		for(var i : String in keys) {
			str += i + " ";
		}
		str += "\n";
		/*
		for(var i : String in control) {
			str += "\t\t\t" + i + " : " + String(control[i]) + "\n";
		}
		*/
		str += "\t\tbuttonlike: " + String(buttonlike) + "\n";
		return str;
	}
}
