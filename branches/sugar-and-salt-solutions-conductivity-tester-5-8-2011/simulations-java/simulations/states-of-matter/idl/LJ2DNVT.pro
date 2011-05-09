;n particle Lennard-Jones in 2D box with WCA boundaries


;wall potential: WCA

function phiwall,x
 result=0.0d0*x
 w=where( abs(x) lt 1.122462048309373017d0)
 if w[0] ge 0 then result[w]=4.0d0/x[w]^12-4.0d0/x[w]^6+1.0d0
 return, result
end

;function phi2wall,rsqr
; result=0.0d0*rsqr
; w=where( rsqr gt 0.01d0 and rsqr lt 1.259921049894873191d0)
; if w[0] ge 0 then result[w]=4.0d0/rsqr[w]^6-4.0d0/rsqr[w]^3+1.0d0
; return, result
;end


function dphidrwall,x
 result=0.0d0*x
 w=where( abs(x) lt 1.122462048309373017d0)
 if w[0] ge 0 then result[w]=24.0d0/x[w]^7-48.0d0/x[w]^13
 return, result
end

;function dphidrrwall,rsqr
; result=0.0d0*rsqr
; w=where( rsqr gt 0.01d0 and rsqr lt  1.122462048309373017d0)
; if w[0] ge 0 then result[w]=24.0d0/rsqr[w]^4-48.0d0/rsqr[w]^7
; return, result
;end


;pair potential between particles: LJ


;function phi,x
; result=0.0d0*x
; w=where( abs(x) lt 2.5d0 and abs(x) gt 0.1d0)
; if w[0] ge 0 then result[w]=4.0d0/x[w]^12-4.0d0/x[w]^6+0.016316891136d0
; return, result
;end

;function phi2,rsqr
; result=0.0d0*rsqr
; w=where( rsqr gt 0.01d0 and rsqr lt 6.25d0)
; if w[0] ge 0 then result[w]=4.0d0/rsqr[w]^6-4.0d0/rsqr[w]^3+0.016316891136d0
; return, result
;end


;function dphidr,x
; result=0.0d0*x
; w=where( abs(x) lt 2.5d0 and abs(x) gt 0.1d0)
; if w[0] ge 0 then result[w]=24.0d0/x[w]^7-48.0d0/x[w]^13
; return, result
;end

;function dphidrr,rsqr
; result=0.0d0*rsqr
; w=where( rsqr gt 0.01d0 and rsqr lt  6.25d0)
; if w[0] ge 0 then result[w]=24.0d0/rsqr[w]^4-48.0d0/rsqr[w]^7
; return, result
;end

;function forcer,rsqr
; result=dblarr(n_elements(rsqr))
; r2=result
; r6=result
; w=where( rsqr gt 0.01d0 and rsqr lt  6.25d0)
; if w[0] ge 0 then r2[w]=1.0d0/rsqr[w] 
; if w[0] ge 0 then r6[w]=r2[w]*r2[w]*r2[w]
; if w[0] ge 0 then result[w]=48.0d0*r2[w]*r6[w]*(r6[w]-0.5d0)
; return, result
;end


pro insertcrystal,nside,n,x,y,LX,LY
  xx=dblarr(1000)
  yy=dblarr(1000)

  a=2.0d0^(1.0d0/6.0d0)
  k=0
  x0=LX/2-a*double(nside-1)/2.0d0
  for j=0,nside-1 do begin
    for i=0,nside-1+j do begin
       xx[k]=x0+i*a
       yy[k]=j*a*sqrt(3.0d0)/2.0d0
       ;print,k,xx[k],yy[k]
       k++
    endfor
    x0 -= a/2.0d0
  endfor
    x0+=a
  for j=1,nside-1 do begin
    for i=0,2*nside-2-j do begin
       xx[k]=x0+i*a
       yy[k]=(nside-1+j)*a*sqrt(3.0d0)/2.0d0
       ;print,k,xx[k],yy[k]
       k++
    endfor
    x0 += a/2.0d0
  endfor

  n=k
  x=xx[0:n-1]
  y=yy[0:n-1]

end

