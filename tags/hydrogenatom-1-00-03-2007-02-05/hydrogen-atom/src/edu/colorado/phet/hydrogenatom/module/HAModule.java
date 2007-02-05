/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.module;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HADefaults;
import edu.colorado.phet.hydrogenatom.control.*;
import edu.colorado.phet.hydrogenatom.energydiagrams.BohrEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.DeBroglieEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SchrodingerEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SolarSystemEnergyDiagram;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.colorado.phet.hydrogenatom.hacks.MetastableHandler;
import edu.colorado.phet.hydrogenatom.help.HAWiggleMe;
import edu.colorado.phet.hydrogenatom.model.*;
import edu.colorado.phet.hydrogenatom.view.*;
import edu.colorado.phet.hydrogenatom.view.LegendPanel.LegendNode;
import edu.colorado.phet.hydrogenatom.view.SpectrometerNode.SpectrometerSnapshotNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * HAModule is the sole module for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetPCanvas _canvas;
    private PNode _rootNode;

    // Control panels
    private HAClockControlPanel _clockControlPanel;
    private ModeSwitch _modeSwitch;
    private AtomicModelSelector _atomicModelSelector;
    private GunControlPanel _gunControlPanel;
    private PSwing _deBroglieViewControlWrapper;

    // Box/beam/gun
    private PNode _boxBeamGunParent;
    private BoxOfHydrogenNode _boxOfHydrogenNode;
    private BeamNode _beamNode;
    private GunNode _gunNode;

    // Animation box
    private AnimationBoxNode _animationBoxNode;
    private ZoomIndicatorNode _zoomIndicatorNode;

    // Alpha Particle traces
    private TracesNode _alphaParticleTracesNode;
    
    // Spectrometer
    private JCheckBox _spectrometerCheckBox;
    private PSwing _spectrometerCheckBoxNode;
    private SpectrometerNode _spectrometerNode;
    private ArrayList _spectrometerSnapshotNodes; // list of SpectrometerSnapshotNode
    private int _spectrometerSnapshotsCounter; // incremented each time a spectrometer snapshot is taken

    // Energy Diagrams
    private JCheckBox _energyDiagramCheckBox;
    private PSwing _energyDiagramCheckBoxNode;
    private PhetPNode _energyDiagramParent;
    private SolarSystemEnergyDiagram _solarSystemEnergyDiagram;
    private BohrEnergyDiagram _bohrEnergyDiagram;
    private DeBroglieEnergyDiagram _deBroglieEnergyDiagram;
    private SchrodingerEnergyDiagram _schrodingerEnergyDiagram;


    private NotToScaleNode _notToScaleLabel;
    private LegendNode _legendNode;
    
    private Font _spectrometerFont;

    private HAModel _model;
    private AbstractHydrogenAtom _atomModel;
    
    private HAWiggleMe _wiggleMe;
    private boolean _wiggleMeInitialized = false;
    
    private HAModelViewManager _modelViewManager;
    
    private MetastableHandler _metastableHandler;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public HAModule() {
        super( SimStrings.get( "HAModule.title" ), new HAClock(), !HADefaults.CLOCK_RUNNING /* startsPaused */ );

        // hide the PhET logo
        setLogoPanel( null );

        // Fonts
        int jComponentFontSize = SimStrings.getInt( "jcomponent.font.size", HAConstants.DEFAULT_FONT_SIZE );
        Font jComponentFont = new Font( HAConstants.DEFAULT_FONT_NAME, HAConstants.DEFAULT_FONT_STYLE, jComponentFontSize );
        int spectrometerFontSize = SimStrings.getInt( "spectrometer.font.size", HAConstants.DEFAULT_FONT_SIZE );
        _spectrometerFont = new Font( HAConstants.DEFAULT_FONT_NAME, HAConstants.DEFAULT_FONT_STYLE, spectrometerFontSize );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        IClock clock = getClock();
        
        {
            // Gun
            Point2D position = new Point2D.Double( 0, 0 );
            double orientation = Math.toRadians( -90 ); // degrees, pointing straight up
            double nozzleWidth = HAConstants.ANIMATION_BOX_SIZE.width;
            Gun gun = new Gun( position, orientation, nozzleWidth, HAConstants.MIN_WAVELENGTH, HAConstants.MAX_WAVELENGTH );
            
            // Space
            double spaceWidth = gun.getNozzleWidth();
            double spaceHeight = HAConstants.ANIMATION_BOX_SIZE.height;
            Rectangle2D bounds = new Rectangle2D.Double( -spaceWidth / 2, -spaceHeight, spaceWidth, spaceHeight );
            Space space = new Space( bounds );

            // Model
            _model = new HAModel( clock, gun, space );
        }

        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( HAConstants.CANVAS_RENDERING_SIZE );
            _canvas.setBackground( HAConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );

            _canvas.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    // update the layout when the canvas is resized
                    updateCanvasLayout();
                }
            } );
        }

        // Root of our scene graph
        _rootNode = new PNode();
        _canvas.addWorldChild( _rootNode );

        // Mode switch (experiment/prediction)
        _modeSwitch = new ModeSwitch();
        _modeSwitch.addChangeListener( new ChangeListener() {
           public void stateChanged( ChangeEvent event ) {
               handleModeChange();
           }
        });

        // Atomic Model selector
        _atomicModelSelector = new AtomicModelSelector();
        _atomicModelSelector.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleAtomicModelChange();
            }
         });
        
        //  Box of Hydrogen / Beam / Gun
        {
            // Parent node, used for layout
            _boxBeamGunParent = new PNode();

            _boxOfHydrogenNode = new BoxOfHydrogenNode( HAConstants.BOX_OF_HYDROGEN_SIZE, HAConstants.TINY_BOX_SIZE );
            _beamNode = new BeamNode( HAConstants.BEAM_SIZE, _model.getGun() );
            _gunNode = new GunNode( _model.getGun() );

            // Layering order
            _boxBeamGunParent.addChild( _beamNode );
            _boxBeamGunParent.addChild( _boxOfHydrogenNode );
            _boxBeamGunParent.addChild( _gunNode );
            
            // Positioning
            final double gunCenterOffset = 28;
            final double boxWidth = _boxOfHydrogenNode.getFullBounds().getWidth();
            final double gunWidth = _gunNode.getFullBounds().getWidth();
            if ( boxWidth > gunWidth ) {
                _boxOfHydrogenNode.setOffset( 0, 0 );
                _beamNode.setOffset( ( boxWidth - _beamNode.getFullBounds().getWidth() ) / 2, _boxOfHydrogenNode.getFullBounds().getMaxY() );
                _gunNode.setOffset( ( ( boxWidth - gunWidth ) / 2 ) + gunCenterOffset, _beamNode.getFullBounds().getMaxY() );
            }
            else {
                _boxOfHydrogenNode.setOffset( ( ( gunWidth - boxWidth ) / 2 ) - gunCenterOffset, 0 );
                _beamNode.setOffset( _boxOfHydrogenNode.getFullBounds().getX() + ( boxWidth - _beamNode.getFullBounds().getWidth() ) / 2, _boxOfHydrogenNode.getFullBounds().getMaxY() );
                _gunNode.setOffset( 0, _beamNode.getFullBounds().getMaxY() );
            }
        }

        // Animation box
        {
            // animation box
            _animationBoxNode = new AnimationBoxNode( HAConstants.ANIMATION_BOX_SIZE );

            // zoom indicator
            _zoomIndicatorNode = new ZoomIndicatorNode();
        }
        
        // Alpha Particles tracer
        {
            _alphaParticleTracesNode = new TracesNode( _model );
            _alphaParticleTracesNode.setBounds( 0, 0, _animationBoxNode.getWidth(), _animationBoxNode.getHeight() );
            _animationBoxNode.getTraceLayer().addChild( _alphaParticleTracesNode );
            _alphaParticleTracesNode.setEnabled( false );
        }

        // Gun control panel
        _gunControlPanel = new GunControlPanel( _canvas, _model.getGun(), _alphaParticleTracesNode );

        // deBroglie view control
        {
            final DeBroglieViewControl deBroglieViewControl = new DeBroglieViewControl();
            deBroglieViewControl.setFont( jComponentFont );
            deBroglieViewControl.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    DeBroglieView view = deBroglieViewControl.getSelectedView();
                    setDeBroglieView( view );
                }
            } );
            deBroglieViewControl.setSelectedView( HADefaults.DEBROGLIE_VIEW );
            
            // PSwing wrapper
            _deBroglieViewControlWrapper = new PSwing( _canvas, deBroglieViewControl );
            deBroglieViewControl.setEnvironment( _deBroglieViewControlWrapper, _canvas );
        }
        
        // Spectrometer
        {
            // Checkbox
            _spectrometerCheckBox = new JCheckBox( SimStrings.get( "label.showSpectrometer" ) );
//            _spectrometerCheckBox.setOpaque( false ); //XXX preferrable to setting background, but looks lousy on Windows
            _spectrometerCheckBox.setBackground( HAConstants.CANVAS_BACKGROUND );
            _spectrometerCheckBox.setForeground( HAConstants.CANVAS_LABELS_COLOR );
            _spectrometerCheckBox.setFont( jComponentFont );
            _spectrometerCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleSpectrometerCheckBox();
                }
            } );
            _spectrometerCheckBoxNode = new PSwing( _canvas, _spectrometerCheckBox );
            _spectrometerCheckBoxNode.addInputEventListener( new CursorHandler() );

            // Spectrometer
            String title = SimStrings.get( "label.photonsEmitted" );
            _spectrometerNode = new SpectrometerNode( _canvas, HAConstants.SPECTROMETER_SIZE, title, _spectrometerFont, HAConstants.SPECTROMETER_MIN_WAVELENGTH, HAConstants.SPECTROMETER_MAX_WAVELENGTH );
            _spectrometerNode.addCloseListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    _spectrometerCheckBox.setSelected( false );
                }
            } );
            _spectrometerNode.addSnapshotListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleSpectrometerSnapshot();
                }
            } );

            // List of snapshots
            _spectrometerSnapshotNodes = new ArrayList();
        }

        // Energy diagrams
        {
            // checkbox
            _energyDiagramCheckBox = new JCheckBox( SimStrings.get( "label.showEnergyDiagram" ) );
//            _energyDiagramCheckBox.setOpaque( false );//XXX preferrable to setting background, but looks lousy on Windows
            _energyDiagramCheckBox.setBackground( HAConstants.CANVAS_BACKGROUND );
            _energyDiagramCheckBox.setForeground( HAConstants.CANVAS_LABELS_COLOR );
            _energyDiagramCheckBox.setFont( jComponentFont );
            _energyDiagramCheckBox.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    updateEnergyDiagram();
                    // don't draw the legend if the energy diagram is covering it up
                    _legendNode.setVisible( !_energyDiagramCheckBox.isSelected() );
                }
            } );
            _energyDiagramCheckBoxNode = new PSwing( _canvas, _energyDiagramCheckBox );
            _energyDiagramCheckBoxNode.addInputEventListener( new CursorHandler() );

            // diagrams
            _solarSystemEnergyDiagram = new SolarSystemEnergyDiagram( _canvas );
            _bohrEnergyDiagram = new BohrEnergyDiagram( _canvas, getClock() );
            _deBroglieEnergyDiagram = new DeBroglieEnergyDiagram( _canvas, getClock() );
            _schrodingerEnergyDiagram = new SchrodingerEnergyDiagram( _canvas, getClock() );
            
            ActionListener closeListener = new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    _energyDiagramCheckBox.setSelected( false );
                }
            };
            _solarSystemEnergyDiagram.addCloseListener( closeListener );
            _bohrEnergyDiagram.addCloseListener( closeListener );
            _deBroglieEnergyDiagram.addCloseListener( closeListener );
            _schrodingerEnergyDiagram.addCloseListener( closeListener );

            // parent node for all diagrams
            _energyDiagramParent = new PhetPNode();
            _energyDiagramParent.addChild( _solarSystemEnergyDiagram );
            _energyDiagramParent.addChild( _bohrEnergyDiagram );
            _energyDiagramParent.addChild( _deBroglieEnergyDiagram );
            _energyDiagramParent.addChild( _schrodingerEnergyDiagram );
        }

        // "Not to scale" label
        _notToScaleLabel = new NotToScaleNode();
        
        // Legend
        _legendNode = new LegendNode( _canvas );

        // Layering order on the canvas (back-to-front)
        {
            _rootNode.addChild( _modeSwitch );
            _rootNode.addChild( _atomicModelSelector );
            _rootNode.addChild( _boxBeamGunParent );
            _rootNode.addChild( _animationBoxNode );
            _rootNode.addChild( _zoomIndicatorNode );
            _rootNode.addChild( _gunControlPanel );
            _rootNode.addChild( _spectrometerCheckBoxNode );
            _rootNode.addChild( _spectrometerNode );
            _rootNode.addChild( _legendNode );
            _rootNode.addChild( _energyDiagramCheckBoxNode );
            _rootNode.addChild( _energyDiagramParent );
            _rootNode.addChild( _notToScaleLabel );
            if ( !HAConstants.DEBROGLIE_VIEW_IN_MENUBAR ) {
                _rootNode.addChild( _deBroglieViewControlWrapper );
            }
        }
        
        //----------------------------------------------------------------------------
        // Model-View management
        //----------------------------------------------------------------------------
        
        _modelViewManager = new HAModelViewManager( _model, _animationBoxNode );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Clock controls
        _clockControlPanel = new HAClockControlPanel( (HAClock) getClock() );
        setClockControlPanel( _clockControlPanel );

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------

        if ( hasHelp() ) {
            //XXX add help items to the help pane
//            HelpPane helpPane = getDefaultHelpPane();
        }
        
        // See initWiggleMe for Wiggle Me initialization.

        //----------------------------------------------------------------------------
        // Initialize the module state
        //----------------------------------------------------------------------------

        reset();
        updateCanvasLayout();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public Gun getGun() {
        return _model.getGun();
    }
    
    //----------------------------------------------------------------------------
    // Reset
    //----------------------------------------------------------------------------

    /*
     * Resets the module to its default state.
     * All default values are defined in HADefaults.
     */
    private void reset() {
        
        if ( HADefaults.MODE_EXPERIMENT ) {
            _modeSwitch.setExperimentSelected();
        }
        else {
            _modeSwitch.setPredictionSelected();
        }
        
        _atomicModelSelector.setSelection( HADefaults.ATOMIC_MODEL );
        
        Gun gun = _model.getGun();
        gun.setEnabled( HADefaults.GUN_ENABLED );
        gun.setMode( HADefaults.GUN_MODE );
        gun.setLightType( HADefaults.LIGHT_TYPE );
        gun.setWavelength( HADefaults.WAVELENGTH );
        gun.setLightIntensity( HADefaults.LIGHT_INTENSITY );
        gun.setAlphaParticlesIntensity( HADefaults.ALPHA_PARTICLES_INTENSITY );
        _alphaParticleTracesNode.setEnabled( HADefaults.SHOW_ALPHA_PARTICLE_TRACES );
        
        _spectrometerCheckBox.setSelected( HADefaults.SPECTROMETER_SELECTED );
        _spectrometerNode.setVisible( HADefaults.SPECTROMETER_SELECTED );
        if ( HADefaults.SPECTROMETER_RUNNING ) {
            _spectrometerNode.start();
        }
        else {
            _spectrometerNode.stop();
        }
        
        _energyDiagramCheckBox.setSelected( HADefaults.ENERGY_DIAGRAM_SELECTED );
        updateEnergyDiagram();
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /**
     * Determines the visible bounds of the canvas in world coordinates.
     */ 
    public Dimension getWorldSize() {
        Dimension2D dim = new PDimension( _canvas.getWidth(), _canvas.getHeight() );
        _canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        Dimension worldSize = new Dimension( (int) dim.getWidth(), (int) dim.getHeight() );
        return worldSize;
    }
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    public void updateCanvasLayout() {

        Dimension worldSize = getWorldSize();
//        System.out.println( "HAModule.updateCanvasLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() == 0 || worldSize.getHeight() == 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        
        // margins and spacing
        final double xMargin = 20;
        final double yMargin = 10;
        final double xSpacing = 20;
        final double ySpacing = 10;

        // reusable (x,y) coordinates, for setting offsets
        double x, y;

        // Mode Switch
        {
            // upper left corner
            _modeSwitch.setOffset( xMargin, yMargin );
        }

        // Atomic Model Selector
        {
            // below mode selector, left aligned
            PBounds msb = _modeSwitch.getFullBounds();
            x = msb.getX();
            y = msb.getY() + msb.getHeight() + ySpacing;
            _atomicModelSelector.setOffset( x, y );
        }

        // Box of Hydrogen / Beam / Gun
        {
            // to the left of the atomic model selector, top aligned
            PBounds ab = _atomicModelSelector.getFullBounds();
            _boxBeamGunParent.setOffset( ab.getMaxX() + xSpacing, ab.getY() );
        }
        
        // Animation box
        {
            // to the right of the box/beam/gun, below the "not to scale" label
            PBounds bb = _boxBeamGunParent.getFullBounds();
            PBounds ntsb = _notToScaleLabel.getFullBounds();
            x = bb.getMaxX() + xSpacing;
            y = yMargin + ntsb.getHeight() + ySpacing;
            _animationBoxNode.setOffset( x, y );
        }
        
        // deBroglie view control, inside top-left of animation box
        {
            x = _animationBoxNode.getFullBounds().getX();
            y = _animationBoxNode.getFullBounds().getY();
            _deBroglieViewControlWrapper.setOffset( x, y );
        }
        
        // "Drawings are not to scale" note
        {
            // centered above animation box
            PBounds bb = _animationBoxNode.getFullBounds();
            x = bb.getX() + ( ( bb.getWidth() - _notToScaleLabel.getFullBounds().getWidth() ) / 2 );
            y = ( bb.getY() - _notToScaleLabel.getFullBounds().getHeight() ) / 2;
            _notToScaleLabel.setOffset( x, y );
        }

        // Gun control panel
        {
            // to the right of atomic model selector, below gun
            x = _atomicModelSelector.getFullBounds().getMaxX() + xSpacing;
            y = _boxBeamGunParent.getFullBounds().getMaxY(); // no vertical spacing!
            _gunControlPanel.setOffset( x, y );
        }
        
        // Adjust gun and gun control panel locations so they don't overlap animation box 
        double overlap = ( _animationBoxNode.getFullBounds().getMaxY() + ySpacing ) - _gunControlPanel.getFullBounds().getY();
        if ( overlap > 0 ) {
            _boxBeamGunParent.setOffset( _boxBeamGunParent.getFullBounds().getX(), _boxBeamGunParent.getFullBounds().getY() + overlap );
            _gunControlPanel.setOffset( _gunControlPanel.getFullBounds().getX(), _gunControlPanel.getFullBounds().getY() + overlap );
        }

        // Zoom Indicator
        {
            PBounds tb = _boxOfHydrogenNode.getTinyBoxGlobalBounds();
            Point2D tp = _rootNode.globalToLocal( tb.getOrigin() );
            Dimension2D td = _rootNode.globalToLocal( tb.getSize() );
            PBounds ab = _animationBoxNode.getFullBounds();
            _zoomIndicatorNode.update( tp, td, ab.getOrigin(), ab.getSize() );
        }

        // Energy Diagram
        {
            // diagram to the right of the black box, at top of canvas
            x = _animationBoxNode.getFullBounds().getMaxX() + xSpacing;
            y = yMargin;
            _energyDiagramParent.setOffset( x, y );
            
            // checkbox under upper-left corner of diagram
            _energyDiagramCheckBoxNode.setOffset( x, y );
        }
        
        // Legend
        {
            x = _animationBoxNode.getFullBounds().getMaxX() + xSpacing;
            y = _animationBoxNode.getFullBounds().getY() + ( ( _animationBoxNode.getFullBounds().getHeight() -  _legendNode.getFullBounds().getHeight() ) / 2 );
            _legendNode.setOffset( x, y );
        }

        // Spectrometer
        {
            // spectrometer below animation box, to the right of gun control panel
            x = _gunControlPanel.getFullBounds().getMaxX() + xSpacing;
            y = _animationBoxNode.getFullBounds().getMaxY() + ySpacing;
            _spectrometerNode.setOffset( x, y );

            // checkbox under upper-left corner of spectrometer
            _spectrometerCheckBoxNode.setOffset( x, y );
        }
        
        initWiggleMe();
        
        applyLayoutHacks();
    }
    
    /*
     * Various hacks that should be addressed in better ways.
     */
    private void applyLayoutHacks() {
        /*
         * For the drag bounds of the WavelengthControl to be updated.
         * WavelengthControl uses a ConstrainedDragHandler which works in screen coordinates.
         * When the screen position of the WavelengthControl is changed, it has no way of telling.
         * So its ConstrainedDragHandler doesn't get its drag bounds updated.
         * This should be addressed by making ConstrainedDragHandler work in the local 
         * coordinates of the node that its constraining. (I think...)
         */
        _gunControlPanel.updateWavelengthControlDragBounds();
    }
    
    //----------------------------------------------------------------------------
    // Wiggle Me
    //----------------------------------------------------------------------------
    
    /*
     * Initializes a wiggle me that points to the gun on/off button.
     */
    private void initWiggleMe() {
        if ( !_wiggleMeInitialized ) {
            
            // Create wiggle me, add to root node.
            String wiggleMeString = SimStrings.get( "wiggleMe.gun" );  
            _wiggleMe = new HAWiggleMe( _canvas, wiggleMeString );
            _rootNode.addChild( _wiggleMe );
            
            // Animate from the upper-left to the gun button position
            PNode gunButtonNode = _gunNode.getButtonNode();
            Rectangle2D bounds = _rootNode.globalToLocal( gunButtonNode.getGlobalFullBounds() );
            final double x = bounds.getX() + ( bounds.getWidth() / 2 );
            final double y = bounds.getY();
            _wiggleMe.setOffset( 0, -100 );
            _wiggleMe.animateTo( x, y );
            
            // Clicking on the canvas makes the wiggle me go away.
            _canvas.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    _wiggleMe.setEnabled( false );
                    _rootNode.removeChild( _wiggleMe );
                    _canvas.removeInputEventListener( this );
                    _wiggleMe = null;
                }
            } );
            
            _wiggleMeInitialized = true;
        }
    }

    //----------------------------------------------------------------------------
    // Atomic Models
    //----------------------------------------------------------------------------

    private void handleModeChange() {
        _atomicModelSelector.setVisible( _modeSwitch.isPredictionSelected() );
        updateAtomicModel();
        updateEnergyDiagram();
        _model.removeAllAlphaParticles();
        _model.removeAllPhotons();
        _spectrometerNode.reset();
        _legendNode.setVisible( _modeSwitch.isPredictionSelected() );
    }
    
    private void handleAtomicModelChange() {
        updateAtomicModel();
        updateEnergyDiagram();
        _model.removeAllAlphaParticles();
        _model.removeAllPhotons();
        _spectrometerNode.reset();
    }

    public void updateAtomicModel() {

        _deBroglieViewControlWrapper.setVisible( false );
        
        _alphaParticleTracesNode.clear();
        
        _solarSystemEnergyDiagram.clearAtom();
        _bohrEnergyDiagram.clearAtom();
        _deBroglieEnergyDiagram.clearAtom();
        _schrodingerEnergyDiagram.clearAtom();
        
        if ( _metastableHandler != null ) {
            _metastableHandler.cleanup();
            _metastableHandler = null;
        }
        
        if ( _atomModel != null ) {
            _model.removeModelElement( _atomModel );
            _atomModel.removePhotonEmittedListener( _spectrometerNode );
            _atomModel = null;
        }
        
        Point2D position = _model.getSpace().getCenter();
        
        if ( _modeSwitch.isExperimentSelected() ) {
            _atomModel = new ExperimentModel( position );
        }
        else {
            AtomicModel atomicModel = _atomicModelSelector.getSelection();
            if ( atomicModel == AtomicModel.BILLIARD_BALL ) {
                _atomModel = new BilliardBallModel( position );
            }
            else if ( atomicModel == AtomicModel.PLUM_PUDDING ) {
                _atomModel = new PlumPuddingModel( position );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                SolarSystemModel solarSystemModel = new SolarSystemModel( position );
                _atomModel = solarSystemModel;
                _solarSystemEnergyDiagram.setAtom( solarSystemModel );
            }
            else if ( atomicModel == AtomicModel.BOHR ) {
                BohrModel bohrModel = new BohrModel( position );
                _atomModel = bohrModel;
                _bohrEnergyDiagram.setAtom( bohrModel );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                DeBroglieModel deBroglieModel = new DeBroglieModel( position );
                _atomModel = deBroglieModel;
                _deBroglieEnergyDiagram.setAtom( deBroglieModel );
                _deBroglieViewControlWrapper.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                SchrodingerModel schrodingerModel = new SchrodingerModel( position );
                _atomModel = schrodingerModel;
                _schrodingerEnergyDiagram.setAtom( schrodingerModel );
                _metastableHandler = new MetastableHandler( getClock(), _model.getGun(), schrodingerModel );
            }
            else { 
                throw new UnsupportedOperationException( "unsupported atom model: " + atomicModel );
            }
        }
        
        assert ( _atomModel != null );
        _model.addModelElement( _atomModel );
        _atomModel.addPhotonEmittedListener( _spectrometerNode );
        
        int groundState = _atomModel.getGroundState();
        double[] transitionWavelengths = _atomModel.getTransitionWavelengths( groundState );
        _gunControlPanel.setTransitionWavelengths( transitionWavelengths );
    }

    /**
     * Sets the type of view for deBroglie atoms.
     * If the current atom is a deBroglie atom, then reset the model
     * so that the view will update.
     * 
     * @param view
     */
    public void setDeBroglieView( DeBroglieView view ) {
        DeBroglieModel.DEFAULT_VIEW = view;
        AbstractHydrogenAtom atom = _model.getAtom();
        if ( atom instanceof DeBroglieModel ) {
            ( (DeBroglieModel) atom ).setView( view );
        }
    }
    
    //----------------------------------------------------------------------------
    // Spectrometer
    //----------------------------------------------------------------------------
    
    public void handleSpectrometerCheckBox() {
        final boolean visible = _spectrometerCheckBox.isSelected();
        _spectrometerNode.setVisible( visible );
        Iterator i = _spectrometerSnapshotNodes.iterator();
        while ( i.hasNext() ) {
            SpectrometerSnapshotNode snapshotNode = (SpectrometerSnapshotNode) i.next();
            snapshotNode.setVisible( visible );
        }
    }
    
    private void handleSpectrometerSnapshot() {
        
        // increment the counter used to title the snapshots
        _spectrometerSnapshotsCounter++;

        // create the snapshot title
        String title = SimStrings.get( "label.snapshot" ) + " " + _spectrometerSnapshotsCounter + ": ";
        if ( _modeSwitch.isPredictionSelected() ) {
            title += _atomicModelSelector.getSelectionName();
        }
        else {
            title += SimStrings.get( "title.spectrometer.experiment" );
        }

        // create the snapshot's Piccolo node
        final SpectrometerSnapshotNode snapshotNode = _spectrometerNode.getSnapshot( title );
        snapshotNode.addCloseListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                _rootNode.removeChild( snapshotNode );
                _spectrometerSnapshotNodes.remove( snapshotNode );
            }
        } );
        _rootNode.addChild( snapshotNode );

        // set the snapshot's position
        PBounds sb = _spectrometerNode.getFullBounds();
        double x = 0;
        double y = 0;
        if ( _spectrometerSnapshotNodes.size() == 0 ) {
            // first snapshot goes directly above spectrometer
            x = sb.getX();
            y = sb.getY() - snapshotNode.getFullBounds().getHeight() - 5;
        }
        else {
            // overlap the most-recently created snapshot, slightly above and to the left 
            PBounds fb = ((PNode)_spectrometerSnapshotNodes.get( _spectrometerSnapshotNodes.size() - 1 )).getFullBounds();
            x = fb.getX() - 30; // far enough to the left that you can see the other snapshot's close button
            y = fb.getY() - 20;
        }
        snapshotNode.setOffset( x, y );
        
        // add to snapshot list *after* setting bounds
        _spectrometerSnapshotNodes.add( snapshotNode );
    }
    
    //----------------------------------------------------------------------------
    // Energy Diagrams
    //----------------------------------------------------------------------------
    
    public void updateEnergyDiagram() {

        AtomicModel atomicModel = _atomicModelSelector.getSelection();

        _energyDiagramCheckBoxNode.setVisible( false );
        _bohrEnergyDiagram.setVisible( false );
        _deBroglieEnergyDiagram.setVisible( false );
        _schrodingerEnergyDiagram.setVisible( false );
        _solarSystemEnergyDiagram.setVisible( false );

        if ( _modeSwitch.isPredictionSelected() ) {
            if ( atomicModel == AtomicModel.BOHR ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _bohrEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _deBroglieEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _schrodingerEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _energyDiagramCheckBoxNode.setVisible( true );
                _solarSystemEnergyDiagram.setVisible( _energyDiagramCheckBox.isSelected() );
            }
        }
    }
}
