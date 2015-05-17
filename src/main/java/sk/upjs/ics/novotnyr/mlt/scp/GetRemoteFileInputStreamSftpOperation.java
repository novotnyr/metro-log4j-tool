package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;
import java.util.Scanner;

public abstract class GetRemoteFileInputStreamSftpOperation extends SftpChannelHandler {
        private String remoteFilePath;

        public GetRemoteFileInputStreamSftpOperation(String remoteFilePath) {
            this.remoteFilePath = remoteFilePath;
        }

        @Override
        protected void doInChannel(ChannelSftp channel) {
            try {
                InputStream inputStream = channel.get(this.remoteFilePath);
                doInInputStream(inputStream);
            } catch (SftpException e) {
                throw new JSchChannelOperationException("Unable to get file from " + this.remoteFilePath);
            }
        }

    protected abstract void doInInputStream(InputStream inputStream);

}
