package ni.org.ics.zip.appmovil;

import android.app.Application;

public class MyZipApplication extends Application{
	
	private String passApp;

	public String getPassApp() {
		return passApp;
	}

	protected void setPassApp(String passApp) {
		this.passApp = passApp;
	}

}
