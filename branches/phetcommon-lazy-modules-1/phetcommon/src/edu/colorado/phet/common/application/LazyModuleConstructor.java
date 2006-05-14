package edu.colorado.phet.common.application;

/**
 * User: Sam Reid
 * Date: May 14, 2006
 * Time: 10:27:47 AM
 * Copyright (c) May 14, 2006 by Sam Reid
 */
public abstract class LazyModuleConstructor {
    String name;

    protected LazyModuleConstructor( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
