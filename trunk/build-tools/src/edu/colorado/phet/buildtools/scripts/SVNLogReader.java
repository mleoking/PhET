package edu.colorado.phet.buildtools.scripts;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.util.ProcessOutputReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SVNLogReader {
    private File trunk;

    public SVNLogReader(File trunk) {
        this.trunk = trunk;
    }

    public static void main(String[] args) {
        String trunk = args[0];
        String project = args[1];
        ArrayList<Log> logs = new SVNLogReader(new File(trunk)).getLog(project);
        for (int i = 0; i < logs.size(); i++) {
            Log log = logs.get(i);
            System.out.println(log);
        }
    }

    static class ChangeSet {
        String[] entries;

        ChangeSet(String[] entries) {
            this.entries = entries;
        }
    }

    public static class Log {
        PhetProject project;
        ChangeSet changeSet;

        public Log(PhetProject project, ChangeSet changeSet) {
            this.project = project;
            this.changeSet = changeSet;
        }

        @Override
        public String toString() {
            String s = project.getName() + "\n";
            for (int i = 0; i < changeSet.entries.length; i++) {
                  s+= "\t"+changeSet.entries[i]+"\n";
            }
            return s;
        }
    }

    private ArrayList<Log> getLog(String project) {
        PhetProject proj = getProject(project);
        int since = 34844 + 1;//add 1 so we skip the commit of the version for last deploy
        //todo: determine version automatically
        PhetProject[] dependencies = proj.getAllDependencies();
        ArrayList<Log> changeSets = new ArrayList<Log>();
        for (int i = 0; i < dependencies.length; i++) {
            changeSets.add(new Log(dependencies[i], getLog(dependencies[i], since)));
        }
        return changeSets;
    }

    private ChangeSet getLog(PhetProject dependency, int since) {
        try {
            BuildLocalProperties.initRelativeToTrunk(trunk);
        } catch (IllegalStateException ise) {
        }
        BuildLocalProperties properties = BuildLocalProperties.getInstance();
        AuthenticationInfo auth = properties.getRespositoryAuthenticationInfo();
        String[] args = new String[]{"svn", "log", "-r", "HEAD:" + since, "--username", auth.getUsername(), "--password", auth.getPassword()};
        System.out.print("Getting log for "+dependency.getName()+"...");
        ProcessOutputReader.ProcessExecResult output = exec(args, dependency.getProjectDir());
        StringTokenizer st = new StringTokenizer(output.getOut(), "\n");
        ArrayList<String> entries = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            entries.add(token);
        }
        System.out.println("done.");
        return new ChangeSet(entries.toArray(new String[0]));
    }

    //todo: copied from ProcessOutputReader
    public static ProcessOutputReader.ProcessExecResult exec(String[] args, File dir) {
        try {
            Process p = Runtime.getRuntime().exec(args, null, dir);
            try {
                ProcessOutputReader processOutputReader = new ProcessOutputReader(p.getInputStream());
                processOutputReader.start();

                ProcessOutputReader processErr = new ProcessOutputReader(p.getErrorStream());
                processErr.start();

                int code = p.waitFor();

                // wait for ProcessOutputReaders to finish also
                processOutputReader.join(5000);
                processErr.join(5000);

                return new ProcessOutputReader.ProcessExecResult(args, code, processOutputReader.getOutput(), processErr.getOutput());
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private PhetProject getProject(String project) {
        PhetProject[] p = PhetProject.getAllProjects(trunk);
        for (int i = 0; i < p.length; i++) if (p[i].getName().equals(project)) return p[i];
        return null;
    }
}
