package com.filesop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import com.beans.*;

public class FileOperations 
{
    public static final int disc_Blcks = 30;
    public static final int emp_blck = 0;
    public static final int pointerLnkBlck = disc_Blcks + 4;
    public static final int dirInfo = 76;
    public static final int file_Size = 80;
    public static final int directoryInfo = dirInfo + disc_Blcks * file_Size;
    public static final int dataBlckSize = 256;
    public static final int vector_Bit = 0;
    public static final int maxDataBlockSize_avail = 252;
    public static final String extDir = System.getProperty("user.home") + "/Desktop/Portable_File_System/";
    public static final int pfs_Vol_Size = 10240;
    
    private int freeDataBlcks_Pfs;
    ManagedFileBlock file_attr_array[];
    private String vect_Bit;
    private int dBlockIndexFree;
    private String listOfFileBitVector;
    private  RandomAccessFile randomAccessPointer;
    private int blockStartingIndex;
    private int number_of_files;
    byte[] volume;

    
    public FileOperations(String file) throws IOException 
    {
        randomAccessPointer = new RandomAccessFile(file, "rw");
        loadFileInfo();

        if (getRndAccPointer().length() == emp_blck) 
        {
            getRndAccPointer().seek(pfs_Vol_Size - 1);
            getRndAccPointer().write(volume);
            loadFileAttr();
        } else {

        	loadFileProp();
        	getDirMetaData();
        }
        
    }

    private void loadFileInfo() 
    {
    	volume = " ".getBytes();
        file_attr_array = new ManagedFileBlock[32];
        int incrementor = 0;

        while (incrementor < disc_Blcks) {
        	file_attr_array[incrementor] = new ManagedFileBlock();
        	incrementor++;
        }
    }

    

    private int truncateZero(String stringInput) {
        int incrementor = 0;
        while (incrementor < stringInput.length()) 
        {
            if (Character.toString(stringInput.charAt(incrementor)).matches("0")) 
            {
            	 return incrementor;
            }
               
            incrementor++;
        }
        return -1;
    }

    private void loadFileProp() throws IOException 
    {
        byte[] buffer_temp;


        for (int fileInc = 0; fileInc < disc_Blcks; fileInc++) {

        	buffer_temp = new byte[20];

            getRndAccPointer().seek(dirInfo + fileInc * file_Size);
            getRndAccPointer().read(buffer_temp);
            String lv = new String(buffer_temp);
            
            file_attr_array[fileInc].setFilename(lv.substring(0,truncateZero(lv) ));
            buffer_temp = new byte[24];

            getRndAccPointer().seek(dirInfo +
            		fileInc * file_Size + ManagedFileBlock.maxNameSizeFile);

            getRndAccPointer().read(buffer_temp);
            file_attr_array[fileInc].setCreationDate(new String(buffer_temp));

            buffer_temp = new byte[24];

            getRndAccPointer().seek(dirInfo + fileInc * file_Size +
                    ManagedFileBlock.maxNameSizeFile + ManagedFileBlock.maxDataSize);
            getRndAccPointer().read(buffer_temp);
            lv = new String(buffer_temp);


            buffer_temp = new byte[4];
            getRndAccPointer().seek(dirInfo + fileInc * file_Size +
                    ManagedFileBlock.maxNameSizeFile + ManagedFileBlock.maxDataSize);

            getRndAccPointer().read(buffer_temp);
            file_attr_array[fileInc].setStartingDiskBlockID(
                    FileUtility.convertByteArray(buffer_temp));


            buffer_temp = new byte[4];
            getRndAccPointer().read(buffer_temp);
            file_attr_array[fileInc].setEndingDiskBlockID(
                    FileUtility.convertByteArray(buffer_temp));

            buffer_temp = new byte[4];
            getRndAccPointer().read(buffer_temp);
            file_attr_array[fileInc].setFileSize(
                    FileUtility.convertByteArray(buffer_temp));
        }

    }

    
    public static String getInitializationData(int size) 
    {
        String empty = "";
        for (int i = 0; i < size; i++) {
            empty += "0";
        }
        return empty;
    }

