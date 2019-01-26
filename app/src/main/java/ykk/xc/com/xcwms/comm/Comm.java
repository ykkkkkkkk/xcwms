package ykk.xc.com.xcwms.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import ykk.xc.com.xcwms.R;


/**
 * 这是一个公共类
 *
 * @author ykk
 *
 */
public class Comm {
	public static final String publicPaths = Environment.getExternalStorageDirectory().toString()+"/xcwms/";
	/**
	 * WebService
	 * 基于soap协议
	 * webService uri
 	 */
	public static final String WEB_URI = "http://183.60.183.21:8009/cbsw.asmx";
	/**
	 * WebService
	 * 基于soap协议
	 * XML的命名空间
	 */
	public static final String XMLNS = "http://tempuri.org/";

	/**
	 * 字符串截取
	 * (index=0,从0的位置开始截取，否则从大于0的位置截取)
	 */
	public static String subString(String str,char ch, int index) {
		String result = null;
		if (str != null && str != "") {
			if (index == 0) {
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == ch) {
						result = str.substring(0, i);
					}
				}
			}else{
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == ch) {
						result = str.substring(i+1);
					}
				}
			}

		}
		return result;
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideInputMode(Context context) {
		((Activity) context).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	/**
	 * 检测网络连接状态
	 */
//	public static boolean isConnect(Context context){
//		try {
//			ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//			if(con != null){
//				//获取网络连接管理对象
//				NetworkInfo info = con.getActiveNetworkInfo();
//				if(info != null && info.isConnected()){
//					//判断当前网络是否已经连接
//					if(info.getState() == NetworkInfo.State.CONNECTED){
//						return true;
//					}
//				}
//			}
//		} catch (Exception e) {
//			Log.d("error", e.toString());
//		}
//		return false;
//	}

	/**
	 * 得到系统时间 0:显示日期和时间,1:日期和时间加周期，2:简版日期时间和毫秒数
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSysDate(int type) {
		SimpleDateFormat dateFormat = null;
		switch (type) {
			case 0:
				dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				break;
			case 1:
				dateFormat = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm:ss");
				break;
			case 2:
				dateFormat = new SimpleDateFormat("yyMdHmsSS");
				break;
			case 3:
				dateFormat = new SimpleDateFormat("yyyyMMdd");
				break;
			case 4:
				dateFormat = new SimpleDateFormat("yyMdHHmmssSS");
				break;
			case 5:
				dateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
				break;
			case 6:
				dateFormat = new SimpleDateFormat("HH:mm");
				break;
			case 7:
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				break;
			case 8:
				dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				break;
			case 9:
				dateFormat = new SimpleDateFormat("yyyy.MM.dd");
				break;
		}
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 字符串不能为空的终极判断 父方法
	 *
	 * @param obj
	 * @return
	 */
	public static String isNULLS(Object obj) {
		return (obj != null && !obj.equals("null")) ? obj.toString() : "";
	}
	public static String isNULLS(JSONObject json, String key) throws JSONException {
		return json.has(key) ? isNULLS(json.getString(key)) : "";
	}

	/**
	 * 说明:(字符串不能为空的终极判断 ) 作者: y1
	 */
	public static String isNULL2(Object obj, String defaVal) {
		String result = isNULLS(obj);
		return result.length() > 0 ? result : defaVal;
	}

	/**
	 * 数字字符串转成Double
	 * 如果Double.parseDouble(null);这个是报空指针异常
	 */
	public static double parseDouble(Object obj) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Double.parseDouble(result) : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	public static double parseDouble(Object obj, double defaVal) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Double.parseDouble(result) : defaVal;
		} catch (NumberFormatException e) {
			return defaVal;
		}
	}

	/**
	 * 数字字符串转成Int
	 * 如果Integer.parseInt(null);这个是报空指针异常
	 */
	public static int parseInt(Object obj) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Integer.parseInt(result) : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 显示系统日期的方法
	 *
	 * @param context
	 * @param dateView
	 * @param viewType
	 *            (0:TextView, 1:EditText)
	 */
	public static void showDateDialog(Activity context, final View dateView,
									  final int viewType) {
		// Date date = new Date();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String currentDate = sdf.format(date);
		String currentDate = getSysDate(7);
		int currentYear = Integer.parseInt(currentDate.substring(0, 4));
		int currentMonth = Integer.parseInt(currentDate.substring(5, 7));
		int currentDay = Integer.parseInt(currentDate.substring(8, 10));

		// 新建日期对话框
		final DatePickerDialog dateDialog = new DatePickerDialog(context, null,
				currentYear, currentMonth - 1, currentDay);
		// 设置对话框标题，默认为当前日期+星期
		dateDialog.setTitle(currentDate);
		// 手动设置按钮
		/*
		 * 确定按钮
		 */
		// dateDialog.setCancelable(false); //不允许使用返回键
		dateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 通过dateDialog.getDatePicker（）获得dialog上的datepicker组件，然后获得日期信息
						DatePicker datePicker = dateDialog.getDatePicker();
						int year = datePicker.getYear();
						int month = datePicker.getMonth() + 1;
						int day = datePicker.getDayOfMonth();
						String strMonth = month + "";
						String strDay = day + "";
						if (month < 10) {
							strMonth = "0" + month;
						}
						if (day < 10) {
							strDay = "0" + day;
						}
						String text = year + "-" + strMonth + "-" + strDay;
						// 给对应的控件赋值
						if (viewType == 0) {
							TextView tv = (TextView) dateView;
							tv.setText(text);
						} else if (viewType == 1) {
							EditText tv = (EditText) dateView;
							tv.setText(text);
						}
						dialog.dismiss();
					}
				});
		/*
		 * 取消按钮
		 */
		dateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dateDialog.show();
	}

	/**
	 * 克隆分为两种：1.浅克隆（地址指向一样，A的数据改变，B也随着改变。），深克隆（互不相干）
	 * list深度克隆
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		List<T> dast = (List<T>) in.readObject();
		return dast;
	}

	/**
	 * 克隆分为两种：1.浅克隆（地址指向一样，A的数据改变，B也随着改变。），深克隆（互不相干）
	 * 对象深度克隆
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T deepCopy(T src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		T dast = (T) in.readObject();
		return dast;
	}

	/**
	 * 生成13位唯一条码(根据表的自增长id来生成)
	 */
	public static String randBarcode(String id) {
		int idLen = id.length() + 1; // 这里为什么加1，应为id的前面要加个0占位
		int remainLen = 13 - idLen;
		// 随机生成数字码
		StringBuilder code = new StringBuilder();
		Random rand = new Random();// 随机生成类
		int randInt = 0;
		for (int i = 0; i < remainLen; i++) {
			randInt = rand.nextInt(10);
			code.append(randInt == 0 ? 1 : randInt);
		}

		return code.toString() + "0" + id;
	}

	/**
	 * 提示框
	 * @return
	 */
	public static void showWarnDialog(Activity mContext, String message) {
		AlertDialog.Builder build = new AlertDialog.Builder(mContext);
		build.setIcon(R.drawable.caution);
		build.setTitle("系统提示");
		build.setMessage(message);
		build.setNegativeButton("知道了", null);
		build.setCancelable(false);
		build.show();
	}

	/**
	 * 判断当前设备是手机还是平板
	 * @param context
	 * @return 平板返回 True，手机返回 False
	 */
	public static boolean isPad(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * 判断扫码是键盘按键是否有效
	 * @param context
	 * @param event
	 * @return
	 */
	public static boolean smKeyIsValid(Activity context, KeyEvent event) {
		// 按了删除键，回退键
//        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
		// PDA：（240 为PDA两侧面扫码键，241 为PDA中间扫码键）
		// 外接扫码枪：（条码号的数字代表：0=7，1=8，以此下推9=16，字母代表一个字符为四个数字：A=59 29 一组，以此下推Z=59 54；59就代表是字母的意思）
//        if(!(event.getKeyCode() == 240 || event.getKeyCode() == 241)) {
//            return false;
//        }
		// 外接扫码枪：条码号的数字代表：0=7，1=8，以此下推9=16，字母代表一个字符为59开头：A=59 29 一组，以此下推Z=59 54；59就代表是字母的意思
		boolean isPad = Comm.isPad(context);
		if(isPad) { // 是Pad平板
			boolean isBool = false;
			int[] numberArr = {7,8,9,10,11,12,13,14,15,16,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,59};
			int len = numberArr.length;
			for(int i=0; i<len; i++) {
				Log.e("平板平板----", ""+event.getKeyCode());
				if(event.getKeyCode() == numberArr[i]) isBool = true;
			}
			return isBool;
		} else { // 手机或者PDA
			if(!(event.getKeyCode() == 240 || event.getKeyCode() == 241)) {
				Log.e("PDAPDAPDAPDA", ""+event.getKeyCode());
				return false;
			}
		}
		return true;
	}


	/**
	 * 根据wifi信息获取本地mac
	 * @param context
	 * @return
	 */
	public static String getAddressMac(Activity context){
		WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo winfo = wifi.getConnectionInfo();
		String mac =  winfo.getMacAddress();
		return mac;
	}

	/**
	 *
	 * @param editText
	 */
	public static void  showSoftInputFromWindow(EditText editText) {
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, 0);
	}

}