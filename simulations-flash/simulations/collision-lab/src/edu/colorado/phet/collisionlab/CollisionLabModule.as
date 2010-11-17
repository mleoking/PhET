package edu.colorado.phet.collisionlab {
import flash.display.DisplayObjectContainer;

public class CollisionLabModule {

    public function CollisionLabModule() {
    }

    public function resetAll(): void { throw new Error( "abstract" ); } // TODO: called from control panel. should be handled mainly here?

    public function attach( parent: DisplayObjectContainer ): void { throw new Error( "abstract" ); }
}
}