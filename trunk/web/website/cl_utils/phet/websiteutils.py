#!/usr/bin/python

import httplib

def clear_cache():
    conn = httplib.HTTPConnection('phet.colorado.edu')
    conn.request('GET', '/admin/cache-clear.php?cache=all')
    resp = conn.getresponse()
    return resp.status == 200
