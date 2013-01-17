package edu.colorado.phet.forcesandmotionbasics.motion;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * @author Sam Reid
 */
public class StackableBucketNode extends StackableNode {
    private final BufferedImage image;
    private final Property<Option<Double>> acceleration;
    private final PhetPPath water;
    private final ArrayList<Double> history = new ArrayList<Double>();

    public StackableBucketNode( IUserComponent component, final StackableNodeContext context, final BufferedImage image, final double mass, final int pusherOffset, BooleanProperty showMass, final Property<Option<Double>> acceleration ) {
        super( component, context, image, mass, pusherOffset, showMass, false, image, image );
        this.image = image;
        this.acceleration = acceleration;
        water = new PhetPPath( Color.blue );
        addChild( water );
        water.moveToBack();
    }

    public void stepInTime( final double dt ) {
        double acceleration = this.acceleration.get().get();
        history.add( acceleration );
        while ( history.size() > 10 ) {
            history.remove( 0 );
        }
        //Metrics based on original image size of 98 pixels wide.
        double s = ( (double) image.getWidth() ) / 98.0;
        LinearFunction leftLineX = new LinearFunction( 0, 1, 1 * s, 10 * s );
        LinearFunction leftLineY = new LinearFunction( 0, 1, 9 * s, 102 * s );

        LinearFunction rightLineX = new LinearFunction( 1, 0, 87 * s, 96 * s );
        LinearFunction rightLineY = new LinearFunction( 1, 0, 102 * s, 9 * s );

        double min = 0.5; //Water level when acceleration = 0
        double sum = 0.0;
        for ( Double aDouble : history ) {
            sum = sum + aDouble;
        }
        double composite = sum / history.size();
        double delta = -composite / 50;
        final DoubleGeneralPath path = new DoubleGeneralPath( leftLineX.evaluate( min + delta ), leftLineY.evaluate( min + delta ) );
        path.lineTo( leftLineX.evaluate( 1 ), leftLineY.evaluate( 1 ) );
        path.lineTo( rightLineX.evaluate( 1 ), rightLineY.evaluate( 1 ) );
        path.lineTo( rightLineX.evaluate( min - delta ), rightLineY.evaluate( min - delta ) );
        path.closePath();

        water.setPathTo( path.getGeneralPath() );
    }
}