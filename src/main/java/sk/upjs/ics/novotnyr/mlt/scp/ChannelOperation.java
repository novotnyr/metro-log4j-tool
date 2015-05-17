package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.Channel;

public interface ChannelOperation<C extends Channel> {
    void execute(C channel) throws JSchChannelOperationException;
}
