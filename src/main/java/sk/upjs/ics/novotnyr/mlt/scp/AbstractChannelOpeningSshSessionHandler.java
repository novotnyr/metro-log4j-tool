package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.*;

public abstract class AbstractChannelOpeningSshSessionHandler<C extends Channel> implements SshSessionHandler {
    private String channelType;

    private Class<C> channelClass;

    public AbstractChannelOpeningSshSessionHandler(String channelType, Class<C> channelClass) {
        this.channelType = channelType;
        this.channelClass = channelClass;
    }

    @Override
    public void doInSession(Session session) throws SshSessionException {
        C channel = openChannel(session, this.channelType, channelClass);
        handleChannel(channel);
        close(channel);
    }

    protected <T> T openChannel(Session session, String channelType, Class<T> channelClass) {
        Channel channel = null;
        try {
            channel = session.openChannel(channelType);
            return (T) channel;
        } catch (JSchException e) {
            throw new JSchChannelOpenException("Unable to open channel of type " + channelType + "", e);
        } catch (ClassCastException e) {
            String foundChannelClass = "N/A";
            if(channel != null) {
                foundChannelClass = channelType.getClass().getName();
            }

            throw new JSchChannelOpenException("Channel has incorrect type. Expecting " + channelClass + " but found " + foundChannelClass);
        }
    }

    protected void handleChannel(C channel) {
        try {
            channel.connect();
            doInChannel(channel);
        } catch (JSchException e) {
            throw new JschChannelConnectionException("Unable to connect to channel " + channel, e);
        } finally {
            channel.disconnect();
        }
    }

    protected abstract void doInChannel(C channel);

    private void close(C channel) {
        channel.disconnect();
    }

}
