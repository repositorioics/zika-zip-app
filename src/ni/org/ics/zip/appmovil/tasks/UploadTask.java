package ni.org.ics.zip.appmovil.tasks;

import ni.org.ics.zip.appmovil.database.ZipAdapter;
import ni.org.ics.zip.appmovil.listeners.UploadListener;
import android.content.Context;
import android.os.AsyncTask;

public abstract class UploadTask extends
AsyncTask<String, String, String> {

	protected static final String TAG = UploadTask.class.getSimpleName();

	private Context mContext;

	public UploadTask(Context context) {
		mContext = context;
	}
	protected UploadListener mStateListener;
	protected ZipAdapter va = new ZipAdapter(mContext,"1234",false);

	@Override
	protected void onProgressUpdate(String... values) {
		synchronized (this) {
			if (mStateListener != null) {
				// update progress and total
				mStateListener.progressUpdate(values[0], Integer.valueOf(values[1]), Integer.valueOf(values[2]));
			}
		}

	}

	@Override
	protected void onPostExecute(String result) {
		synchronized (this) {
			if (mStateListener != null)
				mStateListener.uploadComplete(result);
		}
	}

	public void setUploadListener(UploadListener sl) {
		synchronized (this) {
			mStateListener = sl;
		}
	}
}

