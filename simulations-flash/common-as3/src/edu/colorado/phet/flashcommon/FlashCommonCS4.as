package edu.colorado.phet.flashcommon {
import flash.display.DisplayObjectContainer;
import flash.display.Sprite;
import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.geom.Point;

public class FlashCommonCS4 extends FlashCommon {
    private static var flashInstance:FlashCommonCS4 = null;
    private var playAreaWidth:Number;
    private var playAreaHeight:Number;
    public function FlashCommonCS4( root:DisplayObjectContainer, playAreaWidth:Number, playAreaHeight:Number) {
        trace( "FlexCommon starting up" );
        this.root = root;
        this.playAreaWidth = playAreaWidth;
        this.playAreaHeight = playAreaHeight;

        CommonStrings.init( root.loaderInfo );
        SimStrings.init( root.loaderInfo );
    }

    override public function initialize( root:Sprite ):void {
        super.initialize( root );
        //Override the values set in AsWingManager
        root.stage.scaleMode=StageScaleMode.SHOW_ALL;
        root.stage.align = "";
    }

    public static function getInstance( root:DisplayObjectContainer, playAreaWidth:Number, playAreaHeight:Number):FlashCommonCS4 {
        if ( flashInstance == null ) {
            flashInstance = new FlashCommonCS4( root,playAreaWidth, playAreaHeight);
            instance = flashInstance;
        }
        return flashInstance;
    }

    override public function getFlashArg( key:String ):String {
        return root.loaderInfo.parameters[key];
    }
    public override function getPlayAreaWidth():Number {
        return playAreaWidth;
    }

    public override function getPlayAreaHeight():Number{
        return playAreaHeight;
    }
}
}