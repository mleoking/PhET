import sys, os
tmp = os.getcwd().split('/')
sys.path.append( "/".join( tmp[ 0 : len( tmp ) - 1 ] ) )