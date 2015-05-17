package sk.upjs.ics.novotnyr.mlt.gui.scp;

import java.io.File;

public class SshHostConfiguration {
    private String hostName;

    private int port;

    private String userName;

    private File privateKeyFile;

    private String privateKeyPassphrase;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public File getPrivateKeyFile() {
        return privateKeyFile;
    }

    public void setPrivateKeyFile(File privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }

    public void setPrivateKeyFile(String privateKeyFile) {
        setPrivateKeyFile(new File(privateKeyFile));
    }

    public String getPrivateKeyPassphrase() {
        return privateKeyPassphrase;
    }

    public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
        this.privateKeyPassphrase = privateKeyPassphrase;
    }
}
