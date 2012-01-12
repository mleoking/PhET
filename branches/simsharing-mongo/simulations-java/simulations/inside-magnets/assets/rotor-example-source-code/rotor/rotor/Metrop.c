
#include <math.h>

double ran1();

/********************************************************************/
int lookvort(N,i1,nbrs,phi)
/* 
   Looks at plaquette whose lower left corner is site i1
   to see if there is a vortex there.
*/
 int N,i1,**nbrs;
 double *phi;
 {
   int i2,i3,i4;
   double dphi;
   int vorticity;

   /* loop in xy-plane: */
   i2=nbrs[i1][0]; /* nbr in x-direction.   */
   i3=nbrs[i2][2]; /* nbr in x+y-direction. */
   i4=nbrs[i1][2]; /* nbr in y-direction.   */
   if(i2==N || i3==N || i4==N) dphi=0.0;
   else
     dphi=deltaphi(phi[i1],phi[i2])
          +deltaphi(phi[i2],phi[i3])
          +deltaphi(phi[i3],phi[i4])
          +deltaphi(phi[i4],phi[i1]);
   if(dphi>0.5) vorticity=VORTEX;
   else if(dphi<-0.5) vorticity=ANTIVORTEX;
        else          vorticity=0;
   
   return(vorticity);
}

/********************************************************************/
int Metrop(N,x,y,nbrs,phase,sx,sy,sz,phi,vort,
	   J,Bx,By,bx,by,Ka,bc,rt,sinc,e,mx,my,mz,nv)
