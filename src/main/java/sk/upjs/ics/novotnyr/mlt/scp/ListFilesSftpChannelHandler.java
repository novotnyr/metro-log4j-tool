package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.util.Vector;

public class ListFilesSftpChannelHandler extends SftpChannelHandler {
    @Override
    protected void doInChannel(ChannelSftp channel) {
        listFiles(channel);
    }

    private void listFiles(ChannelSftp sftp)  {
        try {
            final Vector files = sftp.ls(".");
            for (Object obj : files) {
                System.out.println(obj);
            }
        } catch (SftpException e) {
            throw new IllegalStateException(e);
        }
    }

}
