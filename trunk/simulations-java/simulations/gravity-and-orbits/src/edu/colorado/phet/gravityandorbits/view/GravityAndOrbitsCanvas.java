/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Or;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.DefaultIconButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsDefaults;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.IsSelected.isSelected;
import static edu.colorado.phet.common.phetcommon.model.Not.not;

/**
 * Canvas template.
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private final PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );

    public GravityAndOrbitsCanvas( final GravityAndOrbitsModel model, final GravityAndOrbitsModule module, final GravityAndOrbitsMode mode, final double forceScale ) {
        super( GravityAndOrbitsDefaults.VIEW_SIZE );
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        setBackground( Color.black );

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
            final BodyNode bodyNode = new BodyNode( body, modelViewTransformProperty, module.getScaleProperty(), mousePositionProperty, this, body.getLabelAngle() );
            addChild( bodyNode );
            addChild( mode.getMassReadoutFactory().apply( bodyNode, module.getShowMassProperty() ) );
        }

        //Add force vector nodes
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

        // Control Panel
        final GravityAndOrbitsControlPanel controlPanel = new GravityAndOrbitsControlPanel( module, model );
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( controlPanel );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 3 ), Color.green ) );
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 2 );
            setScale( 0.93 );//fine tuned so that control panels for all modes always fit on the screen, and match up with each other
        }};
        addChild( controlPanelNode );

        //Earth System button
        final ButtonNode earthSystemButton = new ButtonNode( "Earth System", (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), GravityAndOrbitsControlPanel.FOREGROUND, GravityAndOrbitsControlPanel.BACKGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 5 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getModeProperty().getValue().resetBodies();//also clears the deviated enable flag
                }
            } );

            //Gray out this button until the user changes something significant to the system dynamics
            mode.getDeviatedFromEarthSystemProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setEnabled( mode.getDeviatedFromEarthSystemProperty().getValue() );
                }
            } );
        }};
        addChild( earthSystemButton );

        //Reset all button
        addChild( new ButtonNode( PhetCommonResources.getString( PhetCommonResources.STRING_RESET_ALL ), (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), GravityAndOrbitsControlPanel.FOREGROUND, GravityAndOrbitsControlPanel.BACKGROUND ) {{
            setOffset( earthSystemButton.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, earthSystemButton.getFullBounds().getMaxY() + 5 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.resetAll();
                }
            } );
        }} );

        //See docs in mode.rewind
        final ArrayList<Property<Boolean>> p = new ArrayList<Property<Boolean>>();
        for ( Body body : model.getBodies() ) {
            p.add( body.anyPropertyChanged() );
        }
        addChild( new FloatingClockControlNode( not( module.clockPaused ), mode.getTimeFormatter(), model.getClock() ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
            final RewindButton child = new RewindButton( 60 );
            child.addListener( new DefaultIconButton.Listener() {
                public void buttonPressed() {
                    mode.rewind();
                }
            } );
            child.setOffset( getPlayPauseButton().getFullBounds().getMinX() - child.getFullBounds().getWidth() - 5, getPlayPauseButton().getFullBounds().getCenterY() - child.getFullBounds().getHeight() / 2 );
            addChild( child );
            final Or anyPropertyChanged = new Or( p );
            anyPropertyChanged.addObserver( new SimpleObserver() {
                public void update() {
                    child.setEnabled( anyPropertyChanged.getValue() );
                }
            } );
        }} );

        addChild( new MeasuringTape( isSelected( Scale.REAL, module.getScaleProperty() ).and( module.getMeasuringTapeVisibleProperty() ),
                                     new Property<ImmutableVector2D>( new ImmutableVector2D( mode.getInitialMeasuringTapeLocation().getP1() ) ),
                                     new Property<ImmutableVector2D>( new ImmutableVector2D( mode.getInitialMeasuringTapeLocation().getP2() ) ), modelViewTransformProperty ) {{
        }} );
    }

    public void addChild( PNode node ) {
        _rootNode.addChild( node );
    }

}
