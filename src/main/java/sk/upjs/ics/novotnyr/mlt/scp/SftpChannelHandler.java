package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.ChannelSftp;

public abstract class SftpChannelHandler extends AbstractChannelOpeningSshSessionHandler<ChannelSftp> {
    public SftpChannelHandler() {
        super("sftp", ChannelSftp.class);
    }

    @Override
    protected abstract void doInChannel(ChannelSftp channel);
}
