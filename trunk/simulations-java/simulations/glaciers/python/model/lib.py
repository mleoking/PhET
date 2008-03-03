"""
auxiliary functions for glacier model

Tue Sep 25 22:22:12 MDT 2007
"""

from pylab import *

################################################################################

def derivative( x, f, interp=True ):
    """derivative( x, f, interp=True ) -> x, df/dx
    Send it a 2 (1-column) arrays: x,f(x).
    The derivative is returned as an array of x,f'(x).
    If interp, the slopes are interpolated back to the original x values.
         else, the slopes are given at in-between x values.
    Changed to accomodate complex f arrays (June 2004). x must be real.
    """
    dx = array( x[1:] - x[:-1], typecode='d' ) # shorter than x by 1
    df = f[1:] - f[:-1]                          # shorter than f by 1
    midpt_x = x[:-1] + dx/2.0
    try: 
        df_dx = df / dx
    except OverflowError:
        print 'OverflowError differentiating array.'
        raise
    if not interp: 
        return midpt_x, df_dx
    # interpolate slope data back to original x positions
    df_dx = interpolate( df_dx, midpt_x, x, 0 )
    return df_dx
    
def interpolate( f,x,x_i,axis=-1 ):
    """interp( f,x,x_i,axis=-1 )  ->  f_i
    'f'    is an n-dimensional array, whose nth dimension has axis values 'x'.
    'x_i'  are the x-values to interpolate to.
    'axis' specifies which dimension of f that x belongs to.
    If x_i is a number, f_i has one less dimension than f (else shape unchanged)
    """
    if len(x) != f.shape[axis]: raise ValueError, 'len(x) != f.shape[axis]'
    dims = len(f.shape)
    axis %= dims
    if axis != dims-1:   # reorder so axis is the last dim
        order = range(dims)
        order.remove(axis)
        order += [axis]  
        f_s = transpose( take( f, argsort(x), axis ), order )
        trans = 1
    else:
        f_s = take( f, argsort(x), axis )
        trans = 0
    x_s = sort( x ).astype(float)
    try:               # x_i is a sequence
        numpts = len(x_i)
        reduce_dim = 0
    except TypeError:  # x_i must be a number
        x_i = [x_i]
        numpts = 1
        reduce_dim = 1
    bins = clip(  searchsorted( x_s, x_i ), 1, len(x)-1 )
    x_hi = take( x_s, bins  )
    x_lo = take( x_s, bins-1)
    f_hi = take( f_s, bins  , -1 )   
    f_lo = take( f_s, bins-1, -1 )
    f_i = f_lo + (f_hi-f_lo)*(x_i-x_lo)/(x_hi-x_lo)
    if trans:       # undo the tanspose
        order = range(dims-1)
        order[axis:axis] = [dims-1]
        f_i = transpose( f_i, order )
    if reduce_dim:  # data goes to one-less dimension
        shape = list(f_i.shape)
        shape = shape[:axis] + shape[axis+1:]
        f_i = reshape( f_i, shape )
    return f_i
    

def smooth( f ):
    """ smooth a 1D array """
    pass


################################################################################

