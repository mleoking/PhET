/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.util.DoubleRange;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.ColorUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BeamInNode is the visual representation of the portion of the 
 * laser beam that is coming into the microscope objective.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamInNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final int MAX_ALPHA_CHANNEL = 180; // 0-255
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private Color _laserColor;
    private PPath _pathNode; // the beam node
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BeamInNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _laserColor = VisibleColor.wavelengthToColor( _laser.getVisibleWavelength() );
        
        double width = modelViewTransform.modelToView( laser.getDiameterAtObjective() );
        double height = modelViewTransform.modelToView( laser.getDistanceFromObjectiveToControlPanel() );
        _pathNode = new PPath();
        _pathNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
        _pathNode.setPaint( _laserColor );
        _pathNode.setStroke( null );
        addChild( _pathNode );
        
        updateAlpha();
        updateVisible();
    }

    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POWER ) {
                updateAlpha();
            }
            else if ( arg == Laser.PROPERTY_RUNNING ) {
                updateVisible();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the alpha of the beam's color to match the power of the laser.
     */
    private void updateAlpha() {
        double power = _laser.getPower();
        DoubleRange powerRange = _laser.getPowerRange();
        int alpha = (int) ( MAX_ALPHA_CHANNEL * ( power - powerRange.getMin() ) / ( powerRange.getMax() - powerRange.getMin() ) );
        Color newColor = ColorUtils.addAlpha( _laserColor, alpha );
        _pathNode.setPaint( newColor );
    }
    
    private void updateVisible() {
        setVisible( _laser.isRunning() );
    }
}
