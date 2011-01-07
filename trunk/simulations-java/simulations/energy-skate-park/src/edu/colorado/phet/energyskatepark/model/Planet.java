// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 16, 2005
 * Time: 11:45:12 PM
 */
public abstract class Planet {
    private String name;
    private double gravity;
    private Paint groundPaint;
    private Paint groundLinePaint;
    private boolean groundVisible;

    protected Planet( String name, double gravity, Paint groundPaint, Paint groundLinePaint, boolean groundVisible ) {
        this.gravity = gravity;
        this.name = name;
        this.groundPaint = groundPaint;
        this.groundLinePaint = groundLinePaint;
        this.groundVisible = groundVisible;
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
        module.getEnergySkateParkModel().setGravity( gravity );
    }

    protected abstract void setupImage( EnergySkateParkModule module );

    public boolean isDefault() {
        return false;
    }

    public Paint getGroundPaint() {
        return groundPaint;
    }

    public Paint getGroundLinePaint() {
        return groundLinePaint;
    }

    public boolean isGroundVisible() {
        return groundVisible;
    }

    public static class Space extends Planet {
        public Space() {
            super( EnergySkateParkStrings.getString( "location.space" ), EnergySkateParkModel.G_SPACE, Color.black, Color.black, false );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            BufferedImage image = null;
            try {
//                image = ImageLoader.loadBufferedImage( "energy-skate-park/images/space.jpg" );
                image = ImageLoader.loadBufferedImage( "energy-skate-park/images/blackhole_large_2.jpg" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            module.getEnergySkateParkSimulationPanel().getRootNode().setBackground( image );
        }

        public boolean isGroundVisible() {
            return false;
        }
    }

    public static class Earth extends Planet {
        public Earth() {
            super( EnergySkateParkStrings.getString( "location.earth" ), EnergySkateParkModel.G_EARTH, new Color( 100, 170, 100 ), new Color( 0, 130, 0 ), true );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            BufferedImage image = null;
            try {
//                image = ImageLoader.loadBufferedImage( "energy-skate-park/images/background-gif.gif" );
//                image = ImageLoader.loadBufferedImage( "energy-skate-park/images/earth3.gif" );
                image = ImageLoader.loadBufferedImage( "energy-skate-park/images/earth-background.jpg" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            module.getEnergySkateParkSimulationPanel().getRootNode().setBackground( image );
        }

        public boolean isDefault() {
            return true;
        }
    }

    public static class Moon extends Planet {
        public Moon() {
            super( EnergySkateParkStrings.getString( "location.moon" ), EnergySkateParkModel.G_MOON, Color.gray, Color.darkGray, true );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            BufferedImage image = null;
            try {
                image = ImageLoader.loadBufferedImage( "energy-skate-park/images/moon2.jpg" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            module.getEnergySkateParkSimulationPanel().getRootNode().setBackground( image );
        }
    }

    public static class Jupiter extends Planet {
        public Jupiter() {
            super( EnergySkateParkStrings.getString( "location.jupiter" ), EnergySkateParkModel.G_JUPITER, new Color( 173, 114, 98 ), new Color( 62, 44, 58 ), true );
        }

        protected void setupImage( EnergySkateParkModule module ) {
            try {
                BufferedImage image = ImageLoader.loadBufferedImage( "energy-skate-park/images/jupiter4.jpg" );
                module.getEnergySkateParkSimulationPanel().getRootNode().setBackground( image );
            }
            catch( IOException e1 ) {
                e1.printStackTrace();
            }
        }
    }
}
