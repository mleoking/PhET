
/*----------------------------------------------------------------------
   from testarro.c--Draws spin arrow plots a la wavefunc.pro idl program.
   Takes the raw values of Xi and plots them as arrows.                */

void draw_arrow(window, gc, x1, y1, x2, y2, sz, form)
/*    subroutine for drawing arrows    */
Window window; 
GC gc; 
float x1,y1;   /* tail point of arrow, pixel coords.  */
float x2,y2;   /* head point of arrow, pixel coords.  */
float sz;      /* choose color of arrow (turned off). */
int form;      /* 0==open head, 1==closed head.       */
{
  int icolor;
  float head=0.40;
  float headb=0.60;
  float xlong,ylong;
  float xa,ya,vxa,vya;
  float xb,yb,vxb,vyb;
  if(sz >  1.0) sz -= 2.0;
  if(sz < -1.0) sz += 2.0;
  icolor=(int) (15.0*(sz+1.0)/2.0);   /*  colors from pallette here  */
/*  setcolor(icolor);  */

  if (form == 1)   /* line arrow.  */
  {
    XDrawLine(display,window,gc,(int)(x1),(int)(y1),(int)(x2),(int)(y2));
  }
  else    /*   shorter line, open head for form==0.   */
  {
    xa=head*x1+headb*x2;
    ya=head*y1+headb*y2;
    XDrawLine(display,window,gc,(int)(x1),(int)(y1),(int)(xa),(int)(ya));
  }
  xlong=x2-x1;    /* rotate arrow to correct orientation  */
  ylong=y2-y1;
  xa=-head;
  ya=0.5*head;
  vxa=xa*xlong-ya*ylong;
  vya=xa*ylong+ya*xlong;
  XDrawLine(display,window,gc,(int)(x2),(int)(y2),(int)(x2+vxa),(int)(y2+vya));
/*---------------------------*/
  xb=-head;
  yb=-0.5*head;
  vxb=xb*xlong-yb*ylong;
  vyb=xb*ylong+yb*xlong;
  XDrawLine(display,window,gc,(int)(x2),(int)(y2),(int)(x2+vxb),(int)(y2+vyb));
/*---------------------------*/
 if (form == 0)
 {
/*   shorter line, open head for form = 0.   */
  xa=head*x1+headb*x2;
  ya=head*y1+headb*y2;
  XDrawLine(display,window,gc,(int)x1,(int)y1,(int)(xa),(int)(ya));
  XDrawLine(display,window,gc,(int)(x2+vxa),(int)(y2+vya),(int)(x2+vxb),(int)(y2+vyb));
 }
  return;
}

/******************************************************/
void map_coords(x,y,X,Y)
float  x, y;  /* physical coordinates. */
float *X,*Y;  /*    pixel coordinates. */
/*  Takes coordinates (x,y) which should be from xmin to xmax, and maps
    them into pixel coordinates (X,Y) ranging appropriately inside the window.  */
{
  *X=X0+(x-xmin)*XScale;
  *Y=Y0-(y-ymin)*YScale;
  return;
}



/******************************************************/
void spinarrow(window,gc,x,y,sx,sy,sz)
Window window; 
GC gc; 
float x,y;        /* lattice coordinates */
double sx,sy,sz;  /* spin components     */

/*  Calculates pixels, draws a spin arrow in desired coord system. */
{
  extern int View;    /* spin component projected towards the viewer. */
  float s1,s2,s3;     /* rotated spin components.		      */
  float x1,y1,x2,y2;  /* physical coordinates for the lattice spins.  */
  float X1,Y1,X2,Y2;  /* pixel coords for arrows and other objects.   */
  float dx,dy;
  int form;

  if(View==1) {s1=(float)sy; s2=(float)sz; s3=(float)sx;}  /* look towards sx  */
         else {s1=(float)sx; s2=(float)sy; s3=(float)sz;}  /* look towards sz  */

  dx=0.5*s1;
  dy=0.5*s2;
  x1=x-dx; y1=y-dy; 
  x2=x+dx; y2=y+dy;
  /* line arrow heads for s3>=0, open for s3<0. (original form).  */
  /* form=(int)(s3+1.0);  */
  /* line arrow heads for s3<0, open for s3>=0. (new version). */
  form=1-(int)(s3+1.0); 
  map_coords(x1,y1,&X1,&Y1);
  map_coords(x2,y2,&X2,&Y2);
  draw_arrow(window,gc,X1,Y1,X2,Y2,s3,form);
}
