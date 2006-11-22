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

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * ReactionGraphic
 * <p/>
 * A graphic that shows the mechanics of the reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionGraphic extends RegisterablePNode implements MRModel.ModelListener {
    private PImage aNode = new PImage();
    private PImage cNode = new PImage( );
    private PImage abNode= new PImage( );
    private PImage bcNode = new PImage( );

    /**
     *
     * @param reaction
     * @param arrowColor
     * @param model
     */
    public ReactionGraphic( Reaction reaction, Color arrowColor, MRModel model ) {

        model.addListener( this );
        if( reaction instanceof A_BC_AB_C_Reaction ) {
            Insets insets = new Insets( 0, 3, 0, 3 );
            setMoleculeImages( model.getEnergyProfile() );

            Arrow arrowC = new Arrow( new Point2D.Double( 0, 0 ),
                                     new Point2D.Double( 30, 0 ),
                                     12, 8, 1 );
            Arrow arrowA = new Arrow( new Point2D.Double( 30, 0 ),
                                     new Point2D.Double( 0, 0 ),
                                     12, 8, 1 );
            PNode arrowNode = new PNode( );
            PPath arrowANode = new PPath( arrowA.getShape() );
            arrowANode.setPaint( arrowColor );
            arrowANode.setStrokePaint( arrowColor);
            arrowNode.addChild( arrowANode );
            PPath arrowCNode = new PPath( arrowC.getShape() );
            arrowCNode.setPaint( arrowColor );
            arrowCNode.setStrokePaint( arrowColor);
            arrowNode.addChild( arrowCNode );
            arrowCNode.setOffset( arrowANode.getWidth(), 0);

            Font font = new Font( "Lucida sans", Font.BOLD, 18);
            PText plusA = new PText( "+" );
            plusA.setTextPaint( arrowColor );
            plusA.setFont( font );
            PText plusC = new PText( "+" );
            plusC.setFont( font );
            plusC.setTextPaint( arrowColor );

            aNode.setOffset( 0, -aNode.getFullBounds().getHeight() / 2 );
            setChainedOffset( plusA, aNode, insets );
            setChainedOffset( bcNode, plusA, insets );
            setChainedOffset( arrowNode, bcNode, insets );
            setChainedOffset( abNode, arrowNode, insets );
            setChainedOffset( plusC, abNode, insets );
            setChainedOffset( cNode, plusC, insets );

            addChild( aNode );
            addChild( cNode );
            addChild( abNode );
            addChild( bcNode );
            addChild( arrowNode );
            addChild( plusA );
            addChild( plusC );

            setRegistrationPoint( getFullBounds().getWidth() / 2, 0 );
        }
        else {
            throw new IllegalArgumentException( "Reaction not recognized" );
        }
    }

    private void setChainedOffset( PNode nodeToSet, PNode nodeToLeft, Insets insets) {
        Point2D offset = new Point2D.Double( nodeToLeft.getOffset().getX() + nodeToLeft.getFullBounds().getWidth() + insets.left + insets.right,
                                             -nodeToSet.getFullBounds().getHeight() / 2 );
        nodeToSet.setOffset( offset );
    }

    private void setMoleculeImages( EnergyProfile profile ) {
        aNode.setImage( new MoleculeIcon( MoleculeA.class, profile ).getImage() );
        cNode.setImage( new MoleculeIcon( MoleculeC.class, profile ).getImage() );
        abNode.setImage( new MoleculeIcon( MoleculeAB.class, profile ).getImage() );
        bcNode.setImage( new MoleculeIcon( MoleculeBC.class, profile ).getImage() );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.ModelListener
    //--------------------------------------------------------------------------------------------------

    public void energyProfileChanged( EnergyProfile profile ) {
        setMoleculeImages( profile );
    }
}
