#include <stddef.h>
extern double TWOPI;

int Wolff(N,x,y,nbrs,sx,sy,sz,ClSites,virgin,Jxy,beta,energy,mx,my)
/*
    Makes a single cluster in the XY-symmetry system according to the
    Wolff algorithm, and "flips" it. Does not change sz, which must 
    be done by other type of move.  Assumes equal coupling jx=jy=Jxy.
*/
int N;		     /* Number of sites in system. 			 */
float *x,*y;         /* site locations, for spin drawing. 		 */
int **nbrs;          /* N x 4 nearest neighbor table. 			 */
double Jxy;          /*  in-plane exchange, Jxy<0 == FM,  Jxy>0 == AFM.  */
double *sx,*sy,*sz;  /* spin components.		 		 */
double beta;         /* inverse temperature. 				 */
double *energy;      /* total system instantaneous energy. 		 */
double *mx,*my;      /* FM: magnetization.  AFM: staggared magnetization.*/
int *ClSites;        /*  Ordered array of cluster members.	*/
int *virgin;         /*  1 indicates site not yet included into a cluster. 
		           This array must be initialized to N 1's. */
{
  double phi;
  double s1x,s1y;    /*  a site's neighbor.	*/
  double rx,ry;      /*  random reflection axis.	*/
  double project;    /*  projection of spin onto (rx,ry).	*/
  double dsx,dsy;    /*  increments of the spin.	*/
  double act,dE;     /*  update probability, energy increment.	*/
  int Nupdated;      /*  The number of sites changed so far by cluster flips.	*/
                     /*      the subroutine returns when Nupdated >= N/4.	*/
  int NCl;	     /*  Number of clusters formed on this call.  */
  int NS;            /*  The number of sites in cluster being identified.	*/
  int kn,nbr;        /*  a site's neighbor index.  */
  int i,j,j0;        /* site indeces. */
  double betaJxy;    /* coupling scaled by temperature. */
  double delE;       /* net scaled energy change. */

/* Wolff scheme:
 
   Start from a randomly chosen site. "Flip" that site by the Wolff 
   reflection operation along a randomly chosen axis.  Then decide whether 
   to link it to its neighbors according to the following 
   rules, which apply to all sites that are included into the cluster.
 
   The site that was flipped has changes (dsx,dsy).  Use the scalar
   product of this change with a neighboring site's spin to calculate
   a change in energy dE=E(nolink)-E(link).  dE represents the difference in final 
   energy for that bond of the lattice depending whether there is a
   link placed there or not.  The decision to put NOLINK there follows
   a usual Metropolis-like rule:  
 
   if dE<0, place NOLINK on this bond.  
   else
     if dE>0, place NOLINK there with probability exp(-beta*dE). 
              (this is equivalent to a LINK with probability 1-exp(-beta*dE).
 
   If a link is created, then also flip that site.  Continue to apply
   these operations to all neighboring sites of those sites in the
   cluster, until the cluster grows no more.  The entire cluster that
   was formed then has already been completely flipped.
 
   Also, one can note that summing all of all the calculated increments dE
   whether a link is placed or not, will result in the correct energy of 
   the final state.  
 
   Final state has one cluster formed, with al sites in it having had the
   same reflection operation applied to them.

   Make a set of cluster moves, and keep track of total number of sites
   changed by all cluster moves so far, Nupdated.  Return when Nupdated >= N/4.  */ 

 delE=0.0;           /* keeps track of net system energy change.     */
 betaJxy=beta*Jxy;   /* scale by beta here to remove multiplications 
                        from inside the Metropolis acceptance test.  */
 Nupdated=NCl=0;
 while(Nupdated<(N>>2))
 {
/* Generate the axis (rx,ry) along which all spins in cluster will be inverted.
   It is chosen randomly within the xy-plane.  */ 
   phi=TWOPI*ran1(1);
   rx=cos(phi);
   ry=sin(phi);
   NS=0;    /* counter for number of sites in new cluster. */
   j0=((int)((double)N*ran1(1)))%N;  /* random origin of new cluster. */

/* Identify the cluster being formed.  */
   i=0;
   ClSites[NS]=j0;     /* first member of cluster is stored. */
   virgin[j0]=0;       /* mark the site as already used. */
   NS++;

/* loop to test each site of cluster. */
   while(i<NS)
   {                   /* i labels which member of the cluster is	*/ 
                       /*   having its neighbors tested for inclusion.	*/ 
     j=ClSites[i++];

     /* clear the spin before it is redrawn in new position. */
     spinarrow(win,gcclear,x[j],y[j],sx[j],sy[j],sz[j]);

     project=rx*sx[j]+ry*sy[j]; /* apply reflection operation to  	*/
     project=project+project;   /*  this spin, and save its changes  	*/
     dsx=-(project*rx);         /*  for energy difference calculations. */
     dsy=-(project*ry);
     sx[j] += dsx;              /* apply the reflection already. 	*/
     sy[j] += dsy;
     *mx += dsx; 
     *my += dsy; 
                       
     /* redraw the spin in its new position. */
     /* white line arrow heads for sz>0, blue open for sz<0. */
     if(sz[j]>=0.0) spinarrow(win,gcbw  ,x[j],y[j],sx[j],sy[j],sz[j]);
        else        spinarrow(win,gcdeep,x[j],y[j],sx[j],sy[j],sz[j]);

/* loop over nearest neighbors of site being tested. */
     for(kn=0; kn<4; kn++) 
     {
       nbr=nbrs[j][kn];   /* kn = (0,1,2,3) = (right,left,above,below). */
       s1x=sx[nbr];
       s1y=sy[nbr];
       dE=(betaJxy*(dsx*s1x+dsy*s1y)); /* scaled energy diff. dE=beta*(E(nolink)-E(link)) */
       delE += dE;                     /* include scaled energy differences... */
                                       /* those on links formed will cancel out. */
       if(virgin[nbr]) 
       {
/*
  dE is the energy change if we do NOT put a link between site j and
  its neighbor being currently tested.  When dE < 0, it means that NOT
  putting a link on this bond always will make the energy go down, which
  is always acceptable according to usual Metropolis rules.
  When dE > 0, then the probability to put NoLink there is exp(-beta*dE).,
  and therefore, the probability to put a link there is 1-exp(-beta*dE).
  Thus acceptance probability is act=1-exp(-(beta*dE))-ran().
  Note that 1-ran() is same as another ran().
*/
         if(dE>0.0) 
	 {
           act=ran1(1)-exp(-dE);
           if(act>0.0) 
	   {
             ClSites[NS]=nbr;    /* include site nbr into cluster. */
             virgin[nbr]=0;      /* site loses its virginity. */
             NS++;               /* number of sites in cluster increases. */
           }  /* end acceptance. */
         }  /* end energy test. */
       } /* end virgin test. */
     } /* end for(kn)  all neighbors of cluster member at site j */
  } /* end while(i<=NS)  Done finding the current cluster. */

  /* re-vive the virgins before next cluster build. */
  for(i=0; i<NS; i++) { j=ClSites[i]; virgin[j]=1; }   
  NCl++;
  Nupdated += NS;
 }
 *energy += delE/beta;  /* delE is (beta x total dE). */
 return(NCl);
}
