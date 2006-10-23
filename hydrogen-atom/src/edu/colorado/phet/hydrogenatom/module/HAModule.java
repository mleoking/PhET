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
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.AtomicModelSelector;
import edu.colorado.phet.hydrogenatom.control.GunControlPanel;
import edu.colorado.phet.hydrogenatom.control.HAClockControlPanel;
import edu.colorado.phet.hydrogenatom.control.ModeSwitch;
import edu.colorado.phet.hydrogenatom.energydiagrams.BohrEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.DeBroglieEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SchrodingerEnergyDiagram;
import edu.colorado.phet.hydrogenatom.energydiagrams.SolarSystemEnergyDiagram;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.hydrogenatom.enums.GunMode;
import edu.colorado.phet.hydrogenatom.enums.LightType;
import edu.colorado.phet.hydrogenatom.factory.AlphaParticleNodeFactory;
import edu.colorado.phet.hydrogenatom.factory.BilliardBallNodeFactory;
import edu.colorado.phet.hydrogenatom.factory.PhotonNodeFactory;
import edu.colorado.phet.hydrogenatom.help.HAWiggleMe;
import edu.colorado.phet.hydrogenatom.model.*;
import edu.colorado.phet.hydrogenatom.spectrometer.SpectrometerNode;
import edu.colorado.phet.hydrogenatom.view.*;
import edu.colorado.phet.hydrogenatom.view.LegendPanel.LegendNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;


