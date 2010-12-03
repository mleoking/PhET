/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/2/10
 * Time: 10:26 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon {
import flash.display.Stage;

public class StageHandler {
    private static const listeners = new Array();
    private static const stageHandler: StageHandler = new StageHandler();
    private static var notified: Boolean = false;

    public static function addStageCreationListener( f: Function ): void {
        listeners.push( f );
    }

    public static function notifyListeners( stage: Stage ): void {
        trace( stage );
        if ( !notified ) {
            for each ( var listener: Function in listeners ) {
                listener( stage );
            }
            notified = true;
        }
    }
}
}
