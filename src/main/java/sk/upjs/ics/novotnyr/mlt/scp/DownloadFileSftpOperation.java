package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;
import java.util.Scanner;

public class DownloadFileSftpOperation extends SftpChannelHandler {
    private String remoteFilePath;

    public DownloadFileSftpOperation(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    @Override
    protected void doInChannel(ChannelSftp channel) {
        try {
            InputStream inputStream = channel.get(this.remoteFilePath);
            Scanner scanner = new Scanner(inputStream);
            if(scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (SftpException e) {
            throw new JSchChannelOperationException("Unable to get file from " + this.remoteFilePath);
        }
    }
}
