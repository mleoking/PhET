package edu.colorado.phet.common.view.lightweight;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 7:45:29 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public interface GraphicListener {
    void boundsChanged( LightweightGraphic lightweightGraphic );

    void paintChanged( LightweightGraphic lightweightGraphic );

}
