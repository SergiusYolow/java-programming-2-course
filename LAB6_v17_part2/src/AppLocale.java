import java.util.*;

public class AppLocale {
	private static final String strMsg = "Msg";
	private static Locale loc = Locale.getDefault();
	private static ResourceBundle res = 
			ResourceBundle.getBundle( AppLocale.strMsg, AppLocale.loc );
	
	static Locale get() {
		return AppLocale.loc;
	}
	
	static void set( Locale loc ) {
		AppLocale.loc = loc;
		res = ResourceBundle.getBundle( AppLocale.strMsg, AppLocale.loc );
	}
	
	static ResourceBundle getBundle() {
		return AppLocale.res;
	}
	
	static String getString( String key ) {
		return AppLocale.res.getString(key);
	}
	
	// Resource keys:
	
	public static final String route="route";
	public static final String type="type";
	public static final String amount="amount";
	public static final String inter="inter";
	public static final String bus="bus";
	public static final String tram="tram";
	public static final String trolleybus="trolleybus";
	public static final String land_trafic="land_trafic";
	public static final String first_route="first_route";
	public static final String second_route="second_route";
	public static final String third_route="third_route";
	public static final String fourth_route="fourth_route";
	public static final String creation="creation";
	
}