int N,bc;
float *x,*y;
int **nbrs,*phase,*vort,*nv;
double *sx,*sy,*sz,*phi,J[3],Bx,By,*bx,*by,Ka,rt,sinc,*e,*mx,*my,*mz;
{
/*
   Makes a single Metropolis Monte Carlo pass 
   using single spin"-flip" moves, and redraws
   each spin as it is updated.  J[3]<0 for FM.
   Includes B-field along x only.
*/
   extern double ONEPI,TWOPI;
   int kyes,i,isite,j,jp,nbr1,nbr2,nbr3,nbr4,np[4];
   double realN,x1,y1,xn,yn,angle,ss;
   double sxn,syn,dsx,dsy,dek,de,act;

   float X1,Y1,size=0.5;
   int v,dv; /* nbrs vortex occupations and changes. */

   kyes=0;
   realN=(double)N;
   for(i=0; i<N; i++)
   {
      isite=(int)(realN*ran1(1))%N;
/*
    use method of adding small increments to spins, in XY plane only
*/
      angle=TWOPI*ran1(1);
      y1=sin(angle);
      x1=cos(angle);

      xn=sx[isite]+sinc*x1;
      yn=sy[isite]+sinc*y1;

      ss=1.0/sqrt(xn*xn+yn*yn);
      sxn=ss*xn;
      syn=ss*yn;
/*
    compute delta-e for the site.
*/
      dsx=sxn-sx[isite];
      dsy=syn-sy[isite];

      nbr1=nbrs[isite][0];  np[0]=isite;  /* neighboring plaquettes. */
      nbr2=nbrs[isite][1];  np[1]=nbr2;
      nbr3=nbrs[isite][2];  np[2]=nbrs[nbr2][3];
      nbr4=nbrs[isite][3];  np[3]=nbr4;
/*
   expressions including a uniform magnetic field, and with jx1=jx2, etc. !!!
   Note that J<0 is FM coupling, J>0 is AFM coupling. 
*/
      de=( dsx*(J[0]*(sx[nbr1]+sx[nbr2]+sx[nbr3]+sx[nbr4])-Bx-bx[isite])
          +dsy*(J[1]*(sy[nbr1]+sy[nbr2]+sy[nbr3]+sy[nbr4])-By-by[isite]) );
/* 
   anisotropic boundary condition for spins following the boundary
*/
      dek=0.0;
      if(bc==2)
      {  if(nbr1==N) dek += Ka*(sxn*sxn-sx[isite]*sx[isite]);
	 if(nbr2==N) dek += Ka*(sxn*sxn-sx[isite]*sx[isite]);
         if(nbr3==N) dek += Ka*(syn*syn-sy[isite]*sy[isite]);
         if(nbr4==N) dek += Ka*(syn*syn-sy[isite]*sy[isite]);  }

      else if(bc==3) /* anisotropic term only on longer edge. */
      {  if(nbr1==N && !longx) dek += Ka*(sxn*sxn-sx[isite]*sx[isite]);
	 if(nbr2==N && !longx) dek += Ka*(sxn*sxn-sx[isite]*sx[isite]);
         if(nbr3==N && longx) dek += Ka*(syn*syn-sy[isite]*sy[isite]);
         if(nbr4==N && longx) dek += Ka*(syn*syn-sy[isite]*sy[isite]);  }

/*
    compute probability of flipping the site...
    include dek here for decision, but not as part of system energy.
*/
      act=1.0;
      if((de+dek)>0.0) act=exp(-rt*(de+dek))-ran1(1);
      if(act>0.0)
/*
    perform appropriate updating of energy, spin  and magnetization:
*/
      {
      	 /* printf("\nisite=%d updated",isite); */
	 /* clear the spin before it is redrawn in new position. */
	 /* white line arrow heads for sz>0, blue open for sz<0. */
         spinarrow(win,gcclear,x[isite],y[isite],sx[isite],sy[isite],0.0);


         /* update the spin and other quantities. */
         kyes++;
         *e += de;
         *mx += dsx;
         *my += dsy;

         sx[isite]=sxn;
         sy[isite]=syn;

	 /* calculate new in-plane angle. */
	 phi[isite]=atan2(syn,sxn)+ONEPI*(double)((1-phase[isite])>>1);

	 /* redraw the spin in its new position. */
	 /* white line arrow heads for sz>0, blue open for sz<0. */
	 if(sz[isite]>=0.0) spinarrow(win,gcbw,  x[isite],y[isite],sx[isite],sy[isite],0.0);
	     else	    spinarrow(win,gcdeep,x[isite],y[isite],sx[isite],sy[isite],0.0);


         /* check whether any vortices have been created or destroyed
            in the neighboring unit cells.			*/
         for(jp=0; jp<4; jp++)
         {
           j=np[jp];
	   v=lookvort(N,j,nbrs,phi);
   	   /* printf("\n%d: loop[%d,%d,%d,%d]: dphi[%d]=%f  :",jp,j,j2,j3,j4,j,dphi); */
           /* printf("\n phi's = %f, %f, %f, %f",phi[j],phi[j2],phi[j3],phi[j4]); */
           dv=v-vort[j]; /* change in vorticity. */
           /* printf("\n    old v=%d, new v=%d dv=%d",vort[j],v,dv); */
           if(dv)
           { 
             *nv += dv%2; /* update vorticity and picture. */
	     vort[j]=v; 
             map_coords(x[j]+0.5,y[j]+0.5,&X1,&Y1);
             if(dv>=2) 
               draw_plus(win, gcbrt, X1, Y1, size*XScale, size*YScale);
             else if(dv==1)
               draw_minus(win, gcbrt, X1, Y1, size*XScale);
             else if(dv==-1)
               draw_minus(win, gcclear, X1, Y1, size*XScale);
             else if(dv<=-2)
             {   
               draw_plus(win, gcclear, X1, Y1, size*XScale, size*YScale);
               if(dv==-2) draw_minus(win, gcbrt, X1, Y1, size*XScale);
             }
           } /* end dv test. */
         } /* end plaquette checking */
      }  /* end site update. */
   } /* loop over sites. */
   return kyes;
} /* end step.c */

/*********************************************/
void averages(nsamp,Nsites,beta,e,M,C,X)
int nsamp,Nsites;
double beta,e,M;
double *C,*X;
{
  static double esum,e2sum,Msum,M2sum;
  double eave,e2ave,Mave,M2ave,r,rN;

  if(nsamp==1) { esum=e2sum=Msum=M2sum=0.0; }
  esum += e; e2sum += e*e; Msum += M; M2sum += M*M;

  r=1.0/(double)nsamp;   rN=1.0/(double)Nsites;
  eave=r*esum; e2ave=r*e2sum; Mave=r*Msum; M2ave=r*M2sum;
  *C=beta*(beta*rN)*(e2ave-eave*eave);
  *X=(beta*rN)*(M2ave-Mave*Mave);

  return;
}
