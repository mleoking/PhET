/*************************/
double deltaphi(phi1,phi2)
double phi1,phi2;
{
   extern double ONEPI,TWOPI;
   double delta;

   delta=phi2-phi1;
   if(delta>ONEPI) delta -= TWOPI;
   else if(delta<-ONEPI) delta += TWOPI;
   return(delta);
}

/*******************************************************************/
void draw_plus(window, gc, x, y, xsize, ysize)
/* Draws a plus sign of width and height = size, at position (x,y) */
Window window; 
GC gc; 
float x,y,xsize,ysize;
{
   int x1,x2,y1,y2,dx,dy;
   extern int DrawVorts;
   
   if(DrawVorts) 
   {
   dx=((int)xsize)>>1;
   x1=(int)x-dx; x2=(int)x+dx; y1=y2=(int)y;
   XDrawLine(display,window,gc,x1,y1,x2,y2);
   dy=((int)ysize)>>1;
   x1=x2=(int)x; y1=(int)y-dy; y2=(int)y+dy;
   XDrawLine(display,window,gc,x1,y1,x2,y2);
   }
   return;
}


/********************************************************************/
void draw_minus(window, gc, x, y, size)
/* Draws a minus sign of width and height = size, at position (x,y) */
Window window; 
GC gc; 
float x,y,size;
{
   int x1,x2,y1,y2,dx;
   extern int DrawVorts;
   
   if(DrawVorts) 
   {
   dx=((int)size)>>1;
   x1=(int)x-dx; x2=(int)x+dx; y1=y2=(int)y;
   XDrawLine(display,window,gc,x1,y1,x2,y2);
   }
   return;
}


/*************************************************/
int vcount(Nsites,x,y,nbrs,phase,sx,sy,sz,phi,vort)
int Nsites;
float *x,*y;
int  **nbrs,*phase,*vort;
double *sx,*sy,*sz,*phi;
{
    int i1,i2,i3,i4,nv;
    double dphi;
    float X1,Y1,size=0.5;
/*   
    Computes the number of vortices plus antivortices 
    for an XY model on a 2D lattice and draws them.
    Assumes periodic boundary conditions in all directions.
    nv = number of vortices+antivortices found.
*/
 
/*  ROTATE ODD SUBLATTICE SPINS THRU PI FOR ANTIFERROMAGNETS */
    for(i1=0; i1<Nsites; i1++) phi[i1]=atan2(sy[i1],sx[i1])
                                   +ONEPI*(double)((1-phase[i1])>>1);
    nv=0;

    for(i1=0; i1<Nsites; i1++)
    {
        /* loop in xy-plane: */
        i2=nbrs[i1][0]; /* nbr in x-direction.   */
        i3=nbrs[i2][2]; /* nbr in x+y-direction. */
        i4=nbrs[i1][2]; /* nbr in y-direction.   */
	/* printf("%d: (x,y)=(%f, %f),  i2=%d, i3=%d, i4=%d\n",i1,x[i1],y[i1],i2,i3,i4); */
        if(i2==Nsites || i3==Nsites || i4==Nsites) 
	   dphi=0.0;
	else
           dphi=deltaphi(phi[i1],phi[i2])
               +deltaphi(phi[i2],phi[i3])
               +deltaphi(phi[i3],phi[i4])
               +deltaphi(phi[i4],phi[i1]);

	/* erase anything already there. */
	map_coords(x[i1]+0.5,y[i1]+0.5,&X1,&Y1);
	draw_plus(win, gcclear, X1, Y1, size*XScale, size*YScale);

        if(dphi>0.5)
        {  nv++;
	   vort[i1]=VORTEX;
	   map_coords(x[i1]+0.5,y[i1]+0.5,&X1,&Y1);
	   draw_plus(win, gcbrt, X1, Y1, size*XScale, size*YScale);
        } 
        else if (dphi<-0.5)
        {  nv++;
	   vort[i1]=ANTIVORTEX;
	   map_coords(x[i1]+0.5,y[i1]+0.5,&X1,&Y1);
	   draw_minus(win, gcbrt, X1, Y1, size*XScale);
        }
        else
           vort[i1]=0;
    }
    return(nv);
}

