package edu.colorado.phet.resonance {
import mx.core.UIComponent;

public class Resonance extends UIComponent {
    public function Resonance( w: Number, h: Number ) {
        this.addChild( new MainView( new ShakerModel( 10 ), w, h ) );
    }
}
}