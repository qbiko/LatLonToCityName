package org.tmc;

import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.*;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import javax.swing.*;

public class ShpToData {
    private static final String CHARSET = "ISO-8859-2";
    public static void main(String[] args) throws Exception {
        //jednostki ewidencyjne
        File jednEwidFile = new File(new File("").getAbsolutePath() + "/src/main/resources/jednostki_ewidencyjne.shp");

        Map<String, Object> jednEwidMap = new HashMap<>();
        jednEwidMap.put("url", jednEwidFile.toURI().toURL());
        jednEwidMap.put( "charset", CHARSET );

        DataStore jednEwidDataStore = DataStoreFinder.getDataStore(jednEwidMap);
        String jednEwidTypeName = jednEwidDataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> jednEwidSource = jednEwidDataStore.getFeatureSource(jednEwidTypeName);
        Filter filter = Filter.INCLUDE;

        final FeatureCollection<SimpleFeatureType, SimpleFeature> jednEwidCollection = jednEwidSource.getFeatures(filter);
        final CoordinateReferenceSystem dataCRS = jednEwidCollection.getSchema().getCoordinateReferenceSystem();

        //gminy
        File gminyFile = new File(new File("").getAbsolutePath() + "/src/main/resources/gminy.shp");

        Map<String, Object> gminyMap = new HashMap<>();
        gminyMap.put("url", gminyFile.toURI().toURL());
        gminyMap.put( "charset", CHARSET );

        DataStore gminyDataStore = DataStoreFinder.getDataStore(gminyMap);
        String gminyTypeName = gminyDataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> gminySource = gminyDataStore.getFeatureSource(gminyTypeName);

        final FeatureCollection<SimpleFeatureType, SimpleFeature> gminyCollection = gminySource.getFeatures(filter);

        //powiaty
        File powiatyFile = new File(new File("").getAbsolutePath() + "/src/main/resources/powiaty.shp");

        Map<String, Object> powiatyMap = new HashMap<>();
        powiatyMap.put("url", powiatyFile.toURI().toURL());
        powiatyMap.put( "charset", CHARSET );

        DataStore powiatyDataStore = DataStoreFinder.getDataStore(powiatyMap);
        String powiatyTypeName = powiatyDataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> powiatySource = powiatyDataStore.getFeatureSource(powiatyTypeName);

        final FeatureCollection<SimpleFeatureType, SimpleFeature> powiatyCollection = powiatySource.getFeatures(filter);

        //wojewodztwa
        File wojFile = new File(new File("").getAbsolutePath() + "/src/main/resources/województwa.shp");

        Map<String, Object> wojMap = new HashMap<>();
        wojMap.put("url", wojFile.toURI().toURL());
        wojMap.put( "charset", CHARSET );

        DataStore wojDataStore = DataStoreFinder.getDataStore(wojMap);
        String wojTypeName = wojDataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> wojSource = wojDataStore.getFeatureSource(wojTypeName);

        final FeatureCollection<SimpleFeatureType, SimpleFeature> wojCollection = wojSource.getFeatures(filter);

        //obreby ewidencyjne
        File obFile = new File(new File("").getAbsolutePath() + "/src/main/resources/obreby_ewidencyjne.shp");

        Map<String, Object> obMap = new HashMap<>();
        obMap.put("url", obFile.toURI().toURL());
        obMap.put( "charset", CHARSET );

        DataStore obDataStore = DataStoreFinder.getDataStore(obMap);
        String obTypeName = obDataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> obSource = obDataStore.getFeatureSource(obTypeName);

        final FeatureCollection<SimpleFeatureType, SimpleFeature> obCollection = obSource.getFeatures(filter);


        //1. Create the frame.
        JFrame frame = new JFrame("FrameDemo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(new Dimension(400,400));


        Container container = frame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        //gdansk 18.630833, 54.337778
        final JTextField latTextField = new JTextField("", 15);
        final JTextField lngTextField = new JTextField("", 15);
        latTextField.setText("54.337778");
        lngTextField.setText("18.630833");

        JLabel latLabel = new JLabel("Szer. geograficzna: ");
        JLabel lngLabel = new JLabel("Dl. geograficzna: ");

        JLabel mjscLabel = new JLabel("Miejscowość: ");
        final JTextField mjscTextField = new JTextField("", 15);

        JLabel gminaLabel = new JLabel("Gmina: ");
        final JTextField gminaTextField = new JTextField("", 15);

        JLabel powiatyLabel = new JLabel("Powiat: ");
        final JTextField powiatyTextField = new JTextField("", 15);

        JLabel wojLabel = new JLabel("Województwo: ");
        final JTextField wojTextField = new JTextField("", 15);

        container.add(lngLabel);
        container.add(lngTextField);
        container.add(latLabel);
        container.add(latTextField);

        JButton button = new JButton("Sprawdź");

        container.add(button);
        container.add(mjscLabel);
        container.add(mjscTextField);
        container.add(gminaLabel);
        container.add(gminaTextField);
        container.add(powiatyLabel);
        container.add(powiatyTextField);
        container.add(wojLabel);
        container.add(wojTextField);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                double longtitude; //dl. geograficzna
                double latitude; //sz. geograficzna

                longtitude = Double.parseDouble(lngTextField.getText());
                latitude = Double.parseDouble(latTextField.getText());

                try {
                    String obResult = getAttributeValue(longtitude,latitude,dataCRS,obCollection);
                    if(obResult.matches(".*\\d+.*"))
                        mjscTextField.setText(prepareText(getAttributeValue(longtitude,latitude,dataCRS,jednEwidCollection)));
                    else
                        mjscTextField.setText(prepareText(obResult));
                } catch (Exception e1) {
                    mjscTextField.setText(e1.getMessage());
                }

                try {
                    gminaTextField.setText(prepareText(getAttributeValue(longtitude,latitude,dataCRS,gminyCollection)));
                } catch (Exception e1) {
                    gminaTextField.setText(e1.getMessage());
                }

                try {
                    powiatyTextField.setText(getAttributeValue(longtitude,latitude,dataCRS,powiatyCollection));
                } catch (Exception e1) {
                    powiatyTextField.setText(e1.getMessage());
                }

                try {
                    wojTextField.setText(getAttributeValue(longtitude,latitude,dataCRS,wojCollection));
                } catch (Exception e1) {
                    wojTextField.setText(e1.getMessage());
                }

                        //lipno 52.844472, 19.172284
                        //gdansk 18.630833, 54.337778
                        //wrzesina 53.794775, 20.257858
                        //zuromin 53.064375, 19.911203
                        //borówno 53.284404, 18.338799
            }
        });

        frame.pack();
        frame.setVisible(true);
    }


    private static String getAttributeValue(double longtitude, double latitude, CoordinateReferenceSystem dataCRS,
                                     FeatureCollection<SimpleFeatureType, SimpleFeature> collection) throws Exception{
        FeatureIterator<SimpleFeature> features = collection.features();
        try {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                MathTransform transform = CRS.findMathTransform(dataCRS, DefaultGeographicCRS.WGS84, true);
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Geometry geometry2 = JTS.transform(geometry, transform);

                MultiPolygon multiPolygon1 = (MultiPolygon) geometry2;

                GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
                Coordinate coord = new Coordinate(longtitude, latitude);
                Point point = geometryFactory.createPoint(coord);
                if (multiPolygon1.contains(point)) {
                    String attributeValue = (String) feature.getAttribute("jpt_nazwa_");
                    return attributeValue;
                }
            }
        } catch(Exception ex){
            throw new Exception("Nie znaleziono podanych współrzędnych");
        }
        finally {
            features.close();
        }
        throw new Exception("Nie znaleziono podanych współrzędnych");
    }

    private static String prepareText(String text){
        if(!text.contains(".")){
            String output = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
            return output;
        }
        return text;
    }

}
