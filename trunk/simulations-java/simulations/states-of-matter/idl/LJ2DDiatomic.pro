;n particle Lennard-Jones in 2D box with WCA boundaries


;random diatomic initialization
pro diatomicInitialize,n,x,y,LX,LY,a
  for i=0,n-1,2 do begin
   tryagain:
   x[i]=randomu(seed)*LX
   y[i]=randomu(seed)*LY
   theta=2.0d0*!dpi*randomu(seed)
   x[i+1]=x[i]+a*cos(theta)
   y[i+1]=y[i]+a*sin(theta)
   if (x[i+1] lt 0.0d0) then goto, tryagain
   if (y[i+1] lt 0.0d0) then goto, tryagain
   if (x[i+1] gt LX) then goto, tryagain
   if (y[i+1] gt LY) then goto, tryagain
   ok=1
   for j=0,i-1 do begin
     rsqr=(x[j]-x[i])^2+(y[j]-y[i])^2
     if (rsqr lt 1.d0) then ok=0
     rsqr=(x[j]-x[i+1])^2+(y[j]-y[i+1])^2
     if (rsqr lt 1.d0) then ok=0
    endfor
    if (ok eq 0) then goto, tryagain
  endfor
end
  


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


pro verletdiatomic,n,x,y,vx,vy,LX,LY,a,k,mg,forcex,forcey,t,timestep,ke,pe,e,temperature,thermostat
;velocity verlet
  ke=0.0d0
  pe=0.0d0
  forcexnext=dblarr(n)
  forceynext=dblarr(n)
  
 timestepsqrhalf=timestep*timestep*0.5d0
 timestephalf=timestep*0.5d0
  
  ;update positions
  x += timestep*vx+timestepsqrhalf*forcex
  y += timestep*vy+timestepsqrhalf*forcey

  ;calculate new forces


  ;wall forces
  for i=0,n-1 do begin
    if (x[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(1.0d0+x[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fx=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      forcexnext[i]+=fx
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (y[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(1.0d0+y[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fy=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      forceynext[i]+=fy
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (LX-x[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(LX+1.0d0-x[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fx=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      forcexnext[i]-=fx
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (LY-y[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(LY+1.0d0-y[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fy=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      forceynext[i]-=fy
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
  endfor
   


;brute-force force calculation
for i=0,n-1 do for j=i+1,n-1 do begin

  if (i mod 2 eq 0 and j eq i+1) then begin

    ;intramolecular bond forces

    
      dx=x[i]-x[j]
      dy=y[i]-y[j]
      r=sqrt(dx*dx+dy*dy)
      f0=-k*(r-a)/r
      fx=f0*dx
      fy=f0*dy
      forcexnext[i]+=fx
      forceynext[i]+=fy
      forcexnext[i+1]-=fx
      forceynext[i+1]-=fy
      pe += 0.5d0*k*(r-a)^2

  endif else begin
   
   ;intermolecular forces
   dx=x[i]-x[j]
   dy=y[i]-y[j]
   rsqr=dx*dx+dy*dy
   if (rsqr lt 6.25d0) then begin
      r2inv=1.0d0/rsqr 
      r6inv=r2inv*r2inv*r2inv
      f0=48.0d0*r2inv*r6inv*(r6inv-0.5d0)
      forcexnext[i] += dx*f0
      forcexnext[j] -= dx*f0
      forceynext[i] += dy*f0
      forceynext[j] -= dy*f0
      
      pe += 4.0d0*r6inv*(r6inv-1.0d0)+0.016316891136d0
   endif
   endelse

endfor

  ;calculate new velocities

   vx += timestephalf*(forcex+forcexnext)
   vy += timestephalf*(forcey+forceynext)


  ke = 0.5d0*total(vx^2+vy^2)   

;modified Andersen Thermostat to maintain fixed temperature
;modification to reduce abruptness of heat bath interactions
;for bare Andersen, set gamma=0.0d0

;if constTQ eq 1 then begin
;     gamma=0.99d0
;     ;i=fix(randomu(seed)*n)
;     ;i=long(t/timestep) mod n
;     ;vx[i]=gamma*vx[i]+randomn(seed)*sqrt(temperature*(1-gamma^2))
;     ;vy[i]=gamma*vy[i]+randomn(seed)*sqrt(temperature*(1-gamma^2))
;      vx=gamma*vx+randomn(seed,n)*sqrt(temperature*(1-gamma^2))
;      vy=gamma*vy+randomn(seed,n)*sqrt(temperature*(1-gamma^2))
;endif

;isokinetic thermostat
if thermostat eq 2 then begin
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
timestep=(0.5d0^7)
seed=-238474698L
for i=0,1000 do rtest=randomn(seed)

;nlayers=4L
;n=1L+3L*nlayers*(nlayers-1)

n=40L
LX=20.0d0
LY=24.0d0
k=1000.0d0
a=0.9d0
x=dblarr(n)
y=dblarr(n)
circlex=0.5d0*cos(dindgen(21)/20.0d0*2.0d0*!dpi)
circley=0.5d0*sin(dindgen(21)/20.0d0*2.0d0*!dpi)

diatomicInitialize,n,x,y,LX,LY,a

  plot,[0],xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic,charsize=1,title=string(temperature)+' '+string(ke/n)+' '+string(itimestep)
  for i=0,n-1 do oplot,x[i]+circlex,y[i]+circley



temperature0=1.0d0
temperaturestep=-0.4d0
mg=0.0
ntimesteps=60000L
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


plot,x,y,psym=2,xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic
for i=0,n-1 do oplot,x[i]+circlex,y[i]+circley





plot,x,y,psym=2,xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic
for i=0,n-1 do oplot,x[i]+circlex,y[i]+circley


;calculate initial forces
;initializeforces,n,x,y,vx,vy,LX,LY,mg,forcex,forcey,t,timestep,ke,pe,e,temperature0

xsave=x
ysave=y
vx=dblarr(n)
vy=dblarr(n)
forcex=dblarr(n)
forcey=dblarr(n)
verletdiatomic,n,x,y,vx,vy,LX,LY,a,k,mg,forcex,forcey,t,timestep,ke,pe,e,0.0,0
x=xsave
y=ysave


;initialize random velocities
for i=0,n-1 do begin 
 vx[i]=randomn(seed)*sqrt(temperature)
 vy[i]=randomn(seed)*sqrt(temperature)
endfor




;temperature and equilibration schedule
tempschedule=dblarr(ntimesteps)
equilibschedule=intarr(ntimesteps)+1
equilibtime=10000L
stattime=10000L
it=0
while (it+equilibtime+stattime lt ntimesteps+1) do begin
tempschedule[it:it+equilibtime+stattime-1]=temperature
equilibschedule[it:it+equilibtime-1]=2
equilibschedule[it+equilibtime:it+equilibtime+stattime-1]=0
;equilibschedule[it+equilibtime-1000:it+equilibtime-1]=2
it += equilibtime+stattime
temperature += temperaturestep
endwhile


temperature=temperature0




istore=0L
for itimestep=0L,ntimesteps-1L do begin

temperature=tempschedule[itimestep]
thermostat=equilibschedule[itimestep]


verletdiatomic,n,x,y,vx,vy,LX,LY,a,k,mg,forcex,forcey,t,timestep,ke,pe,e,temperature,thermostat



if (itimestep mod 1 eq 0) then begin
  estore[istore]=e
  kestore[istore]=ke
  pestore[istore]=pe
  istore++
endif


if (itimestep mod 8L eq 0) then begin
  ;plot,x,y,psym=2,xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic
  plot,[0],xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic,charsize=1,title=string(temperature)+' '+string(ke/n)+' '+string(itimestep)
  for i=0,n-1 do oplot,x[i]+circlex,y[i]+circley
  for i=0L,4*n/5-1,2 do oplot,[x[i],x[i+1]],[y[i],y[i+1]]
 wait,0.0001
endif



endfor

;for ii=0L,2500000-1,100000 do print,tempschedule[ii],(total(kestore[ii+10000:ii+99999])/90000.)/n,(total(pestore[ii+10000:ii+99999])/90000.)/n,(total(kestore[ii+10000:ii+99999]^2)/90000.-(total(kestore[ii+10000:ii+99999])/90000.)^2)/n/tempschedule[ii]^2,(total(pestore[ii+10000:ii+99999]^2)/90000.-(total(pestore[ii+10000:ii+99999])/90000.)^2)/n/tempschedule[ii]^2

it=0
while (it+equilibtime+stattime lt ntimesteps+1) do begin
temp0=tempschedule[it]
kebar=total(kestore[it+equilibtime:it+equilibtime+stattime-1])/n/stattime
pebar=total(pestore[it+equilibtime:it+equilibtime+stattime-1])/n/stattime
print,temp0,kebar,pebar
it += equilibtime+stattime
endwhile


end
