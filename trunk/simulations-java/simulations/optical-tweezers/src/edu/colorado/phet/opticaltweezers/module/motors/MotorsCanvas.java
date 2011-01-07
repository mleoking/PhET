// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module.motors;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.charts.PotentialEnergyChartNode;
import edu.colorado.phet.opticaltweezers.defaults.MotorsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.OTAbstractCanvas;
import edu.colorado.phet.opticaltweezers.view.*;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * MotorsCanvas is the canvas for MotorsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsCanvas extends OTAbstractCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private MotorsModel _model;
    
    // View
    private MicroscopeSlideNode _microscopeSlideNode;
    private LaserNode _laserNode;
    private DNAStrandNode _dnaStrandBeadNode;
    private DNAStrandNode _dnaStrandFreeNode;
    private BeadNode _beadNode;
    private PPath _beadDragBoundsNode;
    private PPath _laserDragBoundsNode;
    private OTRulerNode _rulerNode;
    private PPath _rulerDragBoundsNode;
    private PotentialEnergyChartNode _potentialEnergyChartNode;
    private TrapForceNode _trapForceNode;
    private FluidDragForceNode _dragForceNode;
    private DNAForceNode _dnaForceNode;
    private EnzymeANode _enzymeANode;
    private EnzymeBNode _enzymeBNode;
    
    // Control
    private PSwing _resetDNAButtonWrapper;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MotorsCanvas( MotorsModel model ) {
        super( MotorsDefaults.VIEW_SIZE );
        
        _model = model;
        
        Fluid fluid = model.getFluid();
        MicroscopeSlide microscopeSlide = model.getMicroscopeSlide();
        Laser laser = model.getLaser();
        DNAStrand dnaStrandBead = model.getDNAStrandBead();
        DNAStrand dnaStrandFree = model.getDNAStrandFree();
        Bead bead = model.getBead();
        EnzymeA enzymeA = model.getEnzymeA();
        EnzymeB enzymeB = model.getEnzymeB();
        OTModelViewTransform modelViewTransform = model.getModelViewTransform();
        
        setBackground( OTConstants.CANVAS_BACKGROUND );
        
        // When the canvas is resized...
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                // add stuff here...
            }
        } );

        // Microscope slide
        _microscopeSlideNode = new MicroscopeSlideNode( microscopeSlide, fluid, modelViewTransform, MotorsDefaults.FLUID_SPEED_RANGE.getMax() );
        
        // Laser
        _laserDragBoundsNode = new PPath();
        _laserDragBoundsNode.setStroke( null );
        _laserNode = new LaserNode( laser, modelViewTransform, _laserDragBoundsNode );
        _laserNode.setElectricFieldVisible( false );
        
        // DNA Strands
        _dnaStrandBeadNode = new DNAStrandNode( dnaStrandBead, modelViewTransform );
        _dnaStrandFreeNode = new DNAStrandNode( dnaStrandFree, modelViewTransform );
        _dnaStrandFreeNode.setStrandColor( MotorsDefaults.DNA_FREE_STRAND_COLOR );
        
        // Enzymes
        _enzymeANode = new EnzymeANode( enzymeA, modelViewTransform );
        _enzymeBNode = new EnzymeBNode( enzymeB, modelViewTransform );
        
        // Pushpin
        PushpinNode pushpinNode = new PushpinNode();
        Point2D dnaPosition = modelViewTransform.modelToView( dnaStrandBead.getPosition() );
        pushpinNode.setOffset( dnaPosition );
        
        // Bead
        _beadDragBoundsNode = new PPath();
        _beadDragBoundsNode.setStroke( null );
        _beadNode = new BeadNode( bead, modelViewTransform, _beadDragBoundsNode );
        
        // Force vectors, use same reference values so that scale is the same!
        {
            final double modelReferenceMagnitude = laser.getMaxTrapForce().getMagnitude();
            final double viewReferenceLength = MotorsDefaults.FORCE_VECTOR_REFERENCE_LENGTH;
            _trapForceNode = new TrapForceNode( laser, bead, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
            _dragForceNode = new FluidDragForceNode( fluid, bead, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
            _dnaForceNode = new DNAForceNode( bead, dnaStrandBead, fluid, modelViewTransform, modelReferenceMagnitude, viewReferenceLength );
        }
        
        // Ruler
        _rulerDragBoundsNode = new PPath();
        _rulerDragBoundsNode.setStroke( null );
        _rulerNode = new OTRulerNode( MotorsDefaults.RULER_MAJOR_TICK_INTERVAL, MotorsDefaults.RULER_MINOR_TICKS_BETWEEN_MAJORS,
                laser, model.getModelViewTransform(), _rulerDragBoundsNode );
        _rulerNode.setOffset( 0, modelViewTransform.modelToView( MotorsDefaults.RULER_Y_POSITION ) );
        _rulerNode.setXOffsetFudgeFactor( 4 );
        
        // Potential Energy chart
        _potentialEnergyChartNode = new PotentialEnergyChartNode( bead, laser, modelViewTransform, MotorsDefaults.POTENTIAL_ENERGY_SAMPLE_WIDTH );
        
        // "Reset DNA" button
        JButton resetDNAButton = new JButton( OTResources.getString( "button.resetDNA" ) );
        Font font = new PhetFont( Font.BOLD, 18 );
        resetDNAButton.setFont( font );
        resetDNAButton.setOpaque( false );
        resetDNAButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleResetDNAButton();
            }
        });
        _resetDNAButtonWrapper = new PSwing( resetDNAButton );
        // center the button above the enzyme
        PBounds eBounds = _enzymeANode.getFullBoundsReference();
        _resetDNAButtonWrapper.setOffset( eBounds.getX() + ( eBounds.getWidth() / 2 ) - ( _resetDNAButtonWrapper.getFullBoundsReference().getWidth() / 2 ),
                eBounds.getMaxY() - 150 );
        
        // Layering order of nodes on the canvas
        addNode( _microscopeSlideNode );
        addNode( _laserNode );
        addNode( _laserDragBoundsNode );
        addNode( _beadNode );
        addNode( _dnaStrandBeadNode );
        addNode( _dnaStrandFreeNode );
        addNode( _enzymeANode );
        addNode( _enzymeBNode );
        addNode( pushpinNode );
        addNode( _beadDragBoundsNode );
        addNode( _trapForceNode );
        addNode( _dragForceNode );
        addNode( _dnaForceNode );
        addNode( _potentialEnergyChartNode );
        addNode( _rulerNode );
        addNode( _rulerDragBoundsNode );
        addNode( _resetDNAButtonWrapper );
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
    
    public DNAStrandNode getDNAStrandBeadNode() {
        return _dnaStrandBeadNode;
    }
    
    public DNAStrandNode getDNAStrandFreeNode() {
        return _dnaStrandFreeNode;
    }
    
    public EnzymeANode getEnzymeANode() {
        return _enzymeANode;
    }
    
    public EnzymeBNode getEnzymeBNode() {
        return _enzymeBNode;
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
            System.out.println( "MotorsCanvas.updateLayout worldSize=" + worldSize );
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
     * Resets the DNA.
     */
    private void handleResetDNAButton() {
        _model.resetDNA();
    }
}
