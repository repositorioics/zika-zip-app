package ni.org.ics.zip.appmovil.listeners;
public interface DownloadListener {
	void downloadComplete(String result);
	void progressUpdate(String message, int progress, int max);
}
