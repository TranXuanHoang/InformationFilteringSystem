package filter;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * The <code>LoadArticles</code> class create underlying thread for
 * loading articles selected by user when the <b>Load Article...</b>
 * menu item is selected.
 * 
 * @author Tran Xuan Hoang
 */
public class LoadArticles extends SwingWorker<Integer, Article> {
	File[] files;
	Vector<Article> articles;
	FilterAgent filterAgent;
	int filterType;

	InfoFilterFrame infoFilterFrame;
	JEditorPane articleEditorPane;
	JProgressBar taskProgressBar;

	private int loadedArts;

	public LoadArticles(File[] files, Vector<Article> articles,
			FilterAgent filterAgent, int filterType,
			InfoFilterFrame infoFilterFrame,
			JEditorPane articleEditorPane,
			JLabel taskProgressLabel,
			JProgressBar taskProgressBar) {
		this.files = files;
		this.articles = articles;
		this.filterAgent = filterAgent;
		this.filterType = filterType;

		this.infoFilterFrame = infoFilterFrame;
		this.articleEditorPane = articleEditorPane;
		this.taskProgressBar = taskProgressBar;

		loadedArts = 0;
	}

	/**
	 * Loads selected articles from personal computer.
	 * This method also sends the corresponding
	 * signal to method 'process' and 'done' to update the
	 * GUI while loading articles.
	 */
	@Override
	protected Integer doInBackground() throws Exception {
		for (int i = 0; i < files.length; i++) {
			String filePath = files[i].toString();

			Article article = loadOneArticle(filePath);

			setProgress(100 * (i + 1) / files.length);

			if (article != null) {
				publish(article);
				loadedArts++;
			}
		}

		return null;
	}

	/**
	 * Updates the loaded articled in the article table on GUI.
	 */
	protected void process(List<Article> publishedVals) {
		for (int i = 0; i < publishedVals.size(); i++) {
			Article article = publishedVals.get(i);

			articles.addElement(article);
			filterAgent.score(article, filterType);

			infoFilterFrame.refreshTable();
			articleEditorPane.setText(article.getBody());
			articleEditorPane.setCaretPosition(0);
		}
	} // end method process

	/**
	 * Update the GUI when finish reading all articles.
	 */
	protected void done() {
		taskProgressBar.setVisible(false);

		infoFilterFrame.displayTaskMSG("Loaded " + loadedArts +
				" article" + (loadedArts > 1 ? "s" : ""));
	} // end method done

	/**
	 * Loads and scores an article from a personal computer.
	 * @param filePath the absolute path of the file from which
	 * the <code>Article</code> will be read.
	 */
	private Article loadOneArticle(String filePath) {
		String fileName = filePath.substring(
				filePath.lastIndexOf(File.separatorChar) + 1);

		if (fileName != null) {
			int type = Article.typeOfFile(fileName);
			Article article = new Article(filePath, type);

			switch (type) {
			case Article.FROM_TEXT_FILE:
				String text = Article.readArticle(filePath);
				article.setBody(text);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_PDF_FILE:
				String pdf = Utilities.getContentsOfPDFFile(filePath);
				article.setBody(pdf);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_MS_WORD_FILE:
				String msword = Utilities.getContentsOfWordFile(filePath);
				article.setBody(msword);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_PPTX_FILE:
				String pptx = Utilities.getContentsOfPPTXFile(filePath);
				article.setBody(pptx);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_HTML_FILE:
				String html = Article.readArticle(filePath);
				article.setBody(html);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/html");
				break;

			default:
				System.out.println("Error: the type of the selected file \"" +
						filePath + "\" is not allowed to be read.");
				return null;
			}

			return article;
		}

		return null;
	}
} // end class LoadArticles