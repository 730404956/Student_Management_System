package com.acetering.app.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/4/15
 */

public class FileUtil {

    private Context context;
    private static FileUtil util = null;

    private FileUtil(Context context) {
        this.context = context;
    }

    public static String ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        //打开文件
        File file = new File(path);
        String read_content = "";
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                String encode = getEncodeType(strFilePath);
                InputStream instream = new FileInputStream(file);
                String content = ""; //文件内容字符串
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
                read_content = new String(content.getBytes(encode), encode);
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");

            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return read_content;
    }

    public static FileUtil getInstance(Context context) {
        if (util == null) {
            util = new FileUtil(context);
        }
        return util;
    }

    /**
     * 判断文件的编码格式
     *
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String getEncodeType(String fileName) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(
                new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        return code;
    }

    public static final int FILE_SELECT_CODE = 0x95258f;

    public Intent getFileChooserIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return Intent.createChooser(intent, "Select a File to Upload");
    }
}