/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;

/**
 *
 * @author John Blanco
 */
public class QuadEmissionFrequencyControlPanel extends PNode {

    public QuadEmissionFrequencyControlPanel( PhotonAbsorptionModel model ){

        // Create the main background shape.
        PNode backgroundNode = new PhetPPath( new RoundRectangle2D.Double(0, 0, 800, 150, 10, 10), Color.LIGHT_GRAY );

        // Add the radio buttons that set the emission frequency.

        // Add everything in the needed order.
        addChild( backgroundNode );
    }
}
