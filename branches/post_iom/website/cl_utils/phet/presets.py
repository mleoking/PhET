#!/usr/bin/python

# Preset the configuration for various environments
PRESETS = {'tigercat' :
               {'verbose' : False,
                'legacy_support' : False,
                'use_database' : True,
                'dev_version' : None,
                'jar_cmd' : '/web/chroot/phet/usr/local/java/bin/jar',
                'sim_root' : '/web/chroot/phet/usr/local/apache/htdocs/sims'
                },
           'spot' :
               {'verbose' : False,
                'legacy_support' : False,
                'use_database' : False,
                'dev_version' : 'NEWEST',
                'jar_cmd' : '/usr/java/bin/jar',
                'sim_root' : '/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev'
                },
           'phet-server' :
               {'verbose' : False,
                'legacy_support' : False,
                'use_database' : False,
                'dev_version' : None,
                'jar_cmd' : '/web/chroot/phet/usr/local/java/bin/jar',
                'sim_root' : '/web/chroot/phet/usr/local/apache/htdocs/sims'
                },
           'danomac' :
               {'verbose' : False,
                'legacy_support' : False,
                'use_database' : False,
                'dev_version' : None,
                'jar_cmd' : '/usr/bin/jar',
                'sim_root' : '/Users/danielmckagan/Workspaces/PhET/workspace/_webfiles/redeployed_sims'
                },
           'danomacdev' :
               {'verbose' : False,
                'legacy_support' : False,
                'use_database' : False,
                'dev_version' : 'NEWEST',
                'jar_cmd' : '/usr/bin/jar',
                'sim_root' : '/Users/danielmckagan/Workspaces/PhET/workspace/_webfiles/dev'
                },
           'danolinux' :
               {'verbose' : False,
                'legacy_support' : False,
                'use_database' : False,
                'dev_version' : None,
                'jar_cmd' : '/usr/lib/jvm/java-6-sun-1.6.0.03/bin/jar',
                'sim_root' : '/var/www/dev/phet/dev'
                },
           'danolinuxdev' :
               {'verbose' : False,
                'legacy_support' : False,
                'use_database' : False,
                'dev_version' : 'NEWEST',
                'jar_cmd' : '/usr/lib/jvm/java-6-sun-1.6.0.03/bin/jar',
                'sim_root' : '/var/www/dev/phet/sims'
                },
           }
