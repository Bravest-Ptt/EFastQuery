package bravest.ptt.efastquery.data.wordbook;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by pengtian on 2017/2/13.
 */

public class XmlParser {

    private static final String TAG = "PXmlParser";

    private XmlParser() {

    }

    public static XmlParser getInstance() {
        return new XmlParser();
    }


    public ArrayList<WordBook> parseXml(String fileName) {
        ArrayList<WordBook> datas = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            Log.d(TAG, "parseXml: file not exist");
            return datas;
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            Log.d(TAG, "parseXml: filename = " + fileName);

            doc.getDocumentElement().normalize();
            NodeList nlRoot = doc.getElementsByTagName(XmlBuilder.ELEMENT_ROOT);
            Element eleRoot = (Element)nlRoot.item(0);

            NodeList nlItem = eleRoot.getElementsByTagName(XmlBuilder.ELEMENT_ITEM);
            int itemLength = nlItem.getLength();

            Log.d(TAG, "parseXml: itemlength = " +itemLength);
            for (int i = 0; i < itemLength; i++) {
                Element eleItem = (Element) nlItem.item(i);
                WordBook data = new WordBook();

                //word
                NodeList nlWord = eleItem.getElementsByTagName(XmlBuilder.ELEMENT_WORD);
                Element eleWord = (Element) nlWord.item(0);
                String word = eleWord.getChildNodes().item(0).getNodeValue();
                data.setWord(word);

                //trans
                NodeList nlTrans = eleItem.getElementsByTagName(XmlBuilder.ELEMENT_TRANS);
                Element eleTrans = (Element) nlTrans.item(0);
                String trans = eleTrans.getChildNodes().item(0).getNodeValue();
                data.setTrans(trans);

                //phonetic
                NodeList nlPhonetic = eleItem.getElementsByTagName(XmlBuilder.ELEMENT_PHONETIC);
                Element elePhonetic = (Element) nlPhonetic.item(0);
                String phonetic = elePhonetic.getChildNodes().item(0).getNodeValue();
                data.setPhonetic(phonetic);

                //tags
                NodeList nlTags = eleItem.getElementsByTagName(XmlBuilder.ELEMENT_TAGS);
                Element eleTags = (Element) nlTags.item(0);
                String tags = eleTags.getChildNodes().item(0).getNodeValue();
                data.setTags(tags);

                //progress
                NodeList nlProgress = eleItem.getElementsByTagName(XmlBuilder.ELEMENT_TAGS);
                Element eleProgress = (Element) nlProgress.item(0);
                String progress = eleProgress.getChildNodes().item(0).getNodeValue();
                data.setProgress(progress);

                datas.add(data);
            }

        } catch (ParserConfigurationException e) {
            Log.d(TAG, "parseXml: e " + e + 1);
            System.out.println(e.getMessage());
        } catch (SAXException e) {
            Log.d(TAG, "parseXml: e " + e + 2);
            System.out.println(e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "parseXml: e " + e + 3);
            System.out.println(e.getMessage());
        }
        return datas;
    }
}
