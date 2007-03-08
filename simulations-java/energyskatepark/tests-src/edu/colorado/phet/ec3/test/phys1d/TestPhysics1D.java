package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class TestPhysics1D extends JFrame {
    private JFrame controlFrame;
    private SwingClock clock;
    private JFrame ccpFrame;
    private PSwingCanvas pSwingCanvas;
    private Particle particle;
    private ParticleStage particleStage = new ParticleStage();
    private SplineLayer splineLayer = new SplineLayer( particleStage );
    private static final String VERSION = "1.05.07";
    double totalONTrackError=0;
    double totalOffTrackError=0;
    private double normTerm=0;

    public TestPhysics1D() {
        super( "Test Physics 1D (" + VERSION + ")" );
        pSwingCanvas = new PSwingCanvas();
        pSwingCanvas.setPanEventHandler( null );
        pSwingCanvas.setZoomEventHandler( null );
        pSwingCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setContentPane( pSwingCanvas );

        final ParametricFunction2D cubicSpline = new CubicSpline2D( new Point2D[]{
                new Point2D.Double( 1 * 2, 0.5 * 2 ),
                new Point2D.Double( 2 * 2, 1 * 2 ),
                new Point2D.Double( 3 * 2, 0.5 * 2 ),
                new Point2D.Double( 4 * 2, 2 * 2 ),
                new Point2D.Double( 5 * 2, 0.5 * 2 )
        } );

        particleStage.addCubicSpline2D( cubicSpline );
        pSwingCanvas.getLayer().scale( 65 );
        pSwingCanvas.getLayer().addChild( splineLayer );
        setSize( 800, 600 );

        Point2D[] pts = cubicSpline.getOffsetSpline( 1.0, true, 100 );
        final LinearSpline2D linearSpline2D = new LinearSpline2D( pts );
        final ParametricFunction2DNode linearSegmentNode = new ParametricFunction2DNode( linearSpline2D );
        linearSegmentNode.setVisible( false );
        pSwingCanvas.getLayer().addChild( linearSegmentNode );

        particle = new Particle( particleStage );
//        particle.switchToTrack( linearSpline2D, 0.5, true );
        final ParticleNode particleNode = new ParticleNode( particle );
        final ParticleImageNode particleImageNode = new ParticleImageNode( particle );

        pSwingCanvas.getLayer().addChild( particleNode );
        pSwingCanvas.getLayer().addChild( particleImageNode );

        int msPerClockTick = 30;
        clock = new SwingClock( msPerClockTick, msPerClockTick / 1000.0 );
        clock.addClockListener( new ClockAdapter() {

            public void simulationTimeChanged( ClockEvent clockEvent ) {
//                double energy = particle.getKineticEnergy();
                double e1 = particle.getTotalEnergy();
                double offsetE1 = getOffsetEnergy();
//                System.out.println( "energy = " + energy );
                System.out.println("clockEvent = " + clockEvent.getSimulationTimeChange());
                particle.stepInTime( clockEvent.getSimulationTimeChange() );
                double e2 = particle.getTotalEnergy();
                double offsetE2 = getOffsetEnergy();
                double de = ( e2 - normTerm) / normTerm;
//                System.out.println( "ON TRACK: de=" + de + ", e1=" + e1 + ", e2=" + e2 );

                double offsetDE = ( offsetE2 - normTerm) / normTerm;
//                System.out.println( "OFF TRACK: de= " + offsetDE + ", e1=" + offsetE1 + ", e2=" + offsetE2 );

//                totalOffTrackError+=offsetDE;
//                totalONTrackError+=de;
                System.out.println("ontrack="+de+", \tofftrack="+offsetDE);
//                System.out.println( "OFF TRACK: de=" + );

            }
        } );


        ccpFrame = new JFrame( "Clock Controls" );
        ccpFrame.setContentPane( new ClockControlPanel( clock ) );
        ccpFrame.pack();
        ccpFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        ccpFrame.setLocation( getX(), getY() + getHeight() );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        controlFrame = new JFrame();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );

        final Particle1D particle1d = particle.getParticle1D();
        JRadioButton verlet = new JRadioButton( "Verlet", particle1d.getUpdateStrategy() instanceof Particle1D.Verlet );
        verlet.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createVerlet() );
            }
        } );
        JRadioButton constantVel = new JRadioButton( "Constant Velocity", particle1d.getUpdateStrategy() instanceof Particle1D.ConstantVelocity );
        constantVel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 1 );
                particle1d.setUpdateStrategy( particle1d.createConstantVelocity() );
            }
        } );
        JRadioButton euler = new JRadioButton( "Euler", particle1d.getUpdateStrategy() instanceof Particle1D.Euler );
        euler.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createEuler() );
            }
        } );
