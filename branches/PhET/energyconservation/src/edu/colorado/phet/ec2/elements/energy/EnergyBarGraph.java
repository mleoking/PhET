/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.energy;

import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.ec2.elements.car.Car;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.DefaultKeyedValues2DDataset;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jul 24, 2003
 * Time: 4:53:45 PM
 * Copyright (c) Jul 24, 2003 by Sam Reid
 */
public class EnergyBarGraph implements ObservingGraphic {
    Car car;
    private double x;
    private double y;
    private double width;
    private double height;
    private ImageObserver target;
    private JFreeChart chart;
    private BufferedImage bufferedImage;
    private DefaultKeyedValues2DDataset data;
    Comparable rowKey = new Double( 20 );
    Comparable columnkey = new Double( 20 );

    public EnergyBarGraph( Car car, double x, double y, double width, double height, ImageObserver target ) {
        this.car = car;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.target = target;
        car.addObserver( this );
    }

    public synchronized void paint( Graphics2D g ) {
//        if (chart != null)
//            chart.draw(g, new Rectangle2D.Double(100, 100, 200, 200));
//
//        chart.createBufferedImage()
        if( bufferedImage != null ) {
            g.drawImage( bufferedImage, 100, 100, target );
        }
    }

    double heightValue = 0;

    public synchronized void update( Observable o, Object arg ) {
        heightValue += .2;
        if( heightValue > 20 ) {
            heightValue = .2;
        }
        double ke = car.getKineticEnergy();
        double pe = car.getPotentialEnergy();
        double energy = ke + pe;
//        CategoryDataset data=new DefaultCategoryDataset();

        if( chart == null ) {
//            CategoryDataset cd=new
//            data = new DefaultKeyedValues2DDataset();
            DatasetUtilities util;
            double value = heightValue;
            data.addValue( value, rowKey, columnkey );
            this.chart = ChartFactory.createBarChart( "Energy graph", "category", "Value", data, PlotOrientation.VERTICAL, true, true, false );
        }
        ChartRenderingInfo info = new ChartRenderingInfo();

        bufferedImage = chart.createBufferedImage( 200, 200, info );
        data.addValue( heightValue, rowKey, columnkey );
    }
}
