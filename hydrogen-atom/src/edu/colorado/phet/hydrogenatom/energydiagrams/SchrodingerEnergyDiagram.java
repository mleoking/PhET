/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class SchrodingerEnergyDiagram extends AbstractEnergyDiagram implements Observer {
    
    private static final double X_MARGIN = 10;
    private static final double Y_MARGIN = 10;
    
    private static final double LINE_LINE_SPACING = 10;
    private static final double LINE_LABEL_SPACING = 10;

    private double _lHeight;
    
    public SchrodingerEnergyDiagram( PSwingCanvas canvas ) {
        super( SchrodingerModel.getNumberOfStates(), canvas );
        
        assert( SchrodingerModel.getGroundState() == 1 ); // n=1 must be ground state
        assert( SchrodingerModel.getNumberOfStates() == 6 ); // 6 states

        PNode lNode = createLNode();
        lNode.setOffset( getDrawingArea().getX() + X_MARGIN, Y_MARGIN );
        getStateLayer().addChild( lNode );
        _lHeight = lNode.getFullBounds().getHeight();
        
        for ( int n = 1; n <= SchrodingerModel.getNumberOfStates(); n++ ) { 
            PNode levelNode = createNNode( n );
            double x = getXOffset( 0 );
            double y = getYOffset( n );
            levelNode.setOffset( x, y );
            getStateLayer().addChild( levelNode );
        }
    }
    
    protected void initElectronPosition() {
        updateElectronPosition();
    }
    
    public void update( Observable o, Object arg ) {
        if ( o instanceof BohrModel ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                updateElectronPosition();
            }
        }
    }
    
    private void updateElectronPosition() {
        SchrodingerModel atom = (SchrodingerModel) getAtom();
        ElectronNode electronNode = getElectronNode();
        final int n = atom.getElectronState();
        final int l = atom.getSecondaryElectronState();
        final double x = getXOffset( l ) + ( LINE_LENGTH / 2 );
        final double y = getYOffset( n ) - ( electronNode.getFullBounds().getHeight() / 2 );
        electronNode.setOffset( x, y );
    }
    
    protected double getXOffset( int l ) {
        return getDrawingArea().getX() + X_MARGIN + ( l * LINE_LENGTH ) + ( l * LINE_LINE_SPACING );
    }
    
    protected double getYOffset( int n ) {
        final double minE = getEnergy( 1 );
        final double maxE = getEnergy( BohrModel.getNumberOfStates() );
        final double rangeE = maxE - minE;
        Rectangle2D drawingArea = getDrawingArea();
        final double electronHeight = getElectronNode().getFullBounds().getHeight();
        final double height = drawingArea.getHeight() - ( 2 * Y_MARGIN ) - _lHeight - electronHeight;
        double y = drawingArea.getY() + Y_MARGIN + _lHeight + electronHeight + ( height * ( maxE - getEnergy( n ) ) / rangeE );
        return y;
    }
    
    private static PNode createLNode() {
        
        final int numberOfStates = SchrodingerModel.getNumberOfStates();
        
        PComposite parentNode = new PComposite();
        
        PText labelNode = new PText( "l=");
        labelNode.setFont( LABEL_FONT );
        labelNode.setTextPaint( LABEL_COLOR );
        parentNode.addChild( labelNode );
        labelNode.setOffset( -labelNode.getWidth(), 0 );
        
        for ( int l = 0; l < numberOfStates; l++ ) {
            PText valueNode = new PText( String.valueOf( l ) );
            valueNode.setFont( LABEL_FONT );
            valueNode.setTextPaint( LABEL_COLOR );
            valueNode.setOffset( ( l * LINE_LENGTH ) + ( l * LINE_LINE_SPACING ) + ( LINE_LENGTH / 2 ) - ( valueNode.getWidth() / 2 ), 0 );
            parentNode.addChild( valueNode );
        }
        
        return parentNode;
    }
    
    private static PNode createNNode( int state ) {
        
        final int numberOfStates = SchrodingerModel.getNumberOfStates();

        PComposite linesParentNode = new PComposite();
        for ( int i = 0; i < state; i++ ) {
            PNode lineNode = createLineNode();
            lineNode.setOffset( i * ( LINE_LENGTH + LINE_LINE_SPACING ), 0 );
            linesParentNode.addChild( lineNode );
        }

        PNode labelNode = createLabelNode( state );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( linesParentNode );
        parentNode.addChild( labelNode );
        
        linesParentNode.setOffset( 0, 0 );
        double x = ( numberOfStates * LINE_LENGTH ) + ( ( numberOfStates - 1 ) * LINE_LINE_SPACING ) + LINE_LABEL_SPACING;
        double y = -( ( linesParentNode.getHeight() / 2 ) + ( labelNode.getHeight() / 2 ) );
        if ( state == 6 ) {
            // HACK requested by Sam McKagan: for n=6, move label up a bit to prevent overlap with n=5
            labelNode.setOffset( x, y - 3.5 );
        }
        else {
            labelNode.setOffset( x, y );
        }
        
        return parentNode;
    }
}
