package sk.upjs.ics.novotnyr.mlt.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TempFileCache {
    public static final Logger logger = LoggerFactory.getLogger(TempFileCache.class);

    private Set<File> tempFiles = new HashSet<File>();

    public void add(File tempFile) {
        this.tempFiles.add(tempFile);
    }

    public void evict() {

    }
}
