package com.beans;

public class BlockOfData {

	
	int nextBlockId;
	byte[] dataInFile;
	
	public BlockOfData(int dataInFile){
		this.dataInFile = new byte[dataInFile]	;
		nextBlockId = -1;
	}

	public void setDataInFile(byte[] fileData) 
	{
		this.dataInFile = fileData;
	}
	public byte[] getDataInFile() 
	{
		return dataInFile;
	}

}
