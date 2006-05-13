/***********************************************************/
/*   Complex.h --- The C++ header for complex operations   */
/*   in Scientific Computing.      Li Ju. March.29,1995    */
/***********************************************************/

/*
** When compiling, please remember to link the math library -lm.  
*/

#include <math.h>
#include <iostream.h>
#include "complex.h"

Complex :: Complex(Dtype a, Dtype b)
{
  real = a;
  img  = b;
  return;
}

Complex :: Complex(Complex &a)
{
  real = a.real;
  img  = a.img;
  return;
}

Complex Complex :: operator+ (Complex &a)
{
  return (Complex(a.real+real,a.img+img));
}

Complex Complex :: operator+ (Dtype a)
{
  return (Complex(a+real,img));
}

/*   It's a friend */
Complex operator+ (Dtype a,Complex &b)
{
  return (Complex(a+b.real,b.img));
}

Complex Complex :: operator- (Complex &a)
{
  return (Complex(real-a.real,img-a.img));
}

Complex Complex :: operator- (Dtype a)
{
  return (Complex(real-a,img));
}

/* It's a friend */
Complex operator- (Dtype a, Complex &b)
{
  return (Complex(a-b.real,0.-b.img));
}

Complex Complex :: operator* (Complex &a)
{
  return (Complex(a.real*real-a.img*img,a.real*img+a.img*real));
}

Complex Complex :: operator* (Dtype a)
{
  return (Complex(real*a,img*a));
}

Complex operator* (Dtype a, Complex &b)
{
  return (Complex(b.real*a,b.img*a));
}

Complex Complex :: operator/ (Complex &a)
{
  Dtype q,g,h;
  q = a.real*a.real+a.img*a.img;
  g = real*a.real + img*a.img;
  h = img*a.real - real*a.img;
  return(Complex(g/q,h/q));
}
 
Complex Complex :: operator/ (Dtype a)
{
 return(Complex(real/a,img/a));
}

Complex operator/ (Dtype a, Complex &b)
{
 Dtype q;
 q = a/(b.real*b.real+b.img*b.img);
 return (Complex(q*b.real,-q*b.img));
}

Dtype  Complex :: Abs()
{
   return (sqrt(real*real+img*img));
}

Dtype Complex :: Arg()
{
   return (atan(img/real));
}

int Complex :: operator == (Complex &a)
{
  if ((real==a.real)&&(img==a.img)) return(1);
    else return(0);
}

int Complex :: operator == (Dtype a)
{
  if ((real==a)&&(img==0.)) return(1);
   else return(0);
}

int operator == (Dtype a, Complex &b)
{
  if ((a==b.real)&&(b.img==0.)) return(1);
   else return(0);
}

int Complex :: operator != (Complex &a)
{
  if ((real==a.real)&&(img==a.img)) return(0);
    else return(1);
}

int Complex :: operator != (Dtype a)
{
  if ((real==a)&&(img==0.)) return(0);
   else return(1);
}

int operator != (Dtype a, Complex &b)
{
  if ((a==b.real)&&(b.img==0.)) return(0);
   else return(1);
}

ostream & operator << (ostream &a,Complex &b)
{
 return (a<<b.real<<"+i"<<b.img<<'\n');
}

istream & operator >> (istream &a,Complex &b)
{
 return (a>>b.real>>b.img);
}

