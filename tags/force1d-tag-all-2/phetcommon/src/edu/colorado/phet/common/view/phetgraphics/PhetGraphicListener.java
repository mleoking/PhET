package edu.colorado.phet.common.view.phetgraphics;

/**
 * User: University of Colorado, PhET
 * Date: Nov 15, 2004
 * Time: 5:50:26 PM
 * Copyright (c) Nov 15, 2004 by University of Colorado, PhET
 */
public interface PhetGraphicListener {
    public void phetGraphicMoved( PhetGraphic phetGraphic );

    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic );

    public void phetGraphicContentChanged( PhetGraphic phetGraphic );
}
