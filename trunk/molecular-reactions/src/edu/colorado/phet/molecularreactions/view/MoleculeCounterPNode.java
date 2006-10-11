/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.colorado.phet.molecularreactions.model.*;

import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.*;

/**
 * MoleculeCounter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeCounterPNode extends PNode {

    public MoleculeCounterPNode( PSwingCanvas canvas, MRModel model, Class moleculeClass ) {

        // Spinners
        PPath outerBackground = new PPath( new RoundRectangle2D.Double( 0, 0, 50, 50, 5, 5 ) );
        outerBackground.setPaint( new Color( 50, 30, 10 ) );
        addChild( outerBackground );
        PPath innerBackground = new PPath( new RoundRectangle2D.Double( 5, 20, 40, 20, 5, 5 ) );
        innerBackground.setPaint( Color.white );
        addChild( innerBackground );

        MoleculeCountSpinner moleculeCounter = new MoleculeCountSpinner( moleculeClass, model );
        moleculeCounter.setBorder( null );
        PSwing readout = new PSwing( canvas, moleculeCounter );
        readout.setOffset( 10, 23 );
        addChild( readout );

        // Labels
        String s = null;
        if( moleculeClass == MoleculeA.class ) {
            s = "A";
        }
        if( moleculeClass == MoleculeAB.class ) {
            s = "AB";
        }
        if( moleculeClass == MoleculeBC.class ) {
            s = "BC";
        }
        if( moleculeClass == MoleculeC.class ) {
            s = "C";
        }
        JLabel label = new JLabel( s );
        label.setHorizontalAlignment( SwingConstants.CENTER );
        PSwing labelPSwing = new PSwing( canvas, label );
        labelPSwing.setOffset( 25, 3 );
        PPath labelBackground = new PPath( new RoundRectangle2D.Double( 5, 3, 40, 15, 5, 5 ) );
        labelBackground.setPaint( Color.white );
        addChild( labelBackground );
        addChild( labelPSwing );
    }
}
