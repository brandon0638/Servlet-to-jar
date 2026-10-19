
package com.model;

public class MapFile {

    private String file;
    private String extension;
    private byte[] bytes;

    public MapFile() {
    }

    public MapFile(String file, String extension, byte[] bytes) {
        this.file = file;
        this.extension = extension;
        this.bytes = bytes;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
