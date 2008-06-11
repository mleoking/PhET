package edu.colorado.phet.circuitconstructionkit.model;

/**
 * Created by: Sam
 * Mar 21, 2008 at 9:56:54 AM
 */
public class CCKDefaults {
    public static final double WIRE_THICKNESS = 0.3;
    public static final double DEFAULT_SCALE = 109.3 / 100.0;
    public static final double modelTimeScale = 5;

    public static double determineTilt() {
        return -0.8058034940839864;
//        LightBulbGraphic lbg = new LightBulbGraphic(new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
//        double w = lbg.getCoverShape().getBounds().getWidth() / 2;
//        double h = lbg.getCoverShape().getBounds().getHeight();
//        double v = -Math.atan2(w, h);
//        System.out.println("Tilt="+v);
//        return v;
    }
}
