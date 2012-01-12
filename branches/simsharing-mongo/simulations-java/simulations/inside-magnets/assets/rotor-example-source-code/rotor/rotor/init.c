#include <math.h>

extern double *tmpx, *tmpy;   /* temp arrays for dynamics. */


/***************************************************************/
void init(N,idum,sx,sy,omega)
int  N;           
int idum;
double *sx,*sy,*omega;
{
   extern double TWOPI;
   int i;
   double phi;
/*
    sets a random initial condition for XY rotor spins.  
    lattice size is nx by ny, but stored linearly.
    init. condition is uniform over the unit sphere.
*/


    sx[N]=sy[N]=omega[N]=0.0;    /* last site is unoccupied. */
    tmpx[N]=tmpy[N]=0.0;         /* do also for tmp spin arrays. */

    ran1(-abs(idum)-1);
    for(i=0; i<N; i++)
    {
    /*   xn=2.0*ran1(1)-1.0;
         sq=sqrt(1.0-xn*xn);
         phidot[i]=xn;  */
      omega[i]=0.0;
      phi=TWOPI*ran1(1);
      sx[i]=cos(phi);
      sy[i]=sin(phi);
    }
return;
}

/***************************************************************/
void align(N,idum,sx,sy,omega)
int  N;           
double *sx,*sy,*omega;
{
   extern double TWOPI;
   int i;
   double phi,sq,xn,yn,zn;
/*
    sets a random aligned initial condition for the spins.  
    lattice size is nx by ny, but stored linearly.
*/
    sx[N]=sy[N]=omega[N]=0.0;    /* last site is unoccupied. */
    tmpx[N]=tmpy[N]=0.0;         /*  also for tmp spin arrays. */

    xn=2.0*ran1(-abs(idum)-1)-1.0;
    sq=sqrt(1.0-xn*xn); 

    phi=TWOPI*ran1(1);
    yn=sin(phi);
    xn=cos(phi);
    zn=0.0;
    for(i=0; i<N; i++)
    {
      sx[i]=xn;
      sy[i]=yn;
      omega[i]=zn;
    }
return;
}
