package sk.upjs.ics.novotnyr.mlt.gui;

import sk.upjs.ics.novotnyr.mlt.HttpTransportPipeEvent;
import sk.upjs.ics.novotnyr.mlt.HttpTransportPipeEventExporter;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportLogEventAction extends AbstractAction {

	private HttpTransportPipeEventExporter exporter = new HttpTransportPipeEventExporter();

	private HttpTransportPipeEvent logEvent;

	private Configuration configuration = Configuration.getInstance();

	private Window parentWindow;

	public ExportLogEventAction(Window parent) {
		super("Export log event", new ImageIcon(ClassLoader.getSystemResource("Data-Export-32.png")));
		this.parentWindow = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser(configuration.getExportFolder());
		if(fileChooser.showSaveDialog(parentWindow) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File selectedFile = fileChooser.getSelectedFile();
		configuration.setExportFolder(selectedFile.getParentFile());
		export(selectedFile);
	}

	private void export(File file) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			exporter.export(logEvent, fileWriter);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parentWindow, "Unable to export log event", "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			if(fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public void setLogEvent(HttpTransportPipeEvent logEvent) {
		this.logEvent = logEvent;
	}
}
