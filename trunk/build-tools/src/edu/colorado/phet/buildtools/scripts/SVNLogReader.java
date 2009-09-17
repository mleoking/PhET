package edu.colorado.phet.buildtools.scripts;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.util.ProcessOutputReader;

import java.io.*;
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
        {
            ArrayList<Log> logs = new SVNLogReader(new File(trunk)).getLog(project, "log");
            for (int i = 0; i < logs.size(); i++) System.out.println(logs.get(i));
        }

        {
            ArrayList<Log> logs = new SVNLogReader(new File(trunk)).getLog(project, "diff");
            for (int i = 0; i < logs.size(); i++) System.out.println(logs.get(i));
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
                s += "\t" + changeSet.entries[i] + "\n";
            }
            return s;
        }
    }

    private ArrayList<Log> getLog(String project, String command) {
        PhetProject proj = getProject(project);
        int since = getLastDeploy(proj) + 1;
        System.out.println("Getting info since revision: " + since);

        return getLogs(proj, since, command);
    }

    //automatically identify revision based on last deploy indicated in changelog
    private int getLastDeploy(PhetProject proj) {
        File f = proj.getChangesFile();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
            String line = bufferedReader.readLine();
            while (line != null) {
                //use lines of the form # 0.01.14 (34844) Sep 4, 2009
                if (line.trim().startsWith("#") && line.indexOf('(') > 0 && line.indexOf(')') > 0) {
                    return getSVNNumber(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Last deploy not found");
    }

    private int getSVNNumber(String line) {
        String substring = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        return Integer.parseInt(substring);
    }

    private ArrayList<Log> getLogs(PhetProject proj, int since, String command) {
        PhetProject[] dependencies = proj.getAllDependencies();
        ArrayList<Log> changeSets = new ArrayList<Log>();
        for (int i = 0; i < dependencies.length; i++) {
            changeSets.add(new Log(dependencies[i], getChangeSet(dependencies[i], since, command)));
        }
        return changeSets;
    }

    private ChangeSet getChangeSet(PhetProject dependency, int since, String command) {
        try {
            BuildLocalProperties.initRelativeToTrunk(trunk);
        } catch (IllegalStateException ise) {
        }
        BuildLocalProperties properties = BuildLocalProperties.getInstance();
        AuthenticationInfo auth = properties.getRespositoryAuthenticationInfo();
        String[] args = new String[]{"svn", command, "-r", "HEAD:" + since, "--username", auth.getUsername(), "--password", auth.getPassword()};
        System.out.print("Getting "+command+" for " + dependency.getName() + "...");
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
