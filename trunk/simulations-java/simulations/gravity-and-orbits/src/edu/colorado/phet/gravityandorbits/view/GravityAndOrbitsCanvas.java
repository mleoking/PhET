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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsConstants;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
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
        addChild( new VectorNode( model.getPlanet(), modelViewTransform2D, module.getForcesProperty(), model.getPlanet().getForceProperty(), VectorNode.FORCE_SCALE ) );
        addChild( new VectorNode( model.getSun(), modelViewTransform2D, module.getForcesProperty(), model.getSun().getForceProperty(), VectorNode.FORCE_SCALE ) );
        addChild( new VectorNode( model.getMoon(), modelViewTransform2D, new AndProperty( module.getForcesProperty(), module.getMoonProperty() ), model.getMoon().getForceProperty(), VectorNode.FORCE_SCALE ) );
        addChild( new GrabbableVectorNode( model.getPlanet(), modelViewTransform2D, module.getVelocityProperty(), model.getPlanet().getVelocityProperty(), VectorNode.VELOCITY_SCALE ) );
        addChild( new GrabbableVectorNode( model.getSun(), modelViewTransform2D, module.getVelocityProperty(), model.getSun().getVelocityProperty(), VectorNode.VELOCITY_SCALE ) );
        addChild( new GrabbableVectorNode( model.getMoon(), modelViewTransform2D, new AndProperty( module.getVelocityProperty(), module.getMoonProperty() ), model.getMoon().getVelocityProperty(), VectorNode.VELOCITY_SCALE ) );
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

        new JDialog( parentFrame, false ) {{
            setContentPane( new VerticalLayoutPanel() {{
                //public static final double SUN_MASS = 1.989E30;
                final BodyConfigPanel sunPanel = new BodyConfigPanel( model.getSun());
                add( sunPanel );
                //public static double EARTH_MASS = 5.9742E24;
                //private final double EARTH_ORBIT_RADIUS = 149668992000.0;
                //private final double EARTH_ORBITAL_SPEED = -29.78E3;
                final BodyConfigPanel planetPanel = new BodyConfigPanel( model.getPlanet());
                add( planetPanel );

                //public static double MOON_MASS = 7.3477E22;
                //moon x = planet.getX() + 384399E3 = 150053391000 = 1.50053391000 E11
                //private final double MOON_ORBITAL_SPEED = EARTH_ORBITAL_SPEED - 1.022E3; = -30802 = -3.0802E4
                final BodyConfigPanel moonPanel = new BodyConfigPanel( model.getMoon());
                add( moonPanel );
                add( new JButton( "Apply" ) {{
                    final ActionListener listener = new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            sunPanel.apply();
                            planetPanel.apply();
                            moonPanel.apply();
                        }
                    };
                    sunPanel.setListener(listener);
                    planetPanel.setListener(listener);
                    moonPanel.setListener(listener);
                    addActionListener( listener );
                }} );
            }} );
            pack();
        }}.setVisible( true );
    }

    public static class BodyConfigPanel extends JPanel {
        private final NumericControl massControl;
        private final NumericControl xControl;
        private final NumericControl yControl;
        private final NumericControl vxControl;
        private final NumericControl vyControl;
        private Body body;
        private ActionListener listener;

        public BodyConfigPanel( Body body
        ) {
            this.body = body;
            final SimpleObserver apply = new SimpleObserver() {
                public void update() {
                    listener.actionPerformed(null);
                }
            };
            massControl = new NumericControl( body.getName()+", mass", "kg", getMantissa(body.getMass()), getExponent(body.getMass()), apply );
            add( massControl );
            xControl = new NumericControl( body.getName()+", x", "m", getMantissa(body.getX()), getExponent(body.getX()), apply );
            add( xControl );
            yControl = new NumericControl( body.getName()+", y", "m", getMantissa( body.getY() ), getExponent( body.getY() ), apply );
            add( yControl );
            vxControl = new NumericControl( body.getName()+", vx", "m/s", getMantissa( body.getVelocity().getX() ), getExponent( body.getVelocity().getX() ), apply );
            add( vxControl );
            vyControl = new NumericControl( body.getName()+", vy", "m/s", getMantissa( body.getVelocity().getY() ), getExponent( body.getVelocity().getY() ), apply );
            add( vyControl );
            setBorder( BorderFactory.createTitledBorder( body.getName() ) );
        }

        private int getExponent( double v ) {
            if (v==0){
                return 0;
            }
            v = Math.abs(v);
            for (int exp = 0;exp<1000;exp++){
                if (v<Math.pow( 10,exp )){
                    return exp-1;
                }
            }
            throw new RuntimeException( "No exponent found :(");
        }

        private double getMantissa( double v) {
            if (v==0){
                return 0;
            }
            double y = v/Math.pow( 10,getExponent(v ));
            return y;
        }

        public void apply() {
            body.resetAll();
            if (body.getName().equals( "Sun" )){
                System.out.println( "hello" );
            }
            body.setMass( massControl.getValue() );
            body.setPosition( xControl.getValue(), yControl.getValue() );
            body.setVelocity( new ImmutableVector2D( vxControl.getValue(), vyControl.getValue() ) );
            body.clearTrace();
            System.out.println( "updated body = "+body );
        }

        public void setListener( ActionListener listener ) {
            this.listener=listener;
        }
    }

    public static class NumericControl extends JPanel {
//        private final LinearValueControl linearValueControl;
        private final JSpinner exponentSpinner;
        private JSpinner mantissaControl;
        private String title;
        private String units;

        public NumericControl( String title, String units, double prefix, int exponent, final SimpleObserver update ) {
           this.title=title;
            this.units = units;
            add(new JLabel(title+": "+units));
            mantissaControl = new JSpinner(new SpinnerNumberModel( prefix, -10.0,10.0,0.001) );
            mantissaControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    update.update();
                }
            } );
            add(mantissaControl);

            if (exponent<0 || exponent>100){
                throw new RuntimeException( "out of bounds exponent");
            }
            exponentSpinner = new JSpinner( new SpinnerNumberModel( exponent, 0, 100, 1 ) );
            exponentSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    update.update();
                }
            } );
            add( exponentSpinner );
        }

        public double getValue() {
            final double v = (Double) mantissaControl.getValue() * Math.pow( 10, (Integer) exponentSpinner.getValue() );
            System.out.println( "> getting value, title = "+title+", units = "+units+", mantissa = "+mantissaControl.getValue()+", exp = "+exponentSpinner.getValue()+", value = "+ v );
            return v;
        }
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
