#! /usr/bin/env python
"""
Makes plots of H for a given climate history.
Plots are saved in 'plot_dir' for animation.
"""

import model
import os,cPickle
from pylab import *

################################################################################
# config

climate_config = {
                    'default_t0':20.,      # modern temp at sea level (celcius)
                    'default_p0':4e3,      # elevation of half-max snowfall (m)
                    'default_pmax':2.0,    # max precip (m/yr)
                    'snow_transition_width':300., # (m)
                    'melt_v_elev':30.,
                    'melt_v_temp':200.,
                    'z0':1300., 
                    'z1':4200.,
                  }
mountain_config = {
                    #'headwater_width':5e3, 
                    'headwater_width':0.0, 
                    'headwall_length':800., 
                    'headwall_steepness':5e3,
                    'bump':False, 
                    'bump_attenuation':30., 
                    'scoop':500., 
                    'bump_position':30.,
                   }
init_glacier_config =  {
                    #'form':'quadratic',     # parabola
                    #'form':'powersix',     # x^6 shape
                    'form':'combo',     # half parabola, half x^6
                    'max_height':200.,     # m
                    'length':27e3,         # m
                   }
domain_config =    {
                    'default_dt':0.02,  # years in one timestep (yrs)
                    'num_columns':100,     # num ice columns in whole domain
                    'x_max':80e3,           # edge of simulation (m)
                    'H_limit':1e3,      # stop if H exceeds this height
                   }

# list climate history to follow:
climate_history = [ # t0     p0   runtime
                   [ 19.0, 4.0e3, 2.0e3, ],
                   [ 18.0, 3.0e3, 2.0e3, ],
                   [ 16.0, 2.5e3, 2.0e3, ],
                   [ 15.0, 2.0e3, 2.0e3, ],
                   [ 16.0, 2.5e3, 2.0e3, ], 
                  ]

run_name = os.sys.argv[0][2:-3]
plot_dir = 'runs/'+run_name  # directory for figs

################################################################################
# make a bunch of plots:

def make_plots( g, runtime=2e3, plot_every=100., plot_dir='figs/foo' ):
    """
    Pass in glacier object. Makes a bunch of figures for an animation.
    """
    plot_every_i = int( plot_every/g.dt )
    dat = []
    i = 0
    start_time = g.time
    while g.time-start_time < runtime:
        if i%plot_every_i==0:
            print '\tstep %07d, time= %g yr'%(i,g.time)
            os.sys.stdout.flush()
            id = int(g.time)
            if 1:
                g.plot('F',fignum=2,label='valley floor')
                g.plot('H',fignum=2)
                savefig('%s/H_%07d.png'%(plot_dir,id))
                clf()
            if 0:
                g.plot('stress',fignum=4,scale=1e-5)
                savefig('%s/stress_%07d.png'%(plot_dir,id))
                clf()
            if 0:
                g.plot('u',fignum=5)
                savefig('%s/u_%07d.png'%(plot_dir,id))
                clf()
            term_i = g.find_terminus_index()
            dat.append([ g.time, g.columns[term_i].x ])
        g.step()
        i += 1
    return dat

if 1:
    print 'setting up glacier...',
    os.sys.stdout.flush()
    m = model.Mountain( mountain_config )
    c = model.ClimateCalculator( climate_config )
    g = model.Glacier( m, c, domain_config )
    g.set_shape( init_glacier_config )  
    print 'done.\n'
    os.sys.stdout.flush()

if 1:
    print 'saving plots in "%s"...\n'%plot_dir
    os.sys.stdout.flush()
    # set up run:
    dat = []  # time and terminus-x
    plot_every = 100.       # make plots every so many years
    if os.path.exists(plot_dir): os.system('rm -r '+plot_dir)
    os.system('mkdir '+plot_dir)
    for i,(t,p,r) in enumerate(climate_history):
        g.set_new_climate( t0=t, p0=p )
        if t !='off':
            print g
            g.plot_massbal()
            savefig('%s/climate_%02d.png'%(plot_dir,i))
        print 'running to %g kyrs...'%(r/1e3)
        os.sys.stdout.flush()
        dat += make_plots( g, r, plot_every, plot_dir )
        cPickle.dump( array(dat), file('%s/terminus_v_time.dat'%plot_dir,'w'))


if 0: # print out ELA for each climate
    for i,(t,p,r) in enumerate(climate_history):
        g.set_new_climate( t0=t, p0=p )
        print '\t%0.1f\t%0.1f\t%0.1f\t%0.1f'%(t,p/1e3,r/1e3, g.ela/1e3)

