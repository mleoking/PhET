package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Apr 9, 2008 at 8:35:03 PM
 */
public class ScaleNode extends PNode {
    private EatingAndExerciseModel model;
    private Human human;
    private PText weightReadout;
    private double faceWidth = 0.9;
    private double faceHeight = 0.13;
    private double faceY = 0.05;
    private double depthDX = 0.06;
    private double depthDY = 0.1;
    private float strokeWidth = 0.02f;
    private PSwing unitsPSwing;
    private final Color scaleColor = Color.lightGray;
    private PhetPPath faceNode;

    public ScaleNode( final EatingAndExerciseModel model, Human human ) {
        this.model = model;
        this.human = human;
        DoubleGeneralPath topPath = new DoubleGeneralPath();

        topPath.moveTo( -faceWidth / 2, faceY );
        topPath.lineTo( -faceWidth / 2 + depthDX, faceY - depthDY );
        topPath.lineTo( faceWidth / 2 - depthDX, faceY - depthDY );
        topPath.lineTo( faceWidth / 2, faceY );
        topPath.lineTo( -faceWidth / 2, faceY );
        addChild( new PhetPPath( topPath.getGeneralPath(), scaleColor, new BasicStroke( strokeWidth ), Color.black ) );

        DoubleGeneralPath facePath = new DoubleGeneralPath();
        facePath.moveTo( -faceWidth / 2, faceY );
        facePath.lineTo( -faceWidth / 2, faceY + faceHeight );
        facePath.lineTo( faceWidth / 2, faceY + faceHeight );
        facePath.lineTo( faceWidth / 2, faceY );
        facePath.lineTo( -faceWidth / 2, faceY );
        faceNode = new PhetPPath( facePath.getGeneralPath(), scaleColor, new BasicStroke( strokeWidth ), Color.black );
        addChild( faceNode );
        human.addListener( new Human.Adapter() {
            public void weightChanged() {
                updateReadout();
            }

            public void bmiChanged() {
                updateReadout();
            }
        } );
        weightReadout = new EatingAndExercisePText( "??" );
        double TEXT_SCALE = 1.0 / 175.0;
        weightReadout.scale( TEXT_SCALE );
        addChild( weightReadout );

        updateReadout();

        JPanel units = new VerticalLayoutPanel();
        units.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        for ( int i = 0; i < EatingAndExerciseModel.availableUnits.length; i++ ) {
            final JRadioButton jRadioButton = new JRadioButton( EatingAndExerciseModel.availableUnits[i].getShortName(), EatingAndExerciseModel.availableUnits[i] == model.getUnits() );
            buttonGroup.add( jRadioButton );
            final int i1 = i;
            jRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.setUnits( EatingAndExerciseModel.availableUnits[i1] );
                }
            } );
            model.addListener( new EatingAndExerciseModel.Adapter() {
                public void unitsChanged() {
                    jRadioButton.setSelected( model.getUnits() == EatingAndExerciseModel.availableUnits[i1] );
                }
            } );
            units.add( jRadioButton );
        }

        unitsPSwing = new PSwing( units );
        unitsPSwing.setOffset( faceWidth / 2 + strokeWidth / 2, 0 );
        unitsPSwing.scale( TEXT_SCALE * 0.75 );
        addChild( unitsPSwing );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                updateReadout();
            }
        } );
    }

    private void updateReadout() {
        String BMI = EatingAndExerciseResources.getString( "bmi" );
        String BMI_UNITS = EatingAndExerciseResources.getString( "units.bmi" );
        weightReadout.setText( "" + EatingAndExerciseStrings.WEIGHT_FORMAT.format( model.getUnits().modelToViewMass( human.getMass() ) ) + " " + model.getUnits().getMassUnit() + ", " + BMI + ": " + EatingAndExerciseStrings.BMI_FORMAT.format( human.getBMI() ) + " " + BMI_UNITS );
        updateTextLayout();
    }

    private void updateTextLayout() {
        weightReadout.setOffset( 0 - weightReadout.getFullBounds().getWidth() / 2, faceY + faceHeight - weightReadout.getFullBounds().getHeight() - 0.01 );
    }
}
