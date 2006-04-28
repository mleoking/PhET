/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.mri.model.Detector;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.*;

/**
 * DetectorGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DetectorGraphic extends PNode {
    private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );

    public DetectorGraphic( Detector detector ) {
        Rectangle2D shape = new Rectangle2D.Double( 0,
                                                    0,
                                                    detector.getBounds().getWidth(),
                                                    detector.getBounds().getHeight() );
        PPath shapePNode = new PPath( shape );
        shapePNode.setPaint( new Color( 150, 150, 150 ));
        addChild( shapePNode );

        PText label = new PText( "Detector" );
        label.setFont( font );
        label.setTextPaint( Color.white );
        label.rotate( -Math.PI / 2);
        label.setJustification( JLabel.CENTER_ALIGNMENT );
        label.setOffset( 15, shape.getHeight() / 2);
        RegisterablePNode labelPNode = new RegisterablePNode( label );
        labelPNode.setRegistrationPoint( 0, -label.getWidth() / 2 );
        addChild( labelPNode );

        setOffset( detector.getBounds().getX(), detector.getBounds().getY() );
    }
}
