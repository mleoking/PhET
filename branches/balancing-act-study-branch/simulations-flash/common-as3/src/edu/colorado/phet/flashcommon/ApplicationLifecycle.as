/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/2/10
 * Time: 10:26 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon {
import flash.display.Stage;

//REVIEW document this
public class ApplicationLifecycle {
    private static const listeners: Array = new Array();
    private static const applicationLifecycle: ApplicationLifecycle = new ApplicationLifecycle();
    private static var notified: Boolean = false;
    public static var stage: Stage;

    public static function addApplicationCompleteListener( f: Function ): void {
        listeners.push( f );
    }

    //REVIEW doc that this call when the last event is dispatched during an Flex application startup (see mx:Application tag, applicationComplete attribute)
    public static function applicationComplete( _stage: Stage ): void {
        stage = _stage;
        if ( !notified ) {
            for each ( var listener: Function in listeners ) {
                listener();
            }
            notified = true;
        }
    }
}
}