pro initializeforces,n,x,y,vx,vy,LX,LY,mg,forcex,forcey,t,timestep,ke,pe,e,temperature
  ke=0.0d0
  pe=0.0d0
  f0=dblarr(n)
  r2inv=f0
  r6inv=f0

  ;calculateforces

  ;wall forces 
  forcex=-dphidrwall(x+1.0d0)+dphidrwall(LX-x+1.0d0)
  forcey=-dphidrwall(y+1.0d0)+dphidrwall(LY-y+1.0d0)-mg
  pe+=total(phiwall(x+1.0d0)+phiwall(LX-x+1.d0)+phiwall(y+1.0d0)+phiwall(LY-y+1.d0)+mg*y)

  ;collisions
  for i=0,n-1 do begin
    dx=x[i]-x
    dy=y[i]-y
    rsqr=dx^2+dy^2
    w=where(rsqr gt 0.01d0 and rsqr lt  6.25d0)
    if w[0] ge 0 then r2inv[w]=1.0d0/rsqr[w] 
    if w[0] ge 0 then r6inv[w]=r2inv[w]*r2inv[w]*r2inv[w]
    if w[0] ge 0 then f0[w]=48.0d0*r2inv[w]*r6inv[w]*(r6inv[w]-0.5d0)
    if w[0] ge 0 then forcex[w] -= dx[w]*f0[w]
    if w[0] ge 0 then forcey[w] -= dy[w]*f0[w]

    if w[0] ge 0 then pe += 0.5d0*total(4.0d0*r6inv[w]*(r6inv[w]-1.0d0)+0.016316891136d0)  ;overcounting taken care of

  endfor

   ke = 0.5d0*total(vx^2+vy^2)

   e=ke+pe
 

end


pro verlet,n,x,y,vx,vy,LX,LY,mg,forcex,forcey,t,timestep,ke,pe,e,temperature,constTQ,seed
;velocity verlet
  ke=0.0d0
  pe=0.0d0
  f0=dblarr(n_elements(x))
  r2inv=f0
  r6inv=f0

 timestepsqrhalf=timestep*timestep*0.5d0
 timestephalf=timestep*0.5d0
  
  ;update positions
  x += timestep*vx+timestepsqrhalf*forcex
  y += timestep*vy+timestepsqrhalf*forcey

  ;calculate new forces

  ;wall forces 
  forcexnext=-dphidrwall(x+1.0d0)+dphidrwall(LX-x+1.0d0)
  forceynext=-dphidrwall(y+1.0d0)+dphidrwall(LY-y+1.0d0)-mg
  pe+=total(phiwall(x+1.0d0)+phiwall(LX-x+1.d0)+phiwall(y+1.0d0)+phiwall(LY-y+1.d0)+mg*y)


  ;collisions
  for i=0,n-1 do begin
    dx=x[i]-x
    dy=y[i]-y
    rsqr=dx^2+dy^2
    w=where(rsqr gt 0.01d0 and rsqr lt  6.25d0)
    if w[0] ge 0 then r2inv[w]=1.0d0/rsqr[w] 
    if w[0] ge 0 then r6inv[w]=r2inv[w]*r2inv[w]*r2inv[w]
    if w[0] ge 0 then f0[w]=48.0d0*r2inv[w]*r6inv[w]*(r6inv[w]-0.5d0)
    if w[0] ge 0 then forcexnext[w] -= 1.0d0*dx[w]*f0[w]
    if w[0] ge 0 then forceynext[w] -= 1.0d0*dy[w]*f0[w]

    if w[0] ge 0 then pe += 0.5d0*total(4.0d0*r6inv[w]*(r6inv[w]-1.0d0)+0.016316891136d0)  ;overcounting taken care of

  endfor

  ;calculate new velocities

   vx += timestephalf*(forcex+forcexnext)
   vy += timestephalf*(forcey+forceynext)


  ke = 0.5d0*total(vx^2+vy^2)   

;modified Andersen Thermostat to maintain fixed temperature
;modification to reduce abruptness of heat bath interactions
;for bare Andersen, set gamma=0.0d0

if constTQ eq 1 then begin
     gamma=0.99d0
     ;i=fix(randomu(seed)*n)
     ;i=long(t/timestep) mod n
     ;vx[i]=gamma*vx[i]+randomn(seed)*sqrt(temperature*(1-gamma^2))
     ;vy[i]=gamma*vy[i]+randomn(seed)*sqrt(temperature*(1-gamma^2))
      vx=gamma*vx+randomn(seed,n)*sqrt(temperature*(1-gamma^2))
      vy=gamma*vy+randomn(seed,n)*sqrt(temperature*(1-gamma^2))
endif

;isokinetic thermostat
if constTQ eq 2 then begin
     if (temperature eq 0.0d0) then scale= 0.0d0 else scale=sqrt(temperature*n/ke)
     vx *= scale
     vy *= scale
     ke=0.5d0*total(vx^2+vy^2)
endif




   forcex=forcexnext
   forcey=forceynext

 
   e=ke+pe
   t += timestep

end

;main routine

t=0.0d0
timestep=(0.5d0^6)
seed=-238474698L
for i=0,1000 do rtest=randomn(seed)

nlayers=7L
n=1L+3L*nlayers*(nlayers-1)

