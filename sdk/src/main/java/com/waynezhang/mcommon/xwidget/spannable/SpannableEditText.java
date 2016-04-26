package com.waynezhang.mcommon.xwidget.spannable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;

import com.waynezhang.mcommon.util.ScreenUtil;
import com.waynezhang.mcommon.util.StringUtil;
import com.waynezhang.mcommon.util.XmlPullParserUtil;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfoContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpannableEditText extends EditText {
    private CharSequence text;
    private final static String TAG = "SpannableEditText";
    private String POSTESCAPECHART = "##";
    private String PREESCAPECHART = "#";


    // private CharSequence text;
    private boolean _isEscapeEnable = false;
    private CharSequence _strPasteData = "";
    private boolean _isRaw = false; // 是否按照输入原样显示，
    private String _escapedStr = "";

    private ArrayList<String> _content = new ArrayList<String>();

    boolean _iskeydown = false;

    public void setEscapeEnable(boolean flag) {
        _isEscapeEnable = flag;
    }

    public void setPasteString(CharSequence strPasteData){
        Log.d(TAG, "setPasteString data=" + strPasteData.toString());
        _strPasteData = strPasteData;
    }

    public boolean isOneNumberOnly(String s){
        if (s == null || s.length() == 0) return false;
        if (s.charAt(0) >= '0' && s.charAt(0) <= '9'){
            Log.d(TAG, "isOneNumberOnly s=" + s);
            return true;
        }
        return false;
    }

    public boolean isOneLetterOnly(String s){
        if (s == null || s.length() == 0) return false;
        if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z'){
            Log.d(TAG, "isOneLetterOnly s=" + s);
            return true;
        }
        if (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z'){
            Log.d(TAG, "isOneLetterOnly big s=" + s);
            return true;
        }
        return false;
    }

    // "#01" to "{::#01::}" and "##" to "#"
    public String encodeString(String raw){
        Log.d(TAG, "encodeString raw =" + raw);
        if (raw == null || raw.length() == 0) {return "";}
        ArrayList<String> con = new ArrayList<String>();
        con = AddTextContentToList(con, raw, 0, raw.length(), true);

        Log.d(TAG, "encodeString con=" + getStringFramList(con));

        for (int i = 0; i < con.size(); i++){
            if (con.get(i) != null &&con.get(i).equals("#")){
                 // like #01
                if ((i + 2) < con.size() && isOneNumberOnly(con.get(i + 1)) && isOneNumberOnly(con.get(i+2))){
                    Log.d(TAG, "encodeString isOneNumberOnly");
                    con.remove(i);
                    con.add(i, "{::#");
                    String s = con.remove(i+2);
                    con.add(i+2, s + "::}");
                    i+=2;
                }else if ((i + 3) < con.size()
                        && ((isOneNumberOnly(con.get(i+1)) || isOneLetterOnly(con.get(i+1)))
                        && (isOneNumberOnly(con.get(i+2)) || isOneLetterOnly(con.get(i+2)))
                        && (isOneNumberOnly(con.get(i+3)) || isOneLetterOnly(con.get(i+3))))
                ){// like #abc
                    Log.d(TAG, "encodeString isOneNumberOnly isOneLetterOnly");

                    con.remove(i);
                    con.add(i, "{::#");
                    String s = con.remove(i+3);
                    con.add(i+3, s + "::}");
                    i+=3;
                }
            }
        }

        CharSequence ss = getStringFramList(con);
        return StringUtil.removeEscapeEnable(ss.toString());
    }

    // from {::#01::} to #01
    private String getEmotionIdFromEncodeStr(String id){
        id = id.replace("{::", "");
        id = id.replace("::}", "");

        return id;
    }

    // "{::#01::}" to "#01" and "#" to "##"
    // #01 #11 {::#01::} 33333{::#11::}
    public CharSequence decodeString(CharSequence raw){

        Pattern p = Pattern.compile("(\\{::#[0-9][0-9]::\\})|(\\{::#[a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9]::\\})");
        // Pattern p = Pattern.compile("//::#[0-9][0-9]::\\|//::#[a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9]::\\*");

        Matcher m = p.matcher(raw);
        String retStr = "";
//        "//{0//}"
        Log.d(TAG, "decodeString groupCount=" + m.groupCount() + ",p.pattern()=" + p.pattern() + ",raw=" + raw);

        String strs[] = p.split(raw);
        for (int i = 0; i < strs.length; i++){
            strs[i] = strs[i].replace("#", "##");
            Log.d(TAG, "decodeString split i=" + i + ", str=" + strs[i]);
        }

        try{
            int idx=0;

            while (m.find()){
                Log.d(TAG, "decodeString 01 i=" + idx + ", group=" + m.group());
                if (idx>=0 && idx < strs.length){
                    retStr += strs[idx];
                    Log.d(TAG, "decodeString 02 strs=" + strs[idx]);
                }

//                String sg = m.group();
//                sg = sg.replace("{::", "");
//                sg = sg.replace("::}", "");
                retStr += getEmotionIdFromEncodeStr(m.group());
                Log.d(TAG, "decodeString 03 strs=" + retStr);
                idx ++;
            }

            if (idx>=0 && idx < strs.length){
                retStr += strs[idx];
                Log.d(TAG, "decodeString 04 strs=" + strs[idx]);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return  "";
        }

        Log.d(TAG, "decodeString retStr=" + retStr);

//        for (int i = 0; i < m.groupCount(); i++){
//            if (m.group(i) == null || m.group(i).length() == 0) continue;
//
//            Log.d(TAG, "decodeString i=" + i + ", group" + m.group(i));
//            retStr.replace(m.group(i), getEmotionIdFromEncodeStr(m.group(i)));
//        }

        return retStr.subSequence(0, retStr.length());
    }

    public SpannableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent action =" + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _iskeydown = true;
                break;
            case MotionEvent.ACTION_MOVE: {
                Editable editable = getText();
                int pos = this.getSelectionEnd();
                Selection.setSelection(editable, pos, pos);
            }
            break;
            case MotionEvent.ACTION_CANCEL:{
                Editable editable = getText();
                int pos = this.getSelectionEnd();
                Selection.setSelection(editable, pos, pos);
                _iskeydown = false;
                break;
            }
            case MotionEvent.ACTION_UP:
                _iskeydown = false;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public SpannableEditText(Context context) {
        super(context);
        EmotionInfoContainer.initGlobalEmotionInfo(context, null);
    }

    public SpannableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        EmotionInfoContainer.initGlobalEmotionInfo(context, null);
    }

    private void printfList(ArrayList<String> list){
        for (int i = 0; i <list.size(); i++){
            Log.d(TAG, "printfList i=" + i + ", s=" + list.get(i));
        }
    }

    public CharSequence getTextEx(){
        return  getText1();
    }

    private CharSequence getStringFramList(ArrayList<String> list){
        Log.d(TAG, "getStringFramList begin");

        String sRet = "";
        if (list == null) return sRet;

        for (int i = 0; i < list.size(); i++) {
            sRet += list.get(i);
        }

        Log.d(TAG, "getStringFramList sRet=" + sRet);
        return sRet;
    }

    private CharSequence getText1() {
        Log.d(TAG, "getText1");

        if (!_isEscapeEnable){
            return text == null ? "" : text;
        }

        return getStringFramList(_content);
    }

    @Override
    public Editable getText() {
        return super.getText();
    }

    private void DelBeforeDataFromeList(int start, int lengthBefore) {
        Log.d(TAG, "DelBeforeDataFromeList b gettext1=" + getText1());

        printfList(_content);

        if (lengthBefore == 0) return;

        for (int i = start + lengthBefore-1; i >= start; i--) {
            try {
                if (i < 0 || i >= _content.size()) continue;
                Log.d(TAG, "DelBeforeDataFromeList i=" + i + ", s=" + _content.get(i));
                _content.remove(i);

            } catch (Exception ex) {
                Log.w(TAG, "toList i =" + i);
                ex.printStackTrace();
            }
        }

        printfList(_content);

        Log.d(TAG, "DelBeforeDataFromeList e gettext1=" + getText1());
    }

    // isAddDoubleEscape is add "##" to a list itme, false, add "##" to two list item
    private ArrayList<String> AddTextContentToList(ArrayList<String> contentList, String text, int start, int lengthAfter, boolean isAddDoubleEscape) {
        Log.d(TAG, "AddTextContentToList b gettext1=" + getText1() + ", text=" + text + ", start=" + start + ",after=" + lengthAfter + ",isdouble=" + isAddDoubleEscape);

        ArrayList<String> retList = (ArrayList<String>)contentList.clone();

        printfList(retList);
        Log.d(TAG, "AddTextContentToList 03");

        int flag = start;
        for (int i = start; i < start + lengthAfter; i++) {
            try {
                if (i >= text.length()) continue;
                String s = "" + text.charAt(i);
                Log.d(TAG, "AddTextContentToList i=" + i + ", s=" + s);
                if (isAddDoubleEscape) {
                    if (text.charAt(i) == '#' && i + 1 < text.length() && text.charAt(i + 1) == '#') {
                        s += "#";
                        retList.add(flag++, s);
                        i++;

                        continue;
                    }
                }

                retList.add(flag++, s);
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }

        printfList(retList);
        Log.d(TAG, "AddTextContentToList 04");

        return retList;

        // Log.d(TAG, "AddTextContentToList e gettext1=" + getText1());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keycode=" + keyCode + ", event=" + event.toString());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        Log.d(TAG, "onTextContextMenuItem id=" + id );
        return super.onTextContextMenuItem(id);

//        if (_isEscapeEnable && id == android.R.id.paste){
//            ClipboardManager clip = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//            String clipStr = clip.getText().toString();
//            if (clipStr == null || clipStr.length() == 0) return true;
//
//
//            int epos = this.getSelectionEnd();
//            int bpos = this.getSelectionStart();
//            _isRaw = true;
//
//            Log.d(TAG, "onTextContextMenuItem bpos=" + bpos + ", ePos=" + epos + ",getTextEx=" + getTextEx() + ",clipStr=" + clipStr);
//
//            if (epos < 0 || bpos < 0 || epos > _content.size() || bpos > _content.size()) return false;
//
//            if (bpos != epos){
//                int min = Math.min(epos, bpos);
//                int max = Math.max(epos, bpos);
//                for (int i = min; i < max; i++){
//                    try {
//                        _content.remove(i);
//                    }catch (Exception ex){
//                        ex.printStackTrace();
//                    }
//                }
//            }
//
//            try {
//                String s = getText1().toString();
//                String begin = s.substring(0, bpos);
//                String middle = clipStr;
//                String end = s.substring(bpos, s.length());
//
//                String sText = begin + middle + end;
//                Log.d(TAG, "onTextContextMenuItem bpos=" + bpos + ", ePos=" + epos + ", begin=" + begin + ",middle=" + middle + ",end=" + end + ",getTextEx=" + getTextEx());
//
//                setText(sText);
//                Editable editable = getText();
//                middle = StringUtil.removeEscapeEnable(middle); // 为计算光标的位置
//                Selection.setSelection(editable, (begin + middle).length(), (begin + middle).length());
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//
//            return true;
//        }
//
//        return super.onTextContextMenuItem(id);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {

        if (text == null) return;

        Log.d(TAG, "setText str=" + text);

        if (_isEscapeEnable) {
            if (_content == null) _content = new ArrayList<String>();
            _isRaw = true;
            _content.clear();
            _escapedStr = "";
            _content = AddTextContentToList(_content, text.toString(), 0, text.length(), true);
        }else{
            this.text = text;
        }

        if (text.toString().equals("")) {
            super.setText(text, type);
            invalidate();
            return;
        }

        SpannableString spannable = null;
        if (_isEscapeEnable)
            spannable = new SpannableString(StringUtil.removeEscapeEnable(text.toString()));
        else{
            spannable = new SpannableString(StringUtil.removeEscapeEnable(getText1().toString()));
        }

        if (EmotionInfoContainer.mapPngEmotionInfoGlobal != null) {
            Set<Map.Entry<String, Integer>> set = EmotionInfoContainer.mapPngEmotionInfoGlobal.entrySet();
            Vector<Integer> vecHas = new Vector<Integer>();
            for (Iterator<Map.Entry<String, Integer>> it = set.iterator(); it.hasNext(); ) {
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it
                        .next();

                Vector<Integer> vec = null;

                if (_isEscapeEnable) {
                    vec = StringUtil.findStringIndexForEscape(text
                            .toString(), entry.getKey());
                } else {
                    vec = StringUtil.findStringIndex(getText1()
                            .toString(), entry.getKey());
                }

                for (int i = 0; i < vec.size(); ++i) {
                    if (!vecHas.contains(vec.get(i))) {
                        Drawable drawable = getResources().getDrawable(entry.getValue());
                        int padding = ScreenUtil.dip2px(getContext(), 2.5f);
                        MarginImageSpan span = new MarginImageSpan(drawable, ImageSpan.ALIGN_BOTTOM, padding);
                        spannable.setSpan(span, vec.get(i), vec.get(i) + entry.getKey().length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        vecHas.add(vec.get(i));

                    }
                }
            }

        }

        super.setText(spannable, type);
    }


    public void appendText(String info) {
        Log.d(TAG, "appendText str=" + info);

        if (info.equals(XmlPullParserUtil.EMOTION_PNG_DEL_ID)) {
            KeyEvent newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DEL);
            onKeyDown(KeyEvent.KEYCODE_DEL, newEvent);
            return;
        }

        if (_isEscapeEnable) {
            setText(getText1() + info);
        }else{
            setText(getText() + info);
        }
        Editable editable = getText();
        int position = editable.length();
        Selection.setSelection(editable, position);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyPreIme keycode=" + keyCode + ", event=" + event.toString());
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        Log.d(TAG, "onTextChanged begin text=" + text + ",start=" + start + ",lengthBefore=" + lengthBefore + ",lengthAfter=" + lengthAfter);

        if (!_isRaw && text != null && _isEscapeEnable && lengthAfter > 0){// 处理小米没有调用onTextContextMenuItem粘贴通知的问题
            CharSequence cc = text.subSequence(start, start + lengthAfter);
            if (cc != null && cc.toString().contains("{::") && cc.toString().contains("::}")){
                CharSequence ss = decodeString(cc);
                lengthAfter = ss.length();
                DelBeforeDataFromeList(start, lengthBefore);

                String stmp = "";

                if (start >= 0) {
                    stmp = text.subSequence(0, start).toString();
                }

                _content = AddTextContentToList(_content, (stmp + ss).toString(), start, ss.length(), true);
                Log.d(TAG, "onTextChanged 02 text=" + text + ",getText1()=" + getText1() + ",ss=" + ss.toString());
                setText(getText1());

                Editable editable = getText();
                String m = StringUtil.removeEscapeEnable(ss.toString()); // 为计算光标的位置
                Selection.setSelection(editable, start + m.length(), start + m.length());
                return;
            }
        }

        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        Log.d(TAG, "onTextChanged 1 begin text=" + text + ",start=" + start + ",lengthBefore="
                + lengthBefore + ",lengthAfter=" + lengthAfter + ", _escapedStr=" + _escapedStr
                + ",_isAppend=" + _isRaw + ", _isEscapeEnable=" + _isEscapeEnable);

        if (!_isEscapeEnable) return;

        if (_isRaw) {
            // 在settext有处理
            _isRaw = false;
            return;
        }

        if (_content == null) {
            _content = new ArrayList<String>();
        }

        if (text.length() == 0) {
            _content.clear();
            return;
        }

        DelBeforeDataFromeList(start, lengthBefore);
        _content = AddTextContentToList(_content, text.toString(), start, lengthAfter, false);

        printfList(_content);
        Log.d(TAG, "onTextChanged 01");
        if (!text.toString().equals(_escapedStr)) {
            for (int i = start; i < start + lengthAfter; i++) {
                if (i >= _content.size()) {
                    continue;
                }

                if (_content.get(i).equals("#")) { // modified "#" to "##"
                    _content.remove(i);
                    _content.add(i, "##");
                    Log.d(TAG, "onTextChanged i=" + i);
                }
            }
            _escapedStr = text.toString();
        }

        printfList(_content);
        Log.d(TAG, "onTextChanged 02");
        Log.d(TAG, "onTextChanged end text=" + text + ",start=" + start + ",lengthBefore=" + lengthBefore + ",lengthAfter=" + lengthAfter + ",_escapedStr" + _escapedStr + ", getText1()=" + getText1());
    }


}