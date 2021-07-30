
package com.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;




public class PFSMain 
{
	
    private FileOperations fileOperationsObject = null;
    public String prtSrc="%>";
    public static void main(String[] args) throws IOException {
        FileUtility.createNewDir("Portable_File_System");
        System.out.println("Portable File System");
        System.out.println();
        System.out.println("\t\t*****************************************************************************************************************");
        System.out.println("\t\t*\t\t\t\t\t\tEnter any one:\t\t\t\t\t\t\t*");
        System.out.println("\t\t*****************************************************************************************************************");        
        System.out.print("\t\t*\t1) open pfsFilename - To open pfs\t\t\t");
        System.out.println("2) put filename - To save\t\t\t*");
        System.out.print("\t\t*\t3) get filename - Save to windows\t\t\t");
        System.out.println("4) rm filename  - To delete a file\t\t*");
        System.out.print("\t\t*\t5) dir          - List all pfs files\t\t\t");
        System.out.println("6) putr filename remarks   - Append Remark\t*");
        System.out.print("\t\t*\t7) kill pfsName - To delete pfs\t\t\t\t");
        System.out.println("8) quit       - Get out of the current pfs\t*");
        System.out.println("\t\t*\t9) exit         - close the application\t\t\t\t\t\t\t\t\t*");
        System.out.println("\t\t*****************************************************************************************************************");
        System.out.println();
        System.out.println();
        
        
        new PFSMain().checkCommands();
        
    } 
    
    private void checkCommands() throws IOException {

        while (true) {

            String command;
            String input1 = null;
            String input2 = null;
            System.out.print(prtSrc);
            BufferedReader tBuf = new BufferedReader(new InputStreamReader(System.in));
            String input = tBuf.readLine();

            StringTokenizer difCommands = new StringTokenizer(input, " ");
            if (!difCommands.hasMoreTokens()) {
                continue;
            }
            command = difCommands.nextToken();
            if (difCommands.hasMoreTokens())
            	input1 = difCommands.nextToken();
            if (difCommands.hasMoreTokens())
            	input2 = difCommands.nextToken();
            if (difCommands.hasMoreTokens()) {
                System.out.println("Please enter input in proper format.");
                continue;
            }
            checkCmds(command, input1, input2);
        }
    }

    
    private void checkCmds(String cmd, String input1, String input2) throws IOException 
    { 
      switch (cmd.toLowerCase()) 
        {
            case "open":
                if (input1 != null && input2 == null) {
                	File f1 = new File(input1+".pfs");
                    if (f1.exists() && !f1.isDirectory()) {
                        fileOperationsObject = new FileOperations(input1 + ".pfs");
                        System.out.println("Opened already existing PFS.");
                        prtSrc = input1+">";
                    } else if (!f1.exists()) {
                    	System.out.println("Created new PFS "+input1+".");
                    	prtSrc = input1+">";
                        fileOperationsObject = new FileOperations(input1 + ".pfs");
                    }
                } else {
                    System.out.println("Please enter commands in proper format- open pfsfilename");
                }
               
                break;
            case "put":
                if (input2 != null) {
                    System.out.println("Enter proper input file");
                } else if (input1 != null) {
                    File f = new File(input1);

                    if (!f.exists()) {
                        System.out.println("File doesn't exist");
                    } else if (f.getName().length() > 20) {
                        System.out.println("Make sure file name is less than 20 character");
                    } else {
                    	fileOperationsObject.saveTheFile(input1, (int) f.length());
                    	System.out.println("File added successfully");
                    }
                }
                break;
            case "get":
                if (input2 != null) {
                    System.out.println("Enter proper input file");
                } else if (input1 != null) {
                    if (input1.length() > 20) {
                        System.out.println("Make sure file name is less than 20 character");
                    } else {
                        fileOperationsObject.extractFile(input1);
                        System.out.println("File saved to the external drive.");
                    }
                }
                break;
            case "dir":
                if (input1 != null) {
                    System.out.println("Please enter valid command.");
                } else {
                    fileOperationsObject.displayDirFiles();
                }
                break;
            case "kill":
            	
            	File f = new File(input1+".pfs");
            	boolean deleted = f.delete();
            	 if(deleted) {
            		 System.out.println("PFS killed successfully");
            	 }
            	 else {
            		 System.out.println(" You cannot kill the existing pfs. Please quit the pfs first.");
            	 }
            	  break;
            case "quit":
            	fileOperationsObject.quitPfs(input1 + ".pfs");
            	prtSrc="PFS>";
                break;
            case "rm":
                if (input2 != null) {
                    System.out.println("Please enter proper format");
                } else if (input1 != null) {
                    if (input1.length() > 20) {
                        System.out.println("Make sure file name is less than 20 character");
                    } else {
                        fileOperationsObject.removeFile(input1);
                    }
                }
                break;
            case "putr":
                if (input1 == null ) {
                    System.out.println("No file name defined.");
                    System.out.println("In order to add remarks follow the format - putr filename remarks");
                } 
                else if(input2 == null ) { 
                	 System.out.println("Please add remarks");
                }
                
                else if (input1 != null && input2 != null ) {
                    if (input1.length() > 20) {
                        System.out.println("Make sure file name is less than 20 character");
                    } else {
                    	fileOperationsObject.remarks(input1, input2);
                    }
                }
                break;
            case "exit":
            	System.exit(0);
            	break;
            default:
                System.out.println("Not a valid command");
                break;
        }
    }

}



 class BlockOfData {

	
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
 
 

 class ManagedFileBlock 
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


 class FileOperations 
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

    String myPFS = "";
    public FileOperations(String file) throws IOException 
    {
    	myPFS = file;
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
            System.out.println("Heyyyyyyyy "+file_attr_array[1].getFilename());
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

 class FileUtility {

    public static byte[] convertToArrayBytes(int integerValue) 
    {
        final ByteBuffer byteBufTemp = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        byteBufTemp.order(ByteOrder.LITTLE_ENDIAN);
        byteBufTemp.putInt(integerValue);
        return byteBufTemp.array();
    }

    public static int convertByteArray(byte[] byteArray) 
    {
        final ByteBuffer byteBufTemp = ByteBuffer.wrap(byteArray);
        byteBufTemp.order(ByteOrder.LITTLE_ENDIAN);
        return byteBufTemp.getInt();
    }

    public static void createNewDir(String dirName) 
    {
        String dir_path = System.getProperty("user.home") + "/Desktop/" + dirName;
        File dir = new File(dir_path);
        
        if (!dir.exists()) {
            try 
            {
            	dir.mkdir();
            } 
            catch (Exception exp) 
            {
            	exp.printStackTrace();
            }
        }
    }

}

