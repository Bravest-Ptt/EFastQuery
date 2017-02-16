package bravest.ptt.efastquery.data.wordbook;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.Bookmarks;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by pengtian on 2017/2/13.
 */

public class DocBuilder {

    public static final String MODE_DOCX = ".docx";
    public static final String MODE_DOC = ".doc";

    private static final String TEMPLATE_DOC = "template.doc";

    private Context mContext;

    private DocBuilder(Context context) {
        mContext = context;
    }

    public static DocBuilder getInstance(Context context) {
        return new DocBuilder(context);
    }

    public void createDocFile(String fileName) {
        if (fileName == null) {
            return;
        }
            createDoc(fileName);
    }


    public void createDoc(String fileName) {
        try {
            if (mContext == null) {
                return;
            }
            AssetManager assetManager = mContext.getAssets();

            InputStream is = assetManager.open(TEMPLATE_DOC);
            HWPFDocument doc = new HWPFDocument(is);

            log(doc.getDocumentText());
            Range range = doc.getRange();
            //读表格
            //读列表

            //删除range
            //Range r = new Range(2, 5, doc);
            //r.delete();//在内存中进行删除，如果需要保存到文件中需要再把它写回文件
            //把当前HWPFDocument写到输出流中
            doc.write(new FileOutputStream(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "he" +TEMPLATE_DOC));
            this.closeStream(is);

        } catch (IOException e) {
            log(e);
            e.printStackTrace();
        }
    }

    /**
     * 关闭输入流
     * @param is
     */
    private void closeStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void log(Object o){
        Log.d("ptt", String.valueOf(o));
    }
}
