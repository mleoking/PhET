package edu.colorado.phet.energyskatepark.test;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;

/**
 * Author: Sam Reid
 * May 22, 2007, 12:16:05 AM
 */
public class SearchForFallthrough {
    static class ExperimentParam {
        double sx;
        double energyScale;

        public ExperimentParam( double sx, double energyScale ) {
            this.sx = sx;
            this.energyScale = energyScale;
        }
    }

    public static void searchFallThrough1() {
        runExperiment( new String[0], new ExperimentParam( 2, 1.9 ) );
    }

    public static void searchFallThrough2() {
        runExperiment( new String[0], new ExperimentParam( 3, 1.5 ) );
    }

    public static void main( String[] args ) {
        ExperimentParam experiment = new ExperimentParam( 2, 1.9 );
        runExperiment( args, experiment );
    }

    private static void runExperiment( String[] args, ExperimentParam experiment ) {
        //TODO: port this to new EnergySkateParkApplication constructor if it's valuable
//        EnergySkateParkApplication app = new EnergySkateParkApplication( args );
//        app.getModule().getEnergySkateParkModel().removeAllSplineSurfaces();
//        int h0 = 1;
//        int h1 = 8;
////        ExperimentParam experiment = new ExperimentParam( 3, 1.5 );
//
//        EnergySkateParkSpline skateParkSpline = new EnergySkateParkSpline( createDoubleWell( experiment.sx, h0, h1, 9 ) );
//        app.getModule().getEnergySkateParkModel().addSplineSurface( skateParkSpline );
//
////        int numParticles = 30;
//        int numParticles = 40;
//        for( int i = 0; i < numParticles; i++ ) {
//            Body body = app.getModule().createBody();
//            body.setPosition( skateParkSpline.getControlPoint( 1 ).getX(), skateParkSpline.getControlPoint( 1 ).getY() + 0.0001 );
//            double dE = ( h1 - h0 ) * body.getMass() * Math.abs( EnergySkateParkModel.G_EARTH );
//            double velocity = Math.sqrt( 2 * dE / body.getMass() );
//            double spread = velocity * 0.05;//5% spread
//            double dv = ( 0.5 - ( (double)i ) / numParticles ) * 2 * spread;
//            System.out.println( "velocity=" + velocity + ", dv = " + dv );
////            body.setVelocity( (velocity + dv)*1.85, 0 );
//            body.setVelocity( ( velocity + dv ) * experiment.energyScale, 0 );
//            body.setSpline( skateParkSpline, false, 0.2 );
//            app.getModule().getEnergySkateParkModel().addBody( body );
//        }
//        app.startApplication();
    }

    private static SerializablePoint2D[] createDoubleWell( double scaleX, double h0, double h1, double h2 ) {
        return new SerializablePoint2D[]{
                new SerializablePoint2D( 0 * scaleX, h2 ),
                new SerializablePoint2D( 1 * scaleX, h0 ),
                new SerializablePoint2D( 2 * scaleX, h1 ),
                new SerializablePoint2D( 3 * scaleX, h0 ),
                new SerializablePoint2D( 4 * scaleX, h2 )
        };
    }
}
