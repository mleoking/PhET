package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class TestPhysics1D extends JFrame {
    private JFrame controlFrame;
    private SwingClock clock;
    private JFrame ccpFrame;
    private PSwingCanvas pSwingCanvas;
    private CubicSpline2D cubicSpline;
    private Particle particle;

    public TestPhysics1D() {
        pSwingCanvas = new PSwingCanvas();
        pSwingCanvas.setPanEventHandler( null );
        pSwingCanvas.setZoomEventHandler( null );
        pSwingCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setContentPane( pSwingCanvas );

        cubicSpline = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 1, 0.5 ),
                new Point2D.Double( 2, 1 ),
                new Point2D.Double( 3, 0.5 ),
                new Point2D.Double( 4, 2 ),
                new Point2D.Double( 5, 0.5 )
        } );
        CubicSpline2DNode splineNode = new CubicSpline2DNode( cubicSpline );
        pSwingCanvas.getLayer().scale( 100 );
        pSwingCanvas.getLayer().addChild( splineNode );
        setSize( 800, 600 );

        particle = new Particle( cubicSpline );
        ParticleNode particleNode = new ParticleNode( particle );

//        final Particle1D particle1d = new Particle1D( cubicSpline );
//        particle1d.setUpdateStrategy( particle1d.createEuler() );
//        Particle1DNode particle1DNode = new Particle1DNode( particle1d );

//        pSwingCanvas.getLayer().addChild( particle1DNode );
        pSwingCanvas.getLayer().addChild( particleNode );

        int msPerClockTick = 30;
        clock = new SwingClock( msPerClockTick, msPerClockTick / 1000.0 );
        clock.addClockListener( new ClockAdapter() {

            public void simulationTimeChanged( ClockEvent clockEvent ) {
//                System.out.println( "clockEvent.getSimulationTimeChange() = " + clockEvent.getSimulationTimeChange() );
//                particle1d.stepInTime( clockEvent.getSimulationTimeChange() );
                particle.stepInTime( clockEvent.getSimulationTimeChange() );
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
//        JRadioButton verlet = new JRadioButton( "Verlet", particle1d.getUpdateStrategy() instanceof Particle1D.Verlet );
//        verlet.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                particle1d.setVelocity( 0 );
//                particle1d.setUpdateStrategy( particle1d.createVerlet() );
//            }
//        } );
//        JRadioButton constantVel = new JRadioButton( "Constant Velocity", particle1d.getUpdateStrategy() instanceof Particle1D.ConstantVelocity );
//        constantVel.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                particle1d.setVelocity( 1 );
//                particle1d.setUpdateStrategy( particle1d.createConstantVelocity() );
//            }
//        } );
//        JRadioButton euler = new JRadioButton( "Euler", particle1d.getUpdateStrategy() instanceof Particle1D.Euler );
//        euler.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                particle1d.setVelocity( 0 );
//                particle1d.setUpdateStrategy( particle1d.createEuler() );
//            }
//        } );
//
//        JRadioButton verletOffset = new JRadioButton( "Verlet Offset", particle1d.getUpdateStrategy() instanceof Particle1D.VerletOffset );
//        verletOffset.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                particle1d.setVelocity( 0 );
//                particle1d.setUpdateStrategy( particle1d.createVerletOffset() );
//            }
//        } );
//        controlPanel.add( verlet, gridBagConstraints );
//        controlPanel.add( constantVel, gridBagConstraints );
//        controlPanel.add( euler, gridBagConstraints );
//        controlPanel.add( verletOffset, gridBagConstraints );

//        ButtonGroup buttonGroup = new ButtonGroup();
//        buttonGroup.add( verlet );
//        buttonGroup.add( constantVel );
//        buttonGroup.add( euler );
//        buttonGroup.add( verletOffset );
        JButton resetParticle = new JButton( "reset particle" );
        resetParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle.setVelocity( 0, 0 );
                particle.setPosition( 2.5, 0 );
            }
        } );
        controlPanel.add( resetParticle, gridBagConstraints );
        controlFrame.setContentPane( controlPanel );

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

    public CubicSpline2D getCubicSpline() {
        return cubicSpline;
    }


    public Particle getParticle() {
        return particle;
    }

    public static void main( String[] args ) {
        new TestPhysics1D().start();
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
