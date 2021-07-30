package com.beans;

import com.filesop.FileOperations;

public class ManagedFileBlock 
{
	private String filename;
	private int id_begDisk;
    private int id_endDisk;
    private boolean deleted;
    private String creationDate;
    private int fileSize;
    private String remarks;
	public static final int maxDataSize = 24;
    public static final int maxNameSizeFile = 20;
   
	public ManagedFileBlock() {
        String emptyStr = FileOperations.getInitializationData(FileOperations.disc_Blcks);
        setFilename(emptyStr.substring(0, maxNameSizeFile));
        setStartingDiskBlockID(-1);
        setEndingDiskBlockID(-1);
        setCreationDate(emptyStr.substring(0, maxDataSize));
        setFileSize(-1);
        setRemarks("");
    }
    public String getFilename() {
        return filename;
    }

    public byte[] getFilenameInBytes() {
        return filename.getBytes();
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public int getStartingDiskBlockID() {
        return id_begDisk;
    }

    public void setStartingDiskBlockID(int id_begDisk) {
        this.id_begDisk = id_begDisk;
    }
    public int getEndingDiskBlockID() {
        return id_endDisk;
    }

    public void setEndingDiskBlockID(int id_endDisk) {
        this.id_endDisk = id_endDisk;
    }
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
    	this.deleted = deleted;
    }
    public String getCreateDate() {
        return creationDate;
    }

    public byte[] getCreationDateInBytes() {
        return creationDate.getBytes();
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
    public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
    
}
