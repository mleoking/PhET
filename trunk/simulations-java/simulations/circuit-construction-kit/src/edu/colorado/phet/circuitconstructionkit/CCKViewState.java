package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jul 20, 2010
 * Time: 4:18:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CCKViewState {
    private BooleanProperty readoutsVisible = new BooleanProperty(false);
    private BooleanProperty lifelikeProperty = new BooleanProperty(true);

    public void resetAll() {
        readoutsVisible.reset();
    }

    public void setReadoutsVisible(boolean r) {
        readoutsVisible.setValue(r);
    }

    public BooleanProperty getReadoutsVisibleProperty() {
        return readoutsVisible;
    }

    public BooleanProperty getLifelikeProperty() {
        return lifelikeProperty;
    }

    public void setLifelike(boolean lifelike) {
        getLifelikeProperty().setValue(lifelike);
    }
}