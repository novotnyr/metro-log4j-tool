package sk.upjs.ics.novotnyr.mlt.gui.scp;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;
import sk.upjs.ics.novotnyr.mlt.gui.ConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class SshHostConfigurationManager {
    public SshHostConfiguration load(File yamlConfigFile) {
        Yaml yaml = new Yaml(new DefaultRepresenter());
        try {
            SshHostConfiguration configuration = yaml.loadAs(new FileReader(yamlConfigFile), SshHostConfiguration.class);
            return configuration;
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Unable to load SSH user info from " + yamlConfigFile, e);
        }
    }

    public void save(SshHostConfiguration sshHostConfiguration, File yamlConfigFile) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(yamlConfigFile);
            Yaml yaml = new Yaml(new DefaultRepresenter());

            yaml.dump(sshHostConfiguration, fileWriter);
        } catch (Exception e) {
            throw new ConfigurationException("Unable to save SSH user info into file " + yamlConfigFile, e);
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }

    private static class DefaultRepresenter extends Representer {
        public DefaultRepresenter() {
            this.representers.put(File.class, new FileRepresenter());
        }

        public class FileRepresenter implements Represent {
            public Node representData(Object data) {
                File file = (File) data;
                Node scalar = representScalar(new Tag("!!java.io.File"), file.getAbsolutePath());
                return scalar;
            }
        }
    }


    public static void main(String[] args) {
        SshHostConfiguration configuration = new SshHostConfiguration();
        configuration.setHostName("ais2-upvs.ais.upjs.sk");
        configuration.setPort(22);
        configuration.setUserName("test");
        configuration.setPrivateKeyFile(new File("c:\\Projects-upvs\\kluce\\ais2team.private.pem"));
        configuration.setPrivateKeyPassphrase("rnrnrn");

        SshHostConfigurationManager sshUserInfoLoader = new SshHostConfigurationManager();
        sshUserInfoLoader.save(configuration, new File("C:/users/rn/.sshuserinfo.yaml"));

        SshHostConfiguration sshHostConfiguration = sshUserInfoLoader.load(new File("C:/users/rn/.sshuserinfo.yaml"));
    }
}
