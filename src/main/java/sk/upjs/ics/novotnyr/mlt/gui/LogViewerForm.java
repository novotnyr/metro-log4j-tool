package sk.upjs.ics.novotnyr.mlt.gui;

import com.wordpress.tips4java.IconTableCellRenderer;
import com.wordpress.tips4java.TableColumnAdjuster;
import org.apache.commons.io.FileUtils;
import sk.upjs.ics.novotnyr.mlt.CompositeErrorLogEventDetector;
import sk.upjs.ics.novotnyr.mlt.Http500LogEventDetector;
import sk.upjs.ics.novotnyr.mlt.HttpTransportPipeEvent;
import sk.upjs.ics.novotnyr.mlt.Log4jMetroParser;
import sk.upjs.ics.novotnyr.mlt.SoapFaultLogEventDetector;
import sk.upjs.ics.novotnyr.mlt.gui.scp.OpenRemoteLogFileAction;
import sk.upjs.ics.novotnyr.mlt.gui.scp.SshHostConfiguration;
import sk.upjs.ics.novotnyr.mlt.scp.DownloadToTemporaryFileSftpOperation;
import sk.upjs.ics.novotnyr.mlt.scp.GetRemoteFileInputStreamSftpOperation;
import sk.upjs.ics.novotnyr.mlt.scp.JSchTemplate;
import sk.upjs.ics.novotnyr.mlt.scp.SshUserInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static sk.upjs.ics.novotnyr.mlt.gui.EmptyTableModelDecorator.unwrapModel;

public class LogViewerForm extends JFrame {
	public static final LogFile NO_LOG_FILE = null;
	public static final Date NO_DATE_FILTER = null;
	public static final String IDLE = "Idle";

	private LogEventPanel logEventPanel;

	private JSplitPane splitPane;

	private JTable logEventsTable;
	private TableColumnAdjuster tableColumnAdjuster;
	private ExportLogEventAction exportLogEventAction;

	private JLabel statusBarLabel = new JLabel(IDLE);

	private Configuration configuration = Configuration.getInstance();

	private Date currentFilterSince;

	private AbstractOpenLogFileAction currentLogFileAction;

	public LogViewerForm()  {
		initWindowListener();

		initToolBar();

		initLogEventsTable();

		enableDropSupport();

		updateWindowTitle(NO_LOG_FILE);

		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		JScrollPane logEventsTableScrollPane = new JScrollPane(this.logEventsTable);
		logEventsTableScrollPane.setMinimumSize(new Dimension(800, 300));
		this.splitPane.setLeftComponent(logEventsTableScrollPane);

		this.logEventPanel = new LogEventPanel();
		this.logEventsTable.setMinimumSize(new Dimension(800, 300));
		this.splitPane.setRightComponent(logEventPanel);

		add(this.splitPane);

		add(this.statusBarLabel, BorderLayout.SOUTH);

		setSize(800, 600);
		setLocationRelativeTo(null);
		pack();

		this.splitPane.setResizeWeight(0.5);
		this.splitPane.setDividerLocation(0.5);
	}

