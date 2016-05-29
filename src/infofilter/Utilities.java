package infofilter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * The <code>Utilities</code> class defines methods that are able
 * to extract text contents of <b>PDF</b> files, <b>MS Word</b> files
 * and open these files using the default applications installed in
 * personal computer. This class also defines method that allow
 * to load a web page given its <b>URL</b>.
 * 
 * @author Tran Xuan Hoang
 */
public class Utilities {
//	public static void main(String[] args) {
//		String pdfFilePath = "C:\\Users\\jaist\\Desktop\\Deep learning.pdf";
//		String link = "https://en.wikipedia.org/wiki/Artificial_neural_network";
//		String wordFilePath = "C:\\Users\\jaist\\Desktop\\Final Examination.docx";
//
//		//String content = getContentsOfPDFFile(pdfFilePath);
//		//System.out.println(content);
//
//		//String contentOfWordFile = getContentsOfWordFile(wordFilePath);
//		//System.out.println(contentOfWordFile);
//
//		//viewFileUsingSystemApp(filePath);
//		//openWebPageUsingSystemBrowser(link);
//		//viewFileUsingSystemApp(wordFilePath);
//	}

	/**
	 * Opens a file using the system application (application
	 * installed in personal computer) that is registered for
	 * opening the file. <code>.pdf</code> file will be opened by
	 * <i>PDF reader</i> app, <code>.txt</code> will be opened by
	 * <i>text reader</i>, so on.
	 * @param filePath the path of the file to be opened.
	 */
	public static void viewFileUsingSystemApp(String filePath) {
		try {
			if (Desktop.isDesktopSupported()) {
				File file = new File(filePath);
				Desktop desktop = Desktop.getDesktop();
				desktop.open(file);
			} else {
				System.out.println(
						"Error: no application registered for PDFs");
			}
		} catch (Exception e) {
			System.out.println("Error: cannot open PDF file. " + e);
		}
	}

	/**
	 * Opens a default web browser to load the given <code>URL</code>
	 * and to display the loaded web page.
	 * @param link the <code>URL</code> of the web page to be loaded.
	 */
	public static void openWebPageUsingSystemBrowser(String link) {
		try {
			if (Desktop.isDesktopSupported()) {
				URI uri = URI.create(link);
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(uri);
			} else {
				System.out.println(
						"Error: no application registered for web browser");
			}
		} catch (Exception e) {
			System.out.println("Error: cannot open the web page. " + e);
		}
	}

	/**
	 * Extracts the text contents of a PDF file.
	 * @param filePath the abstract path of the PDF file to be extracted.
	 * @return the entire text contents of the PDF file.
	 */
	public static String getContentsOfPDFFile(String filePath) {
		try {
			String contents = "";
			PDDocument document = PDDocument.load(new File(filePath));

			if (!document.isEncrypted()) {
				PDFTextStripper extractor = new PDFTextStripper();
				contents = extractor.getText(document);
			}

			return contents;
		} catch (Exception e) {
			System.out.println(
					"Error: cannot extract text of the pdf file " +
							filePath);
			return "";
		}
	}

	/**
	 * Extracts the text contents of a MS Word file.
	 * @param filePath the abstract path of the MS Word file to be extracted.
	 * @return the entire text contents of the MS Word file.
	 */
	public static String getContentsOfWordFile(String filePath) {
		try {
			String contents = "";

			// open a word file and place it in a xwpd format
			File file = new File(filePath);
			InputStream inputStream = new FileInputStream(file);
			XWPFDocument wordFile = new XWPFDocument(inputStream);
			XWPFWordExtractor extractor = new XWPFWordExtractor(wordFile);

			contents = extractor.getText();
			extractor.close();

			return contents;
		} catch (Exception e) {
			System.out.println(
					"Error: cannot extract text of the word file " +
							filePath + e);
			return "";
		}
	}
} // end class Utilities