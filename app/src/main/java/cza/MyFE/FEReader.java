package cza.MyFE;

import cza.hack.Coder;
import cza.hack.Dictionary;
import cza.hack.RomReader;
import android.util.SparseArray;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;

public class FEReader extends RomReader {
	public static final long PATCHED_POINTER_FLAG = 0x88000000L;
	public Rom mRom;
	public Dictionary mDict;
	private int mFontStart;
	private int mDataEnd;
	private HuffmanTreeNode mRoot;
	private SparseArray<HuffmanTreeNode> mNodes = new SparseArray<HuffmanTreeNode>();
	public Callback mCallback;

	public FEReader(){
		mDict = new Dictionary();
	}
	
	public void loadRom(Rom rom) {
		mRom = rom;
		int pointerStart = (int)readWord(Rom.POINTER_START_POINTER);
		rom.pointerStart = pointerStart + 4;
		mFontStart = (int)readWord(Rom.FONT_POINTER);
		mDataEnd = pointerStart;
		mRoot = new HuffmanTreeNode();
		mRoot.freq = ((int)readWord(mDataEnd) - mFontStart) >> 2;
		buildHuffmanTree(mRoot);
	}

	private void buildHuffmanTree(HuffmanTreeNode node) {
		int addr = mFontStart + (node.freq << 2);
		if (addr > mDataEnd) {
			node.removeFromParent();
			return;
		}
		int word = (int)readWord(addr);
		if (word < 0) {
			//找到码表
			node.code = Coder.reverseHalfWord(word);
			mNodes.append(node.code, node);
			return;
		}
		HuffmanTreeNode left, right;
		left = new HuffmanTreeNode();
		right = new HuffmanTreeNode();
		node.bindLeft(left);
		node.bindRight(right);
		left.freq = word & Coder.FLAG_16BIT;
		buildHuffmanTree(left);
		word >>>= 16;
		right.freq = word;
		buildHuffmanTree(right);
	}

	public String getHuffmanText(int addr){
		position(addr);
		int currentByte = 0;
		int bit = 0;
		int code = 0;
		boolean break_continue;
		HuffmanTreeNode node;
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		while (true) {
			node = mRoot;
			break_continue = false;
			while (node != null) {
				bit--;
				if (bit < 0) {
					currentByte = readByte();
					bit = 7;
				}
				if ((currentByte & 0x01) == 0) 
					node = node.left;
				else 
					node = node.right;
				currentByte >>= 1;
				if (node.isLeaf()) {
					code = node.code;
					if ((code & 0xFF00) != 0) {
						tempList.add(code);
						break_continue = true;
						break;
					} else {
						break;
					}
				}
			}
			if ((!break_continue) && (code & 0xFF) == 0) 
				break;
		}
		return mDict.getText(tempList);
	}
	
	public int[] getHuffmanBytes(String text) throws Exception {
		int[] dictCodes = new int[text.length() + 1];
		int[] huffmanCodes = new int[dictCodes.length << 1];
			mDict.getCodes(text, dictCodes);
		int huffmanCode;
		int huffmanBit = 0;
		int buffer = 0;
		int bit = 0;
		int index = 0;
		HuffmanTreeNode current;
		HuffmanTreeNode parent = null;
		for (int i = 0; i < dictCodes.length; i++){
			current = mNodes.get(dictCodes[i]);
			if (current == null)
				throw new Exception("码表中没有这个字：" + mDict.getText(dictCodes[i]));
			parent = current.parent;
			huffmanCode = 0;
			while (parent != null) {
				huffmanCode |= current.id << huffmanBit;
				huffmanBit++;
				current = current.parent;
				parent = current.parent;
			}
			do {
				huffmanBit--;
				buffer |= ((huffmanCode >> huffmanBit) & 1) << bit;
				bit++;
				if (bit == 8){
					bit = 0;
					huffmanCodes[index++] = buffer;
					buffer = 0;
				}
			} while(huffmanBit > 0);
		}
		if (bit < 8)
			huffmanCodes[index++] = buffer;
		int[] result = new int[index];
		System.arraycopy(huffmanCodes, 0, result, 0, index);
		return result;
	}

	public String toHuffmanText(String text) throws Exception{
		int[] array = getHuffmanBytes(text);
		StringBuilder sb = new StringBuilder();
		for (int bt : array){
			sb.append(Coder.toByteString(bt));
		}
		return sb.toString();
	}
	
	public int readDictCode(){
		return readByte() << 8 | readByte();
	}

	public char readDictChar(){
		return mDict.getText(readDictCode());
	}
	
	public String getDictText(int addr){
		position(addr);
		StringBuilder sb = new StringBuilder();
		char ch;
		while((ch = readDictChar()) != 0){
			sb.append(ch);
		}
		return sb.toString();
	}
	
	public String readText(int addr){
		if (((addr >> 28) & 0xF) == 8)
			return getDictText(addr);
		else 
			return getHuffmanText(addr);
	}
	
	public void writeHuffman(int addr, String text) throws Exception{
		position(addr);
		for (int bt : getHuffmanBytes(text)){
			mBuffer.put((byte)bt);
		}
	}

	public void writeDictCode(int addr, String text){
		position(addr);
		for (int code : mDict.getCodes(text)){
			mBuffer.put((byte)(code >> 8));
			mBuffer.put((byte)(code & Coder.FLAG_8BIT));
		}
	}

	@Override
	public void saveRom(MappedByteBuffer buffer) {
		super.saveRom(buffer);
		if (mCallback != null)
			mCallback.onRomWrite();
		
	}
	
	public void setCallback(Callback callback){
		mCallback = callback;
	}
	
	public boolean requestCheckInnerChange(){
		if (mCallback != null)
			return mCallback.checkChange(Callback.SAVE_CHANGE_INNER);
		return false;
	}
	
	public void notifyDataChanged(){
		mCallback.onRomWrite();
	}

	public interface Callback{
		public int SAVE_CHANGE_INNER = 1; //页内
		public boolean checkChange(int mode);
		public void onRomWrite();
	}
}
