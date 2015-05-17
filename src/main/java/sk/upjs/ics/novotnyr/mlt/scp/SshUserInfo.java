package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SshUserInfo implements UserInfo {
    private static final Logger logger = LoggerFactory.getLogger(SshUserInfo.class);

    private final String userName;
    private final File privateKeyFile;
    private final String privateKeyPassphrase;
    private final boolean isTrustingAllHosts;

    public SshUserInfo(String userName, File privateKeyFile, String privateKeyPassphrase, boolean isTrustingAllHosts) {
        this.userName = userName;
        this.privateKeyFile = privateKeyFile;
        this.privateKeyPassphrase = privateKeyPassphrase;
        this.isTrustingAllHosts = isTrustingAllHosts;
    }

    @Override
    public String getPassphrase() {
        logger.debug("Returning passphrase " + this.privateKeyPassphrase);
        return this.privateKeyPassphrase;
    }

    @Override
    public String getPassword() {
        logger.debug("Returning NULL password");
        return null;
    }

    @Override
    public boolean promptPassword(String message) {
        logger.debug("NOT prompting for password: {}", message);
        return false;
    }

    @Override
    public boolean promptPassphrase(String message) {
        logger.debug("Prompting for passphrase with pre-defined passphrase: {}", message);
        return true;
    }

    @Override
    public boolean promptYesNo(String message) {
        logger.debug("NOT prompting for YES/NO: {}", message);
        return false;
    }

    @Override
    public void showMessage(String message) {
        logger.debug("NOT showing message {}", message);
    }

    public String getUserName() {
        return userName;
    }

    public String getPrivateKeyFileName() {
        return privateKeyFile.toString();
    }

    public boolean isTrustingAllHosts() {
        return isTrustingAllHosts;
    }
}
