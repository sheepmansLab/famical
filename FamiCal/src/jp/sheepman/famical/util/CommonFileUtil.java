package jp.sheepman.famical.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommonFileUtil {

	
	/**
	 * キャッシュファイルを読み取ってfamily_idを返す
	 * @return family_id
	 */
	public static int readChacheFamilyId(File chacheDir){
		int ret = 0;

		//キャッシュファイルを取得
		File chache = new File(chacheDir, CommonConst.CHACHE_FILE);
		//ファイルが存在していた場合
		if(chache.exists()){
			byte[] buffer = new byte[256];
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(chache);
				fis.read(buffer);
				//ファイルの内容を文字列に変換する
				String tmp = new String(buffer, CommonConst.ENCODE);
				//空白を除いた文字列が存在していた場合
				if(tmp.trim().length() > 0){
					try{
						//数値化する(できない場合はエラーとなる)
						ret = Integer.valueOf(tmp.trim());
					} catch(ClassCastException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * キャッシュファイルにfamily_idを書き込む
	 */
	public static void writeChacheFamilyId(File chacheDir, int family_id){
		File chache = new File(chacheDir, CommonConst.CHACHE_FILE);
		FileOutputStream fos = null;
		try {
			//存在している場合削除
			if(chache.exists()){
				chache.delete();
			}
			fos = new FileOutputStream(chache);
			fos.write(String.valueOf(family_id).getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
