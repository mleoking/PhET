/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsConstants;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsDefaults;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas template.
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private GravityAndOrbitsModel model;
    private PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    private ModelViewTransform2D modelViewTransform2D;
    public static final Function.LinearFunction SUN_SIZER = new Function.LinearFunction( 0, 1, 0, 15 );
    public static final Function.LinearFunction PLANET_SIZER = new Function.LinearFunction( 0, 1, 0, 1000 );
    public static final Function.LinearFunction MOON_SIZER = new Function.LinearFunction( 0, 1, 0, 1000 );

    public GravityAndOrbitsCanvas( JFrame parentFrame, final GravityAndOrbitsModel model, final GravityAndOrbitsModule module ) {
        super( GravityAndOrbitsDefaults.VIEW_SIZE );
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        this.model = model;

        setBackground( GravityAndOrbitsConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        // stores the current position of the mouse
        final Property<ImmutableVector2D> mousePositionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent mouseEvent ) {
            }

            public void mouseMoved( MouseEvent mouseEvent ) {
                mousePositionProperty.setValue( new ImmutableVector2D( mouseEvent.getPoint().x, mouseEvent.getPoint().y ) );
            }
        } );

        modelViewTransform2D = new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.30, STAGE_SIZE.height * 0.5 ), 1.5E-9, true );

        module.getMoonProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( module.getMoonProperty().getValue() ) {
                    model.getPlanet().resetAll();
                    model.getMoon().resetAll();
                    model.getSun().resetAll();
                }
            }
        } );

        addChild( new TraceNode( model.getPlanet(), modelViewTransform2D, module.getTracesProperty() ) );
        addChild( new TraceNode( model.getSun(), modelViewTransform2D, module.getTracesProperty() ) );
        addChild( new TraceNode( model.getMoon(), modelViewTransform2D, new AndProperty( module.getTracesProperty(), module.getMoonProperty() ) ) );
        addChild( new BodyNode( model.getSun(), modelViewTransform2D, module.getToScaleProperty(), mousePositionProperty, this, SUN_SIZER, -Math.PI / 4 ) );
        addChild( new BodyNode( model.getPlanet(), modelViewTransform2D, module.getToScaleProperty(), mousePositionProperty, this, PLANET_SIZER, -Math.PI / 4 ) );
        addChild( new BodyNode( model.getMoon(), modelViewTransform2D, module.getToScaleProperty(), mousePositionProperty, this, MOON_SIZER, -Math.PI / 4 - Math.PI / 2 ) {{
            module.getMoonProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.getMoonProperty().getValue() );
                }
            } );
        }} );
        Color FORCE_VECTOR_COLOR_FILL = Color.pink;
        Color FORCE_VECTOR_COLOR_OUTLINE = Color.darkGray;

        Color VELOCITY_VECTOR_COLOR_FILL = Color.red;
        Color VELOCITY_VECTOR_COLOR_OUTLINE = Color.darkGray;
        addChild( new VectorNode( model.getPlanet(), modelViewTransform2D, module.getForcesProperty(), model.getPlanet().getForceProperty(), VectorNode.FORCE_SCALE, FORCE_VECTOR_COLOR_FILL, FORCE_VECTOR_COLOR_OUTLINE ) );
        addChild( new VectorNode( model.getSun(), modelViewTransform2D, module.getForcesProperty(), model.getSun().getForceProperty(), VectorNode.FORCE_SCALE, FORCE_VECTOR_COLOR_FILL, FORCE_VECTOR_COLOR_OUTLINE ) );
        addChild( new VectorNode( model.getMoon(), modelViewTransform2D, new AndProperty( module.getForcesProperty(), module.getMoonProperty() ), model.getMoon().getForceProperty(), VectorNode.FORCE_SCALE, FORCE_VECTOR_COLOR_FILL, FORCE_VECTOR_COLOR_OUTLINE ) );
        addChild( new GrabbableVectorNode( model.getPlanet(), modelViewTransform2D, module.getVelocityProperty(), model.getPlanet().getVelocityProperty(), VectorNode.VELOCITY_SCALE, VELOCITY_VECTOR_COLOR_FILL, VELOCITY_VECTOR_COLOR_OUTLINE ) );
        addChild( new GrabbableVectorNode( model.getSun(), modelViewTransform2D, module.getVelocityProperty(), model.getSun().getVelocityProperty(), VectorNode.VELOCITY_SCALE, VELOCITY_VECTOR_COLOR_FILL, VELOCITY_VECTOR_COLOR_OUTLINE ) );
        addChild( new GrabbableVectorNode( model.getMoon(), modelViewTransform2D, new AndProperty( module.getVelocityProperty(), module.getMoonProperty() ), model.getMoon().getVelocityProperty(), VectorNode.VELOCITY_SCALE, VELOCITY_VECTOR_COLOR_FILL, VELOCITY_VECTOR_COLOR_OUTLINE ) );
        addChild( new MassReadoutNode( model.getSun(), modelViewTransform2D, module.getShowMassesProperty() ) );
        addChild( new MassReadoutNode( model.getPlanet(), modelViewTransform2D, module.getShowMassesProperty() ) );
        addChild( new MassReadoutNode( model.getMoon(), modelViewTransform2D, new AndProperty( module.getShowMassesProperty(), module.getMoonProperty() ) ) );

        // Control Panel
        final GravityAndOrbitsControlPanel controlPanel = new GravityAndOrbitsControlPanel( module, model );
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( controlPanel );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 3 ), Color.green ) );
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );

        //Reset all button
        addChild( new GradientButtonNode( "Reset all", (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), GravityAndOrbitsControlPanel.BACKGROUND, GravityAndOrbitsControlPanel.FOREGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.resetAll();
                }
            } );
        }} );

        addChild( new GravityAndOrbitsClockControlNode( model.getClock() ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );

//        new InitialConditionDialog( parentFrame, model ).setVisible( true );
    }

    public static class AndProperty extends Property<Boolean> {
        Property<Boolean> a;
        Property<Boolean> b;

        public AndProperty( final Property<Boolean> a, final Property<Boolean> b ) {
            super( a.getValue() && b.getValue() );
            this.a = a;
            this.b = b;
            final SimpleObserver updateState = new SimpleObserver() {
                public void update() {
                    setValue( a.getValue() && b.getValue() );
                }
            };
            a.addObserver( updateState );
            b.addObserver( updateState );
        }
    }

    public void addChild( PNode node ) {
        _rootNode.addChild( node );
    }

}
