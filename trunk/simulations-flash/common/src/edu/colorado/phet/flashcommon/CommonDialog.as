import org.aswing.JFrame;
import org.aswing.util.Delegate;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.CommonDialog {

    public var prefix : String;

    public var tabHandler : TabHandler;

    public var common : FlashCommon;

    public var window : JFrame;

    // shorthand for debugging function
    public function debug( str : String ) {
        _level0.debug(str);
    }

    public function CommonDialog( dialogPrefix : String, dialogTitle : String ) {
        prefix = dialogPrefix;

        // shortcut to FlashCommon, but now with type-checking!
        common = _level0.common;

        _level0[ prefix + "Dialog" ] = this;

        window = new JFrame( _level0, dialogTitle );

        // the window shouldn't be resizable
		window.setResizable(false);

		// make sure we can access it from anywhere
		_level0[ prefix + "Window" ] = window;

        // set the background to default
		window.setBackground( common.backgroundColor );

        tabHandler = new TabHandler( false );

        window.addEventListener( JFrame.ON_WINDOW_CLOSING, Delegate.create( this, closeClicked ) );
        window.addEventListener( JFrame.ON_FOCUS_GAINED, Delegate.create( this, windowClicked ) );
    }

    // only call this the first time
    public function displayMe() {
        // fit the window to its contents
		window.setSize( window.getPreferredSize() );

		// center the window
		window.setLocation( ( Stage.width - window.getWidth() ) / 2, ( Stage.height - window.getHeight() ) / 2 );
		window.show();

        setupTabHandler();

        common.keyboardHandler.addTabHandler( tabHandler );
		common.keyboardHandler.setTabHandler( tabHandler );
    }

    public function setupTabHandler() {
        throw new Error( "This should be overridden" );
    }

    public function manualClose() {
        window.hide();
        onClose();
    }

    public function manualOpen() {
        tabHandler.reset();
        window.show();
        onOpen();
    }

    public function closeClicked( src : Object ) {
		onClose();
	}

    public function windowClicked( src : Object ) {
        debug( "Focus changed!!!" );
        tabHandler.giveMeFocus();
    }

    public function onClose() {
		_level0.keyboardHandler.removeTabHandler( tabHandler );
	}
    
    public function onOpen() {
        _level0.keyboardHandler.addTabHandler( tabHandler );
        _level0.keyboardHandler.setTabHandler( tabHandler );
    }


    
    public static function openAboutDialog() {
        if(_level0.aboutWindow) {
            _level0.aboutDialog.manualOpen();
		} else {
            new AboutDialog();
		}
    }

    public static function openPreferencesDialog() {
        if(_level0.preferencesWindow) {
            _level0.preferencesDialog.manualOpen();
		} else {
            new PreferencesDialog();
		}
    }
}
