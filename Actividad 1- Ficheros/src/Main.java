import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) {
        List<Pelicula> catalogo = leerPeliculas("../datos/Peliculas.txt");

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;
        do {
            System.out.println("\n----- CATÁLOGO -----");
            System.out.println("1. Mostrar todas");
            System.out.println("2. Por género");
            System.out.println("3. Por título");
            System.out.println("4. Exportar a XML");
            System.out.println("5. Importar XML");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Fallaste, introduce numero entero");
                continue;
            }
            switch (opcion) {
                case 1:
                    mostrarTodas(catalogo);
                    break;
                case 2:
                    buscarPorGenero(catalogo, scanner);
                    break;
                case 3:
                    buscarPorTitulo(catalogo, scanner);
                    break;
                case 4:
                    exportarXML(catalogo, "../datos/peliculas.xml");
                    break;
                case 5:
                    List<Pelicula> pelisXML = importarXML("../datos/peliculas.xml");
                    System.out.println("\n--- PELÍCULAS RECUPERADAS DESDE XML ---");
                    for (Pelicula p : pelisXML) {
                        System.out.println(p);
                    }
                    break;
                case 0:
                    System.out.println("Saliendo de la aplicación...");
                    break;
                default:
                    System.out.println("Opción no válida. Introduce un número del 0 al 5.");
            }
        } while (opcion != 0);
        scanner.close();
    }

    public static List<Pelicula> leerPeliculas(String nombreFichero) {
        List<Pelicula> lista = new ArrayList<>();
        File file = new File(nombreFichero);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            int numLinea = 0;

            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] campos = linea.split("\\|");
                if (campos.length >= 5) {
                    try {
                        int id = Integer.parseInt(campos[0].trim());
                        String titulo = campos[1].trim();
                        String director = campos[2].trim();
                        int anio = Integer.parseInt(campos[3].trim());
                        String genero = campos[4].trim();

                        lista.add(new Pelicula(id, titulo, director, anio, genero));
                    } catch (NumberFormatException e) {
                        System.err.println("Error númerico en la linea " + numLinea + ": " + linea);
                    }
                } else {
                    System.err.println("Formato incompleto en la línea " + numLinea);
                }
            }
            System.out.println("Fichero cargado: se han leído " + lista.size() + " películas correctamente.");

        } catch (FileNotFoundException e) {
            // Manejo específico exigido en el tema
            System.err.println("Error [FileNotFoundException]: No se encuentra el archivo en la ruta: " + file.getAbsolutePath());
        } catch (IOException e) {
            // Manejo de error de lectura
            System.err.println("Error [IOException]: Fallo al leer los datos del archivo: " + e.getMessage());
        }
        return lista;
    }

    private static void mostrarTodas(List<Pelicula> peliculas) {
        if (peliculas.isEmpty()) {
            System.out.println("El catálogo está vacío.");
            return;
        }

        System.out.println("\n--- LISTADO DE TÍTULOS ---");
        for (Pelicula p : peliculas) {
            System.out.println("- " + p.getTitulo());
        }
    }
    private static void buscarPorGenero(List<Pelicula> peliculas, Scanner scanner) {
        if (peliculas.isEmpty()) {
            System.out.println("El catálogo está vacío.");
            return;
        }
        Set<String> generosUnicos = new TreeSet<>();
        for (Pelicula p : peliculas) {
            generosUnicos.add(p.getGenero());
        }

        List<String> listaGeneros = new ArrayList<>(generosUnicos);
        System.out.println("\n--- GÉNEROS DISPONIBLES ---");
        for (int i = 0; i < listaGeneros.size(); i++) {
            System.out.println((i + 1) + ". " + listaGeneros.get(i));
        }

        System.out.print("Selecciona el número del género deseado: ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine().trim());
            if (seleccion >= 1 && seleccion <= listaGeneros.size()) {
                String generoElegido = listaGeneros.get(seleccion - 1);

                System.out.println("\n--- Películas del género: " + generoElegido + " ---");
                for (Pelicula p : peliculas) {
                    if (p.getGenero().equalsIgnoreCase(generoElegido)) {
                        System.out.println(p);
                    }
                }
            } else {
                System.out.println("Opción fuera de rango.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Debes escribir un número de la lista.");
        }
    }
    private static void buscarPorTitulo(List<Pelicula> peliculas, Scanner scanner) {
        System.out.print("\nIntroduce el título o fragmento a buscar: ");
        String textoBuscado = scanner.nextLine().trim().toLowerCase();

        if (textoBuscado.isEmpty()) {
            System.out.println("No has introducido ningún texto para la búsqueda.");
            return;
        }

        boolean hayCoincidencias = false;
        System.out.println("\n--- RESULTADOS COINCIDENTES ---");
        for (Pelicula p : peliculas) {
            if (p.getTitulo().toLowerCase().contains(textoBuscado)) {
                System.out.println(p);
                hayCoincidencias = true;
            }
        }

        if (!hayCoincidencias) {
            System.out.println("No se encontró ninguna película que contenga: \"" + textoBuscado + "\"");
        }
    }
    public static void exportarXML(List<Pelicula> peliculas, String nombreFichero) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("catalogo");
            doc.appendChild(rootElement);
            for (Pelicula p : peliculas) {
                Element peliculaElem = doc.createElement("pelicula");
                peliculaElem.setAttribute("id", String.valueOf(p.getId()));

                Element titulo = doc.createElement("titulo");
                titulo.setTextContent(p.getTitulo());
                peliculaElem.appendChild(titulo);

                Element director = doc.createElement("director");
                director.setTextContent(p.getDirector());
                peliculaElem.appendChild(director);

                Element anio = doc.createElement("anio");
                anio.setTextContent(String.valueOf(p.getAny()));
                peliculaElem.appendChild(anio);

                Element genero = doc.createElement("genero");
                genero.setTextContent(p.getGenero());
                peliculaElem.appendChild(genero);
                rootElement.appendChild(peliculaElem);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(nombreFichero));
            transformer.transform(source, result);

            System.out.println("Catálogo exportado con éxito a: " + nombreFichero);

        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Error al generar el archivo XML: " + e.getMessage());
        }
    }
    public static List<Pelicula> importarXML(String nombreFichero) {
        List<Pelicula> listaImportada = new ArrayList<>();
        File file = new File(nombreFichero);

        if (!file.exists()) {
            System.err.println("El fichero XML no existe. Usa primero la opción 4 para generarlo.");
            return listaImportada;
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList listaNodos = doc.getElementsByTagName("pelicula");

            for (int i = 0; i < listaNodos.getLength(); i++) {
                Node nodo = listaNodos.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nodo;

                    int id = Integer.parseInt(elem.getAttribute("id"));
                    String titulo = elem.getElementsByTagName("titulo").item(0).getTextContent();
                    String director = elem.getElementsByTagName("director").item(0).getTextContent();
                    int anio = Integer.parseInt(elem.getElementsByTagName("anio").item(0).getTextContent());
                    String genero = elem.getElementsByTagName("genero").item(0).getTextContent();
                    listaImportada.add(new Pelicula(id, titulo, director, anio, genero));
                }
            }
            System.out.println("XML importado con éxito: " + listaImportada.size() + " películas leídas.");

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error al importar el archivo XML: " + e.getMessage());
        }

        return listaImportada;
    }
}