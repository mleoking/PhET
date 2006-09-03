/*PhET, 2004.*/
package edu.colorado.phet.movingman.model;


import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.movingman.MovingManModule;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 1:12:18 PM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public abstract class Mode implements ModelElement {
    private String name;
    private MovingManModule module;
    private boolean takingData;

    public Mode( MovingManModule module, String name, boolean takingData ) {
        this.module = module;
        this.name = name;
        this.takingData = takingData;
    }

    public String toString() {
        return super.toString();
    }

    public boolean equals( Object obj ) {
        return obj.toString().equals( name );
    }

    public abstract void initialize();

    public String getName() {
        return name;
    }

    public boolean isTakingData() {
        return takingData;
    }
}