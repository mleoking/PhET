package edu.colorado.phet.resonance {
import mx.containers.Canvas;
import mx.core.UIComponent;

public class Resonance extends Canvas {
    public function Resonance( w: Number, h: Number ) {
        this.addChild( new MainView( new ShakerModel( 10 ), w, h ) );
    }
}
}