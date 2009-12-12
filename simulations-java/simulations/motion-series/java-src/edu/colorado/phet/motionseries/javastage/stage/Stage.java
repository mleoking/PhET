package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

public class Stage extends SimpleObservable {
    private double width;
    private double height;

    public Stage(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        notifyObservers();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
