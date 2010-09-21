

;n molecule diatomic Lennard-Jones in 2D box with WCA boundaries


;random H2O initialization
pro H2OInitialize,n,xcm,ycm,theta,x0,y0,vxcm,vycm,omega,LX,LY,inertia,mass,netforcex,netforcey,torque
  aOH=1.0/3.12d0
  thetaHOH=109.47122d0*!dpi/180.0d0
  x0=dblarr(3)
  y0=dblarr(3)
  x0[0]=0.0d0
  y0[0]=0.0d0
  x0[1]=aOH
  y0[1]=0.0d0
  x0[2]=aOH*cos(thetaHOH)
  y0[2]=aOH*sin(thetaHOH)
  xcm0=(1.0d0*x0[0]+0.25d0*x0[1]+0.25*x0[2])/1.50d0
  ycm0=(1.0d0*y0[0]+0.25d0*y0[1]+0.25*y0[2])/1.50d0
  for i=0,2 do begin
    x0[i] -= xcm0
    y0[i] -= ycm0
  endfor
  inertia=1.0d0*(x0[0]^2+y0[0]^2)+0.25d0*(x0[1]^2+y0[1]^2)+0.25d0*(x0[2]^2+y0[2]^2)
  mass=1.50d0
  xcm=dblarr(n)
  ycm=dblarr(n)
  vxcm=dblarr(n)
  vycm=dblarr(n)
  theta=dblarr(n)
  omega=dblarr(n)
  netforcex=dblarr(n)
  netforcey=dblarr(n)
  torque=dblarr(n)
  for i=0,n-1 do begin
   tryagain:
   theta[i]=2.0d0*!dpi*randomu(seed)
   xcm[i]=LX*randomu(seed)
   ycm[i]=LY*randomu(seed)
      ok=1
      for j=0,i-1 do begin
        rsqr=(xcm[j]-xcm[i])^2+(ycm[j]-ycm[i])^2
        if (rsqr lt 1.d0) then ok=0
      endfor
      if (ok eq 0) then goto, tryagain
  endfor
end
  
pro plotH2O,n,xcm,ycm,theta,x0,y0,LX,LY,temperature,kecm,itimestep

  circlex=0.5d0*cos(dindgen(21)/20.0d0*2.0d0*!dpi)
  circley=0.5d0*sin(dindgen(21)/20.0d0*2.0d0*!dpi)

  plot,[0],xrange=[-0.5,LX+0.5],yrange=[-0.5,LY+0.5],xstyle=1,ystyle=1,/isotropic,charsize=1,title=string(temperature)+' '+string(kecm/n)+' '+string(itimestep)
  d=[1.0d0,0.6d0,0.6d0]
  for i=0,n-1 do for ii=0,2 do begin
    c=cos(theta[i])
    s=sin(theta[i])
    x=xcm[i]+c*x0[ii]-s*y0[ii]
    y=ycm[i]+s*x0[ii]+c*y0[ii]
    oplot,x+d[ii]*circlex,y+d[ii]*circley
    oplot,[xcm[i],x],[ycm[i],y]
  endfor
end


pro insertcrystal,nside,n,xcm,ycm,LX,LY,theta
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
  xcm=xx[0:n-1]
  ycm=yy[0:n-1]
  theta=dblarr(n)
end


