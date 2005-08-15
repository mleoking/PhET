/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.fourier.FourierConfig;


/**
 * WavePacketWidthTool is the tool used to measure the width of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacketWidthTool extends MeasurementTool {

    private static final Font TOOL_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 16 );
    private static final Color TOOL_COLOR = Color.RED;
    
    private HTMLGraphic _labelGraphic;
    
    public WavePacketWidthTool( Component component ) {
        super( component );
        _labelGraphic = new HTMLGraphic( component, TOOL_FONT, "", TOOL_COLOR );
        _labelGraphic.centerRegistrationPoint();
        setLabel( _labelGraphic, -15 );
        setFillColor( TOOL_COLOR );
        setStroke( null );
    }
    
    public void setLabel( String html ) {
        _labelGraphic.setHTML( html );
        _labelGraphic.centerRegistrationPoint();
    }
}
