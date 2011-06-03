/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/31/11
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import mx.containers.Canvas;

public class NormalModes extends Canvas {
    public function NormalModes( w: Number, h: Number ) {
        percentWidth = 100;
        percentHeight = 100;
        this.addChild( new MainView( new Model( ), w, h ) );
    }
}
}
