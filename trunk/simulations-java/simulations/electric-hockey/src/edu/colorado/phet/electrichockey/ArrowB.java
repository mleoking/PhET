package edu.colorado.phet.electrichockey;

public class ArrowB extends Arrow {

    public double computeWidth() {
        return ( Math.min( 6.0, this.L / 10.0 ) );
    }

}//end of public class