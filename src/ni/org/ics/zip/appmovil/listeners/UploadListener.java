package ni.org.ics.zip.appmovil.listeners;

public interface UploadListener {
    void uploadComplete(String result);
    void progressUpdate(String message, int progress, int max);
}
