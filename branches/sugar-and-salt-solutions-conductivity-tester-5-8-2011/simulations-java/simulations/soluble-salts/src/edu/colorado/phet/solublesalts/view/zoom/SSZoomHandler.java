// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view.zoom;

import java.awt.event.InputEvent;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

/**
 * ZoomHandler
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SSZoomHandler extends PZoomEventHandler {


    public SSZoomHandler() {
        getEventFilter().setAndMask( InputEvent.BUTTON1_MASK );
        setMaxScale( 10 );
        setMinScale( 1 );
//        getEventFilter().setAndMask(InputEvent.BUTTON1_MASK |
//                                              InputEvent.SHIFT_MASK);
    }


    public void mousePressed( PInputEvent e ) {
        super.mousePressed( e );
    }


}
