package sk.upjs.ics.novotnyr.mlt.gui;

import java.awt.event.ActionEvent;
import java.io.File;

public abstract class OpenLocalLogFileAction extends AbstractOpenLogFileAction {
    private File logFile;

    public OpenLocalLogFileAction(File logFile) {
        this.logFile = logFile;
    }


    @Override
    public String getDescription() {
        return this.logFile.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        onLogFileAvailable(new FileSystemLogFile(this.logFile));
    }


}
