package sk.upjs.ics.novotnyr.mlt.gui;

import net.miginfocom.swing.MigLayout;
import sk.upjs.ics.novotnyr.mlt.gui.scp.SshHostConfiguration;
import sk.upjs.ics.novotnyr.mlt.gui.scp.SshHostConfigurationManager;

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

    private JLabel remoteFileLabel = new JLabel("Remote file");

    private JTextField remoteFileTextField = new JTextField(30);

    private JLabel remoteHostLabel = new JLabel("Host");

    private JTextField remoteHostTextField = new JTextField(30);

    private JLabel remotePortLabel = new JLabel("Port");

    private JTextField remotePortTextField = new JTextField(30);

    private JLabel privateKeyFileLabel = new JLabel("Private key file");

    private JTextField privateKeyFileTextField = new JTextField(30);

    private JLabel privateKeyPassphraseLabel = new JLabel("Key passphrase");

    private JTextField privateKeyPassphraseTextField = new JTextField(30);

    private JLabel sshSettingsFileLabel = new JLabel("SSH Config file");

    private JTextField sshSettingsFileTextField = new JTextField(30);

    private JButton sshSettingsFileOpenButton = new JButton("...");

    private final JLabel filterLabel = new JLabel("Include only events after: ");

    private JButton okButton = new JButton("OK");

    private JButton cancelButton = new JButton("Cancel");

    private JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());

    // --- proper ties

    private File sshSettingsFile;

    private SshHostConfiguration sshHostConfiguration;

    // --- dependencies

    private SshHostConfigurationManager sshHostConfigurationManager = new SshHostConfigurationManager();

    public OpenRemoteFileDialog(Window owner) {
        super(owner, "Open remote filtered file...", ModalityType.APPLICATION_MODAL);

        setLayout(new MigLayout("", "[][fill,grow][]", "[][][nogrid]"));

        add(sshSettingsFileLabel);
        add(sshSettingsFileTextField);
        add(sshSettingsFileOpenButton, "wrap");
        sshSettingsFileOpenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenRemoteFileDialog.this.onSshSettingsFileOpenButtonClick(e);
            }
        });

        add(remoteFileLabel);
        add(remoteFileTextField, "wrap");

        add(filterLabel);

        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd. MM. yyyy HH:mm:ss");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(threeDaysAgo());
        add(dateSpinner, "wrap");

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
        this.sshSettingsFile = new File(this.sshSettingsFileTextField.getText());
        loadSshHostConfiguration();
        setVisible(false);
    }

    private void onCancelButtonClick(ActionEvent e) {
        setVisible(false);
        this.sshHostConfiguration = null;
    }


    private void onSshSettingsFileOpenButtonClick(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.sshSettingsFile = fileChooser.getSelectedFile();
            sshSettingsFileTextField.setText(sshSettingsFile.toString());
        }
    }

    private void loadSshHostConfiguration() {
        this.sshHostConfiguration = sshHostConfigurationManager.load(this.sshSettingsFile);
    }

    public SshHostConfiguration getSshHostConfiguration() {
        return sshHostConfiguration;
    }

    public String getRemoteFile() {
        return this.remoteFileTextField.getText();
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
