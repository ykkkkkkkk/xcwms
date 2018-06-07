package ykk.xc.com.xcwms.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.util.LoadingDialog;

/**
 * 父类Fragment
 */
@SuppressLint("HandlerLeak")
public class BaseFragment extends Fragment {
	private Activity mActivity;
	private LoadingDialog parentLoadDialog;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = getActivity();
	}

	/**
	 * 说明:(得到TextView控件的值) 作者: y1
	 */
	public String getValues(TextView txt) {
		return txt.getText().toString();
	}

	/** 说明:(得到EditText控件的值) */
	public String getValues(EditText edit) {
		return edit.getText().toString();
	}

	/** 说明:(得到Button控件的值) */
	public String getValues(Button btn) {
		return btn.getText().toString();
	}

	/**
	 * 设置EditText 控件的值（把把光标显示到最前）
	 */
	public void setTexts(EditText edit, String value) {
		edit.setText(value);
		edit.setSelection(value.length());
	}

	/**
	 * 得到xml文件
	 */
	public SharedPreferences spf(String xmlName) {
		return mActivity.getSharedPreferences(xmlName, Context.MODE_PRIVATE);
	}

	/**
	 * 得到xml中的value
	 *
	 * @param key
	 * @return
	 */
	public String getXmlValues(SharedPreferences spf, String key) {
		return spf.getString(key, "");
	}

	/**
	 * 得到session
	 */
	public String getSession() {
		SharedPreferences spfOther = spf(getResStr(R.string.saveOther));
		return spfOther.getString("session", "");
	}

	public boolean getXmlValues2(SharedPreferences spf, String key) {
		return spf.getBoolean(key, false);
	}

	/**
	 * 设置xml中的值
	 * @param putKey
	 * @param putValue
	 */
	public void setXmlValues(SharedPreferences spf, String putKey,
							 String putValue) {
		spf.edit().putString(putKey, putValue).commit();
	}

	/**
	 * 得到string.xml中的值
	 */
	public String getResStr(int idName) {
		return mActivity.getResources().getString(idName);
	}

	/**
	 * 将ImageView图片转换为圆角图片
	 *
	 * @param bitmap
	 *            原图片
	 * @param pix
	 *            截取比例，如果是8,则圆角半径是宽高的1/8,如果是2,则是圆形图片
	 * @return 截取后的bitmap
	 */
	public Bitmap toRoundComer(Bitmap bitmap, float pix) {
		Bitmap retBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(retBitmap);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, bitmap.getWidth() / pix, bitmap.getHeight()
				/ pix, paint);

		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return retBitmap;
	}

	/**
	 * Toast打印
	 */
	public void toast(String str) {
		Toast.makeText(mActivity, str, Toast.LENGTH_LONG).show();
	}

	/**
	 * 字符串不能为空的终极判断 父方法
	 *
	 * @param obj
	 * @return
	 */
	public String isNULLS(String obj) {
		return (obj != null && !obj.equals("null")) ? obj.toString() : "";
	}
	public String isNULLS(JSONObject json, String key) throws JSONException {
		return json.has(key) ? json.getString(key) : "";
	}

	/**
	 * 说明:(字符串不能为空的终极判断 ) 作者: y1
	 */
	public String isNULL2(String obj, String defaVal) {
		String result = isNULLS(obj);
		return result.length() > 0 ? result : defaVal;
	}

	/**
	 * 数字字符串转成Double 如果Double.parseDouble(null);这个是报空指针异常
	 */
	public double parseDouble(String obj) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Double.parseDouble(result) : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	public double parseDouble(JSONObject json, String key) {
		try {
			String result = isNULLS(json, key);
			return result.length() > 0 ? Double.parseDouble(result) : 0;
		} catch (Exception e) {
			return 0;
		}
	}
	public double parseDouble(String obj, double defaVal) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Double.parseDouble(result) : defaVal;
		} catch (NumberFormatException e) {
			return defaVal;
		}
	}

	/**
	 * 数字字符串转成Int 如果Integer.parseInt(null);这个是报空指针异常
	 */
	public int parseInt(String obj) {
		try {
			String result = isNULLS(obj);
			return result.length() > 0 ? Integer.parseInt(result) : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	public int parseInt(JSONObject json, String key) {
		try {
			String result = isNULLS(json, key);
			return result.length() > 0 ? Integer.parseInt(result) : 0;
		} catch (Exception e) {
			return 0;
		}
	}


	/**
	 * 设置view的间距
	 */
	public void setMargins(View view, int left, int top, int right, int bottom) {
		if(view.getLayoutParams() instanceof MarginLayoutParams) {
			MarginLayoutParams p = (MarginLayoutParams) view.getLayoutParams();
			p.setMargins(left, top, right, bottom);
			view.requestLayout();
		}
	}

	/**
	 * 打开页面然后传值
	 * @param context2    打开的页面
	 * @param bundle  传值
	 */
	public void show(Class<?> context2,Bundle bundle){
		Intent intent = new Intent();
		intent.setClass(getActivity(), context2);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/**
	 * 打开页面然后可以得到回调的值
	 * @param context2    打开的页面
	 * @param code 返回值的状态码
	 * @param bundle  传值
	 */
	public void showForResult(Class<?> context2 , int code , Bundle bundle){
		Intent intent = new Intent();
		intent.setClass(getActivity(), context2);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
		startActivityForResult(intent, code);
	}

	/**
	 * 刷新	发起跳转的onActivity
	 */
	public void setResults(Activity context) {
		Intent intent = new Intent();
		intent.putExtra("isRefresh", true);
		context.setResult(Activity.RESULT_OK, intent);
	}
	public void setResults(Activity context, String str) {
		Intent intent = new Intent();
		intent.putExtra("resultValue", str);
		context.setResult(Activity.RESULT_OK, intent);
	}
	public void setResults(Activity context, Bundle bundle) {
		Intent intent = new Intent();
		intent.putExtras(bundle);
		context.setResult(Activity.RESULT_OK, intent);
	}

	/**
	 * 显示输入法
	 */
	public void showKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 隐藏输入法
	 */
	public void hideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	}

	/**
	 * 得到焦点后不谈出软键盘 只显示光标不显示软键盘
	 */
	public void hideSoftInputMode(Activity context, EditText edit) {
		if (android.os.Build.VERSION.SDK_INT <= 10) {// 3.0以下使用
			edit.setInputType(InputType.TYPE_NULL);
		} else {// 3.0以上使用
			context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try {
				Class<EditText> cls = EditText.class;
				Method setFocus;
				setFocus = cls.getMethod("setShowSoftInputOnFocus",
						boolean.class);
				setFocus.setAccessible(true);
				setFocus.invoke(edit, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 显示加载框
	 * @param loadText
	 */
	public void showLoadDialog(String loadText) {
		parentLoadDialog = new LoadingDialog(mActivity, loadText, true);
	}
	public void showLoadDialog(String loadText, boolean isCancel) {
		parentLoadDialog = new LoadingDialog(mActivity, loadText, isCancel);
	}

	public void hideLoadDialog() {
		if (parentLoadDialog != null) {
			parentLoadDialog.dismiss();
			parentLoadDialog = null;
		}
	}

}
