




class TabHandler {
	
	// stores whether keyboard accessibility is currently active
	public var active : Boolean;
	
	// stores entries for each control in the tab order. all of TabEntry type
	public var entries : Array;
	
	// current index into the entries array
	public var currentIndex : Number;
	
	// text field that takes focus when focus is removed from a text field
	// this makes old text fields so that they do not have keyboard focus
	public var dummyText : TextField;
	
	// stores the last highlight
	public var lastHighlight : MovieClip;
	
	// highlighting methods, for addControl and insertControl
	public static var HIGHLIGHT_NONE : String = "none";
	public static var HIGHLIGHT_GLOBAL : String = "global";
	public static var HIGHLIGHT_LOCAL : String = "local";
	
	// constructor
	public function TabHandler() {
		trace("Initializing TabHandler");
		
		// make this accessible by _level0.tabHandler
		_level0.tabHandler = this;
		
		// disable default keyboard accessibility
		_root.tabIndex = 1;
		_root.tabEnabled = false;
		
		// catch key events to this object
		Key.addListener(this);
		
		// default variables
		active = false;
		entries = new Array();
		currentIndex = -1;
		
		// create dummy text field
		dummyText = _root.createTextField("dummyTxt", _root.getNextHighestDepth(), 0, 0, 0, 0);
		dummyText._visible = false;
		dummyText.type = "input";
		dummyText.tabIndex = 3;
		dummyText.tabEnabled = false;
	}
	
	// return the currently selected entry
	public function currentEntry() : TabEntry {
		return entries[currentIndex];
	}
	
	// return the currently selected control
	public function currentControl() : Object {
		return entries[currentIndex].control;
	}
	
	// insert obj into controls at the specified index
	public function insertControl(obj : Object, idx : Number, highlight : String) : Void {
		entries.splice(idx, 0, new TabEntry(obj, highlight));
	}
	
	// add the control to the end of the tab order
	public function addControl(obj : Object, highlight : String) : Void {
		insertControl(obj, entries.length, highlight);
	}
	
	// get an index for a control with the given object
	public function findIndex(obj : Object) : Number {
		for(var i : Number = 0; i < entries.length; i++) {
			if(entries[i].control == obj) {
				return i;
			}
		}
		
		// signifies that it is not found
		return -1;
	}
	
	// remove a control from the tab order
	public function removeControl(obj : Object) : Void {
		throw new Error("TabHandler.removeControl not implemented yet");
	}
	
	// register a callback to be called when obj is in focus and key is pressed
	public function registerKey(obj : Object, key : Number, callback : Function) : Void {
		//throw new Error("TabHandler.registerKey not implemented yet");
		var idx : Number = findIndex(obj);
		if(idx == -1) { return; }
		entries[idx].keys[key] = callback;
	}
	
	public function registerButton(obj : Object) : Void {
		var idx : Number = findIndex(obj);
		if(idx == -1) { return; }
		entries[idx].buttonlike = true;
	}
	
	// called when ANY key is pressed, anytime
	public function onKeyDown() {
		if(Key.getCode() == Key.TAB) {
			if(Key.isDown(Key.SHIFT)) {
				previous();
			} else {
				next();
			}
		} else if(active) {
			var f : Function = currentEntry().keys[Key.getCode()];
			if(f != undefined) {
				f();
			}
			if(currentEntry().buttonlike && (Key.getCode() == Key.SPACE || Key.getCode() == Key.ENTER)) {
				currentControl().onPress();
			}
		}
	}
	
	// called when ANY key is released, anytime
	public function onKeyUp() {
		if(active && currentEntry().buttonlike && (Key.getCode() == Key.SPACE || Key.getCode() == Key.ENTER)) {
			currentControl().onRelease();
		}
	}
	
	// draw the highlight on the movieclip with the specified bounds
	public function drawHighlights(entry : TabEntry, high : MovieClip, bounds : Object) {
		high.beginFill(0xFF0000);
		high.endFill();
		
		high.lineStyle(2, 0x0000FF);
		high.moveTo(bounds.xMin, bounds.yMin);
		high.lineTo(bounds.xMax, bounds.yMin);
		high.lineTo(bounds.xMax, bounds.yMax);
		high.lineTo(bounds.xMin, bounds.yMax);
		high.lineTo(bounds.xMin, bounds.yMin);
		//_root.debug.text += String(bounds.xMin) + ", " + String(bounds.yMin) + "\n";
		//_root.debug.text += String(bounds.xMax) + ", " + String(bounds.yMax) + "\n";
	}
	
	// used to set an entry to be the one in focus
	public function addFocus(entry : TabEntry) {
		//_root.debug.text += "AddFocus(" + String(entry.control) + ")\n";
		Selection.setFocus(entry.control);
		var bounds : Object;
		//_root.debug.text += entry.highlight + "\n";
		switch(entry.highlight) {
			case HIGHLIGHT_GLOBAL:
				lastHighlight = _root.createEmptyMovieClip("lastHighlight", _root.getNextHighestDepth());
				//_root.debug.text += "Type: " + typeof(entry.control) + "\n";
				if(typeof(entry.control) == "movieclip") {
					bounds = entry.control.getBounds(_root);
				} else if(typeof(entry.control) == "object") {
					bounds = new Object();
					var par : MovieClip = entry.control._parent;
					var pt : Object = {x:entry.control._x, y:entry.control._y};
					par.localToGlobal(pt);
					bounds.xMin = pt.x;
					bounds.yMin = pt.y;
					pt = {x:entry.control._x + entry.control._width, y:entry.control._y + entry.control._height};
					par.localToGlobal(pt);
					bounds.xMax = pt.x;
					bounds.yMax = pt.y;
				}
				
				// makes it so that this clip is displayed
				drawHighlights(entry, lastHighlight, bounds);
				break;
			case HIGHLIGHT_LOCAL:
				lastHighlight = entry.control.createEmptyMovieClip("lastHighlight", entry.control.getNextHighestDepth());
				bounds = entry.control.getBounds(entry.control);
				
				drawHighlights(entry, lastHighlight, bounds);
				break;
		}
	}
	
	// used to remove an entry from focus. done before a new entry is focused
	public function removeFocus(entry : TabEntry) {
		//_root.debug.text += "RemoveFocus(" + String(entry.control) + ")\n";
		Selection.setFocus(dummyText);
		if(entry.highlight != HIGHLIGHT_NONE) {
			lastHighlight.removeMovieClip();
		}
	}
	
	// called from objects when they are somehow selected (for instance, with the mouse)
	public function setFocus(obj : Object) : Void {
		// if we are already focused on this control, ignore
		if(obj == currentControl()) { return; }
		
		var idx : Number = findIndex(obj);
		
		// if obj isn't under tab control, ALSO ignore
		if(idx == -1) { return; }
		
		
		if(active) {
			removeFocus(currentEntry());
		}
		
		currentIndex = idx;
		if(active) {
			addFocus(currentEntry());
		}
	}
	
	// called when tab is pressed
	public function next() {
		//_root.debug.text = "Next\n";
		if(active) {
			removeFocus(currentEntry());
		}
		
		if(entries.length > 0) {
			active = true;
			currentIndex = (currentIndex + 1) % entries.length;
			addFocus(currentEntry());
		}
	}
	
	// called when shift-tab is pressed (tab backwards)
	public function previous() {
		//_root.debug.text = "Previous\n";
		if(active) {
			removeFocus(currentEntry());
		}
		
		if(entries.length > 0) {
			active = true;
			currentIndex = (currentIndex - 1 + entries.length) % entries.length;
			addFocus(currentEntry());
		}
	}
}

