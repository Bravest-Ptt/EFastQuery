package bravest.ptt.efastquery.engine.word;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.entity.word.WordBook;
import bravest.ptt.efastquery.interfaces.IWord;
import bravest.ptt.efastquery.listeners.OnBuildListener;
import bravest.ptt.efastquery.engine.Result;

/**
 * Created by pengtian on 2017/2/13.
 */

public class DocBuilder extends Builder{

    public static final String MODE_DOCX = ".docx";
    public static final String MODE_DOC = ".doc";
    public static final String DOC_DIR = "doc/";

    private static final String TEMPLATE_DOC = "template.doc";

    private Context mContext;

    private DocBuilder(Context context) {
        mContext = context;
    }

    public static DocBuilder getInstance(Context context) {
        return new DocBuilder(context);
    }


    private static final String DOT = ".";
    private static final String WORD_PHONETIC_SPACE = " ";
    private static final String TRANS_SPACE = "    ";
    private static final String ENTER_CHAR = "\n";

    public void createDoc(File file, ArrayList<IWord> data) {
        try {
            AssetManager assetManager = mContext.getAssets();

            InputStream is = assetManager.open(TEMPLATE_DOC);
            HWPFDocument doc = new HWPFDocument(is);

            Range range = doc.getRange();
            if (range == null) {
                Toast.makeText(mContext, mContext.getString(R.string.generate_file_failed), Toast.LENGTH_SHORT).show();
                onResult(null, false);
                return;
            }
            int size = data.size();
            for(int i = 0;i < size; i++) {
                WordBook book = (WordBook) data.get(i);
                range.insertAfter((i + 1)
                        + DOT
                        + book.getWord()
                        + WORD_PHONETIC_SPACE
                        + book.getPhonetic()
                        + ENTER_CHAR);
                if (book.getTrans().contains(Result.JSON_SPLIT)) {
                    String[] trans = book.getTrans().split(Result.JSON_SPLIT);
                    for (String tran : trans) {
                        range.insertAfter(TRANS_SPACE + tran + ENTER_CHAR);
                    }
                } else {
                    range.insertAfter( TRANS_SPACE + book.getTrans() + ENTER_CHAR);
                }
                range.insertAfter(ENTER_CHAR);
            }

            //把当前HWPFDocument写到输出流中
            doc.write(new FileOutputStream(file));
            this.closeStream(is);
            Toast.makeText(mContext, mContext.getString(R.string.generate_file_success, file.getAbsolutePath()), Toast.LENGTH_SHORT).show();
            onResult(file, true);
        } catch (IOException e) {
            onResult(null, false);
            Toast.makeText(mContext, mContext.getString(R.string.generate_file_failed), Toast.LENGTH_SHORT).show();
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

    @Override
    public DocBuilder setBuildListener(OnBuildListener listener) {
        super.setBuildListener(listener);
        return this;
    }
}