//
        JRadioButton verletOffset = new JRadioButton( "Verlet Offset", particle1d.getUpdateStrategy() instanceof Particle1D.VerletOffset );
        verletOffset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createVerletOffset( splineLayer.getOffsetDistance() ) );
            }
        } );

        JButton comp = new JButton("Reset eergy errors");
        comp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                totalOffTrackError=0;
                totalONTrackError=0;

                particle.switchToTrack(cubicSpline, 0.01,true);
                normTerm=particle.getTotalEnergy();
                System.out.println("normTerm = " + normTerm);
//                enable
            }
        });
        controlPanel.add(comp);

        controlPanel.add( verlet, gridBagConstraints );
        controlPanel.add( constantVel, gridBagConstraints );
        controlPanel.add( euler, gridBagConstraints );
        controlPanel.add( verletOffset, gridBagConstraints );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( verlet );
        buttonGroup.add( constantVel );
        buttonGroup.add( euler );
        buttonGroup.add( verletOffset );

        JButton resetParticle = new JButton( "reset particle" );
        resetParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle.setVelocity( 0, 0 );
                particle.setPosition( 2.5, 0 );
            }
        } );
        controlPanel.add( resetParticle, gridBagConstraints );

        final JCheckBox showNormals = new JCheckBox( "show (top) normals", splineLayer.isNormalsVisible() );
        showNormals.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                splineLayer.setNormalsVisible( showNormals.isSelected() );
