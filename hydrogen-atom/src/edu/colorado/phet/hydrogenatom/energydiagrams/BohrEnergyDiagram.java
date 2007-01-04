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

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * BohrEnergyDiagram is the energy level diagram that corresponds
 * to a hydrogen atom based on the Bohr model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BohrEnergyDiagram extends AbstractEnergyDiagram implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Margins inside the drawing area
    private static final double X_MARGIN = 20;
    private static final double Y_MARGIN = 20;
    
    // Horizontal space between a state's line and its label
    private static final double LINE_LABEL_SPACING = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BohrModel _atom;
    private EnergySquiggle _squiggle;
    private int _nPrevious;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param canvas
     */
    public BohrEnergyDiagram( PSwingCanvas canvas ) {
        super( BohrModel.getNumberOfStates(), canvas );
        
        assert( BohrModel.getGroundState() == 1 ); // n=1 must be ground state
        assert( BohrModel.getNumberOfStates() == 6 ); // 6 states

        for ( int n = 1; n <= BohrModel.getNumberOfStates(); n++ ) { 
            PNode levelNode = createStateNode( n );
            double x = getXOffset( n );
            double y = getYOffset( n );
            levelNode.setOffset( x, y );
            getStateLayer().addChild( levelNode );
        }
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the atom associated with the diagram.
     * Initializes the electron's position, based on it's current state.
     * 
     * @param atom
     */
    public void setAtom( BohrModel atom ) {

        if ( _atom != null ) {
            // remove association with existing atom
            _atom.deleteObserver( this );
            _atom = null;
        }
        
        // Electron is invisible if there is no associated atom
        ElectronNode electronNode = getElectronNode();
        electronNode.setVisible( atom != null );
        
        if ( atom != null ) {
            
            // observe the atom
            _atom = atom;
            _atom.addObserver( this );

            // Set electron's initial position
            updateElectronPosition();
            
            // Remember electron's initial state
            _nPrevious = atom.getElectronState();
        }
    }
    
    /**
     * Removes the association between this diagram and any atom.
     */
    public void clearAtom() {
        setAtom( null );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o instanceof BohrModel ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                updateElectronPosition();
                updateSquiggle();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Updates the squiggle to show a state change.
     */
    private void updateSquiggle() {

        // remove any existing squiggle
        if ( _squiggle != null ) {
            getSquiggleLayer().removeChild( _squiggle );
            _squiggle = null;
        }

        // create the new squiggle for photon absorption/emission
        final int n = _atom.getElectronState();
        if ( n != _nPrevious ) {
            double wavelength = 0;
            if ( n > _nPrevious ) {
                // a photon has been absorbed
                wavelength = BohrModel.getWavelengthAbsorbed( _nPrevious, n );
            }
            else {
                // a photon has been emitted
                wavelength = BohrModel.getWavelengthAbsorbed( n, _nPrevious );
            }
            Color color = ColorUtils.wavelengthToColor( wavelength );
            double x1 = getXOffset( _nPrevious );
            double y1 = getYOffset( _nPrevious );
            double x2 = getXOffset( n );
            double y2 = getYOffset( n );
            _squiggle = new EnergySquiggle( color, SQUIGGLE_STROKE, x1, y1, x2, y2 );
            getSquiggleLayer().addChild( _squiggle );
            
            // Remember electron's state
            _nPrevious = n;
        }
    }
    
    /*
     * Updates the position of the electron in the diagram
     * to match the electron's state.
     */
    private void updateElectronPosition() {
        ElectronNode electronNode = getElectronNode();
        final int n = _atom.getElectronState();
        final double x = getXOffset( n ) + ( LINE_LENGTH / 2 );
        final double y = getYOffset( n ) - ( electronNode.getFullBounds().getHeight() / 2 );
        electronNode.setOffset( x, y );
    }
    
    /*
     * Gets the x-offset that corresponds to a specified state.
     * This is used for positioning both the state lines and the electron.
     * 
     * @param state
     * @return double
     */
    private double getXOffset( int state ) {
        return getDrawingArea().getX() + X_MARGIN;
    }
    
    /*
     * Gets the y-offset that corresponds to a specific state.
     * This is used for positioning both the state lines and the electron.
     * 
     * @param state
     * @return double
     */
    private double getYOffset( int state ) {
        final double minE = getEnergy( 1 );
        final double maxE = getEnergy( BohrModel.getNumberOfStates() );
        final double rangeE = maxE - minE;
        Rectangle2D drawingArea = getDrawingArea();
        final double height = drawingArea.getHeight() - ( 2 * Y_MARGIN );
        double y = drawingArea.getY() + Y_MARGIN + ( height * ( maxE - getEnergy( state ) ) / rangeE );
        return y;
    }
    
    /*
     * Creates a node that represents a state (or level) of the electron.
     * This node consists of a horizontal line with an "n=" label to the 
     * right of the line.
     * 
     * @param state
     * @return PNode
     */
    private static PNode createStateNode( int state ) {
        
        PNode lineNode = createStateLineNode();
        PNode labelNode = createStateLabelNode( state );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( lineNode );
        parentNode.addChild( labelNode );
        
        // vertically align centers of label and line
        lineNode.setOffset( 0, 0 );
        double x = lineNode.getWidth() + LINE_LABEL_SPACING;
        double y = -( ( lineNode.getHeight() / 2 ) + ( labelNode.getHeight() / 2 ) );
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
