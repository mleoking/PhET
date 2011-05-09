
import edu.colorado.phet.flashcommon.old.*;

class edu.colorado.phet.flashcommon.old.KeyboardHandler {
	
	public var mainTabHandler : TabHandler;
	
	public var tabHandlers : Array;
	public var currentIndex : Number;
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : void {
		_level0.debug(str);
	}
	
	public function KeyboardHandler() {
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		
		tabHandlers = new Array();
		mainTabHandler = new TabHandler( true );
		tabHandlers.push( mainTabHandler );
		currentIndex = 0;
		
		_level0.keyboardHandler = this;
		
		Key.addListener( this );
	}
	
	public function getCurrentTabHandler() : TabHandler {
		return tabHandlers[currentIndex];
	}
	
	public function getTabHandlerIndex( handler : TabHandler ) : Number {
		for( var i : Number = 0; i < tabHandlers.length; i++ ) {
			if( tabHandlers[i] == handler ) {
				return i;
			}
		}
		
		return -1;
	}
	
	public function addTabHandler( handler : TabHandler ) {
        _level0.debug( "KeyboardHandler addTabHandler( " + handler.toString() + " )\n" );
		tabHandlers.push( handler );
	}
	
	public function removeTabHandler( handler : TabHandler ) {
        _level0.debug( "KeyboardHandler removeTabHandler( " + handler.toString() + " )\n" );
		var idx : Number = getTabHandlerIndex( handler );
		if( idx == -1 ) {
			return;
		}

        var changeHandler : Boolean = getCurrentTabHandler() == handler;

        if( changeHandler ) {
            handler.onRemoveFocus();
        }

		tabHandlers.splice( idx, 1 );
		if( currentIndex >= idx ) {
			currentIndex--;
		}

        if( changeHandler ) {
            getCurrentTabHandler().onAddFocus();
        }
	}
	
	public function setTabHandler( newHandler : TabHandler ) {
        _level0.debug( "KeyboardHandler setTabHandler( " + newHandler.toString() + " )\n" );
		if( newHandler == getCurrentTabHandler() ) {
			return;
		}
		
		getCurrentTabHandler().onRemoveFocus();
		
		var idx : Number = getTabHandlerIndex( newHandler );
		
		if( idx == -1 ) {
			addTabHandler( newHandler );
			idx = getTabHandlerIndex( newHandler );
		}
		
		currentIndex = idx;
		
		newHandler.onAddFocus();
	}
	
	
	public function onKeyDown() {
		//if( currentIndex != 0 ) { return; }
		getCurrentTabHandler().onKeyDown();
	}
	
	public function onKeyUp() {
		//if( currentIndex != 0 ) { return; }
		getCurrentTabHandler().onKeyUp();
	}
	
}