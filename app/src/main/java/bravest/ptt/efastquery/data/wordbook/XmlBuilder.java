package bravest.ptt.efastquery.data.wordbook;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by pengtian on 2017/2/13.
 * Create XML use DOM
 */

public class XmlBuilder {

    private static final String TAG = "XmlBuilder";

    public static final String ELEMENT_ROOT = "wordbook";
    public static final String ELEMENT_ITEM = "item";
    public static final String ELEMENT_WORD = "word";
    public static final String ELEMENT_TRANS = "trans";
    public static final String ELEMENT_PHONETIC = "phonetic";
    public static final String ELEMENT_TAGS = "tags";
    public static final String ELEMENT_PROGRESS = "progress";

    public static final String EXTERNAL_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/efastquery/";

    private static final String EXTERNAL_XML_DIR = "xml/";

    private XmlBuilder() {
    }

    public static XmlBuilder getInstance() {
        return new XmlBuilder();
    }

    public String domCreateXML(ArrayList<WordBook> datas, String group) {
        String xmlWriter = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlVersion("1.0");

            Element root = document.createElement(ELEMENT_ROOT);
            document.appendChild(root);

            int datasLength = datas.size();
            for (int i = 0; i < datasLength; i++) {
                WordBook data = datas.get(i);
                Element item = document.createElement(ELEMENT_ITEM);

                //word
                Element word = document.createElement(ELEMENT_WORD);
                Node wordText = document.createTextNode(data.getWord());
                word.appendChild(wordText);
                item.appendChild(word);

                //trans
                Element trans = document.createElement(ELEMENT_TRANS);
                Node transText = document.createCDATASection(data.getTrans());
                trans.appendChild(transText);
                item.appendChild(trans);

                //phonetic
                Element phonetic = document.createElement(ELEMENT_PHONETIC);
                Node phoneticText = document.createCDATASection(data.getPhonetic());
                phonetic.appendChild(phoneticText);
                item.appendChild(phonetic);

                //tags
                Element tags = document.createElement(ELEMENT_TAGS);
                Node tagsText = document.createTextNode(data.getTags());
                tags.appendChild(tagsText);
                item.appendChild(tags);

                //progress
                Element progress = document.createElement(ELEMENT_PROGRESS);
                Node progressText = document.createTextNode(data.getProgress() + "");
                progress.appendChild(progressText);
                item.appendChild(progress);

                //Add item
                root.appendChild(item);
            }

            Properties properties = new Properties();
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
            properties.setProperty(OutputKeys.VERSION, "1.0");
            properties.setProperty(OutputKeys.ENCODING, "utf-8");
            properties.setProperty(OutputKeys.METHOD, "xml");
            properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperties(properties);

            File xmlDir;
            File xmlFile;
            String xmlDirPath = EXTERNAL_DIR + EXTERNAL_XML_DIR;
            String filepath = group;

            if (filepath == null) {
                filepath = "default";
            }

            //check xml directory exist
            xmlDir = new File(xmlDirPath);
            Log.d(TAG, "domCreateXML:  xmldir =  " + xmlDirPath);
            if (!xmlDir.exists()) {
                Log.d(TAG, "domCreateXML: xml dir not exist");
                if (xmlDir.mkdirs()) {
                    Log.d(TAG, "domCreateXML: create dir success");
                }else {
                    Log.d(TAG, "domCreateXML: create dir failed");
                }
            }

            //check xml file name
            xmlFile = new File(xmlDirPath,filepath + ".xml");
            if (!xmlFile.exists()) {
                xmlFile.createNewFile();
            } else {
                xmlFile = new File(xmlDirPath,filepath + "_" +System.currentTimeMillis()+ ".xml");
                xmlFile.createNewFile();
            }

            Log.d(TAG, "domCreateXML: xmlFile.createNewFile();");
            PrintWriter pw = new PrintWriter(
                    new FileOutputStream(xmlFile)
            );

            DOMSource domSource = new DOMSource(document.getDocumentElement());
            StreamResult result = new StreamResult(pw);
            transformer.transform(domSource, result);

            xmlWriter = pw.toString();

            pw.close();
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "domCreateXML: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            Log.d(TAG, "domCreateXML: " + e);
            e.printStackTrace();
        } catch (TransformerException e) {
            Log.d(TAG, "domCreateXML: " + e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "domCreateXML: " + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "domCreateXML: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return xmlWriter;
    }
}
