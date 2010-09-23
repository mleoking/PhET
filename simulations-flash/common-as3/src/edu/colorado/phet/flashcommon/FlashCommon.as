package edu.colorado.phet.flashcommon {
import flash.display.Sprite;

import org.aswing.AsWingManager;
import org.aswing.FlowLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JWindow;

public class FlashCommon {

    private static var instance:FlashCommon = null;

    public var commonButtons : CommonButtons;

    public static function getInstance():FlashCommon {
        if ( instance == null ) {
            instance = new FlashCommon();
        }
        return instance;
    }

    /**
     * This is a singleton class. No easy way to enforce, so for now, don't call this.
     */
    public function FlashCommon() {
    }

    public function initialize( root: Sprite ):void {
        AsWingManager.initAsStandard( root, false, true );

        commonButtons = new CommonButtons( root );

    }

    public function fromPhetWebsite() : Boolean {
        return false;// TODO remove this, temporary to show preferences button
    }
}
}