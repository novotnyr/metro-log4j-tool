package sk.upjs.ics.novotnyr.mlt.gui.scp;

import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import sk.upjs.ics.novotnyr.mlt.gui.ConfigurationException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ScpFileConfigurationManager {
    public ScpFileConfiguration load(File file) {
        try {
            Yaml yaml = new Yaml();
            Map<String, String> map = yaml.loadAs(new FileReader(file), Map.class);

            ScpFileConfiguration scpFileConfiguration = new ScpFileConfiguration();
            scpFileConfiguration.setRemoteFileName(map.get("remoteFileName"));

            SshHostConfiguration sshHostConfiguration = new SshHostConfiguration();
            sshHostConfiguration.setPrivateKeyFile(map.get("privateKeyFile"));
            sshHostConfiguration.setUserName(map.get("userName"));
            sshHostConfiguration.setPort(Integer.parseInt(map.get("port")));
            sshHostConfiguration.setHostName(map.get("hostName"));

            scpFileConfiguration.setSshHostConfiguration(sshHostConfiguration);

            return scpFileConfiguration;
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Unable to load configuration, file not found " + file, e);
        }
    }

    public void save(ScpFileConfiguration configuration, File file) {
        try {
            DumperOptions options = new DumperOptions();
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);

            Map<String, String> map = new HashMap<String, String>();
            map.put("remoteFileName", configuration.getRemoteFileName());
            map.put("privateKeyFile", configuration.getSshHostConfiguration().getPrivateKeyFile().toString());
            map.put("userName", configuration.getSshHostConfiguration().getUserName());
            map.put("hostName", configuration.getSshHostConfiguration().getHostName());
            map.put("port", String.valueOf(configuration.getSshHostConfiguration().getPort()));

            yaml.dump(map, new FileWriter(file));
        } catch (IOException e) {
            throw new ConfigurationException("Unable to save configuration due to I/O error: " + file, e);
        }
    }
}
