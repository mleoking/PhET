/**
 * Implementation of the Kronig-Penney model.
 */
class KronigPenney implements Solvable {
    /**
     * Creates a new model with the specified potential strength.
     */
    public KronigPenney( double p ) {
        this.p = p;
    }

    public void solve( double x0, double step, double[] vals ) {
        double units = 1;    // h bar squared / 2m
        for( int i = 0; i < vals.length; i++ ) {
            double Ka = solve( x0 );//bisect(x0);//
            vals[i] = Ka * Ka / ( Math.PI * Math.PI );//eval(x0,0);//units*Ka*Ka;//
            x0 += step;
        }
    }


    /**
     * Uses the Newton-Raphson method to solve the KP equation, with the
     * specified wavenumber.
     */
    protected double solve( double ka ) {
        ka *= Math.PI;
        double cos_ka = Math.cos( ka );
        double sign = ( ka > 0 ) ? 1 : -1;
        double x = sign * Math.PI * ( 0.5 + (int)( Math.abs( ka ) / Math.PI ) );
        double newX;

        for( int i = 0; i < 100; i++ ) {
            newX = x - eval( x, cos_ka ) / deriv( x, cos_ka );

            //	Test for underflow
            double m = ( x + newX ) / 2;
            if( m == x || m == newX || x == newX ) {
                return x;
            }

            x = newX;

            //System.err.println(x);
        }

        //throw new NoSolutionsException("Couldn't find a soln");
        return 0;
    }

    /*
         Uses bisection to solve the KP equation, with the specified
         wavenumber.
     */
    /*protected double bisect(double ka)
     {
         int region = (int) (ka/Math.PI);

         double cos_ka = Math.cos(ka);

         double lo = (region+0.0)*Math.PI;
         double mp = (region+0.5)*Math.PI;
         double hi = (region+1.0)*Math.PI;

         if(lo==0)
             lo = Double.MIN_VALUE;

         double loVal = eval(lo,cos_ka);
         double mpVal = eval(mp,cos_ka);
         double hiVal = eval(hi,cos_ka);

         System.err.println("ka = "+ka);

         while(mp!=lo && mp!=hi)
         {
             if(loVal>0 == hiVal>0)
             {
                 System.err.println("lo="+lo+"("+loVal+")   mp="+mp+"("+mpVal+")   hi="+hi+"("+hiVal+")");
                 System.err.println("gave up");
                 return 0;
             }

             if((loVal<0 && mpVal>0) || (loVal>0 && mpVal<0))
             {
                 hi = mp;
                 hiVal = mpVal;
             }
             else
             {
                 lo = mp;
                 loVal = mpVal;
             }

             mp = (lo+hi)/2;
             mpVal = eval(mp,cos_ka);
         }

         return mp;
     }*/


    /**
     * Evaluates the difference between the two sides of the KP equation.
     * The	energy which fits the KP equation is the energy for which this
     * equation is zero.
     *
     * @param    x    The energy at which to evaluate
     * @param    cos_ka    The cosine of the wavenumber
     */
    protected double eval( double x, double cos_ka ) {
        return p * Math.sin( x ) / x + Math.cos( x ) - cos_ka;
    }


    /**
     * Evaluates the derivative of the difference between the two sides of
     * the KP equation.
     *
     * @param    x    The energy at which to evaluate
     * @param    cos_ka    The cosine of the wavenumber
     */
    protected double deriv( double x, double cos_ka ) {
        return -( 1 + p / ( x * x ) ) * Math.sin( x ) + p * Math.cos( x ) / x;
    }

    //	Test harness
/*	public static void main(String[] args)
	{
		double x = Double.valueOf(args[0]).doubleValue();
		double p = 3*Math.PI/2;
		KronigPenney kp = new KronigPenney(p);
		double soln = kp.bisect(x);

		System.err.println(soln);
		System.err.println(p/soln * Math.sin(soln) + Math.cos(soln) - Math.cos(x));
	}*/


    /**    The potential strength	*/
    protected double p;
}
