package sk.upjs.ics.novotnyr.mlt.gui.scp;

public class ScpFileConfiguration {
    private String remoteFileName;

    private SshHostConfiguration sshHostConfiguration;

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

    public SshHostConfiguration getSshHostConfiguration() {
        return sshHostConfiguration;
    }

    public void setSshHostConfiguration(SshHostConfiguration sshHostConfiguration) {
        this.sshHostConfiguration = sshHostConfiguration;
    }
}
