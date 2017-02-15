package bravest.ptt.efastquery.data.wordbook;

/**
 * Created by pengtian on 2017/2/13.
 */

public class DocBuilder {

    public static final String MODE_DOCX = ".docx";
    public static final String MODE_DOC = ".doc";

    private DocBuilder() {
    }

    public static DocBuilder getInstance() {
        return new DocBuilder();
    }

    public void createDocFile(String fileName) {
        if (fileName == null) {
            return;
        }
        if (fileName.endsWith(MODE_DOC)) {
            createDoc(fileName);
        } else if (fileName.endsWith(MODE_DOCX)) {
            createDocx(fileName);
        }
    }


    private void createDoc(String fileName) {

    }

    private void createDocx(String fileName) {

    }
}