	private void initWindowListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				try {
					configuration.save();
				} catch (Exception e) {
					System.err.println("Unable to save configuration");
				}
			}
		});
	}

	private void initToolBar() {
		JToolBar toolBar = new JToolBar();
		initOpenButton(toolBar);
		initRemoteOpenButton(toolBar);
		initExportButton(toolBar);
		initReloadButton(toolBar);

		add(toolBar, BorderLayout.NORTH);
	}

	private void initOpenButton(JToolBar toolBar) {
		JButton openButton = new JButton("Open");
		openButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("Open-32.png")));
		openButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		openButton.setHorizontalTextPosition(SwingConstants.CENTER);
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogViewerForm.this.onOpenButtonClick(e);
			}
		});

		toolBar.add(openButton);
	}

	private void initRemoteOpenButton(JToolBar toolBar) {
		JButton remoteOpenButton = new JButton("Remote Open");
		remoteOpenButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("Open-32.png")));
		remoteOpenButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		remoteOpenButton.setHorizontalTextPosition(SwingConstants.CENTER);
		remoteOpenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogViewerForm.this.onRemoteOpenButtonClick(e);
			}
		});

		toolBar.add(remoteOpenButton);
	}


	private void initExportButton(JToolBar toolBar) {
		exportLogEventAction = new ExportLogEventAction(this);
		exportLogEventAction.setEnabled(false);

		JButton exportButton = new JButton(exportLogEventAction);
		exportButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		exportButton.setHorizontalTextPosition(SwingConstants.CENTER);

		toolBar.add(exportButton);
	}

	private void initReloadButton(JToolBar toolBar) {
		JButton reloadButton = new JButton("Reload");
		reloadButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("icon-reload.png")));
		reloadButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		reloadButton.setHorizontalTextPosition(SwingConstants.CENTER);
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogViewerForm.this.onReloadButtonClick(e);
			}
		});

		toolBar.add(reloadButton);
	}

	private void onOpenButtonClick(ActionEvent e) {
		OpenFilteredFileDialog dialog = new OpenFilteredFileDialog(this);
		dialog.setVisible(true);
		File selectedFile = dialog.getSelectedFile();
		if(selectedFile != null) {
			this.currentFilterSince = dialog.getFilterSince();
			this.currentLogFileAction = new OpenLocalLogFileAction(selectedFile) {
				@Override
				protected void onLogFileAvailable(LogFile logFile) {
					reloadLogFile(logFile);
				}

				@Override
				protected void notifyProgress(String message) {
					updateStatusBar(message);
				}
			};
			this.currentLogFileAction.actionPerformed(null);
		}
		dialog.dispose();
	}

	private void reloadLogFile(LogFile logFile) {
		initLogEventsTableData(logFile, this.currentFilterSince);
		updateWindowTitle(logFile);
		tableColumnAdjuster.adjustColumns();
		this.exportLogEventAction.setEnabled(true);
	}

	private void onReloadButtonClick(ActionEvent e) {
		this.currentLogFileAction.actionPerformed(null);
	}

	private void onRemoteOpenButtonClick(ActionEvent actionEvent) {
		final OpenRemoteFileDialog dialog = new OpenRemoteFileDialog(this);
		dialog.setVisible(true);
		final String remoteFileName = dialog.getRemoteFile();
		SshHostConfiguration sshHostConfiguration = dialog.getSshHostConfiguration();
		if(remoteFileName != null && sshHostConfiguration != null) {
			LogViewerForm.this.currentFilterSince = dialog.getFilterSince();

			this.currentLogFileAction = new OpenRemoteLogFileAction(this, sshHostConfiguration, remoteFileName) {
				@Override
				protected void onLogFileAvailable(LogFile logFile) {
					LogViewerForm.this.reloadLogFile(logFile);
				}

				@Override
				protected void notifyProgress(String message) {
					updateStatusBar(message);
				}
			};
			this.currentLogFileAction.actionPerformed(null);
		}
		dialog.dispose();
	}

	private void updateWindowTitle(LogFile selectedLogFile) {
		StringBuilder title = new StringBuilder("MLT");
		if(selectedLogFile != null) {
			title.append(" [").append(selectedLogFile.getDescription()).append("]");
		}
		this.setTitle(title.toString());
	}

	private LogEventTableCellRenderer getLogEventTableCellRenderer() {
		CompositeErrorLogEventDetector errorDetectors = new CompositeErrorLogEventDetector();
		errorDetectors.addDetector(new Http500LogEventDetector());
		errorDetectors.addDetector(new SoapFaultLogEventDetector());

		return new LogEventTableCellRenderer(errorDetectors);
	}

	private void initLogEventsTable() {
		this.logEventsTable = new JTable();
		this.logEventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.logEventsTable.setRowHeight(64);
		this.logEventsTable.setShowGrid(false);
		this.logEventsTable.setDefaultRenderer(String.class, getLogEventTableCellRenderer());
		this.logEventsTable.setDefaultRenderer(Icon.class, new IconTableCellRenderer());

		initLogEventsTableData(NO_LOG_FILE);

		ListSelectionListener listSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				LogViewerForm.this.onTableSelectionChanged(e);
			}
		};
		this.logEventsTable.getSelectionModel().addListSelectionListener(listSelectionListener);
		this.logEventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableColumnAdjuster = new TableColumnAdjuster(this.logEventsTable);
		tableColumnAdjuster.adjustColumns();

	}

	private void onTableSelectionChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			HttpTransportPipeEvent event = getSelectedLogEvent();
			this.exportLogEventAction.setEnabled(event != null);
			this.exportLogEventAction.setLogEvent(event);
			this.logEventPanel.setLogEvent(event);
		}
	}

	private HttpTransportPipeEvent getSelectedLogEvent() {
		int selectedRow = this.logEventsTable.getSelectedRow();
		if(selectedRow == -1) {
			return null;
		}
		LogEventsTableModel tableModel = unwrapModel(this.logEventsTable, LogEventsTableModel.class);
		return tableModel.getLogEvent(selectedRow);
	}

	private void initLogEventsTableData(final LogFile logFile, final Date filterSince) {
		if(logFile == null) {
			return;
		}

		new SwingWorker<List<HttpTransportPipeEvent>, String>() {
			@Override
			protected List<HttpTransportPipeEvent> doInBackground() {
				publish("START");
				Log4jMetroParser parser = new Log4jMetroParser();
				if(filterSince != null) {
					parser.setSinceDate(filterSince);
				}
				return parser.parse(logFile.getInputStream());
			}

			@Override
			protected void process(List<String> chunks) {
				String message = chunks.get(chunks.size() - 1);
				setLoadingModel();
			}

			private void setLoadingModel() {
				String[][] loadingInProgressModelData = { { "Loading..."} };
				String[] columnNames = { "" };

				DefaultTableModel tableModel = new DefaultTableModel(loadingInProgressModelData, columnNames);
				LogViewerForm.this.logEventsTable.setModel(tableModel);
			}

			@Override
			protected void done() {
				try {
					List<HttpTransportPipeEvent> logEvents = get();
					LogViewerForm.this.logEventsTable.setModel(new EmptyTableModelDecorator(new LogEventsTableModel(logEvents)));
					LogViewerForm.this.tableColumnAdjuster.adjustColumns();
				} catch (InterruptedException e) {
					// ignore
				} catch (ExecutionException e) {
					Throwable doInBackgroundException = e.getCause();
					JOptionPane.showMessageDialog(LogViewerForm.this, doInBackgroundException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}.execute();
	}


	private void initLogEventsTableData(final LogFile logFile) {
		initLogEventsTableData(logFile, NO_DATE_FILTER);
	}

	private void enableDropSupport() {
		DropTargetListener listener = new DropTargetAdapter() {
			@Override
			public void dragOver(DropTargetDragEvent event) {
				if (!event.getTransferable().isDataFlavorSupported(
						DataFlavor.javaFileListFlavor)) {
					event.rejectDrag();
				}
			}

			@Override
			public void drop(DropTargetDropEvent event) {
				Transferable transferable = event.getTransferable();

				try {
					DataFlavor dataFlavor = DataFlavor.javaFileListFlavor;
					// see
					// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6759788
					event.acceptDrop(event.getDropAction());
					List<File> files = (List<File>) transferable
							.getTransferData(dataFlavor);
					File droppedFile = files.get(0);

					LogViewerForm.this.initLogEventsTableData(new FileSystemLogFile(droppedFile));

					event.dropComplete(true);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Component currentWindow = this;
		DropTarget dropTarget = new DropTarget(currentWindow, listener);
		currentWindow.setDropTarget(dropTarget);
	}

	public void updateStatusBar(String message) {
		this.statusBarLabel.setText(message);
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set system L&F");
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LogViewerForm logViewerForm = new LogViewerForm();
				logViewerForm.setVisible(true);
				logViewerForm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			}
		});
	}
}
