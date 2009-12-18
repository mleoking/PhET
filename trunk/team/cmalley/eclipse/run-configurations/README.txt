Files ending in .launch are Eclipse Run Configuration files.

If you create these with the "Run->Run Configuration" menu item, then Eclipse puts them 
in the workspace folder under .metadata/.plugins/org.eclipse.debug.core/.launches.

But by having the files here, they still show up in the Run list (no idea why, maybe .launch is special to Eclipse).

To backup .launch files created via "Run->Run Configuration", run this at a shell:
cd ~/phet-workspace ; cp .metadata/.plugins/org.eclipse.debug.core/.launches/*.launch cmalley/eclipse/run-configurations/