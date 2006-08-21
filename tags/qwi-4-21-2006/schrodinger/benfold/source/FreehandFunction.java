class FreehandFunction implements Function {
    public FreehandFunction( double minX, double maxX, int steps ) {
        this.minX = minX;
        this.maxX = maxX;
        vals = new double[steps];
    }


    public double evaluate( double x ) {
        try {
            //double d = vals[xToIndex(x)];
            double h = ( maxX - minX ) / vals.length;
            double pos = ( x - minX ) / h;
            int index = (int)pos;
            double leftOver = pos - index;
            return vals[index] * ( 1 - leftOver ) + vals[index + 1] * leftOver;
        }
        catch( IndexOutOfBoundsException e ) {
            return 0;
        }
    }

    public void update( double x, double y ) {
        throw new RuntimeException( "Need to know when mouse goes down" );
    }

    public void update( double x, double y, boolean mouseDown ) {
        //System.err.println("x="+x+"    y="+y);
        int thisX = xToIndex( x );

        if( mouseDown ) {
            lastX = thisX;
        }

        int x1 = Math.min( lastX, thisX );
        int x2 = Math.max( lastX, thisX );
        double val1;
        double val2;

        double edge = 0.1;

        x1 = Math.max( x1, (int)( edge * vals.length ) );
        x2 = Math.min( x2, (int)( ( 1 - edge ) * vals.length ) );

        thisX = Math.max( thisX, (int)( edge * vals.length ) );
        thisX = Math.min( thisX, (int)( ( 1 - edge ) * vals.length ) );

        vals[thisX] = y;
        val1 = vals[x1];
        val2 = vals[x2];


        double gradient = ( x2 == x1 ) ? 0 : ( ( val2 - val1 ) / ( x2 - x1 ) );


        for( int i = x1; i <= x2; i++ ) {
            vals[i] = val1 + ( i - x1 ) * gradient;
        }


        lastX = thisX;
    }

    public int xToIndex( double x ) {
        return (int)( vals.length * ( x - minX ) / ( maxX - minX ) );
    }


    protected int lastX;
    protected double minX, maxX;
    protected double[] vals;
}