// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.*;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.TutorialChartFrame;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

public class PlotBeta240 extends PlotOrderParameterVsRandomness {
    private PSwing textComponent;
    private MyButton showBetaPlot;
    private MyButton resetBetaPlot;
    private BetaChart betaChart;
    private TutorialChartFrame chartDialog;
    private ParticleModel.Listener listener;
    private JTextField jTextField;
    private MyButton bestFit;
    private PText betaText;
    private PSwing exponentPlotPanelGraphic;
//    private boolean error = false;

    public PlotBeta240( BasicTutorialCanvas page ) {
        super( page );
        setText( "First, plot the order parameter vs. randomness " +
                 "to determine the critical randomness, " +
                 "then enter that value in the red text box below.  " +
                 "You don't have to be exact, " +
                 "but I can't help you here.  " );
        jTextField = new JTextField( 20 );
        jTextField.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                dataEntered();
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        jTextField.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.red ), "Critical Randomness" ) );
        textComponent = new PSwing( page, jTextField );


        showBetaPlot = new MyButton( page, "Show" );
        showBetaPlot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showBetaPlot();
            }
        } );

        resetBetaPlot = new MyButton( page, "Reset" );
        resetBetaPlot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetBetaPlot();
            }

        } );
        betaChart = new BetaChart( 100000 );
        chartDialog = new TutorialChartFrame( "Plot", betaChart.getChart(), getBasePage().getTutorialApplication().getTutorialFrame() ) {
            public void finishPaint( Graphics g ) {
                super.finishPaint( g );
                paintErrorMessage( g );
            }

        };
        chartDialog.setLocation( 0, 400 );

        listener = new ParticleModel.Adapter() {
            public void steppedInTime() {
                if ( getParticleModel().getTime() % MOD == 0 ) {
                    super.steppedInTime();
                    sampleBetaData();
                }
            }
        };

        bestFit = new MyButton( page, "Linear Fit" );
        bestFit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doBestFit();
                advance();
            }

        } );

        betaText = new PText( "" );
        betaText.setFont( new PhetFont( 16, true ) );
        betaText.setTextPaint( Color.red );

        HorizontalLayoutPanel exponentPlotPanel = new HorizontalLayoutPanel();
        exponentPlotPanel.setBorder( BorderFactory.createTitledBorder( "Critical Exponent Plot" ) );
        exponentPlotPanel.add( showBetaPlot );
        exponentPlotPanel.add( resetBetaPlot );
        exponentPlotPanel.add( bestFit );
        exponentPlotPanelGraphic = new PSwing( page, exponentPlotPanel );
    }

    Font errorFont = new PhetFont( 11, true );

    private void paintErrorMessage( Graphics g ) {
        Point2D sample = null;
        try {
            sample = sampleBetaDataValue();
            if ( Double.isNaN( sample.getX() ) || Double.isNaN( sample.getY() ) || Double.isInfinite( sample.getX() ) || Double.isInfinite( sample.getY() ) ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont( errorFont );
                g2.setColor( Color.red );
                g2.drawString( "Out of range, randomness (on the slider), ", 70, 100 );
                g2.drawString( "must be less than critical randomness (red box)", 70, 120 );
                g2.drawString( "for the logarithm to be defined.", 70, 140 );
            }
        }
        catch ( NoEtaCriticalException e ) {
//            e.printStackTrace();
            Graphics2D g2 = (Graphics2D) g;
            g2.setFont( errorFont );
            g2.setColor( Color.red );
            g2.drawString( "Illegal Critical Randomness input: " + jTextField.getText(), 100, 150 );
        }

    }

    static class MyButton extends JButton {

        public MyButton( BasicTutorialCanvas page, String title ) {
            super( title );
        }
    }

    private void doBestFit() {
        XYSeries dataset = betaChart.getMeanDataSet();
        final ArrayList dataCopy = new ArrayList();
        for ( int i = 0; i < dataset.getItemCount(); i++ ) {
            XYDataItem x = dataset.getDataItem( i );
            dataCopy.add( new Point2D.Double( x.getX().doubleValue(), x.getY().doubleValue() ) );
        }
        LinearRegression.Result result = LinearRegression.main( new LinearRegression.Input() {
            public boolean isEmpty() {
                return dataCopy.isEmpty();
            }

            public Point2D readPoint() {
                return (Point2D) dataCopy.remove( 0 );
            }
        } );
        System.out.println( "result = " + result );
//        double x0 = betaChart.getChart().getXYPlot().getDomainAxis().getLowerBound();
//        double x1= betaChart.getChart().getXYPlot().getDomainAxis().getUpperBound();
        double x0 = getMinX( dataset );
        double x1 = getMaxX( dataset );
        Point2D lhs = new Point2D.Double( x0, result.evaluate( x0 ) );
        Point2D rhs = new Point2D.Double( x1, result.evaluate( x1 ) );

        betaChart.showLine( lhs, rhs );
        DecimalFormat decimalFormat = new DecimalFormat( "0.00" );

        betaText.setText( "Critical Exponent = " + decimalFormat.format( result.getSlope() ) );
    }

    private double getMaxX( XYSeries dataset ) {
        double max = Double.NEGATIVE_INFINITY;
        for ( int i = 0; i < dataset.getItemCount(); i++ ) {
            double x = dataset.getDataItem( i ).getX().doubleValue();
            if ( x > max ) {
                max = x;
            }
        }
        return max;
    }

    private double getMinX( XYSeries dataset ) {
        double min = Double.POSITIVE_INFINITY;
        for ( int i = 0; i < dataset.getItemCount(); i++ ) {
            double x = dataset.getDataItem( i ).getX().doubleValue();
            if ( x < min ) {
                min = x;
            }
        }
        return min;
    }

    double ln( double val ) {
        return Math.log( val ) / Math.log( Math.E );
    }

    Point2D.Double sampleBetaDataValue() throws NoEtaCriticalException {
        double op = getParticleModel().getOrderParameter();
        double eta = getParticleModel().getRandomness();
        double etaCritical = 0;
        etaCritical = getEtaCritical();
        double arg = ( etaCritical - eta ) / etaCritical;
        double x = ln( arg );
        double y = ln( op );
        return new Point2D.Double( x, y );

    }

    static class NoEtaCriticalException extends Exception {

    }

    private double getEtaCritical() throws NoEtaCriticalException {
        try {
            double val = Double.parseDouble( jTextField.getText() );
            return val;
        }
        catch ( NumberFormatException numberFormatException ) {
//            PhetOptionPane.showMessageDialog( jTextField, "Couldn't understand Critical Exponent (in Red Box): " + jTextField.getText() );
            throw new NoEtaCriticalException();
//            return 3;
        }
    }

    private void sampleBetaData() {
        Point2D sample = null;
        try {
            sample = sampleBetaDataValue();
            if ( Double.isNaN( sample.getX() ) || Double.isNaN( sample.getY() ) || Double.isInfinite( sample.getX() ) || Double.isInfinite( sample.getY() ) ) {
                chartDialog.getContentPane().repaint();
            }
            else {
//            System.out.println( "sample = " + sample );
                betaChart.addDataPoint( sample.getX(), sample.getY() );
//            error = false;
            }
        }
        catch ( NoEtaCriticalException e ) {
//            e.printStackTrace();
        }

    }

    private void showBetaPlot() {
        chartDialog.show();
        getParticleModel().addListener( listener );
    }

    private void resetBetaPlot() {
        betaChart.reset();
    }

    boolean firstTime = true;

    private void dataEntered() {
        if ( firstTime ) {
            firstTime = false;
            playHarp();
            append( "  Good!  Now you can determine the critical exponent.  " +
                    "Show the Critical Exponent Plot, take additional readings at " +
                    "different randomness values, " +
                    "and I'll apply the logarithms.  When you have enough data, " +
                    "you can press 'Linear Fit' to compute the critical exponent." );
            showBetaStuff();
        }
    }

    private void showBetaStuff() {
        exponentPlotPanelGraphic.setOffset( getLocationBeneath( textComponent ) );
        addChild( exponentPlotPanelGraphic );
        betaText.setOffset( getLocationBeneath( exponentPlotPanelGraphic ) );
        addChild( betaText );
//        showBetaPlot();
    }

    public void init() {
        super.init();
        textComponent.setOffset( getLocationBeneath( getBottomComponent() ) );
        addChild( textComponent );
        if ( !firstTime ) {
            showBetaStuff();
        }
    }

    public void teardown() {
        super.teardown();
        removeChild( textComponent );
        removeChild( exponentPlotPanelGraphic );
//        removeChild( bestFit );
//        removeChild( showBetaPlot );
//        removeChild( resetBetaPlot );
        removeChild( betaText );
        getParticleModel().removeListener( listener );
        chartDialog.setVisible( false );
    }

    protected boolean advanceWhenStartData() {
        return false;
    }
}