LX=long(sqrt(n*6/sqrt(3.0d0)))*2.0d0^(1.0d0/6.0d0)*1.2
LY=LX*sqrt(3.0d0)/2.
insertcrystal,nlayers,n,x,y,LX,LY

y += 0.0d0

temperature0=0.5d0
temperaturestep=-0.1d0
mg=0.03d0
ntimesteps=100000L
nstore=ntimesteps
nstat=0L
estore=dblarr(nstore)
kestore=dblarr(nstore)
pestore=dblarr(nstore)


temperature=temperature0
;x=dblarr(n)
;y=dblarr(n)
vx=dblarr(n)
vy=dblarr(n)
forcex=dblarr(n)
forcey=dblarr(n)


circlex=0.5d0*cos(dindgen(21)/20.0d0*2.0d0*!dpi)
circley=0.5d0*sin(dindgen(21)/20.0d0*2.0d0*!dpi)



;random initialization
;for i=0,n-1 do begin
; tryagain:
;   x[i]=randomu(seed)*LX
;   y[i]=randomu(seed)*LY
;   ok=1
;   for j=0,i-1 do begin
;	rsqr=(x[j]-x[i])^2+(y[j]-y[i])^2
;	if (rsqr lt 1.26d0) then ok=0
;   endfor
;   if (ok eq 0) then goto, tryagain
;endfor

plot,x,y,psym=2,xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic
for i=0,n-1 do oplot,x[i]+circlex,y[i]+circley


for i=0,n-1 do begin 
 vx[i]=randomn(seed)*sqrt(temperature)
 vy[i]=randomn(seed)*sqrt(temperature)
endfor




plot,x,y,psym=2,xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic
for i=0,n-1 do oplot,x[i]+circlex,y[i]+circley


;calculate initial forces
initializeforces,n,x,y,vx,vy,LX,LY,mg,forcex,forcey,t,timestep,ke,pe,e,temperature0

;start at T=0 and heat slowly to T=0.5
;temperature=0.5d0

;temperature and equilibration schedule
tempschedule=dblarr(ntimesteps)
equilibschedule=intarr(ntimesteps)+1
equilibtime=1000L
stattime=19000L
it=0
while (it+equilibtime+stattime lt ntimesteps+1) do begin
tempschedule[it:it+equilibtime+stattime-1]=temperature
equilibschedule[it:it+equilibtime-1]=1
equilibschedule[it+equilibtime:it+equilibtime+stattime-1]=2
;equilibschedule[it+equilibtime-1000:it+equilibtime-1]=2
it += equilibtime+stattime
temperature += temperaturestep
endwhile


temperature=temperature0




istore=0L
for itimestep=0L,ntimesteps-1L do begin

;temperature=(double(itimestep)/double(ntimesteps))*temperature0

;cooling schedule
;temperature=temperature0-temperature0*fix(itimestep/10000L)/fix(ntimesteps/10000L)

;heating schedule
;temperature=temperature0+double(itimestep/10000L)*0.1d0

if (itimestep mod 1 eq 0) then begin
  estore[istore]=e
  kestore[istore]=ke
  pestore[istore]=pe
  istore++
endif


;if (itimestep lt 10000L) then verlet,n,x,y,vx,vy,LX,LY,mg,forcex,forcey,t,timestep,ke,pe,e,temperature,1,seed else verlet,n,x,y,vx,vy,LX,LY,mg,forcex,forcey,t,timestep,ke,pe,e,temperature,0,seed
temperature=tempschedule[itimestep]
equilib=equilibschedule[itimestep]

;if (itimestep ge 2000) then stop
verlet,n,x,y,vx,vy,LX,LY,mg,forcex,forcey,t,timestep,ke,pe,e,temperature,equilib



if (itimestep mod 8L eq 0) then begin
  ;plot,x,y,psym=2,xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic
  plot,[0],xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic,charsize=1,title=string(temperature)+' '+string(ke/n)+' '+string(itimestep)
  for i=0,n-1 do oplot,x[i]+circlex,y[i]+circley
  wait,.0001
endif



endfor

;for ii=0L,2500000-1,100000 do print,tempschedule[ii],(total(kestore[ii+10000:ii+99999])/90000.)/n,(total(pestore[ii+10000:ii+99999])/90000.)/n,(total(kestore[ii+10000:ii+99999]^2)/90000.-(total(kestore[ii+10000:ii+99999])/90000.)^2)/n/tempschedule[ii]^2,(total(pestore[ii+10000:ii+99999]^2)/90000.-(total(pestore[ii+10000:ii+99999])/90000.)^2)/n/tempschedule[ii]^2

end
