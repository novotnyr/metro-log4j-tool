package sk.upjs.ics.novotnyr.mlt.gui;

import sk.upjs.ics.novotnyr.mlt.LogRecordException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RemoteCachedLogFile implements LogFile {
    private final File cachedRemoteLogFile;
    private final String description;

    public RemoteCachedLogFile(File cachedRemoteLogFile, String description) {

        this.cachedRemoteLogFile = cachedRemoteLogFile;
        this.description = description;
    }


    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(this.cachedRemoteLogFile);
        } catch (FileNotFoundException e) {
            throw new LogRecordException("Cached remote file is gone", e);
        }
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
