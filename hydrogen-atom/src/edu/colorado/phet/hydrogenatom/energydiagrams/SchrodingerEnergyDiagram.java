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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SchrodingerEnergyDiagram extends AbstractEnergyDiagram implements Observer {
    
    private static final double X_MARGIN = 10;
    private static final double Y_MARGIN = 20;
    
    private static final double LINE_LABEL_SPACING = 6;
    
    public SchrodingerEnergyDiagram() {
        super( SchrodingerModel.getNumberOfStates() );
        
        assert( SchrodingerModel.getGroundState() == 1 ); // n=1 must be ground state

        for ( int i = 0; i < BohrModel.getNumberOfStates(); i++ ) { 
            int n = i + 1;
            PNode levelNode = createLevelNode( n );
            double x = getXOffset( n ) + X_MARGIN;
            double y = getYOffset( n ) + Y_MARGIN;
            levelNode.setOffset( x, y );
            addChild( levelNode );
        }
    }
    
    public void update( Observable o, Object arg ) {
        if ( o instanceof BohrModel ) {
            BohrModel atom = (BohrModel) o;
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                ElectronNode electronNode = getElectronNode();
                final int n = atom.getElectronState();
                final double x = getXOffset( n ) + ( getLineLength() / 2 ) + X_MARGIN;
                final double y = getYOffset( n ) - ( electronNode.getFullBounds().getHeight() / 2 ) + Y_MARGIN;
                electronNode.setOffset( x, y );
            }
        }
    }
    
    protected double getXOffset( int state ) {
        return getDrawingArea().getX() + X_MARGIN;
    }
    
    protected double getYOffset( int state ) {
        final double minE = getEnergy( 1 );
        final double maxE = getEnergy( BohrModel.getNumberOfStates() );
        final double rangeE = maxE - minE;
        Rectangle2D drawingArea = getDrawingArea();
        final double height = drawingArea.getHeight() - ( 2 * Y_MARGIN );
        double y = drawingArea.getY() + ( height * ( maxE - getEnergy( state ) ) / rangeE );
        return y;
    }
    
    private static PNode createLevelNode( int state ) {
        
        PNode lineNode = createLineNode();
        PNode labelNode = createLabelNode( state );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( lineNode );
        parentNode.addChild( labelNode );
        
        lineNode.setOffset( 0, 0 );
        labelNode.setOffset( lineNode.getWidth() + LINE_LABEL_SPACING, -( ( lineNode.getHeight() / 2 ) + ( labelNode.getHeight() / 2 ) ) );
        
        return parentNode;
    }
}
