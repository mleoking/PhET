/******************************************************************/
double energy(Nsites,sx,sy,sz,nbrs,J,Bx,By,bx,by,mx,my,mz)
/*
   Calculates total system energy, magnetization.
*/
 int Nsites;
 int  **nbrs;
 double *sx,*sy,*sz;
 double J[3],Bx,By,*bx,*by,*mx,*my,*mz;
 {
   int i,nbr1,nbr2;
   double ex,ey,ez,ebx,eby,e;

   ex= ey= ez= ebx= eby= *mx= *my= *mz= 0.0;

   for(i=0; i<Nsites; i++)
   {
	nbr1=nbrs[i][0]; /* nbr in +x-direction. */
	nbr2=nbrs[i][2]; /* nbr in +y-direction. */

	ex += sx[i]*(sx[nbr1]+sx[nbr2]);  ebx -= sx[i]*(Bx+0.5*bx[i]);
	ey += sy[i]*(sy[nbr1]+sy[nbr2]);  eby -= sy[i]*(By+0.5*by[i]);

	/* KE for the rotor dynamics only. sz is omega[i]. */
	if(sz[i]!=0.0) ez += sz[i]*sz[i];  

	*mx += sx[i];  /* track only in-plane M. */
	*my += sy[i];

   }
   /* for MC, all sz[i]=0.0, which gives ez=0. */
   /* energy including the KE in dynamics is: */
   e = J[0]*ex+J[1]*ey+0.5*Irot*ez+ebx+eby; 

   return(e);
}
/******************************************************************/
void dipole_field(Nsites,sx,sy,xsrc,ysrc,xobs,yobs,D,bx1,by1)
/* 
   Calculates the total magnetic field (bx,by) due to all dipoles,
   as would be measured at an observer's position (xobs,yobs).		  
   D is a parameter related to the magnitudes of the dipoles,
   that determines the relative size of magnetic field they generate.
*/
 int Nsites;
 double *sx,*sy;
 float *xsrc,*ysrc;
 float xobs,yobs;
 double D,*bx1,*by1;
 {
   int j;
   double rx,ry,r,r2,r5;
   double dot,tx,ty,sumx,sumy;
   sumx=sumy=0.0;
   for(j=0; j<Nsites; j++)
   {
      /* all distances in units of a lattice constant. */
      rx=(double)(xobs-xsrc[j]);  ry=(double)(yobs-ysrc[j]);  r2=rx*rx+ry*ry;  
      if(r2<0.1) continue;  /*  avoids the i=j term.   */
      r=sqrt(r2);  r5=1.0/(r2*r2*r);
      dot=rx*sx[j]+ry*sy[j];
      tx=r5*(3.0*dot*rx-r2*sx[j]); /* term of one dipole. */
      ty=r5*(3.0*dot*ry-r2*sy[j]);
      sumx += tx;
      sumy += ty;
   }
   *bx1 = D*sumx;
   *by1 = D*sumy;
   return;
 }
/******************************************************************/
void get_demag_fields(Nsites,sx,sy,x,y,D,bx,by)
/*
   Calculates the dipole fields, arrays (bx,by), at the 
   positions of all the rotors.
*/
 int Nsites;
 double *sx,*sy;
 float *x,*y;
 double D,*bx,*by;
 {
   int i;
   for(i=0; i<Nsites; i++)
   {
      dipole_field(Nsites,sx,sy,x,y,x[i],y[i],D,&bx[i],&by[i]);
   }
   return;
 }
