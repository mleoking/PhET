// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module.physics;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.charts.PotentialEnergyChartNode;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.OTAbstractCanvas;
import edu.colorado.phet.opticaltweezers.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PhysicsCanvas is the canvas for PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsCanvas extends OTAbstractCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private PhysicsModel _model;

    // View
    private MicroscopeSlideNode _microscopeSlideNode;
    private LaserNode _laserNode;
    private BeadNode _beadNode;
    private PPath _beadDragBoundsNode;
    private PPath _laserDragBoundsNode;
    private OTRulerNode _rulerNode;
    private PPath _rulerDragBoundsNode;
    private PotentialEnergyChartNode _potentialEnergyChartNode;
    private TrapForceNode _trapForceNode;
    private FluidDragForceNode _dragForceNode;
    private ChargeDistributionNode _chargeDistributionNode;
    private ChargeExcessNode _chargeExcessNode;

    // Control
    private PSwing _returnBeadButtonWrapper;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PhysicsCanvas( PhysicsModel model ) {
        super( PhysicsDefaults.VIEW_SIZE );

        _model = model;

        Fluid fluid = model.getFluid();
        MicroscopeSlide microscopeSlide = model.getMicroscopeSlide();
        Laser laser = model.getLaser();
        Bead bead = model.getBead();
        OTModelViewTransform modelViewTransform = model.getModelViewTransform();

        setBackground( OTConstants.CANVAS_BACKGROUND );

        // When the canvas is resized...
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                // make the "Return Bead" button visible if the bead is not visible
                updateReturnBeadButtonVisibility();
            }
        } );

        // Microscope slide
        _microscopeSlideNode = new MicroscopeSlideNode( microscopeSlide, fluid, modelViewTransform, PhysicsDefaults.FLUID_SPEED_RANGE.getMax() );

        // Laser
        _laserDragBoundsNode = new PPath();
        _laserDragBoundsNode.setStroke( null );
        _laserNode = new LaserNode( laser, modelViewTransform, _laserDragBoundsNode );

        // Bead
        _beadDragBoundsNode = new PPath();
        _beadDragBoundsNode.setStroke( null );
        _beadNode = new BeadNode( bead, modelViewTransform, _beadDragBoundsNode );
        _beadNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                   updateReturnBeadButtonVisibility();
                }
            }
        });

        // Charge views
        _chargeDistributionNode = new ChargeDistributionNode( bead, laser, modelViewTransform, PhysicsDefaults.CHARGE_MOTION_SCALE_RANGE );
        _chargeExcessNode = new ChargeExcessNode( bead, laser, modelViewTransform );

        // Force vectors, use same reference values so that scale is the same!
        {
            final double modelReferenceMagnitude = laser.getMaxTrapForce().getMagnitude();
            final double viewReferenceLength = PhysicsDefaults.FORCE_VECTOR_REFERENCE_LENGTH;
            _trapForceNode = new TrapForceNode( laser, bead, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
            _dragForceNode = new FluidDragForceNode( fluid, bead, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
        }

        // Ruler
        _rulerDragBoundsNode = new PPath();
        _rulerDragBoundsNode.setStroke( null );
        _rulerNode = new OTRulerNode( PhysicsDefaults.RULER_MAJOR_TICK_INTERVAL, PhysicsDefaults.RULER_MINOR_TICKS_BETWEEN_MAJORS,
                laser, model.getModelViewTransform(), _rulerDragBoundsNode );
        _rulerNode.setOffset( 0, modelViewTransform.modelToView( PhysicsDefaults.RULER_Y_POSITION ) );

        // Potential Energy chart
        _potentialEnergyChartNode = new PotentialEnergyChartNode( bead, laser, modelViewTransform, PhysicsDefaults.POTENTIAL_ENERGY_SAMPLE_WIDTH );

        // "Return Bead" button
        JButton returnBeadButton = new JButton( OTResources.getString( "button.returnBead" ) );
        Font font = new PhetFont( Font.BOLD, 18 );
        returnBeadButton.setFont( font );
        returnBeadButton.setOpaque( false );
        returnBeadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleReturnBeadButton();
            }
        });
        _returnBeadButtonWrapper = new PSwing( returnBeadButton );

        // Layering order of nodes on the canvas
        addNode( _microscopeSlideNode );
        addNode( _laserNode );
        addNode( _laserDragBoundsNode );
        addNode( _beadNode );
        addNode( _beadDragBoundsNode );
        addNode( _chargeDistributionNode );
        addNode( _chargeExcessNode );
        addNode( _trapForceNode );
        addNode( _dragForceNode );
        addNode( _potentialEnergyChartNode );
        addNode( _rulerNode );
        addNode( _rulerDragBoundsNode );
        addNode( _returnBeadButtonWrapper );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public BeadNode getBeadNode() {
        return _beadNode;
    }

    public MicroscopeSlideNode getMicroscopeSlideNode() {
        return _microscopeSlideNode;
    }

    public PPath getLaserDragBoundsNode() {
        return _laserDragBoundsNode;
    }

    public LaserNode getLaserNode() {
        return _laserNode;
    }

    public PotentialEnergyChartNode getPotentialEnergyChartNode() {
        return _potentialEnergyChartNode;
    }

    public PSwing getReturnBeadButtonWrapper() {
        return _returnBeadButtonWrapper;
    }

    public OTRulerNode getRulerNode() {
        return _rulerNode;
    }

    public TrapForceNode getTrapForceNode() {
        return _trapForceNode;
    }

    public FluidDragForceNode getFluidDragForceNode() {
        return _dragForceNode;
    }

    public ChargeDistributionNode getChargeDistributionNode() {
        return _chargeDistributionNode;
    }

    public ChargeExcessNode getChargeExcessNode() {
        return _chargeExcessNode;
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( OTConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );
        }

        // Adjust width of things that must fill the canvas width
        {
            _microscopeSlideNode.setWorldSize( worldSize );
            _rulerNode.setWorldSize( worldSize );
            _potentialEnergyChartNode.setChartSize( worldSize.getWidth(), PotentialEnergyChartNode.DEFAULT_HEIGHT );
        }

        // Adjust drag bounds of bead, so it stays in the fluid
        {
            // This percentage of the bead must remain visible
            final double m = 0.15;

            Rectangle2D sb = _microscopeSlideNode.getCenterGlobalBounds();
            Rectangle2D bb = _beadNode.getGlobalFullBounds();
            x = sb.getX() - ( ( 1 - m ) * bb.getWidth() );
            y = sb.getY();
            w = sb.getWidth() + ( 2 * ( 1 - m ) * bb.getWidth() );
            h = sb.getHeight();
            Rectangle2D globalDragBounds = new Rectangle2D.Double( x, y, w, h );
            Rectangle2D localDragBounds = _beadDragBoundsNode.globalToLocal( globalDragBounds );
            _beadDragBoundsNode.setPathTo( localDragBounds );
        }

        // "Return Bead" button
        {
            // center on canvas
            PBounds returnButtonBounds = _returnBeadButtonWrapper.getFullBoundsReference();
            x = ( worldSize.getWidth() - returnButtonBounds.getWidth() ) / 2;
            y = ( worldSize.getHeight() - returnButtonBounds.getHeight() ) / 2;
            _returnBeadButtonWrapper.setOffset( x, y );
        }

        // Adjust drag bounds of laser, so it stays in canvas
        {
            // This percentage of the laser must remain visible
            final double m = 0.15;

            Rectangle2D sb = _microscopeSlideNode.getCenterGlobalBounds();
            Rectangle2D lb = _laserNode.getGlobalFullBounds();
            double xAdjust = ( 1 - m ) * lb.getWidth();
            x = -xAdjust;
            y = lb.getY();
            w = sb.getWidth() + ( 2 * xAdjust );
            h = lb.getHeight();
            Rectangle2D globalDragBounds = new Rectangle2D.Double( x, y, w, h );
            Rectangle2D localDragBounds = _laserDragBoundsNode.globalToLocal( globalDragBounds );
            _laserDragBoundsNode.setPathTo( localDragBounds );

            // If laser is not visible, move it to center of canvas
            Laser laser = _model.getLaser();
            Rectangle2D worldBounds = new Rectangle2D.Double( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
            Rectangle2D laserBounds = _laserNode.getFullBoundsReference();
            if ( !worldBounds.intersects( laserBounds ) ) {
                OTModelViewTransform modelViewTransform = _model.getModelViewTransform();
                double xModel = modelViewTransform.viewToModel( worldSize.getWidth() / 2 );
                double yModel = laser.getPositionReference().getY();
                laser.setPosition( xModel, yModel );
            }
        }
    }

    /**
     * Makes the "Return Bead" button visible if the bead is not visible on the canvas.
     */
    private void updateReturnBeadButtonVisibility() {

        Dimension2D worldSize = getWorldSize();
        Rectangle2D worldBounds = new Rectangle2D.Double( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
        Rectangle2D beadBounds = _beadNode.getFullBoundsReference();

        // Note: using intersects is a bit imprecise, since the bead is a circle
        _returnBeadButtonWrapper.setVisible( !worldBounds.intersects( beadBounds ) );
        _returnBeadButtonWrapper.setPickable( _returnBeadButtonWrapper.getVisible() );
        _returnBeadButtonWrapper.setChildrenPickable( _returnBeadButtonWrapper.getVisible() );
    }

    /**
     * When the "Return Bead" button is clicked,
     * move the bead to the button's position and hide the button.
     */
    private void handleReturnBeadButton() {

        // Determine the button's coordinates
        PBounds b = _returnBeadButtonWrapper.getFullBoundsReference();
        double x = b.getX() + ( b.getWidth() / 2 );
        double y = b.getY() + ( b.getHeight() / 2 );
        OTModelViewTransform modelViewTransform = _model.getModelViewTransform();
        Point2D p = modelViewTransform.viewToModel( x, y );

        // Move the bead to the button's position
        Bead bead = _model.getBead();
        bead.setMotionEnabled( false );
        bead.setPosition( p );
        bead.setMotionEnabled( true );

        // Hide the button
        _returnBeadButtonWrapper.setVisible( false );
        _returnBeadButtonWrapper.setPickable( false );
        _returnBeadButtonWrapper.setChildrenPickable( false );
    }
}
