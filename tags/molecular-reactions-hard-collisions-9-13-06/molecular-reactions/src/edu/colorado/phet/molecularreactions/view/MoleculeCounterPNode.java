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
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.RoundRectangle2D;
import java.awt.*;

/**
 * MoleculeCounter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeCounterPNode extends PNode /*implements PublishingModel.ModelListener */{
//    private PText readout;
    private Class moleculeClass;

    public MoleculeCounterPNode( PSwingCanvas canvas, MRModel model, Class moleculeClass ) {
        this.moleculeClass = moleculeClass;
        PPath outerBackground = new PPath( new RoundRectangle2D.Double( 0, 0, 40, 30, 5, 5 ) );
        outerBackground.setPaint( new Color( 50, 30, 10 ) );
        addChild( outerBackground );
        PPath innerBackground = new PPath( new RoundRectangle2D.Double( 5, 5, 30, 20, 5, 5 ) );
        innerBackground.setPaint( Color.white );
        addChild( innerBackground );

        MoleculeCounter moleculeCounter = new MoleculeCounter( 2,
                                                               moleculeClass,
                                                               model );
        moleculeCounter.setBorder( null );
        PSwing readout = new PSwing( canvas, moleculeCounter );
//        readout = new PText( "0" );
//        readout = new PText( "0" );
//        readout.setPaint( Color.white );
//        readout.setBounds( 0, 0, 30, 15 );
        readout.setOffset( 10, 8 );
        addChild( readout );

//        model.addListener( this );
    }

//    public void modelElementAdded( ModelElement element ) {
//        if( moleculeClass.isInstance( element ) ) {
//            int cnt = Integer.parseInt( readout.getText() );
//            readout.setText( Integer.toString( ++cnt ) );
//        }
//    }
//
//    public void modelElementRemoved( ModelElement element ) {
//        if( moleculeClass.isInstance( element ) ) {
//            int cnt = Integer.parseInt( readout.getText() );
//            readout.setText( Integer.toString( --cnt ) );
//        }
//    }
}
