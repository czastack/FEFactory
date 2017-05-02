package cza.file;

import cza.util.CheckableItems;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class FileInfos extends CheckableItems<FileInfo> {
	public int dirCount, selection;
	public String path;

	public FileInfos(String dir){
		path = dir;
	}

	public FileInfos(File folder){
		this(folder.listFiles(FileUtils.VISIBLE));
		path = folder.getPath();
	}

	public FileInfos(File[] fileArr){
		super(fileArr.length);
		LinkedList<FileInfo> mList,
			folders = new LinkedList<FileInfo>(),
			files = new LinkedList<FileInfo>();
		ListIterator<FileInfo> itr;
		for (File file: fileArr) {
			FileInfo info = new FileInfo(file);
			mList = info.isDir ? folders : files;
			itr = mList.listIterator();
			while (itr.hasNext()) {
				if (itr.next().after(info)) {
					itr.previous();
					break;
				}
			}
			itr.add(info);
		}
		addAll(folders);
		addAll(files);
		dirCount = folders.size();
	}

	public void checkInfos(boolean isDir){
		boolean isF = !isDir;
		for (int i = 0; i < dirCount; i++){
			get(i).checked = isDir;
		}
		for (int i = dirCount ; i < size(); i++){
			get(i).checked = isF;
		}
		checkedCount = isDir ? dirCount : size() - dirCount;
	}

	public FileInfos getCheckedInfos(){
		FileInfos chks = new FileInfos(path);
		for (FileInfo info: this){
			if (info.checked){
				chks.add(info);
				if (info.isDir){
					chks.dirCount++;
				}
			}
		}
		return chks;
	}

	public List<FileInfo> getList(boolean isDir){
		return isDir ? subList(0, dirCount) : subList(dirCount, size());
	}

	public FileInfos getInfos(boolean isDir){
		FileInfos infos = new FileInfos(path);
		infos.addAll(getList(isDir));
		infos.dirCount = isDir ? dirCount : 0;
		return infos;
	}
	
	public int indexOf(String name){
		int index = -1;
		for (int i = 0, len = size(); i < len; i++) {
			if (get(i).name.equals(name)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public File getFileAt(int index){
		return new File(path, get(index).name);
	}
	
	public File[] toArray(){
		int length = size();
		File[] array = new File[length];
		for (int i = 0; i < length; i++)
			array[i] = getFileAt(i);
		return array;
	}
}
