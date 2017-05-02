package cza.app;

import cza.MyFE.R;
import cza.file.FileUtils;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;

public class MyApp extends Application {
	public final static int
	ICON_DIR = R.drawable.file_folder,
	ICON_GBA = R.drawable.file_gba,
	ICON_FILE = R.drawable.file_unknown;
	public static File SD;
	public static String SDPath;
	public static boolean hasSD;
	
	public static Clipboard mCB;
	public static float scale;
	public static final Collator COMPARATOR = Collator.getInstance(java.util.Locale.CHINA);
	//设置
	public static final String KEY_NOTFIRST = "notFirst";

	@Override
	public void onCreate() {
		super.onCreate();
		mCB = new Clipboard(this);
		scale = this.getResources().getDisplayMetrics().density;
		initString();
		initFile();
	}

	protected void initFile(){
		hasSD = Environment.getExternalStorageState()
			.equals(android.os.Environment.MEDIA_MOUNTED);
		if (hasSD){
			SD = Environment.getExternalStorageDirectory();
			SDPath = SD.getPath();
		}
	}
	
	public static boolean isSD(File folder) {
		return folder.getPath().equals(SDPath);
	}

	public static void toast(Context context, CharSequence text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, int resId){
		toast(context, context.getString(resId));
	}

	public static int getIcon(File f){
		if (f.isDirectory()) return ICON_DIR;
		return getIcon(FileUtils.getType(f));
	}

	public static int getIcon(String type){
		if("gba".equals(type))
			return ICON_GBA;
		return ICON_FILE;
	}

	/**
	 * 用程序打开文件
	 */
	public static Intent openFile(File file) throws Exception{
		if (!file.exists() || !file.canRead()){
			throw new Exception();
		}
		String type = FileUtils.getType(file);
		return new Intent()
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			.setAction(Intent.ACTION_VIEW)
			.setDataAndType(Uri.fromFile(file), FileUtils.getMIMEType(type));
	}
	
	/**
	 * 分享文件
	 */
	public static void shareFiles(Context context, File[] files){
		int length = files.length;
		boolean multiple = false;
		if (length == 0)
			return;
		else if (length > 1)
			multiple = true;
		Intent intent = new Intent();
		if (multiple){
			ArrayList<Uri> uris = new ArrayList<Uri>(length);
			for (File file : files) 
				uris.add(Uri.fromFile(file));
			intent.setAction(android.content.Intent.ACTION_SEND_MULTIPLE)
				.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
				.setType("*/*");
		} else {
			intent.setAction(android.content.Intent.ACTION_SEND)
				.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(files[0]))
				.setType(FileUtils.getMIMEType(files[0]));
		}
		context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
	}
	
	/**
	 * 打开图片资源
	 */
	public static Bitmap readBitmap(Context context, int resId){
		return readBitmap(context.getResources().openRawResource(resId));
	}

	/**
	 * 打开图片资源
	 */
	public static Bitmap readBitmap(InputStream is){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		//获取资源图片
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	protected void initString(){
		mCB.hint = getString(R.string.copySucceed);
	}

	public static int dip2px(float dip){
		return (int)(dip * scale + 0.5f);
	}
}
