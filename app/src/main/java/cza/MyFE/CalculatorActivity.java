package cza.MyFE;

import cza.hack.Coder;
import cza.util.Calculator;
import cza.widget.LP;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class CalculatorActivity extends MyActivity implements 
	View.OnClickListener, 
	RadioGroup.OnCheckedChangeListener,
	InputFilter {
	private EditText inputbox;
	private RadioGroup baseSwitch;
	private ViewGroup buttonGroup;
	private Editable content;
	private Calculator calculator;
	private int mBasein, mBaseout;
	private final char[] BUTTON = {'A', 'B', 'C', '←', 'D', 'E', 'F', '+', '7', '8', '9', '-', '4', '5', '6', '×', '1', '2', '3', '÷', '(', '0', ')', '='};
	
	@Override
    public void onCreate(Bundle savedStates) {
        super.onCreate(savedStates);
		setContentView(R.layout.calculator_activity);
		inputbox = (EditText)findView(R.id.iet);
		inputbox.setTextIsSelectable(true);
		inputbox.setFilters(new InputFilter[]{this});
		content = inputbox.getText();
		baseSwitch = (RadioGroup)findView(R.id.baseSwitch);
		baseSwitch.setOnCheckedChangeListener(this);
		buttonGroup = (ViewGroup)findView(R.id.buttonGroup);
		initButton();
		baseSwitch.check(R.id.option_hex);
		calculator = new Calculator();
	}
	
	private void initButton(){
		int columnCount = 4;
		int rowCount = BUTTON.length / columnCount;
		int i = 0;
		for (int row = 0; row < rowCount; row++){
			ViewGroup group = (ViewGroup)inflateView(R.layout.widget_bar);
			for (int col = 0; col < columnCount; col++){
				Button btn = (Button)inflateView(R.layout.calculator_button);
				char id = BUTTON[i++];
				btn.setId(id);
				btn.setText(String.valueOf(id));
				btn.setOnClickListener(this);
				group.addView(btn, LP.HLine);
			}
			buttonGroup.addView(group);
		}
	}

	@Override
	public void onClick(View v) {
		char id = (char)v.getId();
		if ('←' == id)
			input(null);
		else if ('=' == id)
			workout();
		else 
			input(String.valueOf(id));
	}

	/**
	 * 切换radio更换进制
	 */
	@Override
	public void onCheckedChanged(RadioGroup v, int id) {
		boolean isHex = R.id.option_hex == id;
		if (isHex)
			changeBase(10, 16);
		else 
			changeBase(16, 10);
		for (char i = 'A'; i <= 'F'; i++)
			buttonGroup.findViewById(i).setEnabled(isHex);
	}
	
	private void input(CharSequence text){
		int start = inputbox.getSelectionStart();
		int end = inputbox.getSelectionEnd();
		if (text == null){
			//退格
			if (start == end){
				if (start == 0)
					return;
				else 
					start--;
			}
			content.delete(start, end);
		} else {
			content.replace(start, end, text);
		}
	}

	private int getLineStart(int start){
		Editable edit = content;
		int index = start;
		while (index > 0 && edit.charAt(index - 1) != '\n')
			index--;
		return index;
	}

	private int getCurrentLineStart(){
		return getLineStart(inputbox.getSelectionStart());
	}
	
	private int getLastLineStart(){
		return getLineStart(content.length());
	}

	private String getLine(int start){
		int end = start;
		int length = content.length();
		Editable edit = content;
		while (end < length && edit.charAt(end) != '\n')
			end++;
		return content.subSequence(start, end).toString();
	}
	
	private String mGetLine(int start) throws Exception{
		String text = getLine(start);
		int length = text.length();
		if (length == 0)
			throw new Exception("空算式");
		start = 0;
		//去掉等号
		while (start < length && text.charAt(start) == '=')
			start++;
		if (start == length)
			throw new Exception("空算式");
		return text.substring(start);
	}
	
	//切换进制
	private void changeBase(int basein, int baseout){
		mBasein = basein;
		mBaseout = baseout;
		String text = Coder.toBaseString(content, basein, baseout);
		content.replace(0, content.length(), text);
	}

	private void workout(){
		try {
			String text = mGetLine(getCurrentLineStart());
			//修改工作行
			int lastLineStart = getLastLineStart();
			int end = content.length();
			content.replace(lastLineStart, end, text);
			String result = workout(text);
			content
				.append('\n')
				.append('=')
				.append(result)
				.append('\n');
			inputbox.setSelection(content.length());
		} catch (Exception e) {}
	}

	private String workout(String text) {
		text = text.replace('×', '*').replace('÷', '/');
		text = Coder.toBaseString(text, mBaseout, 10);
		String result = Long.toString(calculator.compute(text), mBaseout).toUpperCase();
		return result;
	}

	/**
	 * 过滤字符
	 */
	@Override
	public CharSequence filter(CharSequence text, int start, int end, Spanned dst, int dstart, int dend) {
		int textLength = text.length();
		if (textLength == 0)
			return text;
		int bufferLength;
		bufferLength = textLength;
		char[] buffer = new char[bufferLength];
		int p;
		int i = 0;
		boolean isHex = mBaseout == 16;
		for (p = 0; p < textLength; p++){
			char ch = text.charAt(p);
			boolean available = 
				ch == '+' || ch == '-' || ch == '*' || ch == '/' || 
				ch == '×' || ch == '÷' || ch == '=' || ch == '\n' || 
				'0' <= ch && ch <= '9' ||
				(isHex && ('A' <= ch && ch <= 'F' || 'a' <= ch && ch <= 'f'));
			if (available)
				buffer[i++] = ch;
			if (i >= bufferLength)
				break;
		}
		return String.valueOf(buffer, 0, i);
	}
}
