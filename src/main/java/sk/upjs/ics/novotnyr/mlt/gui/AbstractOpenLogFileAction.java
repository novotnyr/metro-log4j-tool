package sk.upjs.ics.novotnyr.mlt.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public abstract class AbstractOpenLogFileAction extends AbstractAction {
    public AbstractOpenLogFileAction() {
        super();
    }

    public AbstractOpenLogFileAction(String name) {
        super(name);
    }

    public AbstractOpenLogFileAction(String name, Icon icon) {
        super(name, icon);
    }

    protected abstract void onLogFileAvailable(LogFile file);

    protected void notifyProgress(String message) {

    }

    public abstract String getDescription();


}
