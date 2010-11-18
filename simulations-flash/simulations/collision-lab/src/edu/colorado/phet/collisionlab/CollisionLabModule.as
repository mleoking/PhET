package edu.colorado.phet.collisionlab {
import edu.colorado.phet.collisionlab.model.Model;

import flash.display.DisplayObjectContainer;

public class CollisionLabModule {

    public var myModel: Model;

    private var lastPlaying: Boolean = false;

    public function CollisionLabModule() {
        myModel = createModel();
    }

    public function resetAll(): void { throw new Error( "abstract" ); } // TODO: called from control panel. should be handled mainly here?

    public function attach( parent: DisplayObjectContainer ): void { throw new Error( "abstract" ); }

    public function createModel(): Model { throw new Error( "abstract" ); }

    public function onHide(): void {
        lastPlaying = myModel.playing;
        if ( myModel.playing ) {
            myModel.stopMotion();
        }
    }

    public function onShow(): void {
        if ( lastPlaying ) {
            myModel.startMotion();
        }
    }

    public function allowAddRemoveBalls(): Boolean {
        return true;
    }
}
}