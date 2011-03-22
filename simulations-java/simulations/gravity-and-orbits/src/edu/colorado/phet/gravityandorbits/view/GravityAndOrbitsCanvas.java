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
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.DefaultIconButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.controlpanel.GAOTimeSlider;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.gravityandorbits.GAOStrings.RETURN_OBJECT;
import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.BACKGROUND;
import static java.awt.Color.green;

/**
 * Provides the play area for a single GravityAndOrbitsMode.
 *
 * @author Sam Reid
 * @see GravityAndOrbitsMode
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private final PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    public static final Color buttonBackgroundColor = new Color( 255, 250, 125 );

    public GravityAndOrbitsCanvas( final GravityAndOrbitsModel model, final GravityAndOrbitsModule module, final GravityAndOrbitsMode mode, final double forceScale ) {
        super( new Dimension( 1500, 1500 ) );//view size

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        module.whiteBackgroundProperty.addObserver( new SimpleObserver() {
            public void update() {
                setBackground( module.whiteBackgroundProperty.getValue() ? Color.white : Color.black );
            }
        } );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        // stores the current position of the mouse so we can change to cursor hand when an object moves under the mouse
        final Property<ImmutableVector2D> mousePositionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent mouseEvent ) {
            }

            public void mouseMoved( MouseEvent mouseEvent ) {
                mousePositionProperty.setValue( new ImmutableVector2D( mouseEvent.getPoint().x, mouseEvent.getPoint().y ) );
            }
        } );

        for ( Body body : model.getBodies() ) {
            addChild( new PathNode( body, mode.modelViewTransformProperty, module.showPathProperty, body.getColor() ) );
        }

        Color FORCE_VECTOR_COLOR_FILL = PhetColorScheme.GRAVITATIONAL_FORCE;
        Color FORCE_VECTOR_COLOR_OUTLINE = Color.darkGray;

        Color VELOCITY_VECTOR_COLOR_FILL = PhetColorScheme.VELOCITY;
        Color VELOCITY_VECTOR_COLOR_OUTLINE = Color.darkGray;

        ArrayList<Property<Boolean>> returnable = new ArrayList<Property<Boolean>>();//Use canvas coordinates to determine whether something has left the visible area
        for ( final Body body : model.getBodies() ) {
            final BodyNode bodyNode = new BodyNode( body, mode.modelViewTransformProperty, mousePositionProperty, this, body.getLabelAngle() );
            addChild( bodyNode );
            returnable.add( new Property<Boolean>( false ) {{
                final VoidFunction1<ImmutableVector2D> callback = new VoidFunction1<ImmutableVector2D>() {
                    public void apply( ImmutableVector2D immutableVector2D ) {
                        Rectangle2D canvasBounds = new Rectangle2D.Double( 0, 0, GravityAndOrbitsCanvas.this.getWidth(), GravityAndOrbitsCanvas.this.getHeight() );
                        setValue( !canvasBounds.intersects( bodyNode.getGlobalFullBounds() ) );
                    }
                };
                //Have to listen to when this canvas gets added to the swing scene graph, then update since the bounds are correct then (not on ComponentAdapter methods)
                addHierarchyListener( new HierarchyListener() {
                    public void hierarchyChanged( HierarchyEvent e ) {
                        callback.apply( body.getPosition() );
                    }
                } );
                body.getPositionProperty().addObserver( callback );
            }} );
            addChild( mode.getMassReadoutFactory().apply( bodyNode, module.showMassProperty ) );
        }

        //Add gravity force vector nodes
        for ( Body body : model.getBodies() ) {
            addChild( new VectorNode( body, mode.modelViewTransformProperty, module.showGravityForceProperty, body.getForceProperty(), forceScale, FORCE_VECTOR_COLOR_FILL, FORCE_VECTOR_COLOR_OUTLINE ) );
        }
        //Add velocity vector nodes
        for ( Body body : model.getBodies() ) {
            addChild( new GrabbableVectorNode( body, mode.modelViewTransformProperty, module.showVelocityProperty, body.getVelocityProperty(), mode.getVelocityScale(), VELOCITY_VECTOR_COLOR_FILL, VELOCITY_VECTOR_COLOR_OUTLINE ) );
        }

        for ( Body body : model.getBodies() ) {
            addChild( new ExplosionNode( body, mode.modelViewTransformProperty ) );
        }

        addChild( new GridNode( mode.modelViewTransformProperty, mode.getGridSpacing(), mode.getGridCenter() ) {{
            module.showGridProperty.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.showGridProperty.getValue() );
                }
            } );
        }} );

        // Control Panel
        final PNode controlPanelNode = new ControlPanelNode( new PSwing( new GravityAndOrbitsControlPanel( module, model ) ), BACKGROUND, new BasicStroke( 3 ), green, 4 ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 2 );
            setScale( 0.82 );//fine tuned so that control panels for all modes always fit on the screen, and match up with each other
        }};
        addChild( controlPanelNode );

        //Earth System button
        final Color buttonForegroundColor = Color.BLACK;
        final ButtonNode earthValuesButton = new ButtonNode( GAOStrings.RESET, (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), buttonForegroundColor, buttonBackgroundColor ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 5 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.modeProperty.getValue().resetBodies();//also clears the deviated enable flag
                }
            } );
        }};
        addChild( earthValuesButton );

        //Reset all button
        addChild( new ResetAllButtonNode( module, this, (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), buttonForegroundColor, buttonBackgroundColor ) {{
            setOffset( earthValuesButton.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, earthValuesButton.getFullBounds().getMaxY() + 5 );
            setConfirmationEnabled( false );
        }} );

        //See docs in mode.rewind
        final ArrayList<Property<Boolean>> p = new ArrayList<Property<Boolean>>();
        for ( Body body : model.getBodies() ) {
            p.add( body.anyPropertyDifferent() );
        }
        addChild( new FloatingClockControlNode( Not.not( module.clockPausedProperty ), mode.getTimeFormatter(), model.getClock(), GAOStrings.RESET, new IfElse<Color>( module.whiteBackgroundProperty, Color.black, Color.white ) ) {{
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
                        setEnabled( anyPropertyChanged.getValue() );
                    }
                } );
            }};
            addChild( rewindButton );

            assert mode.timeSpeedScaleProperty != null;
            // Add the speed control slider.
            addChild( new GAOTimeSlider( mode.timeSpeedScaleProperty, rewindButton.getFullBoundsReference().getMinX(), new IfElse<Color>( module.whiteBackgroundProperty, Color.black, Color.white ) ) );
        }} );

        addChild( new MeasuringTape( module.measuringTapeVisibleProperty, mode.measuringTapeStartPoint, mode.measuringTapeEndPoint, mode.modelViewTransformProperty ) );

        // shows the bounds of the "stage", which is different from the canvas
//        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), new BasicStroke( 1f ), Color.RED ) );

        Rectangle2D stage = new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height );
        for ( Body body : mode.getModel().getBodies() ) {
            body.getBounds().setValue( mode.modelViewTransformProperty.getValue().viewToModel( stage ) );
        }
        final MultiwayOr anythingReturnable = new MultiwayOr( returnable );
        addChild( new ButtonNode( RETURN_OBJECT, buttonBackgroundColor ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.returnObjects();
                    module.clockPausedProperty.setValue( true );//At 3/21/2011 meeting we decided that "return object" button should also always pause the clock.
                }
            } );
            anythingReturnable.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( anythingReturnable.getValue() );
                }
            } );
            setOffset( 100, 100 );
        }} );

        //Zoom controls
        addChild( new PNode() {{
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
                        mode.zoomLevel.setValue( Math.min( MAX, mode.zoomLevel.getValue() + DELTA_ZOOM ) );
                    }
                } );
                mode.zoomLevel.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( mode.zoomLevel.getValue() != MAX );
                    }
                } );
            }};
            PNode zoomOutButton = new ZoomButtonNode( outText, Color.black, buttonBackgroundColor, dim, dim ) {{
                setFont( zoomButtonFont );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        mode.zoomLevel.setValue( Math.max( MIN, mode.zoomLevel.getValue() - DELTA_ZOOM ) );
                    }
                } );
                mode.zoomLevel.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( mode.zoomLevel.getValue() != MIN );
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
                        mode.zoomLevel.setValue( modelToView.evaluate( getValue() ) );
                    }
                } );
                mode.zoomLevel.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( (int) modelToView.createInverse().evaluate( mode.zoomLevel.getValue() ) );
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
        }} );
    }

    public void addChild( PNode node ) {
        _rootNode.addChild( node );
    }
}