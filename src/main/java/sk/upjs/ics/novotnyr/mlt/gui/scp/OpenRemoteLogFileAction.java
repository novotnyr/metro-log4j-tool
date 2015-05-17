package sk.upjs.ics.novotnyr.mlt.gui.scp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.upjs.ics.novotnyr.mlt.gui.AbstractOpenLogFileAction;
import sk.upjs.ics.novotnyr.mlt.gui.LogFile;
import sk.upjs.ics.novotnyr.mlt.gui.RemoteCachedLogFile;
import sk.upjs.ics.novotnyr.mlt.scp.DownloadToTemporaryFileSftpOperation;
import sk.upjs.ics.novotnyr.mlt.scp.JSchTemplate;
import sk.upjs.ics.novotnyr.mlt.scp.SshUserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

public abstract class OpenRemoteLogFileAction extends AbstractOpenLogFileAction {
    public static final Logger logger = LoggerFactory.getLogger(OpenRemoteLogFileAction.class);

    private Window owner;

    private SshHostConfiguration sshHostConfiguration;

    private String remoteFileName;

    public OpenRemoteLogFileAction(Window owner, SshHostConfiguration sshHostConfiguration, String remoteFileName) {
        super("Open remote log file");
        this.owner = owner;

        this.sshHostConfiguration = sshHostConfiguration;
        this.remoteFileName = remoteFileName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SshUserInfo userInfo = new SshUserInfo(sshHostConfiguration.getUserName(), sshHostConfiguration.getPrivateKeyFile(), sshHostConfiguration.getPrivateKeyPassphrase(), true);
        final JSchTemplate jSchTemplate = new JSchTemplate(userInfo);
        jSchTemplate.setHost(sshHostConfiguration.getHostName());
        jSchTemplate.setPort(sshHostConfiguration.getPort());

        SwingWorker worker = new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                notifyProgress("Connecting to SCP...");

                DownloadToTemporaryFileSftpOperation operation = new DownloadToTemporaryFileSftpOperation(remoteFileName) {
                    @Override
                    protected void notifyProgress(float percentage) {
                        super.notifyProgress(percentage);
                        OpenRemoteLogFileAction.this.notifyProgress("Downloading " + percentage + " %");
                    }
                };
                jSchTemplate.execute(operation);
                return operation.getTempFile();
            }

            @Override
            protected void done() {
                try {
                    File file = get();

                    notifyProgress("Processing file...");
                    onLogFileAvailable(new RemoteCachedLogFile(file, remoteFileName));
                    notifyProgress("Idle");
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(owner, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(owner, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

            }
        };
        worker.execute();
    }

    @Override
    public String getDescription() {
        return this.remoteFileName;
    }

}
