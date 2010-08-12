#include <stdio.h>

extern double Irot;	/* rotational inertia of the spin rotors. */
extern double gam;	/* Langevin damping parameter. */
extern double sigtau;	/* variance of the random torques. */

/*****************************************************/
void torque(N,nbrs,sx,sy,omega,J,Bx,By,bx,by,Ka,bc,dt)
/*
   Applies the torque equation of motion for XY rotor model spins.
   Each site has in-plane coordinates (sx,sy), and rotational speed omega.
   The outputted omega array is omega advanced one time step dt, for
   Langevin dynamics.  The sx and sy arrays are not modified.
*/
int N,bc;                /* Number of sites in system,  boundary conditions. */
int **nbrs;              /* N x 4 nearest neighbor table.                    */
double *sx,*sy,*omega;   /* spin components, angular speed around z-axis.    */
double J[3];             /* Exchange couplings, J<0 == FM,  J>0 == AFM.      */
double Bx,By;	         /* applied magnetic field components.		     */
double *bx,*by;      	 /* demagnetization field components at sites.	     */
double Ka;	         /* anisotropy strength for aniso- boundaries.	     */
double dt;		 /* the time step for integration. 		     */
{
  int i,nbr1,nbr2,nbr3,nbr4;

  double KK=0.0;    /* boundary anisotropy term. */
  double gx,gy;     /* Hamiltonian XY field acting on a spin.  */
  double sigomega;  /* variance of the random velocity changes.	   */
  double dtI;	    /* time step divided by rotational inertia. */
  double dtg;	    /* time step multiplied by damping.  */
  double domega;    /* change in rotational speed of a site. */

  sigomega=sigtau*sqrt(dt)/Irot;
  dtI=dt/Irot;
  dtg=dt*gam;

  if(bc==2 || bc==3) KK=2.0*Ka; /* for anisotropic edge forces. */

  for(i=0; i<N; i++)
  {
    nbr1=nbrs[i][0];
    nbr2=nbrs[i][1];
    nbr3=nbrs[i][2];
    nbr4=nbrs[i][3];
    gx = sx[nbr1]+sx[nbr2]+sx[nbr3]+sx[nbr4];
    gy = sy[nbr1]+sy[nbr2]+sy[nbr3]+sy[nbr4];

    gx *= J[0];  gx -= Bx+bx[i];  /* applied and demagnetization fields. */
    gy *= J[1];  gy -= By+by[i];

/* include anisotropic boundary field for bc=2, open system + anisotropic edge. */
    if(bc==2)
    {  if(nbr1==N) gx += KK*sx[i];
       if(nbr2==N) gx += KK*sx[i];
       if(nbr3==N) gy += KK*sy[i];
       if(nbr4==N) gy += KK*sy[i];  }

    else if(bc==3) /* anisotropic terms only on longer edges. */
    {  if(nbr1==N && !longx) gx += KK*sx[i];
       if(nbr2==N && !longx) gx += KK*sx[i];
       if(nbr3==N && longx)  gy += KK*sy[i];
       if(nbr4==N && longx)  gy += KK*sy[i];  }

    domega = (gx*sy[i]-gy*sx[i])*dtI;  /* is multiplied by dt, divided by Irot. */

    /* if(i==1 || i==2) printf("torque: domega[%d]= %17.10e\n",i,domega); */

    if(gam>0.001)  /* include Langevin terms in acceleration equation. */
    { domega += sigomega*rangauss(1)-dtg*omega[i]; }

    omega[i] += domega;  /* advance the velocity forward one time step. */

  }
return;
} 

/******************************************************************************/
void  LangevinStep(N,x,y,nbrs,sx,sy,omega,J,Bx,By,bx,by,Ka,bc,kdt,dt,t,mx,my,mz)
/*
   Makes a number kdt of Verlet-like Langevin time steps for
   XY rotor spins, and redraws them in their new positions.
*/
int N,bc;            /* Number of sites in system,  boundary conditions. */
float *x,*y;         /* site locations, for spin drawing.                */
int **nbrs;          /* N x 4 nearest neighbor table.                    */
double *sx,*sy;      /* spin components.  				 */
double *omega;       /* sz is replaced by angular speed omega.  	 */
double J[3];         /* Exchange couplings, J<0 == FM,  J>0 == AFM.	 */
double Bx,By;	     /* applied magnetic field components.		 */
double *bx,*by;      /* demagnetization field components at sites.	 */
double Ka;	     /* anisotropy strength for aniso- boundaries.	 */
int kdt;             /* number of steps taken before re-drawing spins.   */
double dt; 	     /* the time integration step.			 */
double *t; 	     /* the current time.  (updated).			 */
double *mx,*my,*mz;  /* FM or AFM: magnetization (updated).		 */

{
  extern double *oldx, *oldy; /* previous spin state. */
  extern double *tmpx, *tmpy; /* intermediate state.  */
  double dt2;
  int i,idt;
  double r;

  dt2=0.5*dt;
  
  for(i=0; i<N; i++)  /* save old state, for erasing old state spins. */
  {
    oldx[i]=sx[i];
    oldy[i]=sy[i];
  }

  for(idt=0; idt<kdt; idt++)
  {

  /* First do a position update over half a time step. 
     The tmp arrays hold positions at t+0.5*dt.          */
     for(i=0; i<N; i++) 
     { 
       tmpx[i]=sx[i]-dt2*omega[i]*sy[i];
       tmpy[i]=sy[i]+dt2*omega[i]*sx[i];
     }

  /* Next is to find the new angular speeds on the sites, after 
     a time interval of dt. The outputted omega is omega(t+dt). */
     torque(N,nbrs,tmpx,tmpy,omega,J,Bx,By,bx,by,Ka,bc,dt);

  /* Let the positions propagate for another half time step. */
     for(i=0; i<N; i++)
     { 
       sx[i]=tmpx[i]-dt2*omega[i]*tmpy[i];
       sy[i]=tmpy[i]+dt2*omega[i]*tmpx[i];
     }
     
  /* loop to rescale to unit length. */
     for(i=0; i<N; i++) 
     { 
       r=1.0/sqrt(sx[i]*sx[i]+sy[i]*sy[i]);
       sx[i] *= r; 
       sy[i] *= r;
     }

  /* at this point, everything corresponds to the values at t+dt. */

     *t += dt;
  }
 
  /* loop to redraw the spin field. */

  *mx=*my=*mz=0.0;
  for(i=0; i<N; i++) 
  { 
    /* clear the spin before it is redrawn in new position. */
    spinarrow(win,gcclear,x[i],y[i],oldx[i],oldy[i],0.0);

    /* redraw the spin in its new position. */
    /* white line arrow heads for sz>0, blue open for sz<0. */
    if(omega[i]>=0.0) spinarrow(win,gcbw,  x[i],y[i],sx[i],sy[i],0.0);
       else        spinarrow(win,gcdeep,x[i],y[i],sx[i],sy[i],0.0);

    /* calculate the current magnetization, XY components only. */
    *mx += sx[i];
    *my += sy[i];
  }
  return;
}
