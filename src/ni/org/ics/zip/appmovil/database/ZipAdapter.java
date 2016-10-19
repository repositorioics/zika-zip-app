package ni.org.ics.zip.appmovil.database;

/**
 * Adaptador de la base de datos Cohorte
 * 
 * @author William Aviles
 */

import java.util.ArrayList;
import java.util.List;

import ni.org.ics.zip.appmovil.domain.Zp00Screening;
import ni.org.ics.zip.appmovil.domain.users.Authority;
import ni.org.ics.zip.appmovil.domain.users.UserSistema;
import ni.org.ics.zip.appmovil.helpers.UserSistemaHelper;
import ni.org.ics.zip.appmovil.helpers.Zp00ScreeningHelper;
import ni.org.ics.zip.appmovil.utils.MainDBConstants;
import ni.org.ics.zip.appmovil.utils.FileUtils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

public class ZipAdapter {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	private final String mPassword;
	private final boolean mFromServer;
	

	public ZipAdapter(Context context, String password, boolean fromServer) {
		mContext = context;
		mPassword = password;
		mFromServer = fromServer;
	}
	
	private static class DatabaseHelper extends ZipSQLiteOpenHelper {

		DatabaseHelper(Context context, String password, boolean fromServer) {
			super(FileUtils.DATABASE_PATH, MainDBConstants.DATABASE_NAME, MainDBConstants.DATABASE_VERSION, context,
					password, fromServer);
			createStorage();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(MainDBConstants.CREATE_USER_TABLE);
			db.execSQL(MainDBConstants.CREATE_ROLE_TABLE);
			db.execSQL(MainDBConstants.CREATE_SCREENING_TABLE);
		}

