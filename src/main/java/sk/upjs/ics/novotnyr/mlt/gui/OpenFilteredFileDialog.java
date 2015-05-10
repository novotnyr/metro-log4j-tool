package sk.upjs.ics.novotnyr.mlt.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OpenFilteredFileDialog extends JDialog {
	private final JLabel fileNameLabel;
	private final JComboBox fileNameComboBox;
	private final JButton openFileButton;
	private final JSpinner dateSpinner;
	private final JLabel filterLabel;
	private final JButton okButton;
	private final JButton cancelButton;

	private Configuration configuration = Configuration.getInstance();
	private File selectedFile;

	public OpenFilteredFileDialog(Window owner) throws HeadlessException {
		super(owner, "Open filtered file...", ModalityType.APPLICATION_MODAL);
		setLayout(new MigLayout("", "[][fill,grow][]", "[][nogrid][nogrid]"));
		setLocationRelativeTo(owner);


		fileNameLabel = new JLabel("Filename:");
		add(fileNameLabel);

		fileNameComboBox = new JComboBox(configuration.getMostRecentlyUsedFiles().toArray());
		fileNameComboBox.setEditable(true);
		fileNameComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onFileNameComboBoxActionPerformed(e);
			}
		});
		add(fileNameComboBox);

		openFileButton = new JButton("Open");
		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOpenFileButtonClick(e);
			}
		});
		add(openFileButton, "wrap");

		filterLabel = new JLabel("Include only events after: ");
		add(filterLabel);

		dateSpinner = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd. MM. yyyy HH:mm:ss");
		dateSpinner.setEditor(dateEditor);
		dateSpinner.setValue(threeDaysAgo());
		add(dateSpinner, "wrap");

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOkButtonClick(e);
			}
		});
		add(okButton, "tag ok");

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancelButtonClick(e);
			}
		});
		add(cancelButton, "tag cancel");

		pack();
	}

	private Date threeDaysAgo() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.roll(Calendar.DAY_OF_MONTH, -3);
		return calendar.getTime();
	}

	private void onFileNameComboBoxActionPerformed(ActionEvent e) {
		this.selectedFile = new File(this.fileNameComboBox.getModel().getSelectedItem().toString());
	}

	private void onCancelButtonClick(ActionEvent e) {
		setVisible(false);
		this.selectedFile = null;
	}

	private void onOkButtonClick(ActionEvent e) {
		if(this.selectedFile == null) {
			onFileNameComboBoxActionPerformed(null);
		}
		setVisible(false);
	}

	private void onOpenFileButtonClick(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser(configuration.getMostRecentlyUsedOpenFolder());
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			this.selectedFile = fileChooser.getSelectedFile();
			configuration.setMostRecentlyUsedOpenFolder(this.selectedFile.getParentFile());
			configuration.addMostRecentlyUsedFile(this.selectedFile);
			this.fileNameComboBox.getEditor().setItem(this.selectedFile.getPath());
		}
	}

	public File getSelectedFile() {
		return selectedFile;
	}

	public Date getFilterSince() {
		return (Date) dateSpinner.getModel().getValue();
	}

}
