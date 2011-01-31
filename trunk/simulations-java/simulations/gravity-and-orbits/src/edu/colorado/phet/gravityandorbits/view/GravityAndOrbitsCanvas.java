// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.*;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Provides the play area for a single GravityAndOrbitsMode.
 *
 * @author Sam Reid
 * @see GravityAndOrbitsMode
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private final PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );

    public GravityAndOrbitsCanvas( final GravityAndOrbitsModel model, final GravityAndOrbitsModule module, final GravityAndOrbitsMode mode, final double forceScale ) {
        super( new Dimension( 1500, 1500 ) );//view size
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        module.getInvertColorsProperty().addObserver( new SimpleObserver() {
            public void update() {
                setBackground( module.getInvertColorsProperty().getValue() ? Color.white : Color.black );
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

        Property<ModelViewTransform> modelViewTransformProperty = mode.getModelViewTransformProperty();

        for ( Body body : model.getBodies() ) {
            addChild( new PathNode( body, modelViewTransformProperty, module.getShowPathProperty(), body.getColor(), module.getScaleProperty() ) );
        }

        Color FORCE_VECTOR_COLOR_FILL = PhetColorScheme.GRAVITATIONAL_FORCE;
        Color FORCE_VECTOR_COLOR_OUTLINE = Color.darkGray;

        Color VELOCITY_VECTOR_COLOR_FILL = PhetColorScheme.VELOCITY;
        Color VELOCITY_VECTOR_COLOR_OUTLINE = Color.darkGray;

        for ( Body body : model.getBodies() ) {
            final BodyNode bodyNode = new BodyNode( body, modelViewTransformProperty, module.getScaleProperty(), mousePositionProperty, this, body.getLabelAngle(), getChild( body, model.getBodies() ) );
            addChild( bodyNode );
            addChild( mode.getMassReadoutFactory().apply( bodyNode, module.getShowMassProperty() ) );
        }

        //Add gravity force vector nodes
        for ( Body body : model.getBodies() ) {
            addChild( new VectorNode( body, modelViewTransformProperty, module.getShowGravityForceProperty(), body.getForceProperty(), forceScale, body.getCartoonForceScale(), FORCE_VECTOR_COLOR_FILL, FORCE_VECTOR_COLOR_OUTLINE ) );
        }
        //Add velocity vector nodes
        for ( Body body : model.getBodies() ) {
            addChild( new GrabbableVectorNode( body, modelViewTransformProperty, module.getShowVelocityProperty(), body.getVelocityProperty(), mode.getVelocityScale(), 1.0, VELOCITY_VECTOR_COLOR_FILL, VELOCITY_VECTOR_COLOR_OUTLINE ) );
        }

        for ( Body body : model.getBodies() ) {
            addChild( new ExplosionNode( body, modelViewTransformProperty ) );
        }

        addChild( new GridNode( modelViewTransformProperty, mode.getGridSpacing(), mode.getGridCenter() ) {{
            module.getShowGridProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.getShowGridProperty().getValue() );
                }
            } );
        }} );

        // Control Panel
        final GravityAndOrbitsControlPanel controlPanel = new GravityAndOrbitsControlPanel( module, model );
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( controlPanel );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 3 ), Color.green ) );
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 2 );
            setScale( 0.82 );//fine tuned so that control panels for all modes always fit on the screen, and match up with each other
        }};
        addChild( controlPanelNode );

        //Earth System button
        final Color buttonForegroundColor = Color.BLACK;
        final Color buttonBackgroundColor = new Color( 255, 250, 125 );
        final ButtonNode earthValuesButton = new ButtonNode( GAOStrings.EARTH_VALUES, (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), buttonForegroundColor, buttonBackgroundColor ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 5 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getModeProperty().getValue().resetBodies();//also clears the deviated enable flag
                }
            } );
        }};
        addChild( earthValuesButton );

        //Reset all button
        addChild( new ResetAllButtonNode( module, this, (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), buttonForegroundColor, buttonBackgroundColor ) {{
            setOffset( earthValuesButton.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, earthValuesButton.getFullBounds().getMaxY() + 5 );
        }} );

        //See docs in mode.rewind
        final ArrayList<Property<Boolean>> p = new ArrayList<Property<Boolean>>();
        for ( Body body : model.getBodies() ) {
            p.add( body.anyPropertyDifferent() );
        }
        addChild( new FloatingClockControlNode( Not.not( module.getClockPausedProperty() ), mode.getTimeFormatter(), model.getClock(), GAOStrings.RESET, new IfElse<Color>( module.getInvertColorsProperty(), Color.black, Color.white ) ) {{
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

            assert mode.getTimeSpeedScaleProperty() != null;
            // Add the speed control slider.
            addChild( new GAOTimeSlider( mode.getTimeSpeedScaleProperty(), rewindButton.getFullBoundsReference().getMinX(), new IfElse<Color>( module.getInvertColorsProperty(), Color.black, Color.white ) ) );
        }} );

        addChild( new MeasuringTape( new And( new ValueEquals<Scale>( module.getScaleProperty(), Scale.REAL ), module.getMeasuringTapeVisibleProperty() ),
                                     mode.getMeasuringTapeStartPoint(),
                                     mode.getMeasuringTapeEndPoint(), modelViewTransformProperty ) {{
        }} );

        // shows the bounds of the "stage", which is different from the canvas
//        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), new BasicStroke( 1f ), Color.RED ) );

        Rectangle2D stage = new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height );
        for ( Body body : mode.getModel().getBodies() ) {
            body.getBounds().setValue( mode.getModelViewTransformProperty().getValue().viewToModel( stage ) );
        }
        final MultiwayOr anythingReturnable = new MultiwayOr( new ArrayList<Property<Boolean>>() {{
            for ( Body body : model.getBodies() ) {add( body.getReturnable() );}
        }} );
        addChild( new ButtonNode( "Return Object" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.returnObjects();
                }
            } );
            anythingReturnable.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( anythingReturnable.getValue() );
                }
            } );
            setOffset( 100, 100 );
        }} );
    }

    private Body getChild( Body parent, ArrayList<Body> bodies ) {
        for ( Body body : bodies ) {
            if ( body.getParent() == parent ) {
                return body;
            }
        }
        return null;
    }

    public void addChild( PNode node ) {
        _rootNode.addChild( node );
    }

}
