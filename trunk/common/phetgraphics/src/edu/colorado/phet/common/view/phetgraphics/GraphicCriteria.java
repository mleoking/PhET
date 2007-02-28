package edu.colorado.phet.common.view.phetgraphics;


/**
 * User: Sam Reid
 * Date: Apr 6, 2005
 * Time: 11:23:02 PM
 * Copyright (c) Apr 6, 2005 by Sam Reid
 */
public interface GraphicCriteria {
    public boolean isSatisfied( PhetGraphic phetGraphic );

    public static class AssignableFrom implements GraphicCriteria {
        Class theInterface;

        public AssignableFrom( Class theInterface ) {
            this.theInterface = theInterface;
        }

        public boolean isSatisfied( PhetGraphic phetGraphic ) {
            if( theInterface.isAssignableFrom( phetGraphic.getClass() ) ) {
                return true;
            }
            else {
                return false;
            }
        }
    }
}
