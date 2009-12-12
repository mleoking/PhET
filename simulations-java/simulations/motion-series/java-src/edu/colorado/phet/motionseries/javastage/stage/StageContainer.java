package edu.colorado.phet.motionseries.javastage.stage;

import java.awt.geom.Rectangle2D;

interface StageContainer {
    Rectangle2D getContainerBounds();

    void addListener(Listener listener);

    public static interface Listener {
        void changed();
    }
}
