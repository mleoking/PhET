package edu.colorado.phet.ec3.test.phys1d;

import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 13, 2007
 * Time: 11:12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPhysics1D extends JFrame {
    private JFrame controlFrame;
    private Timer timer;

    public TestPhysics1D() {
        PSwingCanvas pSwingCanvas = new PSwingCanvas();
        pSwingCanvas.setPanEventHandler( null );
        pSwingCanvas.setZoomEventHandler( null );
        pSwingCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setContentPane( pSwingCanvas );

        CubicSpline2D cubicSpline = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 100, 50 ),
                new Point2D.Double( 200, 100 ),
                new Point2D.Double( 300, 50 ),
                new Point2D.Double( 450, 200 ),
                new Point2D.Double( 525, 50 )
        } );
        CubicSpline2DNode splineNode = new CubicSpline2DNode( cubicSpline );
        pSwingCanvas.getLayer().addChild( splineNode );
        setSize( 800, 600 );

        final Particle particle = new Particle( cubicSpline );
        ParticleNode particleNode = new ParticleNode( particle );
        pSwingCanvas.getLayer().addChild( particleNode );

        final Particle1D particle1d = new Particle1D( cubicSpline );
        particle1d.setUpdateStrategy( particle1d.createEuler() );
        Particle1DNode particle1DNode = new Particle1DNode( particle1d );
        pSwingCanvas.getLayer().addChild( particle1DNode );

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double dt = 0.001;
                particle1d.stepInTime( dt );
                particle.stepInTime( dt );
            }
        } );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        controlFrame = new JFrame();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
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
                particle1d.setVelocity( 1000 * 5 );
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

        JRadioButton verletOffset = new JRadioButton( "Verlet Offset", particle1d.getUpdateStrategy() instanceof Particle1D.VerletOffset );
        verletOffset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.setVelocity( 0 );
                particle1d.setUpdateStrategy( particle1d.createVerletOffset() );
            }
        } );
        controlPanel.add( verlet, gridBagConstraints );
        controlPanel.add( constantVel, gridBagConstraints );
        controlPanel.add( euler, gridBagConstraints );
        controlPanel.add( verletOffset, gridBagConstraints );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( verlet );
        buttonGroup.add( constantVel );
        buttonGroup.add( euler );
        buttonGroup.add( verletOffset );

        controlFrame.setContentPane( controlPanel );

        JButton resetEnergyError = new JButton( "Reset Energy Error" );
        resetEnergyError.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                particle1d.resetEnergyError();
            }
        } );
        controlPanel.add( resetEnergyError, gridBagConstraints );

        controlFrame.pack();
        controlFrame.setLocation( this.getX() + this.getWidth(), this.getY() );
        controlFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        new TestPhysics1D().start();
    }

    private void start() {
        //To change body of created methods use File | Settings | File Templates.
        setVisible( true );
        controlFrame.setVisible( true );
        timer.start();
    }
}
