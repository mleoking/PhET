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
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class BohrEnergyDiagram extends AbstractEnergyDiagram implements Observer {

    private static final double E1 = -13.6; // eV
    
    private static final double X_MARGIN = 10;
    private static final double Y_MARGIN = 20;
    
    private static final double LINE_LENGTH = 6;
    private static final Stroke LINE_STROKE = new BasicStroke( 2f );
    private static final Color LINE_COLOR = Color.BLACK;
    
    private static final String LABEL_FORMAT = "n={0}";
    private static final Font LABEL_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    private static final Color LABEL_COLOR = Color.BLACK;
    
    private static final double LINE_LABEL_SPACING = 6;
    
    private double[] _energies;
    
    public BohrEnergyDiagram() {
        super();
        
        assert( BohrModel.getGroundState() == 1 ); // n=1 must be ground state

        _energies = calculateEnergies();
        _energies = new double[] { E1, -5.2, -2.8, -1.51, -0.85, -0.38 };//XXX distorted to reduce overlap
        
        for ( int i = 0; i < _energies.length; i++ ) { 
            int n = i + 1;
            PNode levelNode = createLevelNode( n );
            double x = getXOffset( n );
            double y = getYOffset( n );
            levelNode.setOffset( x, y );
            addChild( levelNode );
        }
    }
    
    private PNode createLevelNode( int state ) {
        
        PPath horizontalLineNode = new PPath( new Line2D.Double( 0, 0, LINE_LENGTH, 0 ) );
        horizontalLineNode.setStroke( LINE_STROKE );
        horizontalLineNode.setStrokePaint( LINE_COLOR );
        
        Object[] args = new Object[] { new Integer( state ) };
        String label = MessageFormat.format( LABEL_FORMAT, args );
        PText labelNode = new PText( label );
        labelNode.setFont( LABEL_FONT );
        labelNode.setTextPaint( LABEL_COLOR );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( horizontalLineNode );
        parentNode.addChild( labelNode );
        
        horizontalLineNode.setOffset( 0, 0 );
        labelNode.setOffset( horizontalLineNode.getWidth() + LINE_LABEL_SPACING, -( ( horizontalLineNode.getHeight() / 2 ) + ( labelNode.getHeight() / 2 ) ) );
        
        return parentNode;
    }
    
    private double[] calculateEnergies() {
        int numberOfStates = BohrModel.getNumberOfStates();
        double E[] = new double[ numberOfStates ];
        for ( int i = 0; i < E.length; i++ ) {
            int n = i + 1;
            E[i] = E1 / ( n * n );
        }
        return E;
    }
    
    private double getXOffset( int state ) {
        return getDrawingArea().getX() + X_MARGIN;
    }
    
    private double getYOffset( int state ) {
        final int index = state - 1;
        final double minE = _energies[ 0 ];
        final double maxE = _energies[ _energies.length - 1 ];
        final double rangeE = maxE - minE;
        Rectangle2D drawingArea = getDrawingArea();
        final double height = drawingArea.getHeight() - ( 2 * Y_MARGIN );
        double y = drawingArea.getY() + Y_MARGIN + ( height * ( maxE - _energies[index] ) / rangeE );
        return y;
    }
    
    public void setAtom( AbstractHydrogenAtom atom ) {
        super.setAtom( atom );
        update( atom, AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE );
    }
    
    public void update( Observable o, Object arg ) {
        if ( o instanceof BohrModel ) {
            BohrModel atom = (BohrModel) o;
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                ElectronNode electronNode = getElectronNode();
                final int n = atom.getElectronState();
                final double x = getXOffset( n ) + ( LINE_LENGTH / 2 );
                final double y = getYOffset( n ) - ( electronNode.getFullBounds().getHeight() / 2 );
                electronNode.setOffset( x, y );
            }
        }
    }
}
