package edu.colorado.phet.resonance {
import flash.display.Sprite;

import mx.core.UIComponent;

public class SpriteUIComponent extends UIComponent {
    public function SpriteUIComponent(sprite:Sprite) {
        super();

        explicitHeight = sprite.height;
        explicitWidth = sprite.width;

        addChild(sprite);
    }
}
}