package sk.upjs.ics.novotnyr.mlt.gui;

import net.miginfocom.swing.MigLayout;
import sk.upjs.ics.novotnyr.mlt.gui.scp.ScpFileConfiguration;
import sk.upjs.ics.novotnyr.mlt.gui.scp.ScpFileConfigurationManager;
import sk.upjs.ics.novotnyr.mlt.gui.scp.SshHostConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OpenRemoteFileDialog extends JDialog {

    private Configuration configuration = Configuration.getInstance();

    private JLabel userNameLabel = new JLabel("User");

    private JTextField userNameTextField = new JTextField(30);

    private JLabel remoteFileLabel = new JLabel("Remote file");

    private JTextField remoteFileTextField = new JTextField(30);

    private JLabel remoteHostLabel = new JLabel("Host");

    private JTextField remoteHostTextField = new JTextField(30);

    private JLabel remotePortLabel = new JLabel("Port");

    private JTextField remotePortTextField = new JTextField(30);

    private JLabel privateKeyFileLabel = new JLabel("Private key file");

    private JTextField privateKeyFileTextField = new JTextField(30);

    private JLabel privateKeyPassphraseLabel = new JLabel("Key passphrase");

    private JPasswordField privateKeyPassphraseTextField = new JPasswordField(30);

    private JButton scpFileConfigurationSaveButton = new JButton("Save");

    private JButton scpFileConfigurationLoadButton = new JButton("Load");

    private final JLabel filterLabel = new JLabel("Include only events after: ");

    private JButton okButton = new JButton("OK");

    private JButton cancelButton = new JButton("Cancel");

    private JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());

    // --- proper ties

    private ScpFileConfiguration scpFileConfiguration;

    // --- dependencies

    private ScpFileConfigurationManager scpFileConfigurationManager = new ScpFileConfigurationManager();


    public OpenRemoteFileDialog(Window owner) {
        super(owner, "Open remote filtered file...", ModalityType.APPLICATION_MODAL);

        setLayout(new MigLayout("", "[][fill,grow]", "[nogrid][][][][][nogrid]"));

        add(remoteHostLabel);
        add(remoteHostTextField, "growx");

        add(remotePortLabel);
        add(remotePortTextField, "wrap, width 40:40:");

        add(userNameLabel);
        add(userNameTextField, "wrap");

        add(privateKeyFileLabel);
        add(privateKeyFileTextField, "wrap");

        add(privateKeyPassphraseLabel);
        add(privateKeyPassphraseTextField, "wrap");

        add(remoteFileLabel);
        add(remoteFileTextField, "wrap");

        add(filterLabel);

        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd. MM. yyyy HH:mm:ss");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(threeDaysAgo());
        add(dateSpinner, "wrap");

        add(scpFileConfigurationLoadButton);
        scpFileConfigurationLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenRemoteFileDialog.this.onScpFileConfigurationLoadButtonClick(e);
            }
        });

        add(scpFileConfigurationSaveButton);
        scpFileConfigurationSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenRemoteFileDialog.this.onScpFileConfigurationSaveButtonClick(e);
            }
        });

        add(okButton, "tag ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenRemoteFileDialog.this.onOkButtonClick(e);
            }
        });

        add(cancelButton, "tag cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenRemoteFileDialog.this.onCancelButtonClick(e);
            }
        });

        setLocationRelativeTo(owner);

        pack();
    }


    private void onOkButtonClick(ActionEvent e) {
        this.scpFileConfiguration = bindToScpFileConfiguration();
        setVisible(false);
    }

    private void onCancelButtonClick(ActionEvent e) {
        setVisible(false);
        this.scpFileConfiguration = null;
    }


    private void onScpFileConfigurationLoadButtonClick(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File sshSettingsFile = fileChooser.getSelectedFile();
            this.scpFileConfiguration = scpFileConfigurationManager.load(sshSettingsFile);
            bindFromScpFileConfiguration();
        }
    }

    private void bindFromScpFileConfiguration() {
        this.userNameTextField.setText(this.scpFileConfiguration.getSshHostConfiguration().getUserName());
        this.remoteHostTextField.setText(this.scpFileConfiguration.getSshHostConfiguration().getHostName());
        this.remotePortTextField.setText(String.valueOf(this.scpFileConfiguration.getSshHostConfiguration().getPort()));
        this.privateKeyFileTextField.setText(this.scpFileConfiguration.getSshHostConfiguration().getPrivateKeyFile().toString());
        this.privateKeyPassphraseTextField.setText(this.scpFileConfiguration.getSshHostConfiguration().getPrivateKeyPassphrase());

        this.remoteFileTextField.setText(scpFileConfiguration.getRemoteFileName());
    }

    private void onScpFileConfigurationSaveButtonClick(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File scpSettingsFile = fileChooser.getSelectedFile();

            ScpFileConfiguration scpFileConfiguration = bindToScpFileConfiguration();
            this.scpFileConfigurationManager.save(scpFileConfiguration, scpSettingsFile);
            this.scpFileConfiguration = scpFileConfiguration;
        }
    }

    private ScpFileConfiguration bindToScpFileConfiguration() {
        ScpFileConfiguration scpFileConfiguration = new ScpFileConfiguration();
        SshHostConfiguration sshHostConfiguration = new SshHostConfiguration();
        scpFileConfiguration.setSshHostConfiguration(sshHostConfiguration);

        sshHostConfiguration.setHostName(remoteHostTextField.getText());
        sshHostConfiguration.setPort(Integer.parseInt(remotePortTextField.getText()));
        sshHostConfiguration.setUserName(userNameTextField.getText());
        sshHostConfiguration.setPrivateKeyFile(privateKeyFileTextField.getText());
        sshHostConfiguration.setPrivateKeyPassphrase(privateKeyPassphraseTextField.getText());

        scpFileConfiguration.setRemoteFileName(remoteFileTextField.getText());
        return scpFileConfiguration;
    }


    public SshHostConfiguration getSshHostConfiguration() {
        return this.scpFileConfiguration.getSshHostConfiguration();
    }

    public String getRemoteFile() {
        return this.scpFileConfiguration.getRemoteFileName();
    }

    private Date threeDaysAgo() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH, -3);
        return calendar.getTime();
    }

    public Date getFilterSince() {
        return (Date) dateSpinner.getModel().getValue();
    }

    public static void main(String[] args) {
        OpenRemoteFileDialog openRemoteFileDialog = new OpenRemoteFileDialog(null);
        openRemoteFileDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        openRemoteFileDialog.setVisible(true);

    }
}
