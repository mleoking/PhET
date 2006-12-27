/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */
/*********************************************************/

#include <iostream.h>
#include <math.h>
#include <stdio.h>
#include "complex.h"
/* The complex operation packet */

#define XMESH 500    /* # of X intevals from 0-1 */
#define YMESH 500    /* # of Y intevals from 0-1 */
#define TAU  2E-6    /* Time inteval             */
#define PI   3.14159265457

float K,Time;
int   LAMBDA;
long  Steps;

/*
** Initialize the wave pack.
*/
void InitWave(Complex w[XMESH+1][YMESH+1])
{
  int i,j;
  for (i=0;i<=XMESH;i++)
    for (j=0;j<=YMESH;j++)
      w[i][j] = Complex(cos(K*i/XMESH),sin(K*i/XMESH));
}

/*
** Potential function
*/
float V(int i,int j)
{
  if (Time < 300*TAU) return(0);
  if ((i-250)*(i-250)+(j-250)*(j-250)<2500)
    return (1E4);
  else return(0.);
}

/* 
** Cayley propagator.
*/
void Cayley(Complex w[XMESH+1][YMESH+1])
{
 static Complex alpha[XMESH+YMESH],beta[XMESH+YMESH],gamma[XMESH+YMESH];
 Complex XAP(0,-0.5*XMESH*XMESH*TAU);
 Complex XA0,XA00(1,XMESH*XMESH*TAU),XA0V(0,0.5*TAU);
 Complex YAP(0,-0.5*YMESH*YMESH*TAU);
 Complex YA0,YA00(1,YMESH*YMESH*TAU),YA0V(0,0.5*TAU);
 Complex bi,bj;

 int i,j,N;

 for (j=1;j<YMESH;j++)
   {
     N = XMESH;
     alpha[N-1] = 0;
     beta[N-1]  = Complex(cos(K-K*K*Time),sin(K-K*K*Time));
 
     for (i=N-1;i>=1;i--)
       {
	 XA0 = XA00+XA0V*V(i,j)/2.;
	 bi = (2-XA0)*w[i][j]-XAP*(w[i-1][j]+w[i+1][j]);
	 gamma[i] = -1/(XA0+XAP*alpha[i]);
	 alpha[i-1] = gamma[i]*XAP;
	 beta[i-1]  = gamma[i]*(XAP*beta[i]-bi);
       }

     w[0][j] = Complex(cos(K*K*Time),-sin(K*K*Time));
     for (i=0;i<=N-1;i++)
       w[i+1][j] = alpha[i]*w[i][j]+beta[i];
   }
 
 for (i=1;i<XMESH;i++)
   {
     N = YMESH;
     alpha[N-1] = 0;
     beta[N-1]  = Complex(cos(K*i/XMESH-K*K*Time),sin(K*i/XMESH-K*K*Time));
 
     for (j=N-1;j>=1;j--)
       {
	 YA0 = YA00+YA0V*V(i,j)/2.;
	 bj = (2-YA0)*w[i][j]-YAP*(w[i][j-1]+w[i][j+1]);
	 gamma[j] = -1/(YA0+YAP*alpha[j]);
	 alpha[j-1] = gamma[j]*YAP;
	 beta[j-1]  = gamma[j]*(YAP*beta[j]-bj);
       }

     w[i][0] = Complex(cos(K*i/XMESH-K*K*Time),sin(K*i/XMESH-K*K*Time));
     for (j=0;j<=N-1;j++)
       w[i][j+1] = alpha[j]*w[i][j]+beta[j];
   }
}

int main()
{
	cout<<"Hello!"<<endl;
  FILE * OUTPUT1;
  long Shot,i,ii,j;
  char filename[30];
  Complex w[XMESH+1][YMESH+1];
  /* w[][] is where the wave function is kept. */
  
  Steps = 1500;
  Shot  = 30;
  Time  = 0.;
  
  K = 10*PI;
  LAMBDA = (int) 2*PI/K*XMESH;

  InitWave(w);
  
  for (i=0;i<=Steps;i++)
    {
      printf ("Running to Step %d\n",i);
      if (i%Shot==0)
	{
	  j = i/Shot;
	  sprintf (filename,"a%d.out",j);
	  OUTPUT1 = fopen(filename,"w");

	  for (j=0;j<=YMESH;j+=5)
	    {
	      for (ii=0;ii<=XMESH;ii+=5)
		{
		  fprintf (OUTPUT1,"%f ",w[ii][j].Abs());
		}
	      fprintf (OUTPUT1,"\n");
	    }
	  fclose (OUTPUT1);
	}
      Time += TAU;
      Cayley(w);
    }
  return(1);
}	 
