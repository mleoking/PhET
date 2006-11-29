package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.model.EnergySkateParkModel;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 16, 2005
 * Time: 11:45:12 PM
 * Copyright (c) Nov 16, 2005 by Sam Reid
 */
public abstract class Planet {
    private String name;
    private double gravity;

    protected Planet( String name, double gravity ) {
        this.gravity = gravity;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getGravity() {
        return gravity;
    }

    public void apply( EnergySkateParkModule module ) {
        setupImage( module );
        setupGravity( module );
    }

    private void setupGravity( EnergySkateParkModule module ) {
        module.getEnergyConservationModel().setGravity( gravity );
    }

    protected abstract void setupImage( EnergySkateParkModule module );

    public static class Space extends Planet {
        protected Space() {
            super( EnergySkateParkStrings.getString( "space" ), 0.0 );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            BufferedImage image = null;
            try {
//                image = ImageLoader.loadBufferedImage( "images/space.jpg" );
                image = ImageLoader.loadBufferedImage( "images/blackhole_large_2.jpg" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            module.getEnergyConservationCanvas().getRootNode().setBackground( image );
        }
    }

    public static class Earth extends Planet {
        protected Earth() {
            super( EnergySkateParkStrings.getString( "earth" ), EnergySkateParkModel.G_EARTH );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            BufferedImage image = null;
            try {
//                image = ImageLoader.loadBufferedImage( "images/background-gif.gif" );
                image = ImageLoader.loadBufferedImage( "images/earth3.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            module.getEnergyConservationCanvas().getRootNode().setBackground( image );
        }
    }

    public static class Moon extends Planet {
        public Moon() {
            super( EnergySkateParkStrings.getString( "moon" ), EnergySkateParkModel.G_MOON );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            BufferedImage image = null;
            try {
                image = ImageLoader.loadBufferedImage( "images/moon2.jpg" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            module.getEnergyConservationCanvas().getRootNode().setBackground( image );
        }
    }

    public static class Jupiter extends Planet {
        public Jupiter() {
            super( EnergySkateParkStrings.getString( "jupiter" ), EnergySkateParkModel.G_JUPITER );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            try {
                BufferedImage image = ImageLoader.loadBufferedImage( "images/jupiter4.jpg" );
                module.getEnergyConservationCanvas().getRootNode().setBackground( image );
            }
            catch( IOException e1 ) {
                e1.printStackTrace();
            }
        }
    }
}
