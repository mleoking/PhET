package edu.colorado.phet.flexcommon {
import edu.colorado.phet.flashcommon.CommonStrings;
import edu.colorado.phet.flashcommon.FlashCommon;

import mx.core.Application;

public class FlexCommon extends FlashCommon {
    public function FlexCommon() {
        trace( "FlexCommon starting up" );
        
        var commonStrings: * = Application.application.parameters.commonStrings;
        if ( commonStrings != null && commonStrings != undefined ) {
            edu.colorado.phet.flashcommon.CommonStrings.initDocument( new XML( commonStrings ) );
        }
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

    override public function getPlayAreaWidth():Number {
        return root.stage.stageWidth;
    }

    override public function getPlayAreaHeight():Number {
        return root.stage.stageHeight;
    }
}

}