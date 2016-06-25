package filter;

import static javafx.concurrent.Worker.State.FAILED;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

/**
 * The <code>WebViewFXPanel</code> class defines a panel
 * and allows its user class to load and display a web page
 * like web browser.
 *  
 * @author Tran Xuan Hoang
 */
public class WebViewFXPanel extends JFXPanel {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	private WebEngine engine;

	private JLabel lblStatus;
	private JProgressBar progressBar;
	private InfoFilterFrame infoFilterFrame;

	public WebViewFXPanel(JLabel taskProgressLabel,
			JProgressBar taskProgressBar,
			InfoFilterFrame infoFilterFrame) {
		this.lblStatus = taskProgressLabel;
		this.progressBar = taskProgressBar;
		this.infoFilterFrame = infoFilterFrame;

		createScene();
	}

	private void createScene() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WebView view = new WebView();
				engine = view.getEngine();

				// set the JavaScript status handler
				engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
					@Override
					public void handle(final WebEvent<String> event) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								lblStatus.setText(event.getData());
							}
						});
					}
				});

				// show how many percent the web page has been loaded on the progress bar
				engine.getLoadWorker().workDoneProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observableValue,
							Number oldValue, final Number newValue) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								progressBar.setValue(newValue.intValue());
							}
						});
					}
				});

				// show error message if any error occurs while loading web page
				// (i.e. no network connection)
				engine.getLoadWorker()
				.exceptionProperty()
				.addListener(new ChangeListener<Throwable>() {
					@Override
					public void changed(ObservableValue<? extends Throwable> o,
							Throwable old, final Throwable value) {
						if (engine.getLoadWorker().getState() == FAILED) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(
											infoFilterFrame,
											(value != null)
											? engine.getLocation() + "\n" + value.getMessage()
											: engine.getLocation() + "\nUnexpected error.",
											"Loading error...",
											JOptionPane.ERROR_MESSAGE);
								}
							});
						}
					}
				});

				setScene(new Scene(view));
			}
		});
	}

	/**
	 * Loads the web page with the given URL.
	 * @param url the URL of the web page to be loaded.
	 */
	public void loadURL(String url) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String tmp = toURL(url);

				if (tmp == null) {
					tmp = toURL("http://" + url);
				}

				engine.load(tmp);
			}
		});
	}

	private static String toURL(String str) {
		try {
			return new URL(str).toExternalForm();
		} catch (MalformedURLException e) {
			return null;
		}
	}
} // end class WebViewFXPanel