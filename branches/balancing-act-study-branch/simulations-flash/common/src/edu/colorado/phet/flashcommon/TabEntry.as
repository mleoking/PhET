// TabEntry.as
//
// Used in TabHandler. Stores all of the information
// necessary for an object in the tab order.
//
// Author: Jonathan Olson

import flash.geom.Rectangle;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.TabEntry {
	
	// the actual object in the tab order (which is a movieclip, textfield, etc)
	public var control : Object;
	
	// whether to highlight the border, and if so, what method to use
	// should be a value of either TabHandler.HIGHLIGHT_NONE,
	// TabHandler.HIGHLIGHT_GLOBAL or TabHandler.HIGHLIGHT_LOCAL
	public var highlight : String;
	
	public var highlightObject : Object;
	
	// which keypresses to handle and pass to callbacks. this is stored
	// by creating attributes (properties) in the keys object with the
	// name equal to the Key code (Key.getCode()), and the value equal to
	// the callback which should be called
	public var keys : Object;

    public var keyups : Object;
	
	// stores whether this tab entry should act like a button. this connects
	// enter/space to certain events that are used with movieclips acting like
	// a button
	public var buttonlike : Boolean;

    public var highlightWidth : Number = 2.0;
    public var highlightInset : Number = 1.0;
    public var highlightColor : Number = 0x0000FF;

    public var manualBounds : Rectangle;
	
	// constructor
	public function TabEntry(obj : Object, high : String, highobj : Object) {
		control = obj;
		highlight = high;
		highlightObject = highobj;
		if(highlight == undefined) {
			highlight = TabHandler.HIGHLIGHT_GLOBAL;
		}
		keys = new Object();
		buttonlike = false;
	}
	
	public static function createASwingEntry( obj : Object ) : TabEntry {
		return new TabEntry( obj.trigger_mc, TabHandler.HIGHLIGHT_LOCAL, obj.root_mc );
	}
	
	public function getHighlightObject() : Object {
		if( highlightObject ) {
			return highlightObject;
		} else {
			return control;
		}
	}

    public function onKeyDown( keyCode : Number ) {
        var f : Function = keys[ keyCode ];
        if(f != undefined) {
            f();
        }

        if( buttonlike && ( keyCode == Key.SPACE || keyCode == Key.ENTER ) ) {
		    control.onPress();
		}
    }

    public function onKeyUp( keyCode : Number ) {
        var f : Function = keyups[ keyCode ];
        if(f != undefined) {
            f();
        }

        if( buttonlike && ( keyCode == Key.SPACE || keyCode == Key.ENTER ) ) {
		    control.onRelease();
		}
    }

    // draw the highlight on the movieclip with the specified bounds
	public function drawHighlights( high : MovieClip, bounds : Object ) {

		var xa : Number = bounds.xMin + highlightInset;
		var xb : Number = bounds.xMax - highlightInset;
		var ya : Number = bounds.yMin + highlightInset;
		var yb : Number = bounds.yMax - highlightInset;

        if( manualBounds ) {
            xa = manualBounds.left;
            xb = manualBounds.right;
            ya = manualBounds.top;
            yb = manualBounds.bottom;
        }

		high.lineStyle( highlightWidth, highlightColor );
		high.moveTo(xa, ya);
		high.lineTo(xb, ya);
		high.lineTo(xb, yb);
		high.lineTo(xa, yb);
		high.lineTo(xa, ya);
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
		str += "\t\tbuttonlike: " + String(buttonlike) + "\n";
		return str;
	}
}
