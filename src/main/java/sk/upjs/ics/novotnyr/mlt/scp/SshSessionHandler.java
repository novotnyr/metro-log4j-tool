package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.Session;

public interface SshSessionHandler {
    void doInSession(Session session) throws SshSessionException;
}
