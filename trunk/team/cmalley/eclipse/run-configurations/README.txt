Files ending in .launch are Eclipse Run Configuration files.
Put them in the workspace folder under .metadata/.plugins/org.eclipse.debug.core/.launches.
When you delete a project in Eclipse, it typically deletes all of the Run Configurations for the project.
In the case of PhET trunk, these are a royal pain to set up again, because you have to do it 
via the "Run->Run Configurations" menu item.

To backup, run this at a shell:
cd ~/phet-workspace ; cp .metadata/.plugins/org.eclipse.debug.core/.launches/*.launch cmalley/eclipse/run-configurations/