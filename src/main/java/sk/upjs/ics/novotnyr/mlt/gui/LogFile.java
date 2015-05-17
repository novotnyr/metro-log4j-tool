package sk.upjs.ics.novotnyr.mlt.gui;

import java.io.InputStream;

public interface LogFile {
    InputStream getInputStream();

    String getDescription();
}
