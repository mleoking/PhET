

;n molecule diatomic Lennard-Jones in 2D box with WCA boundaries


;random diatomic initialization
pro diatomicInitialize,n,xcm,ycm,theta,x0,y0,vxcm,vycm,omega,LX,LY,a,inertia,mass,netforcex,netforcey,torque
  xcm=dblarr(n)
  ycm=dblarr(n)
  vxcm=dblarr(n)
  vycm=dblarr(n)
  theta=dblarr(n)
  omega=dblarr(n)
  netforcex=dblarr(n)
  netforcey=dblarr(n)
  torque=dblarr(n)
  x0=dblarr(2)
  y0=dblarr(2)
  x0[0]=a/2
  y0[0]=0.0d0
  x0[1]=-a/2
  y0[1]=0.0d0
  inertia=0.0d0
  for ii=0,1 do inertia += (x0[ii]^2+y0[ii]^2)
  mass=2.0d0
  x=dblarr(n,2)
  y=dblarr(n,2)
  for i=0,n-1 do begin
   tryagain:
   theta[i]=2.0d0*!dpi*randomu(seed)
   xcm[i]=LX*randomu(seed)
   ycm[i]=LY*randomu(seed)
   c=cos(theta[i])
   s=sin(theta[i])
   for ii=0,1 do begin
     x[i,ii]=xcm[i]+c*x0[ii]-s*y0[0]
     y[i,ii]=ycm[i]+s*x0[ii]+c*y0[ii]
   endfor
   for ii=0,1 do begin
      if x[i,ii] lt 0.0d0 then goto,tryagain
      if y[i,ii] lt 0.0d0 then goto,tryagain
      if x[i,ii] gt LX then goto,tryagain
      if y[i,ii] gt LY then goto,tryagain
      ok=1
      for j=0,i-1 do for jj=0,1 do begin
        rsqr=(x[j,jj]-x[i,ii])^2+(y[j,jj]-y[i,ii])^2
        if (rsqr lt 1.d0) then ok=0
      endfor
      if (ok eq 0) then goto, tryagain
    endfor
  endfor
end
  
pro plotDiatomic,n,xcm,ycm,theta,x0,y0,LX,LY,temperature,kecm,itimestep

  circlex=0.5d0*cos(dindgen(21)/20.0d0*2.0d0*!dpi)
  circley=0.5d0*sin(dindgen(21)/20.0d0*2.0d0*!dpi)

  plot,[0],xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic,charsize=1,title=string(temperature)+' '+string(kecm/n)+' '+string(itimestep)
  for i=0,n-1 do for ii=0,1 do begin
    c=cos(theta[i])
    s=sin(theta[i])
    x=xcm[i]+c*x0[ii]-s*y0[ii]
    y=ycm[i]+s*x0[ii]+c*y0[ii]
    oplot,x+circlex,y+circley
    oplot,[xcm[i],x],[ycm[i],y]
  endfor
end

;wall potential: WCA
;
;function phiwall,x
; result=0.0d0*x
; w=where( abs(x) lt 1.122462048309373017d0)
; if w[0] ge 0 then result[w]=4.0d0/x[w]^12-4.0d0/x[w]^6+1.0d0
; return, result
;end

;function phi2wall,rsqr
; result=0.0d0*rsqr
; w=where( rsqr gt 0.01d0 and rsqr lt 1.259921049894873191d0)
; if w[0] ge 0 then result[w]=4.0d0/rsqr[w]^6-4.0d0/rsqr[w]^3+1.0d0
; return, result
;end


;function dphidrwall,x
; result=0.0d0*x
; w=where( abs(x) lt 1.122462048309373017d0)
; if w[0] ge 0 then result[w]=24.0d0/x[w]^7-48.0d0/x[w]^13
; return, result
;end

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



pro verletDiatomic,n,xcm,ycm,theta,vxcm,vycm,omega,LX,LY,x0,y0,mass,inertia,mg,netforcex,netforcey,torque,t,timestep,ke,pe,e,temperature,thermostat,kecm,kerot,seed
;velocity verlet

