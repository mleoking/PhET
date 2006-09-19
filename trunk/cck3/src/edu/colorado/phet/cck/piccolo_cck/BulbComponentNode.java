package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common_cck.util.SimpleObserver;

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
        bulb.addObserver( new SimpleObserver() {
            public void update() {
                BulbComponentNode.this.update();
            }
        } );
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

    private void updateTransform() {
        double sign = 1;
        if( !bulb.isConnectAtRight() ) {
            sign = -1;
        }
        setTransform( createTransform( sign * getTilt() ) );
    }

    ModelSlider a = new ModelSlider( "dx", "", -1, 1, 0 );
    ModelSlider b = new ModelSlider( "dy", "", -1, 1, 0 );

    static {

    }

    private void runParamTest() {
        JFrame frame = new JFrame();
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        contentPane.add( a );

        contentPane.add( b );
        a.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        } );
        b.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        } );
        frame.setContentPane( contentPane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//        frame.setVisible( true );
    }

    private AffineTransform createTransform( double theta ) {
        Point2D srcpt = bulb.getStartPoint();
        Point2D endpt = bulb.getEndPoint();
        double angle = new ImmutableVector2D.Double( srcpt, endpt ).getAngle() - Math.PI / 2;

        angle += theta;
        AffineTransform transform = new AffineTransform();

        transform.rotate( angle, srcpt.getX(), srcpt.getY() );
        transform.translate( srcpt.getX(), srcpt.getY() );
//        transform.translate( -1.2, 0.8 );
//        transform.translate( a.getValue(), b.getValue() );
        transform.translate( -0.94, -0.55 );
//        transform.translate( -bulb.getWidth() / 2, -bulb.getHeight() * .93 );//TODO .93 is magick!
        transform.scale( 0.5, 0.6 );
//        trf.scale( bulb.getWidth() / width, bulb.getHeight() / height );

        return transform;
    }

    protected void update() {
        setTransform( new AffineTransform() );
        super.update();
        bulbNode.update();
//        rotate( getTilt() );
        updateTransform();
    }
}