    private void writeAttrPFS(int fileInc, ManagedFileBlock fInfo) throws IOException 
    {
        getRndAccPointer().seek(dirInfo + fileInc * file_Size);
        getRndAccPointer().write(fInfo.getFilenameInBytes());
        getRndAccPointer().seek(dirInfo +fileInc * file_Size + ManagedFileBlock.maxNameSizeFile);
        getRndAccPointer().write(fInfo.getCreationDateInBytes());
        getRndAccPointer().seek(dirInfo + fileInc * file_Size + ManagedFileBlock.maxNameSizeFile + ManagedFileBlock.maxDataSize);
        getRndAccPointer().seek(dirInfo + fileInc * file_Size +ManagedFileBlock.maxNameSizeFile + ManagedFileBlock.maxDataSize);
        getRndAccPointer().write(FileUtility.convertToArrayBytes(fInfo.getStartingDiskBlockID()));
        getRndAccPointer().write(FileUtility.convertToArrayBytes(fInfo.getEndingDiskBlockID()));
        getRndAccPointer().write(FileUtility.convertToArrayBytes(fInfo.getFileSize()));
    }

    
    public void getDirMetaData() throws IOException 
    {
        byte[] arrayBytes = new byte[30];
        getRndAccPointer().seek(vector_Bit);
        getRndAccPointer().read(arrayBytes);
        setBitVector(new String(arrayBytes));

        arrayBytes = new byte[4];
        getRndAccPointer().read(arrayBytes);
        setFreeStartingIndex(FileUtility.convertByteArray(arrayBytes));

        arrayBytes = new byte[4];
        getRndAccPointer().read(arrayBytes);
        setFreeBlocks(FileUtility.convertByteArray(arrayBytes));

        arrayBytes = new byte[30];
        getRndAccPointer().read(arrayBytes);
        setFileListBitVector(new String(arrayBytes));

        arrayBytes = new byte[4];
        getRndAccPointer().read(arrayBytes);
        setFreeFileIndex(FileUtility.convertByteArray(arrayBytes));

        arrayBytes = new byte[4];
        getRndAccPointer().read(arrayBytes);
        setFileCount(FileUtility.convertByteArray(arrayBytes));

    }

    private void saveInfoDirToPFS() throws IOException 
    {

        getRndAccPointer().seek(vector_Bit);
        getRndAccPointer().write(getBitVectorInBytes());
        getRndAccPointer().write(
                FileUtility.convertToArrayBytes(getFreeStartingIndex()));//blockStartingIndex 4
        getRndAccPointer().write(
                FileUtility.convertToArrayBytes(getFreeBlocks()));
        getRndAccPointer().write(getFileListBitVectorInBytes());
        getRndAccPointer().write(
                FileUtility.convertToArrayBytes(getFreeFileIndex()));
        getRndAccPointer().write(
                FileUtility.convertToArrayBytes(getFileCount()));
    }

    private void loadFileAttr() throws IOException
    {
        setFreeStartingIndex(0);
        setFreeBlocks(30);
        setBitVector(getInitializationData(disc_Blcks));
        setFileListBitVector(getInitializationData(disc_Blcks));
        setFileCount(30);
        setFreeFileIndex(0);
        saveInfoDirToPFS();
        ManagedFileBlock emptyFileInfo = new ManagedFileBlock();
        for (int inc = 0;inc < disc_Blcks; inc++) {
        	writeAttrPFS(inc, emptyFileInfo);
        }
}

    private int getFileCount() {
        return number_of_files;
    }

    private void setFileCount(int noOfFiles) {
        this.number_of_files = noOfFiles;
    }

    

    private String getFileListBitVector() {
        return listOfFileBitVector;
    }

    private byte[] getFileListBitVectorInBytes() {
        return listOfFileBitVector.getBytes();
    }
    private int getFreeFileIndex() {
        return dBlockIndexFree;
    }

    private void setFreeFileIndex(int freeDataBlockIndex) {
        this.dBlockIndexFree = freeDataBlockIndex;
    }

    private void setFileListBitVector(String fileListBitVector) {
        this.listOfFileBitVector = fileListBitVector;
    }

    private boolean isFreeSpaceAvailable(int sizeInBytes) {
        if (sizeInBytes < getFreeSpaceAvailable()) {
            return true;
        } else
            return false;
    }

    private int getFreeSpaceAvailable() {
        return freeDataBlcks_Pfs * maxDataBlockSize_avail;
    }

