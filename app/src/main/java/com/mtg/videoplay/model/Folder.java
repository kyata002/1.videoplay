package com.mtg.videoplay.model;

public class Folder {
    String folderpath;
    int foldersize;

    public Folder(String folderpath, int foldersize) {
        this.folderpath = folderpath;
        this.foldersize = foldersize;
    }


    public String getFolderpath() {
        return folderpath;
    }

    public void setFolderpath(String folderpath) {
        this.folderpath = folderpath;
    }

    public int getFoldersize() {
        return foldersize;
    }

    public void setFoldersize(int foldersize) {
        this.foldersize = foldersize;
    }
}
