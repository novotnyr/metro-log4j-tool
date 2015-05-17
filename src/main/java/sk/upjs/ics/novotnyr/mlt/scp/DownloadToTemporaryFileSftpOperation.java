package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class DownloadToTemporaryFileSftpOperation extends SftpChannelHandler implements SftpProgressMonitor {
    public static final Logger logger = LoggerFactory.getLogger(DownloadToTemporaryFileSftpOperation.class);

    private String remoteFilePath;

    private File tempFile;

    public DownloadToTemporaryFileSftpOperation(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    @Override
    protected void doInChannel(ChannelSftp channel) {
        try {
            tempFile = File.createTempFile(UUID.randomUUID().toString(), ".log");

            logger.info("Downloading to {}" ,tempFile);

            channel.get(this.remoteFilePath, tempFile.toString(), this);
        } catch (SftpException e) {
            throw new JSchChannelOperationException("Unable to get file from " + this.remoteFilePath);
        } catch (IOException e) {
            throw new JSchChannelOperationException("Unable to create temporary file for " + this.remoteFilePath);
        }
    }

    public File getTempFile() {
        return tempFile;
    }

    // SftpProgressMonitor methods

    private long totalFileSize;

    private long processedByteCount;


    @Override
    public void init(int op, String src, String dest, long max) {
        logger.info("OP: {}, src = {}, dest = {}, max = {}", op, src, dest, max);
        this.totalFileSize = max;
    }

    @Override
    public boolean count(long count) {
        logger.info("Count: {}", count);
        processedByteCount += count;
        notifyProgress(((float) processedByteCount / totalFileSize));
        return true;
    }

    @Override
    public void end() {
        logger.info("End");
    }

    protected void notifyProgress(float percentage) {
        logger.info("" + (int) (percentage * 100));
    }

}
