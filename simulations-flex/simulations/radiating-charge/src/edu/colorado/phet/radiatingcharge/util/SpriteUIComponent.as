
package edu.colorado.phet.radiatingcharge.util {
import flash.display.Sprite;

import flash.geom.Rectangle;
import mx.core.UIComponent;

public class SpriteUIComponent extends UIComponent {
    public function SpriteUIComponent(sprite:Sprite, resetRegistration:Boolean = false) {
        super();

        explicitHeight = sprite.height;
        explicitWidth = sprite.width;

        addChild(sprite);

        //registration point must be in upper-left corner of sprite or flex layout will not work properly
        if(resetRegistration){
            var rect:Rectangle =  sprite.getBounds(sprite.parent);
            sprite.x = -rect.x;
            sprite.y = -rect.y;
            //trace("sprite bounds of " + sprite.name + " = " + sprite.getBounds(sprite.parent))
        }

    }
}
}