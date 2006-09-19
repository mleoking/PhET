package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 7:17:42 AM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class BulbComponentNode extends ComponentNode {
    private BulbNode bulbNode;
    private Bulb bulb;

    public BulbComponentNode( CCKModel model, Bulb bulb ) {
        super( model, bulb );
        this.bulb = bulb;
        bulbNode = new BulbNode( bulb );
        addChild( bulbNode );
        CircuitSolutionListener circuitSolutionListener = new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                updateIntensity();
            }
        };
        model.getCircuitSolver().addSolutionListener( circuitSolutionListener );
        bulbNode.transformBy( AffineTransform.getScaleInstance( 2, 2.5 ) );
        update();
        runParamTest();
    }

    private double getTilt() {
        double w = bulbNode.getCoverShape().getBounds().getWidth() / 2;
        double h = bulbNode.getCoverShape().getBounds().getHeight();
        return -Math.atan2( w, h );
    }

    public static double getTiltValue( Bulb bulb ) {
        double w = new BulbNode( bulb ).getCoverShape().getBounds().getWidth() / 2;
        double h = new BulbNode( bulb ).getCoverShape().getBounds().getHeight();
        return -Math.atan2( w, h );
    }

    private void updateIntensity() {
        double power = Math.abs( bulb.getCurrent() * bulb.getVoltageDrop() );
        double maxPower = 60;
        if( power > maxPower ) {
            power = maxPower;
        }
        double intensity = Math.pow( power / maxPower, 0.354 );
        bulbNode.setIntensity( intensity );
        //todo notify filament graphic
//        for( int i = 0; i < listeners.size(); i++ ) {
//            IntensityChangeListener intensityChangeListener = (IntensityChangeListener)listeners.get( i );
//            intensityChangeListener.intensityChanged( this, intensity );
//        }
    }

//    private void updateTransform() {
//
//        
//    }

    ModelSlider dxSlider = new ModelSlider( "dx", "", -10, 10, 0 );
    ModelSlider dySlider = new ModelSlider( "dy", "", -10, 10, 0 );
    ModelSlider sxSlider = new ModelSlider( "sx", "", 0, 3, 1 );
    ModelSlider sySlider = new ModelSlider( "sy", "", 0, 3, 1 );
    ModelSlider dThetaSlider = new ModelSlider( "dTheta", "", -Math.PI * 2, Math.PI * 2, 0 );

    private void runParamTest() {
        JFrame frame = new JFrame();
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();

        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        contentPane.add( dxSlider );
        contentPane.add( dySlider );
        contentPane.add( sxSlider );
        contentPane.add( sySlider );
        contentPane.add( dThetaSlider );
        dxSlider.addChangeListener( changeListener );
        dySlider.addChangeListener( changeListener );
        sxSlider.addChangeListener( changeListener );
        sySlider.addChangeListener( changeListener );
        dThetaSlider.addChangeListener( changeListener );

        frame.setContentPane( contentPane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//        frame.setVisible( true );
    }

    private AffineTransform createTransform() {
        double sign = 1;
        if( !bulb.isConnectAtRight() ) {
            sign = -1;
        }
        double theta = sign * getTilt();
        Point2D srcpt = bulb.getStartPoint();
        Point2D endpt = bulb.getEndPoint();
        AffineTransform transform = new AffineTransform();

//        double angle = new ImmutableVector2D.Double( srcpt, endpt ).getAngle() + dThetaSlider.getValue() + theta;
//        transform.rotate( angle, srcpt.getX(), srcpt.getY() );
//        transform.translate( srcpt.getX(), srcpt.getY() );
//        transform.scale( sxSlider.getValue(), sySlider.getValue() );
//        transform.translate( dxSlider.getValue(), dySlider.getValue() );//todo magic numbers

        double angle = new ImmutableVector2D.Double( srcpt, endpt ).getAngle() + 0.3 + theta;
        transform.rotate( angle, srcpt.getX(), srcpt.getY() );
        transform.translate( srcpt.getX(), srcpt.getY() );
        transform.scale( 0.74, 0.79 );
        transform.translate( -1.0, -2.3 );//todo magic numbers


        return transform;
    }

    protected void update() {
        super.update();
//        bulbNode.update();
        setTransform( createTransform() );
    }
}
