package com.example.bodysway;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.ScatterData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Acquisition implements Comparable<Object> {

    private static final String TAG = "Accelerometer";

    private String filename;

    private int idPatient;

    private String nom;

    private String prenom;

    private Date dateAcquisiton;

    private String dateString;

    private List<Mesure> mesures;

    private int rate;

    private int time;

    private boolean eyesOpen = true;

    public Acquisition() {
    }

    public Acquisition(String nom, String prenom, int id, int rate, int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateAcquisiton = new Date();
        dateString = formatter.format(this.dateAcquisiton);
        this.idPatient = id;
        this.nom = nom;
        this.prenom = prenom;
        this.rate = rate;
        this.time = time;
    }

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getDateAcquisiton() {
        return dateAcquisiton;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public void setDateAcquisiton(Date dateAcquisiton) {
        this.dateAcquisiton = dateAcquisiton;
    }

    public List<Mesure> getMesures() {
        return mesures;
    }

    public void setMesures(List<Mesure> mesures) {
        this.mesures = mesures;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isEyesOpen() {
        return eyesOpen;
    }

    public void setEyesOpen(boolean eyesOpen) {
        this.eyesOpen = eyesOpen;
    }

    public String toString() {
        return this.nom + " " + this.prenom
                + "\nDate : " + this.dateString
                + "\nYeux " + (this.eyesOpen ? "ouverts" : "fermés")
                + "\nFréquence : " + this.rate + " Hz"
                + "\nDurée : " + this.time + " s";
    }

    public void sortAcquisition() {
        Collections.sort(this.mesures);
    }

    public LineData fromListToLineData() {
        Collections.sort(mesures);
        LineData data = new LineData();
        for (int i = 0; i < mesures.size(); i++) {
            data.addEntry(new Entry(mesures.get(i).getX(), mesures.get(i).getZ()), 0);
        }
        return data;
    }

    public ScatterData fromListToScatterData() {
        Collections.sort(mesures);
        ScatterData data = new ScatterData();
        for (int i = 0; i < mesures.size(); i++) {
            data.addEntry(new Entry(mesures.get(i).getX(), mesures.get(i).getZ()), 0);
        }
        return data;
    }

    public void saveAcquisition(String filename, Context context) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename,Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fos, "UTF-8"); //definit le fichier de sortie
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "root");

            serializer.startTag(null, "filename");
            serializer.text(this.filename);
            serializer.endTag(null, "filename");

            serializer.startTag(null, "nom");
            serializer.text(this.nom);
            serializer.endTag(null, "nom");

            serializer.startTag(null, "prenom");
            serializer.text(this.prenom);
            serializer.endTag(null, "prenom");

            serializer.startTag(null, "date");
            serializer.text(this.dateString);
            serializer.endTag(null, "date");

            serializer.startTag(null, "rate");
            serializer.text(String.valueOf(this.rate));
            serializer.endTag(null, "rate");

            serializer.startTag(null, "time");
            serializer.text(String.valueOf(this.time));
            serializer.endTag(null, "time");

            serializer.startTag(null, "eyesOpen");
            serializer.text(String.valueOf(this.eyesOpen));
            serializer.endTag(null, "eyesOpen");

            for(int i = 0; i < mesures.size(); i++) {
                serializer.startTag(null, "measure");

                serializer.startTag(null, "indice");
                serializer.text(String.valueOf(i));
                serializer.endTag(null, "indice");

                serializer.startTag(null, "x");
                serializer.text(String.valueOf(mesures.get(i).getX()));
                serializer.endTag(null, "x");

                serializer.startTag(null, "z");
                serializer.text(String.valueOf(mesures.get(i).getZ()));
                serializer.endTag(null, "z");

                serializer.endTag(null, "measure");
            }


            serializer.endTag(null,"root");
            serializer.endDocument();
            serializer.flush();

            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Acquisition getAcquisitionFromFile(String filename, Context context) {
        Acquisition acquisition = new Acquisition();

        File dir = context.getFilesDir();
        File xml = new File(dir,filename);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        NodeList items = null;
        Document dom;
        try {
            db = dbf.newDocumentBuilder();
            dom = db.parse(xml);
            Log.d(TAG, "dom : " + dom.toString());
            dom.getDocumentElement().normalize();

            items = dom.getElementsByTagName("filename");
            acquisition.setFilename(items.item(0).getTextContent());
            Log.d(TAG, "filename : " + items.item(0).getTextContent());

            items = dom.getElementsByTagName("nom");
            acquisition.setNom(items.item(0).getTextContent());
            Log.d(TAG, "nom : " + items.item(0).getTextContent());

            items = dom.getElementsByTagName("prenom");
            acquisition.setPrenom(items.item(0).getTextContent());
            Log.d(TAG, "prenom : " + items.item(0).getTextContent());

            items = dom.getElementsByTagName("date");
            acquisition.setDateString(items.item(0).getTextContent());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = formatter.parse(items.item(0).getTextContent());
            acquisition.setDateAcquisiton(date);

            items = dom.getElementsByTagName("rate");
            acquisition.setRate(Integer.parseInt(items.item(0).getTextContent()));
            
            items = dom.getElementsByTagName("time");
            acquisition.setTime(Integer.parseInt(items.item(0).getTextContent()));

            items = dom.getElementsByTagName("eyesOpen");
            acquisition.setEyesOpen(Boolean.valueOf(items.item(0).getTextContent()));
            Log.d(TAG, "eyesOpen : " + items.item(0).getTextContent());

            //get all measurement tags
            items = dom.getElementsByTagName("measure");
            Log.d(TAG, "nb of measure = " + items.getLength());
            ArrayList<Mesure> arrayList = new ArrayList<>();
            for (int i = 0; i < items.getLength(); i++){
                Element measure = (Element) items.item(i);
                String indice = null;
                String x = null;
                String z = null;
                //Log.d(TAGREADING,"Measurement : " + i + " length : " + measure.getChildNodes().getLength());
                if ((measure.getElementsByTagName("indice") != null)) {
                    indice = measure.getElementsByTagName("indice").item(0).getTextContent();
                }
                if ((measure.getElementsByTagName("x") != null)) {
                    x = measure.getElementsByTagName("x").item(0).getTextContent();
                }
                if ((measure.getElementsByTagName("z") != null)) {
                    z = measure.getElementsByTagName("z").item(0).getTextContent();
                }
                //for all elements in the document
                //Log.d(TAG,"Measurement : " + i + "\n\tx: " + x + "\n\tz: " + z);
                Mesure mesure = new Mesure(Float.valueOf(x), Float.valueOf(z));
                arrayList.add(mesure);
            }
            acquisition.setMesures(arrayList);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return acquisition;
    }

    @Override
    public int compareTo(Object o) {
        Acquisition a = (Acquisition) o;
        if (this.dateAcquisiton == null | a.getDateAcquisiton() == null) {
            return -1;
        }
        return a.getDateAcquisiton().compareTo(this.dateAcquisiton);
    }
}
