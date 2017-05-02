package cza.hack;

import cza.file.FileUtils;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class RomReader {
	public static final int 
	ROM_FLAG = ~0xF8000000,
	POINTER_FLAG = 0x08000000;
	public File mFile;
	private FileChannel mChannel;
	public MappedByteBuffer mBuffer;
	public List<HackLog> mLogs;
	public List<HackLog> mTempLogs;
	
	public RomReader(){
		mLogs = new ArrayList<HackLog>();
		mTempLogs = new ArrayList<HackLog>();
	}
	
	public RomReader(String path) throws Exception{
		this();
		load(path);
	}
	
	public void load(String path) throws Exception{
		mFile = new File(path).getCanonicalFile();
		if ("gba".equals(FileUtils.getType(path))) {
			close();
			mChannel = new RandomAccessFile(path, "rw").getChannel();
			mBuffer = mChannel.map(FileChannel.MapMode.READ_WRITE, 0, mChannel.size());
		} else 
			throw new Exception();
	}
	
	public void position(int addr){
		mBuffer.position(addr & ROM_FLAG);
	}

	public int readByte(int addr){
		position(addr);
		return readByte();
	}
	
	public int readByte(){
		return mBuffer.get() & Coder.FLAG_8BIT;
	}

	public int readHalfWord(int addr){
		position(addr);
		return readHalfWord();
	}

	public int readHalfWord(){
		return readByte() | readByte() << 8;
	}
	
	public long readWord(int addr){
		position(addr);
		return readWord();
	}

	public long readWord(){
		return readHalfWord() | readHalfWord() << 16;
	}

	public long read(int addr, int size){
		if (size == 0)
			return 0;
		position(addr);
		switch (size){
			default:
			case 1:
				return readByte();
			case 2:
				return readHalfWord();
			case 4:
				return readWord();
		}
	}

	public void writeByte(int addr, int value){
		position(addr);
		writeByte(value);
	}

	public void writeByte(int value){
		mBuffer.put((byte)value);
	}

	public void writeHalfWord(int addr, int value){
		position(addr);
		writeHalfWord(value);
	}

	public void writeHalfWord(int value){
		writeByte(value & Coder.FLAG_8BIT);
		writeByte((value >> 8) & Coder.FLAG_8BIT);
	}
	
	public void writeWord(int addr, long value){
		position(addr);
		writeWord(value);
	}

	public void writeWord(long value){
		writeHalfWord((int)(value & Coder.FLAG_16BIT));
		writeHalfWord((int)((value >> 16) & Coder.FLAG_16BIT));
	}
	
	public void addLog(){
		mLogs.addAll(mTempLogs);
		mTempLogs.clear();
	}
	
	public void close() throws Exception{
		if (mChannel != null) {
			mChannel.close();
			mChannel = null;
			mBuffer = null;
		}
	}

	public void ensureCapacity(long size) throws IOException{
		if (size > 0x01400000){
			//不能超过20MB
			throw new IOException("限制大小为20MB");
		}
		if (size > mChannel.size()){
			mBuffer = mChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);
		}
	}
	
	public void save(){
		saveRom(mBuffer);
	}

	public void saveAs(String path) throws Exception{
		File file = new File(path).getCanonicalFile();
		if (mFile.equals(file)) {
			save();
		} else {
			mFile = file;
			mBuffer.position(0);
			FileChannel chanelOut = new RandomAccessFile(file, "rw").getChannel();
			MappedByteBuffer bufferOut = chanelOut.map(FileChannel.MapMode.READ_WRITE, 0, mChannel.size());
			bufferOut.put(mBuffer);
			saveRom(bufferOut);
			mBuffer.clear();
			bufferOut.clear();
			mChannel.close();
			mBuffer = bufferOut;
			mChannel = chanelOut;
		}
	}
	
	public void saveRom(MappedByteBuffer buffer){
		int i;
		int value;
		for (HackLog log : mLogs){
			value = log.value;
			buffer.position(log.addr);
			for (i = 0; i < log.size; i++){
				buffer.put((byte)(value & 0xFF));
				value >>= 8;
			}
		}
		mLogs.clear();
	}
	
	/**
	 * 获取rom信息
	 */
	public String getRomInfo() throws Exception{
		byte[] title = new byte[12];
//		byte[] code = new byte[4];
//		byte[] maker = new byte[2];
		mBuffer.position(0xA0);
		mBuffer.get(title);
		return new String(title);
	}
}