//                particleStage.setNormalsVisible(showNormals.isSelected() );)
//                splineNode.setNormalsVisible( showNormals.isSelected() );
            }
        } );
        controlPanel.add( showNormals, gridBagConstraints );

        final JCheckBox showCurvature = new JCheckBox( "show curvature normals", splineLayer.isCurvatureVisible() );
        showCurvature.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                splineLayer.setCurvatureVisible( showCurvature.isSelected() );
            }
        } );
        controlPanel.add( showCurvature, gridBagConstraints );

        JButton outputSpline = new JButton( "print spline" );
        outputSpline.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "particleStage = " + particleStage.toStringSerialization() );
            }
        } );
        controlPanel.add( outputSpline, gridBagConstraints );

        final ModelSlider elasticity = new ModelSlider( "Elasticity", "", 0.0, 1.0, particle.getElasticity() );
        elasticity.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                particle.setElasticity( elasticity.getValue() );
            }
        } );
        controlPanel.add( elasticity, gridBagConstraints );

        final ModelSlider stickiness = new ModelSlider( "Stickiness", "", 0.0, 2.0, particle.getStickiness() );
        stickiness.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                particle.setStickiness( stickiness.getValue() );
            }
        } );
        controlPanel.add( stickiness, gridBagConstraints );

        JButton updateGraphics = new JButton( "Update Particle Node" );
        updateGraphics.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particleImageNode.update();
                particleNode.update();
                if( particle.isSplineMode() ) {
                    System.out.println( "particle.getParticle1D().getRadiusOfCurvature() = " + particle.getParticle1D().getRadiusOfCurvature() );
                }
            }
        } );
        controlPanel.add( updateGraphics, gridBagConstraints );

        final JCheckBox checkBox = new JCheckBox( "convert normal velocity to thermal on landing", particle.isConvertNormalVelocityToThermalOnLanding() );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle.setConvertNormalVelocityToThermalOnLanding( checkBox.isSelected() );
            }
        } );
        controlPanel.add( checkBox, gridBagConstraints );

        TestJList testJList = new TestJList( new DefaultTestSet(), this );
        controlPanel.add( testJList, gridBagConstraints );

        final JCheckBox jCheckBox = new JCheckBox( "reflect (1d)", particle.getParticle1D().isReflect() );
        jCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle.getParticle1D().setReflect( jCheckBox.isSelected() );
            }
        } );
        controlPanel.add( jCheckBox, gridBagConstraints );

        final JCheckBox showTopOffsetSpline = new JCheckBox( "Show Top Offset Spline", splineLayer.isShowTopOffsetSpline() );
        showTopOffsetSpline.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                splineLayer.setShowTopOffsetSpline( showTopOffsetSpline.isSelected() );
            }
        } );
        controlPanel.add( showTopOffsetSpline, gridBagConstraints );

        final JCheckBox showBottomOffsetSpline = new JCheckBox( "Show Bottom Offset Spline", splineLayer.isShowBottomOffsetSpline() );
        showBottomOffsetSpline.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                splineLayer.setShowBottomOffsetSpline( showBottomOffsetSpline.isSelected() );
            }
        } );
        controlPanel.add( showBottomOffsetSpline, gridBagConstraints );

        final ModelSlider offsetDistance = new ModelSlider( "Offset Distance", "meters", 0, 1.7, splineLayer.getOffsetDistance() );
        offsetDistance.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                splineLayer.setOffsetDistance( offsetDistance.getValue() );
                if( particle1d.getUpdateStrategy() instanceof Particle1D.VerletOffset ) {
                    Particle1D.VerletOffset ve = (Particle1D.VerletOffset)particle1d.getUpdateStrategy();
                    ve.setL( splineLayer.getOffsetDistance() );
                }
            }
        } );
        controlPanel.add( offsetDistance, gridBagConstraints );

        final JCheckBox showParticle = new JCheckBox( "Show Particle", particleNode.getVisible() );
        showParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particleNode.setVisible( showParticle.isSelected() );
            }
        } );
        controlPanel.add( showParticle, gridBagConstraints );

        final JCheckBox showCharacter = new JCheckBox( "Show Character", particleImageNode.getVisible() );
        showCharacter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particleImageNode.setVisible( showCharacter.isSelected() );
            }
        } );
        controlPanel.add( showCharacter, gridBagConstraints );

        final JCheckBox centered = new JCheckBox( "Centered character", particleImageNode.isCentered() );
        centered.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particleImageNode.setCentered( centered.isSelected() );
            }
        } );
        controlPanel.add( centered, gridBagConstraints );


        final JCheckBox linearTrackVisible = new JCheckBox( "Linear track visible", linearSegmentNode.getVisible() );
        linearTrackVisible.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                linearSegmentNode.setVisible( linearTrackVisible.isSelected() );
            }
        } );
        controlPanel.add( linearTrackVisible, gridBagConstraints );

        final ModelSlider switchToLinear = new ModelSlider( "set to linear", "", 0, 1, 0.5 );
        switchToLinear.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                particle.switchToTrack( linearSpline2D, switchToLinear.getValue(), true );
            }
        } );
        controlPanel.add( switchToLinear, gridBagConstraints );

        controlFrame.setContentPane( new JScrollPane(controlPanel) );

//        JButton resetEnergyError = new JButton( "Reset Energy Error" );
//        resetEnergyError.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                particle1d.resetEnergyError();
//            }
//        } );
//        controlPanel.add( resetEnergyError, gridBagConstraints );

        controlFrame.pack();
        controlFrame.setLocation( this.getX() + this.getWidth(), this.getY() );
        controlFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private double getOffsetEnergy() {
        if (particle.getParticle1D().getCubicSpline2D()==null){
            return Double.NaN;
        }
        double R = Math.abs( particle.getParticle1D().getRadiusOfCurvature() );//todo: should this be allowed to be negative?
        double L = splineLayer.getOffsetDistance();
        AbstractVector2D vec = particle.getParticle1D().getSideVector().getScaledInstance( L ).getAddedInstance( particle.getX(), particle.getY() );
        double offE1 = particle.getKineticEnergy() * ( R + L ) / R + particle.getPotentialEnergy() + particle.getMass() * particle.getGravity() * vec.getY();
        return offE1;
    }

    public Particle getParticle() {
        return particle;
    }

    public static void main( String[] args ) {
        new TestPhysics1D().start();
    }

    public void setTestState( TestState testState ) {
        particleStage.clear();
        for( int i = 0; i < testState.numCubicSpline2Ds(); i++ ) {
            particleStage.addCubicSpline2D( new CubicSpline2D( testState.getCubicSpline2D( i ) ) );
        }
        testState.init( particle, particleStage );
    }

    public void start() {
        //To change body of created methods use File | Settings | File Templates.
        setVisible( true );
        controlFrame.setVisible( true );
        ccpFrame.setVisible( true );
        clock.start();
//        timer.start();
    }
}