/******************************************************************/
double eground(nx,ny,Nsites,nbrs,x,y,J,D,Bx,By)
/* 
   Calculates the ground state energy for the dipole-rotor system.
   Assumes all dipoles are aligned with the long direction of the system
   and that the model has XY symmetry, J[0]=J[1].
   Assumes that the dipolar energy is larger than applied field energy,
   or at least, applied and demagnetization field are aligned.
*/
 int nx,ny,Nsites,**nbrs;
 float *x,*y;
 double J[3],D,Bx,By;
 {
   int i,j,bonds,Ns1;
   double *Sx,*Sy,*dipolex,*dipoley;  /* temp spin arrays. */
   double sign, exch, ebx, eby, egrnd;

   Ns1=Nsites+1;  
   /* for grnd state dipolar energy. */
   Sx=(double *) malloc( (size_t) Ns1*sizeof(double) );
   Sy=(double *) malloc( (size_t) Ns1*sizeof(double) );
   dipolex=(double *) malloc( (size_t) Ns1*sizeof(double) );
   dipoley=(double *) malloc( (size_t) Ns1*sizeof(double) );
   Sx[Nsites]=Sy[Nsites]=0.0; /* last site is unoccupied. */
   if(nx>=ny)  /* align spins along x-axis. */
   { if(Bx>=0.0) {sign=1.0;} else {sign=-1.0;}
     for(i=0; i<Nsites; i++) { Sx[i]=sign; Sy[i]=0.0; } }
   else        /* align spins along y-axis. */
   { if(By>=0.0) {sign=1.0;} else {sign=-1.0;}
     for(i=0; i<Nsites; i++) { Sx[i]=0.0; Sy[i]=sign; } }

   if(D>0.005) get_demag_fields(Nsites,Sx,Sy,x,y,D,dipolex,dipoley);
   else { for(i=0; i<Nsites; i++) {dipolex[i]=dipoley[i]=0.0;} }

   bonds=0;
   ebx=eby=0.0;
   for(i=0; i<Nsites; i++)
   {
      ebx -= Sx[i]*(Bx+0.5*dipolex[i]);
      eby -= Sy[i]*(By+0.5*dipoley[i]);
   /* ground state exchange energy needs total # of bonds. */
   /* this works for either periodic or free boundary conditions. */
      for(j=0; j<4; j++) { bonds += (nbrs[i][j]<Nsites)? 1: 0; }
   }
   bonds /= 2;  
   exch = -fabs(J[0])*(double)(bonds);
   egrnd = exch+ebx+eby;
   /* printf("eground: e0 = %f\n",egrnd); */
   free(Sx); free(Sy); free(dipolex); free(dipoley);
   return(egrnd);
}

float dx_field=2.0, dy_field=2.0;
int   n1, n2;
   
/**************************************************/
void ShowMagField(Nsites,nx,ny,Dext,x,y,sx,sy,hx,hy)
/*  
   Calculates the magnetic field caused by the dipoles, outside the magnet
   on a grid of points = npts.  Shows H as blue arrows.
*/
int Nsites,nx,ny;
float *x,*y;
double Dext,*sx,*sy,*hx,*hy;
{
   int i;
   static int field0=1;
   float x1,y1,xx,yy;
   double hh;

/* partition space into field grid but avoid inside the magnet.  */

   xx=0.0;  while(xx>xmin+1.0) xx -= dx_field;
   yy=0.0;  while(yy>ymin+1.0) yy -= dy_field;
   
   x1=xx+dx_field;  y1=yy+dy_field;  i=0;

   while(y1<ymax-1.0)
   {
     if(!(x1>-1. && x1<(float)(nx+2) && y1>-1. && y1<(float)(ny+2)))
     {
	if(field)
	{
          spinarrow(win,gcclear,x1,y1,hx[i],hy[i],-0.01);
          dipole_field(Nsites,sx,sy,x,y,x1,y1,Dext,&hx[i],&hy[i]); /* field at (x1,y1) */

	  if(field==2) /* direction vectors only. */
	  {  hh=1.0/sqrt(hx[i]*hx[i]+hy[i]*hy[i]);  
	     if(hh>0.0) { hx[i] *= hh;  hy[i] *= hh; } }

          spinarrow(win,gcdeep,x1,y1,hx[i],hy[i],-0.01);
	}
	else /* clear old arrows when control is changed. */
        if(field0) { spinarrow(win,gcclear,x1,y1,hx[i],hy[i],-0.01); }
	i++;
     }

     if((x1+dx_field)>=xmax-1.0) {x1=xx+dx_field;  y1 += dy_field;}
       else {x1 += dx_field;}
   }
   field0=field;
   return;
}
