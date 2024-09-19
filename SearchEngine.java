package finalproject;

import java.util.HashMap;
import java.util.ArrayList;


public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, ArrayList of Strings)
	public MyWebGraph internet;
	public XmlParser parser;

	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}


	private void crawlAndIndexHelper(String url) throws Exception {

		for (String pageContent: parser.getContent(url)) {

			ArrayList<String> urlListOfContent = wordIndex.get(pageContent.toLowerCase());

			if (urlListOfContent == null) {
				urlListOfContent = new ArrayList<>();

				urlListOfContent.add(url);

				wordIndex.put(pageContent.toLowerCase(), urlListOfContent);
			}
			else {
				if (!urlListOfContent.contains(url)) {
					urlListOfContent.add(url);
				}
			}
		}
	}

	/*
	 * This does an exploration of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 *
	 * 	This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {
		internet.addVertex(url);

		crawlAndIndexHelper(url);

		internet.setVisited(url, true);

		for (String link: parser.getLinks(url)) {
			if (!internet.getVisited(link)) {
				crawlAndIndex(link);
			}
			internet.addEdge(url, link);
		}
 	}

	/*
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex().
	 * To implement this method, refer to the algorithm described in the
	 * assignment pdf.
	 *
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		for (String url: internet.getVertices()) {
			internet.setPageRank(url, 1.0);
		}

		ArrayList<Double> oldRankList = new ArrayList<>();

		for (String link: internet.getVertices()) {
			oldRankList.add(internet.getPageRank(link));
		}

		computeRanks(internet.getVertices());

		int counter1 = 0;

		for (String url: internet.getVertices()) {
			double oldRank = oldRankList.get(counter1);
			double newRank = internet.getPageRank(url);

			while (!(Math.abs((oldRank) - (newRank)) < epsilon)) {
				oldRank = newRank;

				int counter2 = 0;

				for (String link: internet.getVertices()) {
					oldRankList.set(counter2, internet.getPageRank(link));

					counter2++;
				}

				computeRanks(internet.getVertices());

				newRank = internet.getPageRank(url);
			}
			counter1++;
		}
	}


	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls.
	 * Note that the double in the output list is matched to the url in the input list using
	 * their position in the list.
	 *
	 * This method will probably fit in about 20 lines.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		double damping_factor = 0.5;

		ArrayList<Double> computedRanks = new ArrayList<>();

		for (String links: vertices) {
			double linear_equation = 0;

			for (String linkage: internet.getEdgesInto(links)) {
				linear_equation += (internet.getPageRank(linkage) / internet.getOutDegree(linkage));
			}

			double pageRank = (1 - damping_factor) + (damping_factor * linear_equation);
			computedRanks.add(pageRank);
		}

		int counter = 0;

		for (String links: vertices) {
			internet.setPageRank(links, computedRanks.get(counter));
			counter++;
		}

		return computedRanks;
	}


	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 *
	 * This method will probably fit in about 10-15 lines.
	 */
	public ArrayList<String> getResults(String query) {
		HashMap<String, Double> unsortedArray = new HashMap<>();

		if (wordIndex.containsKey(query.toLowerCase())) {

			for (String url: wordIndex.get(query.toLowerCase())) {
				unsortedArray.put(url, internet.getPageRank(url));
			}
			return Sorting.fastSort(unsortedArray);
		}
		else {
            return new ArrayList<>();
		}
	}
}
