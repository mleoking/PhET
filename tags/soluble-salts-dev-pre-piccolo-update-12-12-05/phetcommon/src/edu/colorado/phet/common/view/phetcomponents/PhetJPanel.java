/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.common.view.phetcomponents;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class PhetJPanel extends GraphicLayerSet {

    /**
     * @param ap
     * @param jp
     */
    public PhetJPanel( ApparatusPanel ap, JPanel jp ) {
        super( ap );

        // Get Swing to set the locations of the components of the panel
        JFrame dummyFrame = new JFrame();
        Container contentPane = dummyFrame.getContentPane();
        contentPane.add( jp );
        dummyFrame.pack();

        // Add a PhetJComponent for the the JPanel itself
        PhetGraphic graphic = PhetJComponent.newInstance( ap, jp );
        this.addGraphic( graphic );

        // Go through the components in the JPanel and add aPhetJComponent for
        // each of them. Use the location that Swing determined for the JComponent
        // for the PhetJComponent
        Component[] components = jp.getComponents();
        PhetGraphic pjc = null;
        for( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            Point location = new Point( (int)component.getLocation().getX(),
                                        (int)component.getLocation().getY() );
            if( component instanceof JComponent ) {
//                pjc = new PhetJComponent( ap, (JComponent)component );
                pjc = PhetJComponent.newInstance( ap, jp );
            }
            if( component instanceof JPanel ) {
                pjc = new PhetJPanel( ap, (JPanel)component );
            }
            this.addGraphic( pjc );
            pjc.setLocation( location );
        }
    }
}
