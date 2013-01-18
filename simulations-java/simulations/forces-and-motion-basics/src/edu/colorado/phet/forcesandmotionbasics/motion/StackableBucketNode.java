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
    private final StackableNodeContext context;

    public StackableBucketNode( IUserComponent component, final StackableNodeContext context, final BufferedImage image, final double mass, final int pusherOffset, BooleanProperty showMass, final Property<Option<Double>> acceleration ) {
        super( component, context, image, mass, pusherOffset, showMass, false, image, image );
        this.image = image;
        this.acceleration = acceleration;
        water = new PhetPPath( new Color( 9, 125, 159 ) );
        addChild( water );
        water.moveToBack();
        this.context = context;
    }

    public void stepInTime( final double dt ) {
        double acceleration = this.acceleration.get().get();
        history.add( acceleration );
        while ( history.size() > 7 ) {
            history.remove( 0 );
        }
        //Metrics based on original image size of 98 pixels wide.
        double padX = 4.5;
        double padY = 9;
        double s = ( (double) image.getWidth() ) / 98.0;
        LinearFunction leftLineX = new LinearFunction( 0, 1, ( 1 + padX ) * s, ( 10 + padX ) * s );
        LinearFunction leftLineY = new LinearFunction( 0, 1, ( 9 - padY ) * s, ( 102 - padY ) * s );

        LinearFunction rightLineX = new LinearFunction( 1, 0, ( 87 - padX ) * s, ( 96 - padX ) * s );
        LinearFunction rightLineY = new LinearFunction( 1, 0, ( 102 - padY ) * s, ( 9 - padY ) * s );

        double min = 0.5; //Water level when acceleration = 0
        double sum = 0.0;
        for ( Double aDouble : history ) {
            sum = sum + aDouble;
        }
        double composite = sum / history.size();
        double delta = context.isInStack( this ) ? -composite / 50 : 0;
        final DoubleGeneralPath path = new DoubleGeneralPath( leftLineX.evaluate( min + delta ), leftLineY.evaluate( min + delta ) );
        path.lineTo( leftLineX.evaluate( 1 ), leftLineY.evaluate( 1 ) );
        path.lineTo( rightLineX.evaluate( 1 ), rightLineY.evaluate( 1 ) );
        path.lineTo( rightLineX.evaluate( min - delta ), rightLineY.evaluate( min - delta ) );
        path.closePath();

        water.setPathTo( path.getGeneralPath() );
    }
}