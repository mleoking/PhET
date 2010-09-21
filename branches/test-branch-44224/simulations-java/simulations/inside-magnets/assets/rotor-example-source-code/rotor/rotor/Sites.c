#include <stdio.h>

/***********************/
int isite(ix,iy,nx,ny)
int ix,iy,nx;
{
   /* Returns nx*ny for a nonexistent or unoccupied site. */
   /* The occupied sites range from 0 to nx*ny-1.         */
   if(ix<0 || ix>=nx || iy<0 || iy>=ny) return(nx*ny);
   else return (nx*iy+ix);
}
/***********************/


/*******************************************************************/
void Sites(bc,nx,ny,x,y,nbrs)
int bc;		/* type of boundary condition.	*/
int nx,ny;	/* system size.			*/
float *x,*y;    /* lattice positions returned.  */
int **nbrs;	/* nbrs[N][4] nn table.		*/
{
   int ix,iy,I;
   int ixp,ixm,iyp,iym;
/* 
    Computes the nearest neighbors table.
    Assumes square lattice system, various boundary conditions.
    Sites are stored in 1D array as: I = nx*iy+ix.
 
    The neighbors are ordered as ixp,ixm,iyp,iym,izp,izm 
    being nbrs(1,I), nbrs(2,I), nbrs(3,I), nbrs(4,I), nbrs(5,I), nbrs(6,I).
*/
   if(bc==0) /* periodic boundary conditions. */
   {
     for(iy=0; iy<ny; iy++)
     {
        iyp=(iy==ny-1)? 0: iy+1;
        iym=(iy==0)? ny-1: iy-1;
        for(ix=0; ix<nx; ix++)
        {
           ixp=(ix==nx-1)? 0: ix+1;
           ixm=(ix==0)? nx-1: ix-1;

           I = isite(ix,iy,nx,ny);
           x[I]=(float)(ix+1);
           y[I]=(float)(iy+1);
           nbrs[I][0]=isite(ixp,iy,nx,ny); 
           nbrs[I][1]=isite(ixm,iy,nx,ny); 
           nbrs[I][2]=isite(ix,iyp,nx,ny);
           nbrs[I][3]=isite(ix,iym,nx,ny);
        }
     }
   }

   /* bc=1 is open system and free bc.  bc=2 is open with anisotropic boundary energy.  */
   else if(bc==1 || bc==2 || bc==3) /* open bc. "missing" boundary sites around the real system. */
   {
     for(iy=0; iy<ny; iy++)
     {
        iyp=iy+1;
        iym=iy-1;
        for(ix=0; ix<nx; ix++)
        {
           ixp=ix+1;
           ixm=ix-1;
           I = isite(ix,iy,nx,ny);
           x[I]=(float)(ix+1);
           y[I]=(float)(iy+1);
           nbrs[I][0]=isite(ixp,iy,nx,ny); 
           nbrs[I][1]=isite(ixm,iy,nx,ny); 
           nbrs[I][2]=isite(ix,iyp,nx,ny);
           nbrs[I][3]=isite(ix,iym,nx,ny);
        }
     } 
   }

   /* nbrs of the zeroed site are also zeroes.  */
   I=nx*ny; nbrs[I][0]=nbrs[I][1]=nbrs[I][2]=nbrs[I][3]=I;

return;
}

/*******************************************************************/
void Phases(nx,ny,kex,phase)
/*
   Generates table of sublattice phases, assuming nx,ny are even.
*/
int nx,ny;      /* system size.                 */
int kex;        /* kex=-1 FM, +1 AFM.           */
int *phase;     /* sublattices[N] for AFM.      */
{
 int ix,iy,I;

 if(kex==1) /* AFM */
 {
   for(iy=0; iy<ny; iy++)
      for(ix=0; ix<nx; ix++) 
      { 
        I = isite(ix,iy,nx,ny);
	phase[I]=2*((ix+iy)%2)-1; 
      }
  }
  else if(kex==-1) /* FM */
  {
      for(ix=0; ix<(nx*ny); ix++) { phase[ix]=1; }
  }
  return;
}
