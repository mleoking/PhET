package edu.colorado.phet.flashcommon {
import flash.display.Sprite;

public class FlashCommonCS4 extends FlashCommon {
    public function FlashCommonCS4( root:Sprite ) {
        trace( "FlexCommon starting up" );
        this.root = root;

        CommonStrings.init( root.loaderInfo );
    }

    private static var flashInstance:FlashCommonCS4 = null;

    public static function getInstance( root:Sprite ):FlashCommonCS4 {
        if ( flashInstance == null ) {
            flashInstance = new FlashCommonCS4( root );
            instance = flashInstance;
        }
        return flashInstance;
    }

    override public function getFlashArg( key:String ):String {
        return root.loaderInfo.parameters[key];
    }
}
}