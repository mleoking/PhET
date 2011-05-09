//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityAndBuoyancyPlayAreaComponent;

//TODO -
// This was initially confusing because it differs greatly from the concept of modes used in Gravity and Orbits,
// where each mode has a separate canvas and model. In this sim, there is one canvas and one model, shared
// by all modes.
//
// A bigger issue is whether this is a good design. Looking at Mode subclasses, all modes share one model,
// and their responsibility is to add/remove objects from the model.  Presumably this is to make updating
// the view easier; the view components just look at one model, and update when the model changes.
// But this makes it impossible for model state to be preserved when switching between modes, unless the
// mode holds DensityObject references, as is done in DensityCustomObjectMode. It's not a big deal
// in Custom model because there's only one DensityObject, but the mode does need to keep a reference to it in
// order to restore it.  If you wanted this "restore model" functionality in all cases (which I can easily
// imaging as a feature request) then it's going to be downright ugly for modes that have multiple DensityObjects.
// Why not have a separate model instance for each mode, and a currentModel observable that is used by view
// objects? Model information then lives in the model, the mode has no need to hold references to
// DensityObjects, and a mode simply sets the currentModel property to the model that's associated with the mode.
// If saving model state when switching modes isn't desired, then call model.reset when switching.
//

/**
 * A mode is a different configuration within a single module (tab), which can be selected via a radio button in the top-right of the canvas.
 */
public class Mode {
    protected var canvas: AbstractDensityAndBuoyancyPlayAreaComponent;

    public function Mode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        this.canvas = canvas;
    }

    //Called when a mode has been exited, the mode should remove any relevant parts from the model and view
    public function teardown(): void {
        canvas.model.teardown();
    }

    //Called when a mode is initialized, should create and initialize any relevant model or view components
    public function init(): void {
        //No AbstractMethodError is thrown since subclasses should call super.init() in case inheritance hierarchy changes or code is added here in the future
    }

    //Called when a mode is reset, should restore original values
    public function reset(): void {
        //No AbstractMethodError is thrown since subclasses should call super.init() in case inheritance hierarchy changes or code is added here in the future
    }
}
}