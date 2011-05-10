// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.IfElse;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.*;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.DefaultIconButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;
import static edu.colorado.phet.gravityandorbits.GAOStrings.*;
import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.BACKGROUND;
import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.CONTROL_FONT;
import static java.awt.Color.BLACK;
import static java.awt.Color.green;

/**
 * Provides the play area for a single GravityAndOrbitsMode.
 *
 * @author Sam Reid
 * @see GravityAndOrbitsMode
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private final PNode rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    public static final Color buttonBackgroundColor = new Color( 255, 250, 125 );

    public GravityAndOrbitsCanvas( final GravityAndOrbitsModel model, final GravityAndOrbitsModule module, final GravityAndOrbitsMode mode, final double forceScale ) {
        super( new Dimension( 1500, 1500 ) );//view size

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        module.whiteBackgroundProperty.addObserver( new SimpleObserver() {
            public void update() {
                setBackground( module.whiteBackgroundProperty.get() ? Color.white : Color.black );
            }
        } );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // stores the current position of the mouse so we can change to cursor hand when an object moves under the mouse
        final Property<ImmutableVector2D> mousePositionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent mouseEvent ) {
            }

            public void mouseMoved( MouseEvent mouseEvent ) {
                mousePositionProperty.set( new ImmutableVector2D( mouseEvent.getPoint().x, mouseEvent.getPoint().y ) );
            }
        } );

        for ( Body body : model.getBodies() ) {
            addChild( new PathNode( body, mode.transform, module.showPathProperty, body.getColor() ) );
        }

        Color forceVectorColorFill = PhetColorScheme.GRAVITATIONAL_FORCE;
        Color forceVectorColorOutline = Color.darkGray;

        Color velocityVectorColorFill = PhetColorScheme.VELOCITY;
        Color velocityVectorColorOutline = Color.darkGray;

        //Add graphics for each of the bodies (including BodyNode, mass readout and wire up 'return object' button).
        ArrayList<Property<Boolean>> returnable = new ArrayList<Property<Boolean>>();//Use canvas coordinates to determine whether something has left the visible area
        for ( final Body body : model.getBodies() ) {
            final BodyNode bodyNode = new BodyNode( body, mode.transform, mousePositionProperty, this, body.getLabelAngle() );
            addChild( bodyNode );
            returnable.add( new Property<Boolean>( false ) {{
                final SimpleObserver updateReturnable = new SimpleObserver() {
                    public void update() {
                        Rectangle2D canvasBounds = new Rectangle2D.Double( 0, 0, GravityAndOrbitsCanvas.this.getWidth(), GravityAndOrbitsCanvas.this.getHeight() );
                        set( !canvasBounds.intersects( bodyNode.getGlobalFullBounds() ) );
                    }
                };
                body.getPositionProperty().addObserver( updateReturnable );
                //Have to listen to when this canvas gets added to the swing scene graph, then update since the bounds are correct then (not on ComponentAdapter methods)
                //This listener solves the problem that the 'return object' button is in the wrong state on startup
                addHierarchyListener( new HierarchyListener() {
                    public void hierarchyChanged( HierarchyEvent e ) {
                        updateReturnable.update();
                    }
                } );

                //This component listener solves the problem that the 'return object' button is in the wrong state when switching between modes
                addComponentListener( new ComponentAdapter() {
                    @Override public void componentResized( ComponentEvent e ) {
                        updateReturnable.update();
                    }
                } );
            }} );
            addChild( mode.massReadoutFactory.apply( bodyNode, module.showMassProperty ) );
        }

        //Add gravity force vector nodes
        for ( Body body : model.getBodies() ) {
            addChild( new VectorNode( body, mode.transform, module.showGravityForceProperty, body.getForceProperty(), forceScale, forceVectorColorFill, forceVectorColorOutline ) );
        }
        //Add velocity vector nodes
        for ( Body body : model.getBodies() ) {
            if ( !body.fixed ) {
                addChild( new GrabbableVectorNode( body, mode.transform, module.showVelocityProperty, body.getVelocityProperty(),
                                                   mode.getVelocityVectorScale(), velocityVectorColorFill, velocityVectorColorOutline, "V" ) );//TODO: i18n of "V", also recommended to trim to 1 char
            }
        }

        //Add explosion nodes, which are always in the scene graph but only visible during explosions
        for ( Body body : model.getBodies() ) {
            addChild( new ExplosionNode( body, mode.transform ) );
        }

        //Add the piccolo node for the overlay grid, setting its visibility based on the module.showGridProperty
        addChild( new GridNode( mode.transform, mode.getGridSpacing(), mode.getGridCenter() ) {{
            module.showGridProperty.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.showGridProperty.get() );
                }
            } );
        }} );

        // Control Panel
        final PNode controlPanelNode = new ControlPanelNode( new PSwing( new GravityAndOrbitsControlPanel( module, model ) ), BACKGROUND, new BasicStroke( 3 ), green, 4 ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 2 );
            setScale( 0.82 );//fine tuned so that control panels for all modes always fit on the screen, and match up with each other
        }};
        addChild( controlPanelNode );

        //Reset mode button
        final Color buttonForegroundColor = BLACK;
        final ButtonNode resetModeButton = new ButtonNode( RESET, (int) ( CONTROL_FONT.getSize() * 1.3 ), buttonForegroundColor, buttonBackgroundColor ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 5 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.modeProperty.get().resetMode();//also clears the deviated enable flag
                }
            } );
        }};
        addChild( resetModeButton );

        //Reset all button
        addChild( new ResetAllButtonNode( module, this, (int) ( CONTROL_FONT.getSize() * 1.3 ), buttonForegroundColor, buttonBackgroundColor ) {{
            setOffset( resetModeButton.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, resetModeButton.getFullBounds().getMaxY() + 5 );
            setConfirmationEnabled( false );
        }} );

        //Make it so a "reset" button appears if anything has changed in the sim
        final ArrayList<Property<Boolean>> p = new ArrayList<Property<Boolean>>();
        for ( Body body : model.getBodies() ) {
            p.add( body.anyPropertyDifferent() );
        }
        //Add the clock control within the play area
        addChild( new FloatingClockControlNode( not( module.clockPausedProperty ), mode.getTimeFormatter(), model.getClock(), CLEAR, new IfElse<Color>( module.whiteBackgroundProperty, Color.black, Color.white ) ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() );

            // Add the rewind button and hook it up as needed.
            final RewindButton rewindButton = new FloatingRewindButton() {{
                addListener( new DefaultIconButton.Listener() {
                    public void buttonPressed() {
                        mode.rewind();
                    }
                } );
                // The rewind button is only enabled when something has
                // changed, otherwise there is nothing to rewind to.
                final MultiwayOr anyPropertyChanged = new MultiwayOr( p );
                anyPropertyChanged.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( anyPropertyChanged.get() );
                    }
                } );
            }};
            addChild( rewindButton );

            assert mode.timeSpeedScaleProperty != null;
            // Add the speed control slider.
            addChild( new SimSpeedControlPNode( 0.1, mode.timeSpeedScaleProperty, 2.0, rewindButton.getFullBoundsReference().getMinX(),
                                                new IfElse<Color>( module.whiteBackgroundProperty, Color.black, Color.white ) ) );
        }} );

        addChild( new MeasuringTape( module.measuringTapeVisibleProperty, mode.measuringTapeStartPoint, mode.measuringTapeEndPoint, mode.transform ) );

        // shows the bounds of the "stage", which is different from the canvas
        if ( false ) {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), new BasicStroke( 1f ), Color.RED ) );
        }

        //Tell each of the bodies about the stage size (in model coordinates) so they know if they are out of bounds
        Rectangle2D stage = new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height );
        for ( Body body : mode.getModel().getBodies() ) {
            body.getBounds().set( mode.transform.get().viewToModel( stage ) );
        }

        //If any body is out of bounds, show a "return object" button
        final MultiwayOr anythingReturnable = new MultiwayOr( returnable );
        addChild( new ButtonNode( RETURN_OBJECT, buttonBackgroundColor ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.returnBodies();
                    module.clockPausedProperty.set( true );//At 3/21/2011 meeting we decided that "return object" button should also always pause the clock.
                }
            } );
            anythingReturnable.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( anythingReturnable.get() );
                }
            } );
            setOffset( 100, 100 );
        }} );

        //Zoom controls
        addChild( createZoomControls( mode ) );
    }

    //TODO: this anonymous PNode should be a PNode subclass, it's not reusable
    private PNode createZoomControls( final GravityAndOrbitsMode mode ) {
        return new PNode() {{
            final double MAX = 1.5;
            final double MIN = 0.5;
            final double DELTA_ZOOM = 0.1;
            final PhetFont zoomButtonFont = new PhetFont( 20, true );
            final PText inText = new PText( GAOStrings.ZOOM_IN ) {{setFont( zoomButtonFont );}};
            final PText outText = new PText( GAOStrings.ZOOM_OUT ) {{setFont( zoomButtonFont );}};
            PDimension size = new PDimension( Math.max( inText.getFullBounds().getWidth(), outText.getFullBounds().getWidth() ),
                                              Math.max( inText.getFullBounds().getHeight(), outText.getFullBounds().getHeight() ) );
            double dim = Math.max( size.getWidth(), size.getHeight() ) - 7;//bring in the insets a bit so there isn't so much padding
            PNode zoomInButton = new ZoomButtonNode( inText, Color.black, buttonBackgroundColor, dim, dim ) {{
                setFont( zoomButtonFont );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        mode.zoomLevel.set( Math.min( MAX, mode.zoomLevel.get() + DELTA_ZOOM ) );
                    }
                } );
                mode.zoomLevel.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( mode.zoomLevel.get() != MAX );
                    }
                } );
            }};
            PNode zoomOutButton = new ZoomButtonNode( outText, Color.black, buttonBackgroundColor, dim, dim ) {{
                setFont( zoomButtonFont );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        mode.zoomLevel.set( Math.max( MIN, mode.zoomLevel.get() - DELTA_ZOOM ) );
                    }
                } );
                mode.zoomLevel.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( mode.zoomLevel.get() != MIN );
                    }
                } );
            }};
            PNode slider = new PSwing( new JSlider( JSlider.VERTICAL, 0, 100, 50 ) {{
                setBackground( new Color( 0, 0, 0, 0 ) );
                setPaintTicks( true );
                setMajorTickSpacing( 25 );
                final Function.LinearFunction modelToView = new Function.LinearFunction( 0, 100, MIN, MAX );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        mode.zoomLevel.set( modelToView.evaluate( getValue() ) );
                    }
                } );
                mode.zoomLevel.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( (int) modelToView.createInverse().evaluate( mode.zoomLevel.get() ) );
                    }
                } );
            }} );
            slider.setScale( 0.7 );
            slider.setOffset( 0, zoomInButton.getFullBounds().getMaxY() );
            zoomOutButton.setOffset( 0, slider.getFullBounds().getMaxY() );
            addChild( zoomInButton );
            addChild( slider );
            addChild( zoomOutButton );

            setOffset( 10, 10 );
        }};
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}