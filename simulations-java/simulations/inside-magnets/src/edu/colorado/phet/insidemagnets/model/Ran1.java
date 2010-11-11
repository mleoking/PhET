#include <stdio.h>

/*********************************************************************************/
double ran1(idum)
/*
   Returns a uniform random deviate between 0.0d0 and 1.0d0.
   Set idum to any negative value to initialize or re-initialize
   the sequence.
*/
int idum;
{
   static double r[97];
   static int   IX1,IX2,IX3;
   int    j;
   double rannum;

   const int M1=259200,IA1=7141,IC1=54773;
   const int M2=134456,IA2=8121,IC2=28411;
   const int M3=243000,IA3=4561,IC3=51349;

   const double rm1=1.0/((double)M1);
   const double rm2=1.0/((double)M2);

   if(idum<0) 
   {
       IX1=(IC1-idum)%M1;
       IX1=(IA1*IX1+IC1)%M1;
       IX2=IX1%M2;
       IX1=(IA1*IX1+IC1)%M1;
       IX3=IX1%M3;
       for(j=0; j<97; j++)
       {
          IX1=(IA1*IX1+IC1)%M1;
          IX2=(IA2*IX2+IC2)%M2;
          r[j]=((double)IX1+((double)IX2)*rm2)*rm1;
       }
   }

   IX1=(IA1*IX1+IC1)%M1;
   IX2=(IA2*IX2+IC2)%M2;
   IX3=(IA3*IX3+IC3)%M3;
   j=(97*IX3)/M3;

   if( j>96 || j<0 ) 
   {
      printf("\nran1: Warning: j=%d is out of range.",j);
   }
   rannum=r[j];
   r[j]=((double)IX1+((double)IX2)*rm2)*rm1;
   return(rannum);
}