    private String getBitVector() {
        return vect_Bit;
    }

    private byte[] getBitVectorInBytes() {
        return vect_Bit.getBytes();
    }

    private void setBitVector(String vector) {
    	vect_Bit = vector;
    }

    private int nextFreeId(StringBuffer vector, int freeIndex) 
    {
        for (int inc = freeIndex + 1; inc < disc_Blcks; inc++) {
            if (vector.charAt(inc) == '0') {
                return inc;
            }
        }
        return -1;
    }

    private RandomAccessFile getRndAccPointer() {
        return randomAccessPointer;
    }

    private int getFreeStartingIndex() {
        return blockStartingIndex;
    }
    private int getFreeBlocks() {
        return freeDataBlcks_Pfs;
    }

    private void setFreeBlocks(int pfsFreeDataBlocks) {
        this.freeDataBlcks_Pfs = pfsFreeDataBlocks;
    }
    private void setFreeStartingIndex(int blockStartingIndex) {
        this.blockStartingIndex = blockStartingIndex;
    }

    public void saveTheFile(String file, int size) throws IOException {

        StringBuffer vct = new StringBuffer(getBitVector());

        int fSysInc = getFreeFileIndex();

        if (fSysInc == -1) {
            System.out.println("Maximum of 15 files can be added");
        } else if (isFreeSpaceAvailable(size)) {
            ManagedFileBlock newFileInfo = file_attr_array[fSysInc];
            int nxtEmptyLoc;
            int availEmptyBlck;
            try (RandomAccessFile fileReader = new RandomAccessFile(file, "r")) {
            	nxtEmptyLoc = getFreeStartingIndex();
            	availEmptyBlck = getFreeBlocks();
                newFileInfo.setFilename(new File(file).getName());
                newFileInfo.setCreationDate(new Date().toLocaleString());
                newFileInfo.setStartingDiskBlockID(nxtEmptyLoc);
                newFileInfo.setFileSize(size);
                while (size > 0) {
                    BlockOfData dBlck = new BlockOfData(size > maxDataBlockSize_avail ? maxDataBlockSize_avail : size);
                    fileReader.read(dBlck.getDataInFile());
                    size -= maxDataBlockSize_avail;

                    getRndAccPointer().seek(directoryInfo + nxtEmptyLoc * dataBlckSize);

                    getRndAccPointer().write(dBlck.getDataInFile());

                    getRndAccPointer().seek(directoryInfo + nxtEmptyLoc * dataBlckSize
                            + maxDataBlockSize_avail);

                    vct.setCharAt(nxtEmptyLoc, '1');
                    if (size > 0) {
                    	nxtEmptyLoc = nextFreeId(vct, nxtEmptyLoc);
                        getRndAccPointer().write(
                                FileUtility.convertToArrayBytes(nxtEmptyLoc));
                    } else {
                        getRndAccPointer().write(
                                FileUtility.convertToArrayBytes(nxtEmptyLoc));
                    }
                    availEmptyBlck--;
                }
            }

            newFileInfo.setEndingDiskBlockID(nxtEmptyLoc);


            setBitVector(vct.toString());

            setFreeStartingIndex(nextFreeId(vct, nxtEmptyLoc));

            setFreeBlocks(availEmptyBlck);

            StringBuffer pfsFileListVector = new StringBuffer(getFileListBitVector());
            pfsFileListVector.setCharAt(fSysInc, '1');
            

            writeAttrPFS(fSysInc, newFileInfo);


            setFreeFileIndex(fetchEmptyFIndex(pfsFileListVector, fSysInc));

            setFileCount(number_of_files - 1);

            setFileListBitVector(pfsFileListVector.toString());


            saveInfoDirToPFS();

        } else {
            System.out.println("Please provide less file size "
                    + getFreeSpaceAvailable() + " bytes"
            );
        }
    }

    private int fetchEmptyFIndex(StringBuffer fLstVct, int fSysInc) {
        for (int i = fSysInc + 1; i < disc_Blcks; i++) {
            if (fLstVct.charAt(i) == '0') {
                return i;
            }
        }
        return -1;
    }

