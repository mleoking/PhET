#!/usr/bin/python

import codecs
import sys

def main():
    if len(sys.argv) != 2:
        print 'usage: cat_jnlp.py jnlp_file'
        print 'Strip the null characters from the JNLP file'
        print 'and print to standaerd out'
        return 1

    jnlp_filename = sys.argv[1]
    data = codecs.open(jnlp_filename, 'r', encoding='utf-16').read()
    print data.encode('ascii', 'ignore')
    return 0
    inf = open(jnlp_filename, 'rb')
    inf.seek(3)
    data = inf.read()
    new_d = ''
    for d in data:
        if d != '\0':
            new_d = new_d + d

    print new_d

if __name__ == '__main__':
    retval = main()
    sys.exit(retval)
