package sk.upjs.ics.novotnyr.mlt;

//import com.jcraft.jsch.UserInfo;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.vfs2.*;
//import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
//import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
//import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
//import org.apache.commons.vfs2.provider.sftp.TrustEveryoneUserInfo;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//
//import java.io.File;
//
//public class RemoteLogTest {
//    private static final Log logger = LogFactory.getLog(RemoteLogTest.class);
//
//    public static void main(String[] args) throws FileSystemException {
//        BasicConfigurator.configure();
//        Logger.getRootLogger().setLevel(Level.ALL);
//
//        String remoteFileUrl = "sftp://root@ais2-upvs.ais.upjs.sk/opt/data-ais2upvs-test/apache-tomcat7/logs/ais2-upvs-ws-trace-log.xml";
//
//        File[] identities = { new File("c:\\Projects-upvs\\kluce\\ais2team.private.pem")};
//
//        FileSystemOptions fileSystemOptions = new FileSystemOptions();
//        SftpFileSystemConfigBuilder fsConfig = SftpFileSystemConfigBuilder.getInstance();
//        fsConfig.setStrictHostKeyChecking(fileSystemOptions, "no");
//        fsConfig.setUserDirIsRoot(fileSystemOptions, false);
//        fsConfig.setIdentities(fileSystemOptions, identities);
//
//
//        FileSystemManager fsManager = VFS.getManager();
//        fsManager.setLogger(logger);
//
//        FileObject logFile = fsManager.resolveFile(remoteFileUrl, fileSystemOptions);
//
//        System.out.println(logFile);
//
//    }
//}
