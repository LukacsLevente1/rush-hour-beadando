package hu.nye.progtech.rushhour.persistence;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LevelXmlFile {

    public Board loadLevel(String fileName) {
        try {
            return readLevel(fileName);
        } catch (Exception e) {
            System.out.println(
                    "XML palya betoltese sikertelen: "
                            + e.getMessage());
            System.out.println(
                    "Alapertelmezett palya betoltese...");

            return new LevelFile().createDefaultBoard();
        }
    }

    private Board readLevel(String fileName)
            throws IOException, ParserConfigurationException,
            SAXException {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Hianyzik az XML fajl neve.");
        }

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("levels/" + fileName);

        if (input == null) {
            throw new IOException(
                    "Nem talalhato: levels/" + fileName);
        }

        try (InputStream stream = input) {

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            factory.setFeature(
                    "http://apache.org/xml/features/"
                            + "disallow-doctype-decl", true);

            factory.setFeature(
                    "http://xml.org/sax/features/"
                            + "external-general-entities", false);

            factory.setFeature(
                    "http://xml.org/sax/features/"
                            + "external-parameter-entities", false);

            factory.setFeature(
                    XMLConstants.FEATURE_SECURE_PROCESSING, true);

            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder =
                    factory.newDocumentBuilder();

            Document document = builder.parse(stream);

            Element root = document.getDocumentElement();

            if (!"level".equals(root.getTagName())) {
                throw new IllegalArgumentException(
                        "A gyoker elem neve level kell legyen.");
            }

            int rows = Integer.parseInt(
                    root.getAttribute("rows"));

            int columns = Integer.parseInt(
                    root.getAttribute("columns"));

            Board board = new Board(rows, columns);

            NodeList vehicles =
                    root.getElementsByTagName("vehicle");

            for (int i = 0; i < vehicles.getLength(); i++) {
                Element element = (Element) vehicles.item(i);

                String id = element.getAttribute("id");

                int length = Integer.parseInt(
                        element.getAttribute("length"));

                Orientation orientation = Orientation.valueOf(
                        element.getAttribute("orientation"));

                int row = Integer.parseInt(
                        element.getAttribute("row"));

                int column = Integer.parseInt(
                        element.getAttribute("column"));

                String targetValue =
                        element.getAttribute("target");

                if (!targetValue.equals("true")
                        && !targetValue.equals("false")) {
                    throw new IllegalArgumentException(
                            "Hibas target ertek.");
                }

                boolean target =
                        Boolean.parseBoolean(targetValue);

                board.addVehicle(new Vehicle(
                        id, length, orientation,
                        row, column, target));
            }

            return board;
        }
    }
}