pro verletH2O,n,xcm,ycm,theta,vxcm,vycm,omega,LX,LY,x0,y0,mass,inertia,mg,netforcex,netforcey,torque,t,timestep,ke,pe,e,temperature,thermostat,kecm,kerot,pecoulomb,seed
;velocity verlet
q0=3.0d0
q=[-2.d0*q0,q0,q0]
x0=[-0.0356125361702611d0,0.2849002843425594d0,-0.1424501396615148d0]
y0=[ -0.0503637310509284d0, -0.0503637310509284d0,0.2518186552546422d0]

 timestepsqrhalf=timestep*timestep*0.5d0
 timestephalf=timestep*0.5d0
  massinverse=1.0d0/mass
  inertiainverse=1.0d0/inertia
  twopi=!dpi
  ;update cm positions and angles
  for i=0,n-1 do begin
    xcm[i] += timestep*vxcm[i]+timestepsqrhalf*netforcex[i]*massinverse
    ycm[i] += timestep*vycm[i]+timestepsqrhalf*netforcey[i]*massinverse
    theta[i] += timestep*omega[i]+timestepsqrhalf*torque[i]*inertiainverse
  endfor
  


  ;calculate new forces, torques and potential energies
  pe=0.0d0
  pecoulomb=0.0d0

  netforcexnext=dblarr(n) ;these can be passed in as workspaces to save creation time in java code
  netforceynext=dblarr(n)
  torquenext=dblarr(n)

  ;atom positions from cm positions and rotation angle
  x=dblarr(n,3)
  y=dblarr(n,3) 
  for i=0,n-1 do begin
    c=cos(theta[i])
    s=sin(theta[i])
    for ii=0,2 do begin
      x[i,ii]=xcm[i]+c*x0[ii]-s*y0[ii]
      y[i,ii]=ycm[i]+s*x0[ii]+c*y0[ii]
    endfor
  endfor


  ;wall forces act on cm so no torques
  for i=0,n-1 do begin
    if (xcm[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(1.0d0+xcm[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fx=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforcexnext[i]+=fx
      ;torquenext[i]-=(y[i,0]-ycm[i])*fx
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (ycm[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(1.0d0+ycm[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fy=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforceynext[i]+=fy
      ;torquenext[i]+=(x[i,0]-xcm[i])*fy
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (LX-xcm[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(LX+1.0d0-xcm[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fx=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforcexnext[i]-=fx
      ;torquenext[i]+=(y[i,0]-ycm[i])*fx
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
    if (LY-ycm[i] lt 0.122462048309373017d0) then begin
      rinv=1.d0/(LY+1.0d0-ycm[i])
      rinv2=rinv*rinv
      rinv6=rinv2*rinv2*rinv2
      fy=48.0d0*rinv*rinv6*(rinv6-0.50d0)
      netforceynext[i]-=fy
      ;torquenext[i]-=(x[i,0]-xcm[i])*fy
      pe+=4.0d0*rinv6*(rinv6-1.0d0)+1.0d0
    endif
  endfor
   
;gravity
  for i=0,n-1 do begin
    netforceynext[i] -= mg
    pe += mg*ycm[i]
  endfor

;brute-force force and torque calculation
for i=0,n-1 do for j=i+1,n-1 do begin
    
   ;intermolecular forces
   ;WCA between centers of mass so no torques
   ; test LJ between centers
   dx=xcm[i]-xcm[j]
   dy=ycm[i]-ycm[j]
   rsqr=dx*dx+dy*dy
;   if (rsqr lt 1.259921049894873191d0) then begin
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
      ;torquenext[i]+=(x[i,0]-xcm[i])*fy-(y[i,0]-ycm[i])*fx
      ;torquenext[j]-=(x[j,0]-xcm[j])*fy-(y[j,0]-ycm[j])*fx
      
;      pe += 4.0d0*r6inv*(r6inv-1.0d0)+1.0d0
      pe += 4.0d0*r6inv*(r6inv-1.0d0)+0.016316891136d0   ;LJ
   endif

   if (rsqr lt 6.25d0) then begin ; cutoff at 2.5

      ;coulomb-like interaction between atoms on different water molecules 
      ;  (1/r^2 instead of couloub and cutoff at r=2.5

    for ii=0,2 do for jj=0,2 do begin
       dx=x[i,ii]-x[j,jj]
       dy=y[i,ii]-y[j,jj]
       rsqr=(dx*dx+dy*dy)
         r2inv=1.0d0/(dx*dx+dy*dy)
         ;rinv=sqrt(r2inv)
         f0=q[ii]*q[jj]*r2inv*r2inv
         fx=f0*dx
         fy=f0*dy
         netforcexnext[i] += fx
         netforcexnext[j] -= fx
         netforceynext[i] += fy
         netforceynext[j] -= fy
         torquenext[i]+=(x[i,ii]-xcm[i])*fy-(y[i,ii]-ycm[i])*fx
         torquenext[j]-=(x[j,jj]-xcm[j])*fy-(y[j,jj]-ycm[j])*fx

         pecoulomb+=0.5d0*q[ii]*q[jj]*(r2inv-0.16)  ;cutoff at r=2.5
;         pecoulomb+=0.5d0*q[ii]*q[jj]*(r2inv)   ;no cutoff
       endfor
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


;Andersen Thermostat to maintain fixed temperature
;modification to reduce abruptness of heat bath interactions
;for bare Andersen, set gamma=0.0d0

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

   pe += pecoulomb
   ke=kecm+kerot
   e=ke+pe
   t += timestep

end

;main routine

t=0.0d0
timestep=(0.5d0^6)
;seed=-238474698L
for i=0,1000 do rtest=randomn(seed)
;nlayers=5L
;n=1L+3L*nlayers*(nlayers-1)

n=50L
LX=20.0d0
LY=20.0d0

H2OInitialize,n,xcm,ycm,theta,x0,y0,vxcm,vycm,omega,LX,LY,inertia,mass,netforcex,netforcey,torque

;insertcrystal,nlayers,n,xcm,ycm,LX,LY,theta


plotH2O,n,xcm,ycm,theta,x0,y0,LX,LY,0.0,0.0,0


temperature0=2.0d0
temperaturestep=-0.2d0
mg=0.01d0
ntimesteps=20000L
nstore=ntimesteps
nstat=0L
estore=dblarr(nstore)
kestore=dblarr(nstore)
pestore=dblarr(nstore)
pecoulombstore=dblarr(nstore)
kecmstore=dblarr(nstore)
kerotstore=dblarr(nstore)

temperature=temperature0




;calculate initial forces and torques

verletH2O,n,xcm,ycm,theta,vxcm,vycm,omega,LX,LY,x0,y0,mass,inertia,mg,netforcex,netforcey,torque,t,timestep,ke,pe,e,temperature,0,kecm,kerot



;initialize random velocities and rotation rates
for i=0,n-1 do begin 
 vxcm[i]=randomn(seed)*sqrt(temperature/mass)
 vycm[i]=randomn(seed)*sqrt(temperature/mass)
 omega[i]=randomn(seed)*sqrt(temperature/inertia)
endfor



;temperature and equilibration schedule
tempschedule=dblarr(ntimesteps)
equilibschedule=intarr(ntimesteps)+1
equilibtime=10000L
stattime=10000L
it=0
while (it+equilibtime+stattime lt ntimesteps+1) do begin
tempschedule[it:it+equilibtime+stattime-1]=temperature
equilibschedule[it:it+equilibtime-1]=1
equilibschedule[it+equilibtime:it+equilibtime+stattime-1]=2
;equilibschedule[it+equilibtime-1000:it+equilibtime-1]=0
it += equilibtime+stattime
temperature += temperaturestep
endwhile


temperature=temperature0




istore=0L
for itimestep=0L,ntimesteps-1L do begin

temperature=tempschedule[itimestep]
thermostat=equilibschedule[itimestep]


verletH2O,n,xcm,ycm,theta,vxcm,vycm,omega,LX,LY,x0,y0,mass,inertia,mg,netforcex,netforcey,torque,t,timestep,ke,pe,e,temperature,thermostat,kecm,kerot,pecoulomb,seed



if (itimestep mod 1 eq 0) then begin
  estore[istore]=e
  kestore[istore]=ke
  kecmstore[istore]=kecm
  kerotstore[istore]=kerot
  pestore[istore]=pe
  pecoulombstore[istore]=pecoulomb
  istore++
endif


if (itimestep mod 8L eq 0) then begin
 plotH2O,n,xcm,ycm,theta,x0,y0,LX,LY,temperature,kecm,itimestep
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
