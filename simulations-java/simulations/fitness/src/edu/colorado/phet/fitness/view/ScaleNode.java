package edu.colorado.phet.fitness.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Apr 9, 2008 at 8:35:03 PM
 */
public class ScaleNode extends PNode {
    private FitnessModel model;
    private Human human;
    private PText weightReadout;
    double faceWidth = 0.9;
    double faceHeight = 0.1;
    double faceY = 0.05;
    double depthDX = 0.06;
    double depthDY = 0.1;
    private float strokeWidth = 0.02f;
    private PText bmiReadout;
    private PSwing unitsPSwing;

    public ScaleNode( final FitnessModel model, Human human ) {
        this.model = model;
        this.human = human;
        DoubleGeneralPath topPath = new DoubleGeneralPath();

        topPath.moveTo( -faceWidth / 2, faceY );
        topPath.lineTo( -faceWidth / 2 + depthDX, faceY - depthDY );
        topPath.lineTo( faceWidth / 2 - depthDX, faceY - depthDY );
        topPath.lineTo( faceWidth / 2, faceY );
        topPath.lineTo( -faceWidth / 2, faceY );
        addChild( new PhetPPath( topPath.getGeneralPath(), new BasicStroke( strokeWidth ), Color.black ) );

        DoubleGeneralPath facePath = new DoubleGeneralPath();
        facePath.moveTo( -faceWidth / 2, faceY );
        facePath.lineTo( -faceWidth / 2, faceY + faceHeight );
        facePath.lineTo( faceWidth / 2, faceY + faceHeight );
        facePath.lineTo( faceWidth / 2, faceY );
        facePath.lineTo( -faceWidth / 2, faceY );
        addChild( new PhetPPath( facePath.getGeneralPath(), new BasicStroke( strokeWidth ), Color.black ) );
        human.addListener( new Human.Adapter() {
            public void bmiChanged() {
                updateBMIReadout();
            }

            public void weightChanged() {
                updateWeightReadout();
            }
        } );
        weightReadout = new PText( "??" );
        double TEXT_SCALE = 1.0 / 175.0;
        weightReadout.scale( TEXT_SCALE );
        addChild( weightReadout );

        bmiReadout = new PText( "??" );
        bmiReadout.scale( TEXT_SCALE );
        addChild( bmiReadout );
        updateWeightReadout();
        updateBMIReadout();

        JPanel units = new VerticalLayoutPanel();
//        units.setBorder( BorderFactory.createTitledBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ),"units" ) );
        units.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        for ( int i = 0; i < FitnessModel.availableUnits.length; i++ ) {
            final JRadioButton jRadioButton = new JRadioButton( FitnessModel.availableUnits[i].getShortName(), FitnessModel.availableUnits[i] == model.getUnits() );
            final int i1 = i;
            jRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.setUnits( FitnessModel.availableUnits[i1] );
                }
            } );
            model.addListener( new FitnessModel.Listener() {
                public void unitsChanged() {
                    jRadioButton.setSelected( model.getUnits() == FitnessModel.availableUnits[i1] );
                }
            } );
            units.add( jRadioButton );
        }
//        units.setBorder(  );

        unitsPSwing = new PSwing( units );
        unitsPSwing.setOffset( faceWidth / 2 + strokeWidth / 2, 0 );
        unitsPSwing.scale( TEXT_SCALE * 0.75 );
        addChild( unitsPSwing );
        model.addListener( new FitnessModel.Listener() {
            public void unitsChanged() {
                updateWeightReadout();
                updateBMIReadout();
            }
        } );
    }

    private void updateBMIReadout() {
        bmiReadout.setText( "BMI: " + new DecimalFormat( "0.0" ).format( human.getBMI() ) + " kg/m^2" );
        updateTextLayout();
    }

    private void updateWeightReadout() {
        weightReadout.setText( "" + new DecimalFormat( "0.0" ).format( model.getUnits().modelToView( human.getMass() ) ) + " " + model.getUnits().getMassUnit() );
        updateTextLayout();
    }

    private void updateTextLayout() {
        weightReadout.setOffset( -faceWidth / 2 + strokeWidth, faceY );
        bmiReadout.setOffset( faceWidth / 2 - strokeWidth - bmiReadout.getFullBounds().getWidth(), faceY );
    }
}
