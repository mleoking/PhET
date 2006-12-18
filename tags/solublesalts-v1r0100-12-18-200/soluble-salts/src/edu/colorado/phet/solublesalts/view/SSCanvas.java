/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.solublesalts.view.zoom.SSZoomHandler;

import java.awt.*;

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
        if( !this.zoomEnabled && zoomEnabled ) {
            setZoomEventHandler( zoomHandler );
        }
        else if( this.zoomEnabled && !zoomEnabled ){
            setZoomEventHandler( null );
        }
        this.zoomEnabled = zoomEnabled;
    }
}
