package edu.colorado.phet.flashcommon {

import flash.display.Stage;

import org.aswing.JFrame;
import org.aswing.event.FrameEvent;

public class CommonDialog extends JFrame {

    public var prefix: String;

    //    public var tabHandler : TabHandler;

    public var common: FlashCommon;

    public var window: JFrame;

    // shorthand for debugging function
    public static function debug( str: String ): void {
        FlashCommon.getInstance().debug( str );
    }

    public function CommonDialog( dialogPrefix: String, dialogTitle: String ) {
        prefix = dialogPrefix;

        // shortcut to FlashCommon, but now with type-checking!
        common = FlashCommon.getInstance();

        //        _level0[ prefix + "Dialog" ] = this;

        window = new JFrame( FlashCommon.getInstance().root, dialogTitle );

        // the window shouldn't be resizable
        window.setResizable( false );

        // make sure we can access it from anywhere
        //		_level0[ prefix + "Window" ] = window;

        // set the background to default
        window.setBackground( common.backgroundColor );

        //        tabHandler = new TabHandler( false );

        window.addEventListener( FrameEvent.FRAME_CLOSING, closeClicked );
        //        window.addEventListener( JFrame.ON_FOCUS_GAINED, Delegate.create( this, windowClicked ) ); // TODO: no event lik this?
    }

    // only call this the first time
    public function displayMe(): void {
        // fit the window to its contents
        //        window.setSize( window.getPreferredSize() );
        window.pack();
        // center the window
        window.setLocationXY( common.getPlayAreaWidth()/2-window.getWidth()/2, common.getPlayAreaHeight()/2-window.getHeight()/2 );
        window.show();

        //        setupTabHandler();

        //        common.keyboardHandler.addTabHandler( tabHandler );
        //		common.keyboardHandler.setTabHandler( tabHandler );
    }

    //    public function setupTabHandler():void {
    //        throw new Error( "This should be overridden" );
    //    }

    public function manualClose(): void {
        window.hide();
        onClose();
    }

    public function manualOpen(): void {
        //        tabHandler.reset();
        window.show();
        onOpen();
    }

    public function closeClicked( evt: FrameEvent ): void {
        onClose();
    }

    public function windowClicked( src: Object ): void {
        debug( "Focus changed!!!" );
        //        tabHandler.giveMeFocus();
    }

    public function onClose(): void {
        //		_level0.keyboardHandler.removeTabHandler( tabHandler );
    }

    public function onOpen(): void {
        //        _level0.keyboardHandler.addTabHandler( tabHandler );
        //        _level0.keyboardHandler.setTabHandler( tabHandler );
    }


    public static function openAboutDialog(): void {
        debug( "opening about dialog" );
        if ( AboutDialog.getInstance() != null ) {
            AboutDialog.getInstance().manualOpen();
        }
        else {
            new AboutDialog();
        }
    }

    public static function openAgreementDialog(): void {
        debug( "opening agreement dialog" );
        if ( AgreementDialog.getInstance() != null ) {
            AgreementDialog.getInstance().manualOpen();
        }
        else {
            new AgreementDialog();
        }
    }


    public static function openCreditsDialog(): void {
        debug( "opening credits dialog" );
        if ( CreditsDialog.getInstance() != null ) {
            CreditsDialog.getInstance().manualOpen();
        }
        else {
            new CreditsDialog();
        }
    }

    public static function openMessageDialog( title: String, message: String, ok: Boolean ): void {
        debug( "opening message dialog" );
        new MessageDialog( title, message, ok );
    }

    public static function openPreferencesDialog(): void {
        debug( "opening preferences dialog" );
        if ( PreferencesDialog.getInstance() != null ) {
            PreferencesDialog.getInstance().manualOpen();
        }
        else {
            new PreferencesDialog();
        }
    }

    public static function openStatisticsDetailsDialog(): void {
        debug( "opening statistics details dialog" );
        if ( StatisticsDetailsDialog.getInstance() != null ) {
            StatisticsDetailsDialog.getInstance().manualOpen();
        }
        else {
            new StatisticsDetailsDialog();
        }
    }

    public static function openUpdateInstallationDetailsDialog(): void {
        debug( "opening installation details dialog" );
        if ( UpdateInstallationDetailsDialog.getInstance() != null ) {
            UpdateInstallationDetailsDialog.getInstance().manualOpen();
        }
        else {
            new UpdateInstallationDetailsDialog();
        }
    }

    public static function openUpdateInstallationDialog( installerTimestamp: Number, installerAskLaterDays: Number ): void {
        debug( "opening update installation dialog" );
        if ( UpdateInstallationDialog.getInstance() != null ) {
            UpdateInstallationDialog.getInstance().manualOpen();
        }
        else {
            new UpdateInstallationDialog( installerTimestamp, installerAskLaterDays );
        }
    }

    public static function openUpdateSimDialog( versionMajor: Number, versionMinor: Number, versionDev: Number, versionRevision: Number, simAskLaterDays: Number ): void {
        debug( "opening update sim dialog" );
        if ( UpdateSimDialog.getInstance() != null ) {
            UpdateSimDialog.getInstance().manualOpen();
        }
        else {
            new UpdateSimDialog( versionMajor, versionMinor, versionDev, versionRevision, simAskLaterDays );
        }
    }
}
}