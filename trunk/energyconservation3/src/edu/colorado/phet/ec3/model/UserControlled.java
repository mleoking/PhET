/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:43 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class UserControlled implements UpdateMode {
    Body body;

    public UserControlled( Body body ) {
        this.body = body;
    }

    public void stepInTime( Body body, double dt ) {
        this.body = body;
    }

    public void init() {
//        body.setCMRotation( 0 );
//        body.setAttachmentPointRotation( Math.PI );
        body.convertToFreefall();
    }

    public UpdateMode copy( Body body ) {
        return new UserControlled( body );
    }

}
