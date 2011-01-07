// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.solublesalts.view.zoom.SSZoomHandler;

/**
 * SSCanvas
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SSCanvas extends PhetPCanvas {

    private boolean zoomEnabled;
    private SSZoomHandler zoomHandler = new SSZoomHandler();

    public SSCanvas( Dimension renderingSize ) {
        super( renderingSize );
    }

    public void setZoomEnabled( boolean zoomEnabled ) {
        if ( !this.zoomEnabled && zoomEnabled ) {
            setZoomEventHandler( zoomHandler );
        }
        else if ( this.zoomEnabled && !zoomEnabled ) {
            setZoomEventHandler( null );
        }
        this.zoomEnabled = zoomEnabled;
    }
}
