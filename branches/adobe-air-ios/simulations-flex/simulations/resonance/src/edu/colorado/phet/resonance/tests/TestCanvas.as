package edu.colorado.phet.resonance.tests {
import mx.containers.Canvas;

public class TestCanvas extends Canvas {
    public function TestCanvas( w: Number, h: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.addChild( new TestMainView( w, h ) );
    }
}
}