x0=[0.45d0,-0.45d0]
y0=[0.0d0,0.0d0]

 timestepsqrhalf=timestep*timestep*0.5d0
 timestephalf=timestep*0.5d0
  massinverse=1.0d0/mass
  inertiainverse=1.0d0/inertia
  ;update cm positions and angles
  for i=0,n-1 do begin
    xcm[i] += timestep*vxcm[i]+timestepsqrhalf*netforcex[i]*massinverse
    ycm[i] += timestep*vycm[i]+timestepsqrhalf*netforcey[i]*massinverse
    theta[i] += timestep*omega[i]+timestepsqrhalf*torque[i]*inertiainverse
  endfor

  ;calculate new forces and potential energies
  ;first divide old forces by 2.0 and then sum the new forces divided by 2.0
  pe=0.0d0

  netforcexnext=dblarr(n)
  netforceynext=dblarr(n)
  torquenext=dblarr(n)

  ;atom positions from cm positions and rotation angle
  x=dblarr(n,2)
  y=dblarr(n,2) 
  for i=0,n-1 do begin
    c=cos(theta[i])
    s=sin(theta[i])
    for ii=0,1 do begin
      x[i,ii]=xcm[i]+c*x0[ii]-s*y0[ii]
      y[i,ii]=ycm[i]+s*x0[ii]+c*y0[ii]
    endfor
  endfor


  ;wall forces
  for i=0,n-1 do for ii=0,1 do begin
    if (x[i,ii] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(1.0d0+x[i,ii])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fx=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforcexnext[i]+=fx
      torquenext[i]-=(y[i,ii]-ycm[i])*fx
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (y[i,ii] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(1.0d0+y[i,ii])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fy=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforceynext[i]+=fy
      torquenext[i]+=(x[i,ii]-xcm[i])*fy
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (LX-x[i,ii] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(LX+1.0d0-x[i,ii])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fx=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforcexnext[i]-=fx
      torquenext[i]+=(y[i,ii]-ycm[i])*fx
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (LY-y[i,ii] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(LY+1.0d0-y[i,ii])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fy=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforceynext[i]-=fy
      torquenext[i]-=(x[i,ii]-xcm[i])*fy
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
  endfor
   
;gravity
  for i=0,n-1 do begin
    netforceynext[i] -= mg
    pe += mg*ycm[i]
  endfor

;brute-force force and torque calculation
for i=0,n-1 do for j=i+1,n-1 do for ii=0,1 do for jj=0,1 do begin
     
   ;intermolecular forces
   dx=x[i,ii]-x[j,jj]
   dy=y[i,ii]-y[j,jj]
   rsqr=dx*dx+dy*dy
   if (rsqr lt 6.25d0) then begin
      r2inv=1.0d0/rsqr 
      r6inv=r2inv*r2inv*r2inv
      f0=48.0d0*r2inv*r6inv*(r6inv-0.5d0)
      fx=dx*f0
      fy=dy*f0
      netforcexnext[i] += fx
      netforcexnext[j] -= fx
      netforceynext[i] += fy
      netforceynext[j] -= fy
      torquenext[i]+=(x[i,ii]-xcm[i])*fy-(y[i,ii]-ycm[i])*fx
      torquenext[j]-=(x[j,jj]-xcm[j])*fy-(y[j,jj]-ycm[j])*fx
      
      pe += 4.0d0*r6inv*(r6inv-1.0d0)+0.016316891136d0
   endif

endfor
  
  ;update cm velocities and angles and calculate kinetic energies
  kecm=0.0d0
  kerot=0.0d0
  for i=0,n-1 do begin
    vxcm[i] += timestephalf*(netforcex[i]+netforcexnext[i])*massinverse
    vycm[i] += timestephalf*(netforcey[i]+netforceynext[i])*massinverse
    omega[i] += timestephalf*(torque[i]+torquenext[i])*inertiainverse
    kecm+=0.5d0*mass*(vxcm[i]^2+vycm[i]^2)   
    kerot+=0.5d0*inertia*omega[i]^2

     netforcex[i]=netforcexnext[i]
     netforcey[i]=netforceynext[i]
     torque[i]=torquenext[i]

  endfor


;modified Andersen Thermostat to maintain fixed temperature
;modification to reduce abruptness of heat bath interactions
if thermostat eq 1 then begin
  gamma=0.999d0
  scalecm=sqrt(temperature*massinverse*(1.0d0-gamma^2))
  scalerot=sqrt(temperature*inertiainverse*(1.0d0-gamma^2))
  for i=0,n-1 do begin
     gauss=randomn(seed,4)
     vxcm[i]=gamma*vxcm[i]+gauss[0]*scalecm
     vycm[i]=gamma*vycm[i]+gauss[1]*scalecm
     omega[i]=gamma*omega[i]+gauss[2]*scalerot
  endfor
endif

;isokinetic thermostat
if thermostat eq 2 then begin
     scale=sqrt(1.5*temperature*(n)/(kecm+kerot))
     vxcm *= scale
     vycm *= scale
     scalesqr=scale*scale
     kecm *= scalesqr
     omega *= scale
     kerot *=scalesqr
endif


   ke=kecm+kerot
   e=ke+pe
   t += timestep

end

;main routine

t=0.0d0
timestep=(0.5d0^6)
;seed=-238474698L
for i=0,1000 do rtest=randomn(seed)
;nlayers=4L
;n=1L+3L*nlayers*(nlayers-1)

n=100L
LX=30.0d0
LY=30.0d0
a=0.9d0

diatomicInitialize,n,xcm,ycm,theta,x0,y0,vxcm,vycm,omega,LX,LY,a,inertia,mass,netforcex,netforcey,torque
plotDiatomic,n,xcm,ycm,theta,x0,y0,LX,LY,0.0,0.0,0



temperature0=0.5d0
temperaturestep=0.0d0
mg=0.005d0
ntimesteps=100000L
nstore=ntimesteps
nstat=0L
estore=dblarr(nstore)
kestore=dblarr(nstore)
pestore=dblarr(nstore)
kecmstore=dblarr(nstore)
kerotstore=dblarr(nstore)

temperature=temperature0




;calculate initial forces and torques

verletDiatomic,n,xcm,ycm,theta,vxcm,vycm,omega,LX,LY,x0,y0,mass,inertia,mg,netforcex,netforcey,torque,t,timestep,ke,pe,e,temperature,0,kecm,kerot,seed



;initialize random velocities (but zero rotation rates)
for i=0,n-1 do begin 
 gauss=randomn(seed,4)
 vxcm[i]=gauss[0]*sqrt(temperature/mass)
 vycm[i]=gauss[1]*sqrt(temperature/mass)
 omega[i]=gauss[2]*sqrt(temperature/inertia)
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
equilibschedule[it+equilibtime:it+equilibtime+stattime-1]=1
;equilibschedule[it+equilibtime-1000:it+equilibtime-1]=2
it += equilibtime+stattime
temperature += temperaturestep
endwhile


temperature=temperature0




istore=0L
for itimestep=0L,ntimesteps-1L do begin

temperature=tempschedule[itimestep]
thermostat=equilibschedule[itimestep]


verletDiatomic,n,xcm,ycm,theta,vxcm,vycm,omega,LX,LY,x0,y0,mass,inertia,mg,netforcex,netforcey,torque,t,timestep,ke,pe,e,temperature,thermostat,kecm,kerot,seed



if (itimestep mod 1 eq 0) then begin
  estore[istore]=e
  kestore[istore]=ke
  kecmstore[istore]=kecm
  kerotstore[istore]=kerot
  pestore[istore]=pe
  istore++
endif


if (itimestep mod 8L eq 0) then begin
 plotDiatomic,n,xcm,ycm,theta,x0,y0,LX,LY,temperature,kecm,itimestep
 wait,0.0001
endif



endfor

;for ii=0L,2500000-1,100000 do print,tempschedule[ii],(total(kestore[ii+10000:ii+99999])/90000.)/n,(total(pestore[ii+10000:ii+99999])/90000.)/n,(total(kestore[ii+10000:ii+99999]^2)/90000.-(total(kestore[ii+10000:ii+99999])/90000.)^2)/n/tempschedule[ii]^2,(total(pestore[ii+10000:ii+99999]^2)/90000.-(total(pestore[ii+10000:ii+99999])/90000.)^2)/n/tempschedule[ii]^2

it=0
while (it+equilibtime+stattime lt ntimesteps+1) do begin
temp0=tempschedule[it]
kecmbar=total(kecmstore[it+equilibtime:it+equilibtime+stattime-1])/n/stattime
kerotbar=total(kerotstore[it+equilibtime:it+equilibtime+stattime-1])/n/stattime
pebar=total(pestore[it+equilibtime:it+equilibtime+stattime-1])/n/stattime
print,temp0,kecmbar,kerotbar,pebar
it += equilibtime+stattime
endwhile


end
