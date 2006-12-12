package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.cck.piccolo_cck.ComponentNode;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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
    private ICCKModule module;
    private CircuitSolutionListener circuitSolutionListener = new CircuitSolutionListener() {
        public void circuitSolverFinished() {
            updateIntensity();
        }
    };
    private static final double SCALE = 0.75;

    public BulbComponentNode( CCKModel model, Bulb bulb, JComponent component, ICCKModule module ) {
        super( model, bulb, component, module );
        this.bulb = bulb;
        this.module = module;
        bulbNode = new BulbNode( bulb );
        addChild( bulbNode );
        model.getCircuitSolver().addSolutionListener( circuitSolutionListener );
        bulbNode.transformBy( AffineTransform.getScaleInstance( 2, 2.5 ) );
//        bulbNode.transformBy( AffineTransform.getScaleInstance( 2*SCALE, 2.5*SCALE ) );
        update();
        runParamTest();
        getHighlightNode().setStroke( new BasicStroke( (float)( 1.0 / 60.0 ) ) );
    }

    public void delete() {
        super.delete();
        getCCKModel().getCircuitSolver().removeSolutionListener( circuitSolutionListener );
        bulbNode.delete();
    }

    public BulbNode getBulbNode() {
        return bulbNode;
    }

    private double getTilt() {
        double w = bulbNode.getCoverShape().getBounds().getWidth() / 2;
        double h = bulbNode.getCoverShape().getBounds().getHeight();
        return -Math.atan2( w, h );
    }

    public static double getTiltValue( Bulb bulb ) {
//        double w = new BulbNode( bulb ).getCoverShape().getBounds().getWidth() / 2;
        double w = new BulbNode( bulb ).getCoverShape().getBounds().getWidth();
        double h = new BulbNode( bulb ).getCoverShape().getBounds().getHeight();
        return -Math.atan2( w, h );
    }

    private void updateIntensity() {
        bulbNode.setIntensity( bulb.getIntensity() );
    }

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

        double angle = new ImmutableVector2D.Double( srcpt, endpt ).getAngle() + 0.3 + theta;
        transform.rotate( angle, srcpt.getX(), srcpt.getY() );
        transform.translate( srcpt.getX(), srcpt.getY() );
//        transform.scale( 0.74, 0.79 );
        transform.scale( 0.74 * SCALE, 0.79 * SCALE * 1.15 );
        transform.translate( -1.0, -2.3 );//todo magic numbers

        return transform;
    }

    protected void update() {
        super.update();
        setTransform( createTransform() );
        getHighlightNode().setVisible( false );
//        bulbNode.setHighlightVisible(bulb.isSelected());
    }

}
