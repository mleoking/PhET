package edu.colorado.phet.signalcircuit;

import java.awt.image.BufferedImage;

public class SignalCircuitResources {
    public static BufferedImage loadBufferedImage( String s ) {
        return SignalCircuitStrings.INSTANCE.getImage( s );
    }
}
