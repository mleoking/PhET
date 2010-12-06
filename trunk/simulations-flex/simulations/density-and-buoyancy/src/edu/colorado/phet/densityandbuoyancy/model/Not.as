/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/6/10
 * Time: 3:22 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.model {
public class Not extends BooleanProperty {
    public function Not( predicate: BooleanProperty ) {
        super( !predicate.value );
        predicate.addListener( function(): void {
            value = !predicate.value;
        } );
        addListener( function(): void {
            predicate.value = !value;
        } );
    }
}
}
