launch.tar contains Eclipse Run Configuration files, which have a .launch suffix.
Put these in your workspace folder under .metadata/.plugins/org.eclipse.debug.core/.launches/.
They will appear in the "Run->Run Configuration" dialog.

NOTE: We must check this in as an archive because Eclipse seems to find .launch files anywhere in the workspace.

To create this archive:

cd ~/phet-workspace/.metadata/.plugins/org.eclipse.debug.core/.launches/
tar cvf ~/phet-workspace/trunk/team/cmalley/eclipse/run-configurations/launch.tar .