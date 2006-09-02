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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.piccolo.PhetPCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Legend
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Legend extends JPanel {

    public Legend() {
        super( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        SimpleMolecule mA = new MoleculeA();
        SimpleMolecule mB = new MoleculeB();
        SimpleMolecule mC = new MoleculeC();

        PNode pA = new SpatialSimpleMoleculeGraphic( mA );
        PNode pB = new SpatialSimpleMoleculeGraphic( mB );
        PNode pC = new SpatialSimpleMoleculeGraphic( mC );

        PhetPCanvas canvasA = new PhetPCanvas();
        canvasA.setSize( 160, 160 );
        canvasA.setBackground( Color.red );
        canvasA.addScreenChild( new PPath( new Rectangle2D.Double( 2,2, 5,5 )) );
//        canvasA.addScreenChild( pA );

//        add( new JLabel( "A"), gbc );
        JPanel jp = new JPanel( );
        jp.add( canvasA );
        add( jp, gbc );
//        add( canvasA, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( "A"), gbc );
    }
}
