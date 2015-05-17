package sk.upjs.ics.novotnyr.mlt.gui;

import sk.upjs.ics.novotnyr.mlt.LogRecordParsingException;
import sk.upjs.ics.novotnyr.mlt.gui.LogFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileSystemLogFile implements LogFile {
    private File file;

    public FileSystemLogFile(File file) {
        if(file == null) {
            throw new NullPointerException("File must be set");
        }
        this.file = file;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new LogRecordParsingException("Unable to open " + file, e);
        }
    }

    @Override
    public String getDescription() {
        return file.toString();
    }
}
