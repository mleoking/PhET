package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.common.phetcommon.model.MutableBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jul 20, 2010
 * Time: 4:18:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CCKViewState {
    private MutableBoolean readoutsVisible = new MutableBoolean(false);
    private MutableBoolean lifelikeProperty = new MutableBoolean(true);

    public void resetAll() {
        readoutsVisible.reset();
    }

    public void setReadoutsVisible(boolean r) {
        readoutsVisible.setValue(r);
    }

    public MutableBoolean getReadoutsVisibleProperty() {
        return readoutsVisible;
    }

    public MutableBoolean getLifelikeProperty() {
        return lifelikeProperty;
    }

    public void setLifelike(boolean lifelike) {
        getLifelikeProperty().setValue(lifelike);
    }
}