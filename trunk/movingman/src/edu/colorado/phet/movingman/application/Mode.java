/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 1:12:18 PM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public abstract class Mode extends ModelElement {
    String name;
    private MovingManModule module;


    public Mode(MovingManModule module, String name) {
        this.module = module;
        this.name = name;
    }

    protected MovingManModule getModule() {
        return module;
    }

    public String toString() {
        return super.toString();
    }

    public boolean equals(Object obj) {
        return obj.toString().equals(name);
    }

    public abstract void initialize();

}