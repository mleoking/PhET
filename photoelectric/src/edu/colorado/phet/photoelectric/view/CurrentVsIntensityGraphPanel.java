/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;

import java.awt.*;

/**
 * CurrentVsIntensityGraphPanel
 * <p/>
 * An ApparatusPanel2 that contains a CurrentVsIntensityGraph, for putting in Swing
 * Containters
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentVsIntensityGraphPanel extends ApparatusPanel2 {
    public CurrentVsIntensityGraphPanel( PhotoelectricModel model,
                                         AbstractClock clock,
                                         int graphInsetX,
                                         int graphInsetY ) {
        super( clock );

        setUseOffscreenBuffer( true );
        setDisplayBorder( false );
        CurrentVsIntensityGraph currentVsIntensityGraph = new CurrentVsIntensityGraph( this, model );
        setPreferredSize( new Dimension( 230, 170 ) );
        currentVsIntensityGraph.setLocation( graphInsetX, graphInsetY );
        addGraphic( currentVsIntensityGraph );
    }
}
