/*********************************************************************************/
double rangauss(idum)
/*
   Returns a gaussian random deviate with zero mean and unit variance.
   Uses Box-Muller transformation as in Press et al, Numerical Recipes.
*/
int idum;
{
   static int iset=0;
   static double  g1;
   double fac,rsq,v1,v2;

   if(iset==0)  /* no saved deviate, get new ones. */
   {
      /* get uniform deviates in a unit circle */
      do {  
	    v1=2.0*ran1(idum)-1.0;
	    v2=2.0*ran1(idum)-1.0;
	    rsq=v1*v1+v2*v2;
	 }  while( rsq>=1.0 || rsq==0.0);
  
      /* transform them into a gaussian distribution: */
      fac=sqrt(-2.0*log(rsq)/rsq);
    
      /* do the Box-Muller to get two normal deviates. 
         and save one for the next call.            */
      g1=fac*v1;
      iset=1;         /* very ugly programming!     */
      return(fac*v2); /* return the other deviate.  */
   }
   else  /* return the saved deviate and unset the flag. */
   {  
      iset=0;
      return(g1);
   }
}