		@Override
		// upgrading will destroy all old data
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MainDBConstants.USER_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + MainDBConstants.ROLE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + MainDBConstants.SCREENING_TABLE);
			onCreate(db);
		}
	}

	public ZipAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext,mPassword,mFromServer);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}
	
	/**
	 * Crea un cursor desde la base de datos
	 * 
	 * @return cursor
	 */
	public Cursor crearCursor(String tabla, String whereString, String projection[], String ordenString) throws SQLException {
		Cursor c = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tabla);
		c = qb.query(mDb,projection,whereString,null,null,null,ordenString);
		return c;
	}

	public static boolean createStorage() {
		return FileUtils.createFolder(FileUtils.DATABASE_PATH);
	}
	
	/**
	 * Metodos para usuarios en la base de datos
	 * 
	 * @param usuario
	 *            Objeto Usuario que contiene la informacion
	 *
	 */
	//Crear nuevo usuario en la base de datos
	public void crearUsuario(UserSistema user) {
		ContentValues cv = UserSistemaHelper.crearUserSistemaContentValues(user);
		mDb.insert(MainDBConstants.USER_TABLE, null, cv);
	}
	//Editar usuario existente en la base de datos
	public boolean editarUsuario(UserSistema user) {
		ContentValues cv = UserSistemaHelper.crearUserSistemaContentValues(user);
		return mDb.update(MainDBConstants.USER_TABLE, cv, MainDBConstants.username + "='" 
				+ user.getUsername()+"'", null) > 0;
	}
	//Limpiar la tabla de usuarios de la base de datos
	public boolean borrarUsuarios() {
		return mDb.delete(MainDBConstants.USER_TABLE, null, null) > 0;
	}
	//Obtener un usuario de la base de datos
	public UserSistema getUsuario(String filtro, String orden) throws SQLException {
		UserSistema mUser = null;
		Cursor cursorUser = crearCursor(MainDBConstants.USER_TABLE, filtro, null, orden);
		if (cursorUser != null && cursorUser.getCount() > 0) {
			cursorUser.moveToFirst();
			mUser=UserSistemaHelper.crearUserSistema(cursorUser);
		}
		if (!cursorUser.isClosed()) cursorUser.close();
		return mUser;
	}
	//Obtener una lista de usuarios de la base de datos
	public List<UserSistema> getUsuarios(String filtro, String orden) throws SQLException {
		List<UserSistema> mUsuarios = new ArrayList<UserSistema>();
		Cursor cursorUsuarios = crearCursor(MainDBConstants.USER_TABLE, filtro, null, orden);
		if (cursorUsuarios != null && cursorUsuarios.getCount() > 0) {
			cursorUsuarios.moveToFirst();
			mUsuarios.clear();
			do{
				UserSistema mUser = null;
				mUser = UserSistemaHelper.crearUserSistema(cursorUsuarios);
				mUsuarios.add(mUser);
			} while (cursorUsuarios.moveToNext());
		}
		if (!cursorUsuarios.isClosed()) cursorUsuarios.close();
		return mUsuarios;
	}
	
	/**
	 * Metodos para roles en la base de datos
	 * 
	 * @param rol
	 *            Objeto Authority que contiene la informacion
	 *
	 */
	//Crear nuevo rol en la base de datos
	public void crearRol(Authority rol) {
		ContentValues cv = UserSistemaHelper.crearRolValues(rol);
		mDb.insert(MainDBConstants.ROLE_TABLE, null, cv);
	}
	//Limpiar la tabla de roles de la base de datos
	public boolean borrarRoles() {
		return mDb.delete(MainDBConstants.ROLE_TABLE, null, null) > 0;
	}
	//Verificar un rol de usuario
	public Boolean buscarRol(String username, String Rol) throws SQLException {
		Cursor c = mDb.query(true, MainDBConstants.ROLE_TABLE, null,
				MainDBConstants.username + "='" + username + "' and " + MainDBConstants.role + "='" + Rol + "'" , null, null, null, null, null);
		boolean result = c != null && c.getCount()>0; 
		c.close();
		return result;
	}
	
	
	/**
	 * Metodos para Zp00Screening en la base de datos
	 * 
	 * @param usuario
	 *            Objeto Zp00Screening que contiene la informacion
	 *
	 */
	//Crear nuevo Zp00Screening en la base de datos
	public void crearZp00Screening(Zp00Screening screening) {
		ContentValues cv = Zp00ScreeningHelper.crearZp00ScreeningValues(screening);
		mDb.insert(MainDBConstants.SCREENING_TABLE, null, cv);
	}
	//Editar Zp00Screening existente en la base de datos
	public boolean editarZp00Screening(Zp00Screening screening) {
		ContentValues cv = Zp00ScreeningHelper.crearZp00ScreeningValues(screening);
		return mDb.update(MainDBConstants.SCREENING_TABLE, cv, MainDBConstants.recordId + "='" 
				+ screening.getRecordId()+"'", null) > 0;
	}
	//Limpiar la tabla de Zp00Screening de la base de datos
	public boolean borrarZp00Screening() {
		return mDb.delete(MainDBConstants.SCREENING_TABLE, null, null) > 0;
	}
	//Obtener un Zp00Screening de la base de datos
	public Zp00Screening getZp00Screening(String filtro, String orden) throws SQLException {
		Zp00Screening mScreening = null;
		Cursor cursorScreening = crearCursor(MainDBConstants.SCREENING_TABLE, filtro, null, orden);
		if (cursorScreening != null && cursorScreening.getCount() > 0) {
			cursorScreening.moveToFirst();
			mScreening=Zp00ScreeningHelper.crearZp00Screening(cursorScreening);
		}
		if (!cursorScreening.isClosed()) cursorScreening.close();
		return mScreening;
	}
	//Obtener una lista de Zp00Screening de la base de datos
	public List<Zp00Screening> getZp00Screenings(String filtro, String orden) throws SQLException {
		List<Zp00Screening> mScreenings = new ArrayList<Zp00Screening>();
		Cursor cursorScreenings = crearCursor(MainDBConstants.SCREENING_TABLE, filtro, null, orden);
		if (cursorScreenings != null && cursorScreenings.getCount() > 0) {
			cursorScreenings.moveToFirst();
			mScreenings.clear();
			do{
				Zp00Screening mScreening = null;
				mScreening = Zp00ScreeningHelper.crearZp00Screening(cursorScreenings);
				mScreenings.add(mScreening);
			} while (cursorScreenings.moveToNext());
		}
		if (!cursorScreenings.isClosed()) cursorScreenings.close();
		return mScreenings;
	}
}
