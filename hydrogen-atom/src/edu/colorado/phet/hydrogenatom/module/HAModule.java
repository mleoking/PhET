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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;

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
import edu.colorado.phet.hydrogenatom.help.HAWiggleMe;
import edu.colorado.phet.hydrogenatom.model.HAClock;
import edu.colorado.phet.hydrogenatom.model.Gun;
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
    private ExperimentAtomNode _experimentAtomNode;
    private BilliardBallAtomNode _billiardBallAtomNode;
    private BohrAtomNode _bohrAtomNode;
    private DeBroglieAtomNode _deBroglieAtomNode;
    private PlumPuddingAtomNode _plumPuddingAtomNode;
    private SchrodingerAtomNode _schrodingerAtomNode;
    private SolarSystemAtomNode _solarSystemAtomNode;

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

    private PhotonNode _samplePhotonNode1, _samplePhotonNode2, _samplePhotonNode3;
    private AlphaParticleNode _sampleAlphaParticleNode;
    
    private Font _spectrometerFont;

    private Gun _gun;
    
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

        // Photon gun
        {
            Point2D position = new Point2D.Double( 0, 0 );
            double orientation = -90; // degrees, pointing straight up
            double beamWidth = 50;
            _gun = new Gun( position, orientation, beamWidth, HAConstants.MIN_WAVELENGTH, HAConstants.MAX_WAVELENGTH );
            getClock().addClockListener( _gun );
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

        // Atomic Model selector
        _atomicModelSelector = new AtomicModelSelector();

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

            // atoms
            _experimentAtomNode = new ExperimentAtomNode();
            _billiardBallAtomNode = new BilliardBallAtomNode();
            _bohrAtomNode = new BohrAtomNode();
            _deBroglieAtomNode = new DeBroglieAtomNode();
            _plumPuddingAtomNode = new PlumPuddingAtomNode();
            _schrodingerAtomNode = new SchrodingerAtomNode();
            _solarSystemAtomNode = new SolarSystemAtomNode();

            // layering order
            _animationRegionNode.addChild( _experimentAtomNode );
            _animationRegionNode.addChild( _billiardBallAtomNode );
            _animationRegionNode.addChild( _bohrAtomNode );
            _animationRegionNode.addChild( _deBroglieAtomNode );
            _animationRegionNode.addChild( _plumPuddingAtomNode );
            _animationRegionNode.addChild( _schrodingerAtomNode );
            _animationRegionNode.addChild( _solarSystemAtomNode );
            
            // positioning, centered in region
            {
                PBounds ab = _animationRegionNode.getFullBounds();
                double x, y;
                
                x = ( ab.getWidth() - _experimentAtomNode.getFullBounds().getWidth() ) / 2;
                y = ( ab.getHeight() - _experimentAtomNode.getFullBounds().getHeight() ) / 2;
                _experimentAtomNode.setOffset( x, y );

                x = ab.getWidth() / 2;
                y = ab.getHeight() / 2;
                _billiardBallAtomNode.setOffset( x, y );
                _bohrAtomNode.setOffset( x, y );
                _deBroglieAtomNode.setOffset( x, y );
                _plumPuddingAtomNode.setOffset( x, y );
                _schrodingerAtomNode.setOffset( x, y );
                _solarSystemAtomNode.setOffset( x, y );
            }
            
            //XXX sample photons and alpha particle
            {
                _samplePhotonNode1 = new PhotonNode( Color.RED );
                _samplePhotonNode2 = new PhotonNode( Color.YELLOW);
                _samplePhotonNode3 = new PhotonNode( HAConstants.UV_COLOR );
                _sampleAlphaParticleNode = new AlphaParticleNode();
                _animationRegionNode.addChild( _samplePhotonNode1 );
                _animationRegionNode.addChild( _samplePhotonNode2 );
                _animationRegionNode.addChild( _samplePhotonNode3 );
                _animationRegionNode.addChild( _sampleAlphaParticleNode );

                double x = 50;
                double y =_animationRegionNode.getFullBounds().getMaxY() - 70;
                _samplePhotonNode1.setOffset( x, y );
                _samplePhotonNode2.setOffset( x + 100, y );
                _samplePhotonNode3.setOffset( x + 200, y );
                _sampleAlphaParticleNode.setOffset( x + 300, y );
            }
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
        
        // Wiggle Me -- location is controlled in SimStrings file
        String wiggleMeString = SimStrings.get( "wiggleMe.gun" );
        final int x = SimStrings.getInt( "wiggleMe.x", 200 );
        final int y = SimStrings.getInt( "wiggleMe.y", 400 );
        final HAWiggleMe wiggleMe = new HAWiggleMe( _canvas, wiggleMeString );
        _rootNode.addChild( wiggleMe );
        wiggleMe.setOffset( x, -100 );
        wiggleMe.animateTo( x, y );
        _canvas.addInputEventListener( new PBasicInputEventHandler() {
            // Clicking on the canvas makes the wiggle me go away.
            public void mousePressed( PInputEvent event ) {
                wiggleMe.setEnabled( false );
                _rootNode.removeChild( wiggleMe );
                _canvas.removeInputEventListener( this );
            }
        } );

        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------

        reset();
        updateCanvasLayout();
    }

    //----------------------------------------------------------------------------
    //
    //----------------------------------------------------------------------------

    private void reset() {
        
        _modeSwitch.setPredictionSelected();
        _atomicModelSelector.setSelection( AtomicModel.BILLIARD_BALL );
        
        _gun.setEnabled( false );
        _gun.setMode( Gun.MODE_PHOTONS );
        _gun.setLightType( Gun.LIGHT_TYPE_MONOCHROMATIC );
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
    }

    public void updateAtomicModelSelector() {
        _atomicModelSelector.setVisible( _modeSwitch.isPredictionSelected() );
    }

    public void updateAtomicModel() {

        AtomicModel atomicModel = _atomicModelSelector.getSelection();

        _experimentAtomNode.setVisible( false );
        _billiardBallAtomNode.setVisible( false );
        _bohrAtomNode.setVisible( false );
        _deBroglieAtomNode.setVisible( false );
        _plumPuddingAtomNode.setVisible( false );
        _schrodingerAtomNode.setVisible( false );
        _solarSystemAtomNode.setVisible( false );

        if ( _modeSwitch.isExperimentSelected() ) {
            _experimentAtomNode.setVisible( true );
        }
        else {
            if ( atomicModel == AtomicModel.BILLIARD_BALL ) {
                _billiardBallAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.BOHR ) {
                _bohrAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.DEBROGLIE ) {
                _deBroglieAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.PLUM_PUDDING ) {
                _plumPuddingAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SCHRODINGER ) {
                _schrodingerAtomNode.setVisible( true );
            }
            else if ( atomicModel == AtomicModel.SOLAR_SYSTEM ) {
                _solarSystemAtomNode.setVisible( true );
            }
        }
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
