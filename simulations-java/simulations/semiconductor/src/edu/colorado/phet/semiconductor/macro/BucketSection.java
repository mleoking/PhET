package edu.colorado.phet.semiconductor.macro;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatterySpinner;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 7:49:27 PM
 */
public class BucketSection extends TransformGraphic {
    EnergySection model;
    private BufferedImage image;
    final ArrayList spackle = new ArrayList();
    static final Random random = new Random();
    int N = 12;
    private double diameter;

    public BucketSection( ModelViewTransform2D transform, EnergySection model, BufferedImage image ) {
        super( transform );
        this.model = model;
        this.image = image;
        for ( int i = 0; i < N; i++ ) {
            diameter = image.getWidth() * 2.0;
            double rand = random.nextDouble() * diameter - diameter / 2;
            double rand2 = random.nextDouble() * diameter - diameter / 2;

            Point pt = new Point( (int) rand, (int) rand2 );
            spackle.add( pt );
        }
    }

    public void paint( Graphics2D graphics2D ) {
        double voltage = Math.abs( model.getVoltage() );//getDiodeSection().getBattery().getVoltage());
        double frac = voltage / BatterySpinner.max;
        int num = (int) ( frac * N );

        EntryPoint[] sources = model.getSources();
        for ( int i = 0; i < sources.length; i++ ) {
            PhetVector phetVector = sources[i].getSource();
            double startX = sources[i].getSource().getX();
            double endX = sources[i].getCell().getX();
            int dir = (int) ( ( startX - endX ) / ( Math.abs( startX - endX ) ) );

            Point pt = super.getTransform().modelToView( phetVector );
            Point at = new Point( (int) ( pt.x + dir * diameter / 2 ), pt.y );
            spackleAt( graphics2D, at, num );
        }
    }

    private void spackleAt( Graphics2D graphics2D, Point pt, int num ) {
        SimpleBufferedImageGraphic iggy = new SimpleBufferedImageGraphic( image );
        for ( int i = 0; i < spackle.size() && i <= num; i++ ) {
            Point rel = (Point) spackle.get( i );
            iggy.setPosition( pt.x + rel.x, pt.y + rel.y );
            iggy.paint( graphics2D );
        }
    }

    public void update() {
    }
}
