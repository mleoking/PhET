// TabHandler.as
//
// Handles keyboard accessibility by use of the TAB key
//
// Author: Jonathan Olson

import org.aswing.JButton;
import org.aswing.util.Delegate;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.TabHandler {
	
	// stores whether keyboard accessibility is currently active. currently
	// it defaults to false, and only becomes active (true) if the tab key
	// is pressed
	public var active : Boolean;
	
	// stores entries for each control in the tab order. all of TabEntry type
	public var entries : Array;
	
	// current index into the entries array. entries[currentIndex] has the
	// keyboard focus
	public var currentIndex : Number;
	
	// text field that takes focus when focus is removed from a text field
	// this makes old text fields so that they do not have keyboard focus
	public static var dummyText : TextField;
	
	// stores the most recent highlight movieclip (where graphics are drawn
	// to highlight the control currently in focus)
	public var lastHighlight : MovieClip;
	
	// highlighting methods, for addControl and insertControl
	public static var HIGHLIGHT_NONE : String = "none"; // no highlighting is done
	public static var HIGHLIGHT_GLOBAL : String = "global"; // highlighting is done on a movieclip in _root
	public static var HIGHLIGHT_LOCAL : String = "local"; // highlighting is done on a child movieclip (moves with the object)
	
	public var common : FlashCommon;
	
	private var debugMain : Boolean;

    private static var handlerCount : Number = 0;

    private var handlerId : Number;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function TabHandler( main : Boolean ) {
		//debug("Initializing TabHandler\n");
		debugMain = main;
        handlerId = handlerCount++;
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// make this accessible by _level0.tabHandler
		if( main ) {
			_level0.tabHandler = this;
			common.tabHandler = this;
        }

        // if we are the first tabhandler active, prepare everything
        if( handlerId == 0 ) {
			
			// disable default keyboard accessibility
			_root.tabIndex = 1;
			_root.tabEnabled = false;
			MovieClip.prototype.tabEnabled = false;
			
			// create dummy text field
			dummyText = _root.createTextField("dummyTxt", 9092, 0, 0, 0, 0);
			dummyText._visible = false;
			dummyText.type = "input";
			dummyText.tabIndex = 3524534;
			dummyText.tabEnabled = false;
		}
		
		// default variables
		active = false;
		entries = new Array();
		currentIndex = -1;
	}
	
	// return the currently selected entry
	public function currentEntry() : TabEntry {
		return entries[currentIndex];
	}
	
	// return the currently selected control
	public function currentControl() : Object {
		return entries[currentIndex].control;
	}
	
	public function insertEntry( entry : TabEntry, idx : Number) : Void {
		entries.splice(idx, 0, entry);
        if( currentIndex >= idx ) {
            currentIndex++;
        }
	}
	
	public function addEntry( entry : TabEntry ) : Void {
		insertEntry( entry, entries.length );
	}
	
	public function addAsWingButton( obj : Object ) : Void {
		addEntry( TabEntry.createASwingEntry( obj ) );
		registerButton( obj.trigger_mc );
        //obj.addEventListener( JButton.ON_PRESS, Delegate.create( this, aswingButtonPressed ) );
	}

    public function addAsWingCheckBox( obj : Object ) : Void {
		addEntry( TabEntry.createASwingEntry( obj ) );
		registerButton( obj.trigger_mc );
        // TODO: possibly add listener for keys to automatically switch the checkbox?
        // downside: wouldn't send modified events to listeners
	}
	
	// insert obj into controls at the specified index
	public function insertControl(obj : Object, idx : Number, highlight : String) : Void {
		entries.splice(idx, 0, new TabEntry(obj, highlight));
        if( currentIndex >= idx ) {
            currentIndex++;
        }
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

    public function findEntryIndex( entry : TabEntry ) : Number {
        for( var i : Number = 0; i < entries.length; i++ ) {
            if( entries[i] == entry ) {
                return i;
            }
        }

        return -1;
    }
	
	// remove a control from the tab order
	public function removeControl(obj : Object) {
		var idx : Number = findIndex( obj );
        if( idx == -1 ) {
            return;
        }
        if( currentIndex == idx ) {
            if( entries.length > 1 ) {
                next();
            } else {
                removeFocus( currentEntry() );
                reset();
            }
        }
        entries.splice( idx, 1 );
        if( currentIndex >= idx ) {
            currentIndex--;
        }
	}

    public function removeEntry( entry : TabEntry ) {
        removeControl( entry.control );
    }
	
	// register a callback to be called when obj is in focus and key is pressed
	public function registerKey(obj : Object, key : Number, callback : Function) {
		//throw new Error("TabHandler.registerKey not implemented yet");
		var idx : Number = findIndex(obj);
		if(idx == -1) {
			debug("WARNING TabHandler: registerKey object is not found\n");
			return;
		}
		entries[idx].keys[key] = callback;
		//debug("registering " + String(callback) + "\n");
	}
	
	// make a specific object in the tab-order act like a button. this will
	// cause space/enter keyboard events to fire specific events within the
	// control to simulate a button-press and release.
	public function registerButton(obj : Object) {
		var idx : Number = findIndex(obj);
		if(idx == -1) { return; }
		entries[idx].buttonlike = true;
	}
	
	// called when ANY key is pressed, anytime
	public function onKeyDown() {
		// hack to hopefully make AsWing work with this
		if(_level0.aswing_focusFrontHolderMC) {
			_level0.aswing_focusFrontHolderMC.removeMovieClip();
		}
		
		if(Key.getCode() == Key.TAB || Key.getCode() == 106) {
			//debug("Old focus: " + Selection.getFocus() + "\n");
			if(Key.isDown(Key.SHIFT)) {
				previous();
			} else {
				next();
			}
			//debug("Current focus: " + Selection.getFocus() + "\n");
		} else if(active) {
            /*
			var f : Function = currentEntry().keys[Key.getCode()];
			if(f != undefined) {
				//debug("TabHandler: keypress!\n");
				f();
			}
			if(currentEntry().buttonlike && (Key.getCode() == Key.SPACE || Key.getCode() == Key.ENTER)) {
				currentControl().onPress();
			}
			*/
            currentEntry().onKeyDown( Key.getCode() );
		}

        if( Key.getCode() == Key.ESCAPE ) {
            onEscape();
        }
	}
	
	// called when ANY key is released, anytime
	public function onKeyUp() {
        /*
		if(active && currentEntry().buttonlike && (Key.getCode() == Key.SPACE || Key.getCode() == Key.ENTER)) {
			currentControl().onRelease();
		}
		*/
        currentEntry().onKeyUp( Key.getCode() );
	}
	
	// used to set an entry to be the one in focus
	public function addFocus(entry : TabEntry) {
		_level0.debug( "TabHandler AddFocus() " + toString() + "\n" );
		
		//debug("TabHandler: Adding focus to:\n" + entry.toString());
		
		Selection.setFocus(entry.control);
		entry.control.addFocus();
		
		
		
		var bounds : Object;
		switch(entry.highlight) {
			case HIGHLIGHT_NONE:
				// if no automatic highlighting is done, we need to notify the control
				// to highlight itself in a custom manner
				
				// MADE DEFAULT FOR ALL
				break;
			case HIGHLIGHT_GLOBAL:
				// sanity check
				if(lastHighlight._parent !== undefined) {
					_level0.debug("WARNING: TabHandler addFocus encountered already existing lastHighlight\n");
                    _level0.debug( "Depth: " + String(lastHighlight._depth) );
					lastHighlight.removeMovieClip();
					_level0.debug("!!!\n");
				}
				
				// global, so we create a movieclip on _root, storing it in 'lastHighlight'
				// TODO: remove hack that overwrites locations randomly. AS2 to blame!
				//lastHighlight = _root.createEmptyMovieClip("lastHighlight", 1000000 + Math.round(Math.random() * 5000));
                var newDepth = _root.getNextHighestDepth();
                if( newDepth > 50000 ) {
                    newDepth -= 724;
                }
				lastHighlight = _root.createEmptyMovieClip("lastHighlight", newDepth );
				
				//if( !debugMain ) { throw new Error( "Boo" ); }
				
				if(typeof(entry.control) == "movieclip") {
					// if it is a movieclip, we can get the exact bounding box from the root perspective.
					bounds = entry.control.getBounds(_root);
				} else if(typeof(entry.control) == "object") {
					// otherwise it is either a button or textfield. if a textfield, the following code
					// will present the correct answer, however the runtime dimensions of a button
					// (not a movieclip acting as a button) cannot be determined.
					bounds = new Object();
					
					// the parent of the object must be a movieclip, we use this to our advantage
					var par : MovieClip = entry.control._parent;
					
					// find the offset relative to the parent
					var pt : Object = {x:entry.control._x, y:entry.control._y};
					
					// transform this into global coordinates
					par.localToGlobal(pt);
					
					// set the upper-left corner of the bounds
					bounds.xMin = pt.x;
					bounds.yMin = pt.y;
					
					// find what would conceptually be the lower-right corner (unless a button does not start at 0,0)
					pt = {x:entry.control._x + entry.control._width, y:entry.control._y + entry.control._height};
					
					// transform this into global coordinates
					par.localToGlobal(pt);
					
					// set the lower-right corner of the bounds
					bounds.xMax = pt.x;
					bounds.yMax = pt.y;
				}
				
				// draw the highlights around the control
				entry.drawHighlights(lastHighlight, bounds);
				break;
			case HIGHLIGHT_LOCAL:
				var mc : MovieClip = MovieClip(entry.getHighlightObject());
				
				// create a child movieclip on our target
				lastHighlight = mc.createEmptyMovieClip("lastHighlight", mc.getNextHighestDepth());
				
				// must be a movieclip, so bounds calculation is simple
				bounds = mc.getBounds(mc);
				
				// draw the highlights around the control
				entry.drawHighlights(lastHighlight, bounds);
				break;
		}
	}
	
	// used to remove an entry from focus. done before a new entry is focused
	public function removeFocus(entry : TabEntry) {
        _level0.debug( "TabHandler removeFocus() " + toString() + "\n" );
		//debug("TabHandler: Removing focus from:\n" + entry.toString());
		entry.control.removeFocus();
		Selection.setFocus(dummyText);
		if(entry.highlight != HIGHLIGHT_NONE) {
			lastHighlight.removeMovieClip();
		} else {
			// made default for all
		}
	}
	
	// called from objects when they are somehow selected (for instance, with the mouse)
	public function setFocus(obj : Object) {
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
		
		//_level0.debug("---\nactive: " + active + "\nentries.length:" + entries.length + "\n");
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
		if(active) {
			removeFocus(currentEntry());
		}
		
		if(entries.length > 0) {
			active = true;
			currentIndex = (currentIndex - 1 + entries.length) % entries.length;
			addFocus(currentEntry());
		}
	}

    public function reset() {
        active = false;
        currentIndex = -1;
    }

    public function onEscape() {
        if( active ) {
            active = false;
            removeFocus( currentEntry() );
        }
    }
	
	public function onAddFocus() {
        _level0.debug( "TabHandler onAddFocus() " + toString() + "\n" );
		if(entries.length > 0 && active) {
            active = true;
            addFocus(currentEntry());
		}
	}
	
	public function onRemoveFocus() {
        _level0.debug( "TabHandler onRemoveFocus() " + toString() + "\n" );
		if( active ) {
			removeFocus( currentEntry() );
		}
	}

    public function giveMeFocus() {
        _level0.keyboardHandler.setTabHandler( this );
    }

    public function aswingButtonPressed( src : Object ) {
        giveMeFocus();
    }

    public function toString() : String {
        return "#" + String( handlerId ) + " active:" + String( active ) + " entries:" + String( entries.length ); 
    }
}

