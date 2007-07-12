/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

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

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.charts.PotentialEnergyChartNode;
import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;
import edu.colorado.phet.opticaltweezers.defaults.GlobalDefaults;
import edu.colorado.phet.opticaltweezers.help.OTWiggleMe;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * DNACanvas is the canvas for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNACanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean HAS_WIGGLE_ME = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private DNAModel _model;
    
    // View
    private PNode _rootNode;
    private MicroscopeSlideNode _microscopeSlideNode;
    private LaserNode _laserNode;
    private DNAStrandNode _dnaStrandNode;
    private BeadNode _beadNode;
    private PPath _beadDragBoundsNode;
    private PPath _laserDragBoundsNode;
    private OTRulerNode _rulerNode;
    private PPath _rulerDragBoundsNode;
    private PotentialEnergyChartNode _potentialEnergyChartNode;
    private TrapForceNode _trapForceNode;
    private FluidDragForceNode _dragForceNode;
    private DNAForceNode _dnaForceNode;
    
    // Control
    private PSwing _returnBeadButtonWrapper;
    
    // Help
    private OTWiggleMe _wiggleMe;
    private boolean _wiggleMeInitialized = false;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNACanvas( DNAModel model ) {
        super( DNADefaults.VIEW_SIZE );
        
        _model = model;
        
        OTClock clock = model.getClock();
        Fluid fluid = model.getFluid();
        MicroscopeSlide microscopeSlide = model.getMicroscopeSlide();
        Laser laser = model.getLaser();
        DNAStrand dnaStrand = model.getDNAStrand();
        Bead bead = model.getBead();
        ModelViewTransform modelViewTransform = model.getModelViewTransform();
        
        setBackground( OTConstants.CANVAS_BACKGROUND );
        
        // When the canvas is resized...
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                // update the layout
                updateLayout();
                // make the "Return Bead" button visible if the bead is not visible
                updateReturnBeadButtonVisibility();
            }
        } );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        // Microscope slide
        _microscopeSlideNode = new MicroscopeSlideNode( microscopeSlide, fluid, modelViewTransform, GlobalDefaults.FLUID_SPEED_RANGE.getMax() );
        
        // Laser
        _laserDragBoundsNode = new PPath();
        _laserDragBoundsNode.setStroke( null );
        _laserNode = new LaserNode( laser, modelViewTransform, _laserDragBoundsNode );
        _laserNode.setElectricFieldVisible( false );
        
        // DNA Strand
        _dnaStrandNode = new DNAStrandNode( dnaStrand, modelViewTransform );
        
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
        
        // Force vectors, use same reference values so that scale is the same!
        {
            final double modelReferenceMagnitude = laser.getMaxTrapForce().getMagnitude();
            final double viewReferenceLength = GlobalDefaults.FORCE_VECTOR_REFERENCE_LENGTH;
            _trapForceNode = new TrapForceNode( laser, bead, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
            _dragForceNode = new FluidDragForceNode( fluid, bead, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
            _dnaForceNode = new DNAForceNode( bead, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
        }
        
        // Ruler
        _rulerDragBoundsNode = new PPath();
        _rulerDragBoundsNode.setStroke( null );
        _rulerNode = new OTRulerNode( DNADefaults.RULER_MAJOR_TICK_INTERVAL, DNADefaults.RULER_MINOR_TICKS_BETWEEN_MAJORS,
                laser, model.getModelViewTransform(), _rulerDragBoundsNode );
        _rulerNode.setOffset( 0, modelViewTransform.modelToView( DNADefaults.RULER_Y_POSITION ) );
        _rulerNode.setXOffsetFudgeFactor( 4 );
        
        // Potential Energy chart
        _potentialEnergyChartNode = new PotentialEnergyChartNode( bead, laser, modelViewTransform, GlobalDefaults.POTENTIAL_ENERGY_SAMPLE_WIDTH );
        
        // "Return Bead" button
        JButton returnBeadButton = new JButton( OTResources.getString( "button.returnBead" ) );
        Font font = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 18 );
        returnBeadButton.setFont( font );
        returnBeadButton.setOpaque( false );
        returnBeadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleReturnBeadButton();
            }
        });
        _returnBeadButtonWrapper = new PSwing( returnBeadButton );
        
        // Layering order of nodes on the canvas
        _rootNode.addChild( _microscopeSlideNode );
        _rootNode.addChild( _laserNode );
        _rootNode.addChild( _laserDragBoundsNode );
        _rootNode.addChild( _dnaStrandNode );
        _rootNode.addChild( _beadNode );
        _rootNode.addChild( _beadDragBoundsNode );
        _rootNode.addChild( _trapForceNode );
        _rootNode.addChild( _dragForceNode );
        _rootNode.addChild( _dnaForceNode );
        _rootNode.addChild( _potentialEnergyChartNode );
        _rootNode.addChild( _rulerNode );
        _rootNode.addChild( _rulerDragBoundsNode );
        _rootNode.addChild( _returnBeadButtonWrapper );
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
    
    public DNAForceNode getDNAForceNode() {
        return _dnaForceNode;
    }
    
    public DNAStrandNode getDNAStrandNode() {
        return _dnaStrandNode;
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    public void updateLayout() {

        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;
        
        Dimension2D worldSize = getWorldSize();
//        System.out.println( "PhysicsModule.updateCanvasLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
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
            x -= 500;//XXX test Return Bead button
            w += 1000;//XXX test Return Bead button
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
                ModelViewTransform modelViewTransform = _model.getModelViewTransform();
                double xModel = modelViewTransform.viewToModel( worldSize.getWidth() / 2 );
                double yModel = laser.getPositionReference().getY();
                laser.setPosition( xModel, yModel );
            }
        }
        
        if ( HAS_WIGGLE_ME ) {
            initWiggleMe();
        }
    }
    
    /**
     * Makes the "Return Bead" button visible if the bead is not visible on the canvas.
     */
    private void updateReturnBeadButtonVisibility() {
        
        Dimension2D worldSize = getWorldSize();
        Rectangle2D worldBounds = new Rectangle2D.Double( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
        Rectangle2D beadBounds = _beadNode.getFullBoundsReference();
        
        //XXX using intersects is a little dodgy since the bead is a circle
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
        ModelViewTransform modelViewTransform = _model.getModelViewTransform();
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
    
    //----------------------------------------------------------------------------
    // Wiggle Me
    //----------------------------------------------------------------------------
    
    /*
     * Initializes a wiggle me
     */
    private void initWiggleMe() {
        if ( !_wiggleMeInitialized ) {
            
            // Create wiggle me, add to root node.
            _wiggleMe = new OTWiggleMe( this, OTResources.getString( "label.wiggleMe" ) );
            _rootNode.addChild( _wiggleMe );
            
            // Animate from the upper-left to some point
            double x = 300;//XXX
            double y = 300;//XXX
            _wiggleMe.setOffset( 0, -100 );
            _wiggleMe.animateTo( x, y );
            
            // Clicking on the canvas makes the wiggle me go away.
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    _wiggleMe.setEnabled( false );
                    _rootNode.removeChild( _wiggleMe );
                    removeInputEventListener( this );
                    _wiggleMe = null;
                }
            } );
            
            _wiggleMeInitialized = true;
        }
    }
}