public class HAModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetPCanvas _canvas;
    private PNode _rootNode;
    private HAController _controller;

    // Control panels
    private HAClockControlPanel _clockControlPanel;
    private ModeSwitch _modeSwitch;
    private AtomicModelSelector _atomicModelSelector;
    private GunControlPanel _gunControlPanel;

    // Box/beam/gun
    private PNode _boxBeamGunParent;
    private BoxOfHydrogenNode _boxOfHydrogenNode;
    private BeamNode _beamNode;
    private GunNode _gunNode;

    // Animation region
    private AnimationRegionNode _animationRegionNode;
    private ZoomIndicatorNode _zoomIndicatorNode;

    // Spectrometer
    private JCheckBox _spectrometerCheckBox;
    private PSwing _spectrometerCheckBoxNode;
    private SpectrometerNode _spectrometerNode;
    private ArrayList _spectrometerSnapshots; // list of Spectrometer
    private int _spectrumSnapshotCounter; // incremented each time a spectrometer snapshot is taken

    // Energy Diagrams
    private JCheckBox _energyDiagramCheckBox;
    private PSwing _energyDiagramCheckBoxNode;
    private PhetPNode _energyDiagramParent;
    private BohrEnergyDiagram _bohrEnergyDiagram;
    private DeBroglieEnergyDiagram _deBroglieEnergyDiagram;
    private SchrodingerEnergyDiagram _schrodingerEnergyDiagram;
    private SolarSystemEnergyDiagram _solarSystemEnergyDiagram;

    private NotToScaleNode _notToScaleLabel;
    private LegendNode _legendNode;
    
    private Font _spectrometerFont;

    private Model _model;
    private Gun _gun;
    private Space _space;
    private AbstractHydrogenAtom _hydrogenAtomModel;
    
    private HAWiggleMe _wiggleMe;
    private boolean _wiggleMeInitialized = false;
    
    private HAModelViewManager _modelViewManager;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public HAModule() {
        super( SimStrings.get( "HAModule.title" ), new HAClock() );

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
        
        // Model
        {
            _model = new Model( clock );
        }
        
        // Photon gun
        {
            Point2D position = new Point2D.Double( 0, 0 );
            double orientation = Math.toRadians( -90 ); // degrees, pointing straight up
            double nozzleWidth = SimStrings.getInt( "animationRegion.width", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.width );
            _gun = new Gun( position, orientation, nozzleWidth, HAConstants.MIN_WAVELENGTH, HAConstants.MAX_WAVELENGTH );
        }
        
        // Space
        Point2D spaceCenter = new Point2D.Double();
        {
            double spaceWidth = _gun.getNozzleWidth();
            double spaceHeight = SimStrings.getInt( "animationRegion.height", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.height );
            Rectangle2D bounds = new Rectangle2D.Double( -spaceWidth/2, -spaceHeight, spaceWidth, spaceHeight );
            _space = new Space( bounds, _gun, _model );
            
            spaceCenter.setLocation( 0, -spaceHeight / 2 );
        }
        
        _model.addModelObject( _gun );
        _model.addModelObject( _space );
        
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
               _space.removeAllAlphaParticles();
               _space.removeAllPhotons();
           }
        });

        // Atomic Model selector
        _atomicModelSelector = new AtomicModelSelector();
        _atomicModelSelector.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                _space.removeAllAlphaParticles();
                _space.removeAllPhotons();
            }
         });
        
        //  Box of Hydrogen / Beam / Gun
        {
            // Parent node, used for layout
            _boxBeamGunParent = new PNode();

            _boxOfHydrogenNode = new BoxOfHydrogenNode( HAConstants.BOX_OF_HYDROGEN_SIZE, HAConstants.TINY_BOX_SIZE );
            _beamNode = new BeamNode( HAConstants.BEAM_SIZE, _gun );
            _gunNode = new GunNode( _gun );

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

        // Animation region
        {
            // Load the region dimensions from the localization file
            int width = SimStrings.getInt( "animationRegion.width", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.width );
            int height = SimStrings.getInt( "animationRegion.height", HAConstants.DEFAULT_ANIMATION_REGION_SIZE.height );
            Dimension size = new Dimension( width, height );
            
            // animation box
            _animationRegionNode = new AnimationRegionNode( size );

            // zoom indicator
            _zoomIndicatorNode = new ZoomIndicatorNode();
        }

        // Gun control panel
        _gunControlPanel = new GunControlPanel( _canvas, _gun );

        // Spectrometer
        {
            // Checkbox
            _spectrometerCheckBox = new JCheckBox( SimStrings.get( "label.showSpectrometer" ) );
//            _spectrometerCheckBox.setOpaque( false ); //XXX preferrable to setting background, but looks lousy on Windows
            _spectrometerCheckBox.setBackground( HAConstants.CANVAS_BACKGROUND );
            _spectrometerCheckBox.setForeground( HAConstants.CANVAS_LABELS_COLOR );
            _spectrometerCheckBox.setFont( jComponentFont );
            _spectrometerCheckBoxNode = new PSwing( _canvas, _spectrometerCheckBox );

            // Spectrometer
            String title = SimStrings.get( "label.photonsEmitted" );
            _spectrometerNode = new SpectrometerNode( _canvas, title, _spectrometerFont, false /* isaSnapshot */);

            // List of snapshots
            _spectrometerSnapshots = new ArrayList();
        }

        // Energy diagrams
        {
            // checkbox
            _energyDiagramCheckBox = new JCheckBox( SimStrings.get( "label.showEnergyDiagram" ) );
//            _energyDiagramCheckBox.setOpaque( false );//XXX preferrable to setting background, but looks lousy on Windows
            _energyDiagramCheckBox.setBackground( HAConstants.CANVAS_BACKGROUND );
            _energyDiagramCheckBox.setForeground( HAConstants.CANVAS_LABELS_COLOR );
            _energyDiagramCheckBox.setFont( jComponentFont );
            _energyDiagramCheckBoxNode = new PSwing( _canvas, _energyDiagramCheckBox );

            // diagrams
            _bohrEnergyDiagram = new BohrEnergyDiagram();
            _deBroglieEnergyDiagram = new DeBroglieEnergyDiagram();
            _schrodingerEnergyDiagram = new SchrodingerEnergyDiagram();
            _solarSystemEnergyDiagram = new SolarSystemEnergyDiagram();
            
            // parent node for all diagrams
            _energyDiagramParent = new PhetPNode();
            _energyDiagramParent.addChild( _bohrEnergyDiagram );
            _energyDiagramParent.addChild( _deBroglieEnergyDiagram );
            _energyDiagramParent.addChild( _schrodingerEnergyDiagram );
            _energyDiagramParent.addChild( _solarSystemEnergyDiagram );
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
            _rootNode.addChild( _animationRegionNode );
            _rootNode.addChild( _zoomIndicatorNode );
            _rootNode.addChild( _gunControlPanel );
            _rootNode.addChild( _spectrometerCheckBoxNode );
            _rootNode.addChild( _spectrometerNode );
            _rootNode.addChild( _legendNode );
            _rootNode.addChild( _energyDiagramCheckBoxNode );
            _rootNode.addChild( _energyDiagramParent );
            _rootNode.addChild( _notToScaleLabel );
        }
        
        //----------------------------------------------------------------------------
        // Model-View management
        //----------------------------------------------------------------------------
        
        _modelViewManager = new HAModelViewManager( _model, _animationRegionNode );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Clock controls
        _clockControlPanel = new HAClockControlPanel( (HAClock) getClock() );
        setClockControlPanel( _clockControlPanel );

        _controller = new HAController( this, _modeSwitch, _atomicModelSelector, 
                _energyDiagramCheckBox, _spectrometerNode, _spectrometerCheckBox );

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
        return _gun;
    }
    
    //----------------------------------------------------------------------------
    //
    //----------------------------------------------------------------------------

    private void reset() {
        
        _modeSwitch.setPredictionSelected();
        _atomicModelSelector.setSelection( AtomicModel.BILLIARD_BALL );
        
        _gun.setEnabled( false );
        _gun.setMode( GunMode.PHOTONS );
        _gun.setLightType( LightType.MONOCHROMATIC );
        _gun.setWavelength( VisibleColor.MIN_WAVELENGTH );
        _gun.setLightIntensity( 1 );
        _gun.setAlphaParticlesIntensity( 1 );
        
        _spectrometerCheckBox.setSelected( true );
        _energyDiagramCheckBox.setSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    public void updateCanvasLayout() {

        Dimension worldSize = getWorldSize();
        System.out.println( "HAModule.updateCanvasLayout worldSize=" + worldSize );//XXX
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
        
        // Animation Region
        {
            // to the right of the box/beam/gun, below the "not to scale" label
            PBounds bb = _boxBeamGunParent.getFullBounds();
            PBounds ntsb = _notToScaleLabel.getFullBounds();
            x = bb.getMaxX() + xSpacing;
            y = yMargin + ntsb.getHeight() + ySpacing;
            _animationRegionNode.setOffset( x, y );
        }
        
        // "Drawings are not to scale" note
        {
            // centered above animation region
            PBounds bb = _animationRegionNode.getFullBounds();
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
        
        // Adjust gun and gun control panel locations so they don't overlap animation region 
        double overlap = ( _animationRegionNode.getFullBounds().getMaxY() + ySpacing ) - _gunControlPanel.getFullBounds().getY();
        if ( overlap > 0 ) {
            _boxBeamGunParent.setOffset( _boxBeamGunParent.getFullBounds().getX(), _boxBeamGunParent.getFullBounds().getY() + overlap );
            _gunControlPanel.setOffset( _gunControlPanel.getFullBounds().getX(), _gunControlPanel.getFullBounds().getY() + overlap );
        }

        // Zoom Indicator
        {
            PBounds tb = _boxOfHydrogenNode.getTinyBoxGlobalBounds();
            Point2D tp = _rootNode.globalToLocal( tb.getOrigin() );
            Dimension2D td = _rootNode.globalToLocal( tb.getSize() );
            PBounds ab = _animationRegionNode.getFullBounds();
            _zoomIndicatorNode.update( tp, td, ab.getOrigin(), ab.getSize() );
        }

        // Energy Diagram
        {
            // checkbox to the right of the black box, at top of canvas
            x = _animationRegionNode.getFullBounds().getMaxX() + xSpacing;
            y = yMargin;
            _energyDiagramCheckBoxNode.setOffset( x, y );

            // diagram below checkbox, left aligned.
            x = _energyDiagramCheckBoxNode.getFullBounds().getX();
            y = _energyDiagramCheckBoxNode.getFullBounds().getMaxY() + ySpacing;
            _energyDiagramParent.setOffset( x, y );
        }
        
        // Legend
        {
            x = _animationRegionNode.getFullBounds().getMaxX() + xSpacing;
            y = _animationRegionNode.getFullBounds().getY() + ( ( _animationRegionNode.getFullBounds().getHeight() -  _legendNode.getFullBounds().getHeight() ) / 2 );
            _legendNode.setOffset( x, y );
        }

        // Spectrometer
        {
            // spectrometer below animation region, to the right of gun control panel
            x = _gunControlPanel.getFullBounds().getMaxX() + xSpacing;
            y = _animationRegionNode.getFullBounds().getMaxY() + ySpacing;
            _spectrometerNode.setOffset( x, y );

            // checkbox at lower right of animation region
            x = _animationRegionNode.getFullBounds().getMaxX() + xSpacing;
            y = _animationRegionNode.getFullBounds().getMaxY() - _spectrometerCheckBoxNode.getFullBounds().getHeight();
            _spectrometerCheckBoxNode.setOffset( x, y );
        }
        
        initWiggleMe();
        
        applyLayoutHacks();
    }
    
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

    public void updateAtomicModelSelector() {
        _atomicModelSelector.setVisible( _modeSwitch.isPredictionSelected() );
    }

    public void updateAtomicModel() {

        if ( _hydrogenAtomModel != null ) {
            _model.removeModelObject( _hydrogenAtomModel );
            _hydrogenAtomModel = null;
        }
        
        Point2D position = _space.getCenter();
        
        if ( _modeSwitch.isExperimentSelected() ) {
            _hydrogenAtomModel = new ExperimentModel( position );
        }
        else {
            AtomicModel atomicModel = _atomicModelSelector.getSelection();
            if ( atomicModel == AtomicModel.BILLIARD_BALL ) {
                _hydrogenAtomModel = new BilliardBallModel( position );
            }
            else if ( atomicModel == AtomicModel.BOHR ) {
                _hydrogenAtomModel = new BohrModel( position );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _hydrogenAtomModel = new DeBroglieModel( position );
            }
            else if ( atomicModel == AtomicModel.PLUM_PUDDING ) {
                _hydrogenAtomModel = new PlumPuddingModel( position );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _hydrogenAtomModel = new SchrodingerModel( position );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _hydrogenAtomModel = new SolarSystemModel( _space.getCenter() );
            }
        }
        
        assert ( _hydrogenAtomModel != null );
        _model.addModelObject( _hydrogenAtomModel );
    }

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

    public void updateSpectrometer() {
        final boolean visible = _spectrometerCheckBox.isSelected();
        _spectrometerNode.setVisible( visible );
        Iterator i = _spectrometerSnapshots.iterator();
        while ( i.hasNext() ) {
            SpectrometerNode spectrometer = (SpectrometerNode) i.next();
            spectrometer.setVisible( visible );
        }
    }

    public void createSpectrometerSnapshot() {

        _spectrumSnapshotCounter++;

        String title = SimStrings.get( "label.snapshot" ) + " " + _spectrumSnapshotCounter + ": ";
        if ( _modeSwitch.isPredictionSelected() ) {
            //XXX replace this call, the title may contain HTML markup
            title += _atomicModelSelector.getSelectionName();
        }
        else {
            title += SimStrings.get( "title.spectrometer.experiment" );
        }

        final SpectrometerNode spectrometer = new SpectrometerNode( _canvas, title, _spectrometerFont, true /* isaSnapshot */);
        
        _rootNode.addChild( spectrometer );
        _controller.addSpectrometerListener( spectrometer );
        _spectrometerSnapshots.add( spectrometer );

        PBounds sb = _spectrometerNode.getFullBounds();
        double x = sb.getX();
        double y = sb.getY() - spectrometer.getFullBounds().getHeight() - ( 10 * _spectrometerSnapshots.size() );
        spectrometer.setOffset( x, y );
    }

    public void deleteSpectrometerSnapshot( SpectrometerNode spectrometer ) {
        if ( spectrometer == _spectrometerNode ) {
            _spectrometerCheckBox.setSelected( false );
        }
        else {
            _rootNode.removeChild( spectrometer );
            _spectrometerSnapshots.remove( spectrometer );
        }
    }
    
    /**
     * Determines the visible bounds of the canvas in world coordinates.
     */ 
    public Dimension getWorldSize() {
        Dimension2D dim = new PDimension( _canvas.getWidth(), _canvas.getHeight() );
        _canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        Dimension worldSize = new Dimension( (int) dim.getWidth(), (int) dim.getHeight() );
        return worldSize;
    }
}
