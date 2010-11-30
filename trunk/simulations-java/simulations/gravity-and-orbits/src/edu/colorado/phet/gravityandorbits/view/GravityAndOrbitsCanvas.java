/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsConstants;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsDefaults;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas template.
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    public static final Function.LinearFunction SUN_SIZER = new Function.LinearFunction( 0, 1, 0, 15 );
    public static final Function.LinearFunction PLANET_SIZER = new Function.LinearFunction( 0, 1, 0, 1000 );
    public static final Function.LinearFunction REAL_SIZER = new Function.LinearFunction( 0, 1, 0, 1);
    public static final Function.LinearFunction MOON_SIZER = new Function.LinearFunction( 0, 1, 0, 1000 );

    public GravityAndOrbitsCanvas( final GravityAndOrbitsModel model, final GravityAndOrbitsModule module, final GravityAndOrbitsMode mode, final double forceScale ) {
        super( GravityAndOrbitsDefaults.VIEW_SIZE );
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

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

        Property<ModelViewTransform> modelViewTransformProperty = mode.getModelViewTransformProperty();

        for ( Body body : model.getBodies() ) {
            addChild( new PathNode( body, modelViewTransformProperty, module.getShowPathProperty(), body.getColor() ) );
        }

        Color FORCE_VECTOR_COLOR_FILL = PhetColorScheme.GRAVITATIONAL_FORCE;
        Color FORCE_VECTOR_COLOR_OUTLINE = Color.darkGray;

        Color VELOCITY_VECTOR_COLOR_FILL = PhetColorScheme.VELOCITY;
        Color VELOCITY_VECTOR_COLOR_OUTLINE = Color.darkGray;

        for ( Body body : model.getBodies() ) {
            addChild( new VectorNode( body, modelViewTransformProperty, module.getShowGravityForceProperty(), body.getForceProperty(), forceScale, FORCE_VECTOR_COLOR_FILL, FORCE_VECTOR_COLOR_OUTLINE ) );
        }
        for ( Body body : model.getBodies() ) {
            addChild( new GrabbableVectorNode( body, modelViewTransformProperty, module.getShowVelocityProperty(), body.getVelocityProperty(), VectorNode.VELOCITY_SCALE, VELOCITY_VECTOR_COLOR_FILL, VELOCITY_VECTOR_COLOR_OUTLINE ) );
        }

        for ( Body body : model.getBodies() ) {
            addChild( new BodyNode( body, modelViewTransformProperty, new Property<Boolean>( false ), mousePositionProperty, this, body.getSizer(), -Math.PI / 4 ) );
        }
        for ( Body body : model.getBodies() ) {
            addChild( new MassReadoutNode( body, modelViewTransformProperty, module.getShowMassProperty() ) );
        }

        // Control Panel
        final GravityAndOrbitsControlPanel controlPanel = new GravityAndOrbitsControlPanel( module, model, mode );
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( controlPanel );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 3 ), Color.green ) );
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 20 );
        }};
        addChild( controlPanelNode );

        //Reset all button
        addChild( new ButtonNode( PhetCommonResources.getString( PhetCommonResources.STRING_RESET_ALL ), (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), GravityAndOrbitsControlPanel.FOREGROUND, GravityAndOrbitsControlPanel.BACKGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.resetAll();
                }
            } );
        }} );

        addChild( new FloatingClockControlNode( mode.getClockRunningProperty(), new Function1<Double, String>() {
            public String apply( Double time ) {
                return (int) ( time / 86400 ) + " Earth Days";
            }
        }, model.getClock() ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );

//        new InitialConditionDialog( parentFrame, model ).setVisible( true );
    }

    public void addChild( PNode node ) {
        _rootNode.addChild( node );
    }

}
