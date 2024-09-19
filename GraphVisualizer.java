package finalproject;
import java.util.Scanner;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class GraphVisualizer {
    protected Graph graph;
    protected static boolean loop = true;

    public static Graph interpretXml(String path, String url) throws Exception {
        SearchEngine searchEngine = new SearchEngine(path);
        searchEngine.crawlAndIndex(url);
        searchEngine.assignPageRanks(0.01);

        Graph graph = new SingleGraph("graphToVisualize");
        graph.setStrict(false);

        // populate nodes and ranks
        for (String vertex : searchEngine.internet.getVertices()) {
            Node toAdd = graph.addNode(vertex);
            toAdd.setAttribute("ui.label", vertex);
            toAdd.setAttribute("rank", searchEngine.internet.getPageRank(vertex));
            toAdd.setAttribute("layout.weight", 3);
        }

        // create edges
        for (String destination : searchEngine.internet.getVertices()) {
            for (String origin : searchEngine.internet.getEdgesInto(destination)) {
                Edge toAdd = graph.addEdge(
                        (origin + "to" + destination), origin, destination, true);
                toAdd.setAttribute("ui.style", "fill-color: rgb(82,148,168);");
            }
        }

        // assign sizes and colors based on rank
        // find highest ranking page
        double highestRank = -1;
        for (String vertex : searchEngine.internet.getVertices()) {
            double rank = searchEngine.internet.getPageRank(vertex);
            if (rank > highestRank) {
                highestRank = rank;
            }
        }
        for (Node node : graph) {
            double rank = searchEngine.internet.getPageRank(node.getId());
            node.setAttribute("ui.color", (float) (rank / highestRank));
            node.setAttribute("ui.size", (float) (6 + 17.5 * (rank / highestRank)));
        }

        return graph;
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("org.graphstream.ui", "swing");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the xml file name: ");
        String xmlPath = scanner.next();
        System.out.print("Enter the url to start crawling from: ");
        String url = scanner.next();
        Graph graph = interpretXml(xmlPath, url);
        graph.setAttribute("ui.stylesheet",
                "graph { fill-color: rgb(0,0,0); }"
                        + "edge {"
                        + "    arrow-size: 10px, 3px;"
                        + "    size: 1px;"
                        + "    shape: cubic-curve;"
                        + "    arrow-shape: arrow;"
                        + "}"
                        + "node {"
                        + "    text-mode: normal;"
                        + "    text-size: 13;"
                        + "    text-alignment: above;"
                        + "    text-offset: 0px, -10px;"
                        + "    text-color: rgb(255,255,255);"
                        + "    fill-mode: dyn-plain;"
                        + "    text-font: Georgia;"
                        + "    fill-color: yellow, red;" + // gradient from yellow to red
                        "    size-mode: dyn-size;"
                        + "}");
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        viewer.enableAutoLayout();
    }
}
