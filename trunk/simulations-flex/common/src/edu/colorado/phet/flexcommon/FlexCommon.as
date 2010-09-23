package edu.colorado.phet.flexcommon {
import edu.colorado.phet.flashcommon.FlashCommon;

import mx.core.Application;

public class FlexCommon extends FlashCommon {
    public function FlexCommon() {
        trace( "FlexCommon starting up" );
    }

    private static var flexInstance:FlexCommon = null;

    public static function getInstance():FlexCommon {
        if ( flexInstance == null ) {
            flexInstance = new FlexCommon();
            instance = flexInstance;
        }
        return flexInstance;
    }

    override public function getFlashArg( key:String ):String {
        return Application.application.parameters[key];
    }
}

}