    public void extractFile(String inputFileName) throws IOException {

        int lFreeInc = findAttributes(inputFileName);
        ManagedFileBlock fContBck = file_attr_array[lFreeInc];
        int begDiskBlock = fContBck.getStartingDiskBlockID();
        int endDiskBlock = fContBck.getEndingDiskBlockID();
        int lfiSize = fContBck.getFileSize();
        BlockOfData blockToRead;
        byte[] buffer_temp;

        if (lFreeInc != -1) {
            FileUtility.createNewDir("ext");
            try (FileOutputStream fos = new FileOutputStream(extDir + inputFileName)) {

                do {

                    blockToRead = new BlockOfData(lfiSize > maxDataBlockSize_avail ? maxDataBlockSize_avail : lfiSize);
                    buffer_temp = new byte[4];
                    getRndAccPointer().seek(directoryInfo + begDiskBlock * dataBlckSize);
                    getRndAccPointer().read(blockToRead.getDataInFile());
                    fos.write(blockToRead.getDataInFile());
                    getRndAccPointer().seek(directoryInfo + begDiskBlock * dataBlckSize + maxDataBlockSize_avail);
                    getRndAccPointer().read(buffer_temp);
                    begDiskBlock = FileUtility.convertByteArray(buffer_temp);
                    lfiSize -= maxDataBlockSize_avail;
                } while (begDiskBlock != endDiskBlock);
            }
        } else {
            System.out.println("No Such File exist.");
        }
    }

    public void removeFile(String fn) throws IOException {
    	
   	 int fSysInd = getFreeFileIndex();
        int c = 0;

        for (int inc = 0; inc < fSysInd; inc++) {
            ManagedFileBlock preFileInfo = file_attr_array[inc];
            if (preFileInfo.getFilename().equals(fn)) {
           	 
            	file_attr_array[inc].setDeleted(true);	
                c++;
                break;
            }
        }
        
        if (c == 0) {
            System.out.println("No such file exist!");
            return;
        }
        else {
       	 System.out.println("File deleted successfully!");
        }
       
    }

    public void displayDirFiles() throws IOException 
     {
        int fileSystemIndex = getFreeFileIndex();
        int count = 0;
        System.out.println("********************************************************************************************************************************************************");
        System.out.print("*\tName		");
        System.out.print("		Size			");
        System.out.print("		CreatedOn			");
        System.out.print("Additional Remarks");
        System.out.println("\t\t       *");

        System.out.println("********************************************************************************************************************************************************");
       
        for (int i = 0; i < fileSystemIndex; i++) {
            ManagedFileBlock preFileInfo = file_attr_array[i];
            if (preFileInfo != null && !preFileInfo.isDeleted()) {
                String name = preFileInfo.getFilename();
                int size = preFileInfo.getFileSize();
                String date = preFileInfo.getCreateDate();
                String remarks = preFileInfo.getRemarks();

                

                System.out.print("*\t"+name);
                System.out.print("\t\t\t"+size);
                System.out.print("Bytes");
                System.out.print("\t\t\t\t"+date);
                
                	System.out.print("\t\t\t"+remarks);
                
                System.out.println("\t\t\t       *");

               
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No Files exist");
            return;
        }
        System.out.println("********************************************************************************************************************************************************");

    }
    public void remarks(String fileName, String remarks) {
    	 int fileSystemIndex = getFreeFileIndex();
         int count = 0;

         for (int i = 0; i < fileSystemIndex; i++) {
             ManagedFileBlock preFileInfo = file_attr_array[i];
             if (preFileInfo.getFilename().equals(fileName)) {
                preFileInfo.setRemarks(""+remarks);
                 count++;
                 break;
             }
         }
         
         if (count == 0) {
             System.out.println("File doesn't exist");
             return;
         }
         else {
        	 System.out.println("Remarks added!");
         }
    }

    private int findAttributes(String inputFileName) {

        for (int i = 0; i < file_attr_array.length; i++) {
            if (file_attr_array[i]!=null && file_attr_array[i].getFilename() == null) {
                return -1;
            }
            if (file_attr_array[i]!=null && file_attr_array[i].getFilename().equals(inputFileName)) 
            {
                return i;
            }
        }
        return -1;
    }


    public void quitPfs(String file) throws IOException 
    {
    	
        getRndAccPointer().close();
        
    }
}
