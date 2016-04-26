package com.waynezhang.mcommon.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.xwidget.titlebar.McTitleBarExtMenuItem;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionInfo;
import com.waynezhang.mcommon.xwidget.emotionpanel.container.EmotionPageInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class XmlPullParserUtil {
	private static final String TAG = XmlPullParserUtil.class.getSimpleName();
    public static final int PAGE_TYPE_PNG_SMALL = 1;
    public static final int PAGE_TYPE_PNG_BIG = 2;
    public static final int PAGE_TYPE_PNG_GIF = 3;

    public static final int EMOTION_TYPE_PNG_SMALL = 1;
    public static final int EMOTION_TYPE_PNG_DEL = 2;
    public static final int EMOTION_TYPE_PNG_BIG = 2;

    public static final String EMOTION_PNG_DEL_ID = "#2D";

	public static String[] parsePageEmotion(InputStream is,
                                            String charSet, Vector<EmotionPageInfo> vecEmotionPageInfo, Map<String, String> mapPngEmotionTxtInfo) {
		String[] pageIconRes = new String[2];
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			parser.setInput(is, charSet);
			int columnSize=0;
			int rowSize=0;
            int total=0;
            int index=0;
			int typePage=0;
			int itemW=0;
			int itemH=0;
			Vector<EmotionInfo> vecE=null;

			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("emotion_list")) {
						pageIconRes[0] = parser.getAttributeValue(null, "page_icon") == null ? "" : parser.getAttributeValue(null, "page_icon");
						pageIconRes[1] = parser.getAttributeValue(null, "page_txt");
						columnSize=Integer.parseInt(parser
                                .getAttributeValue(null, "column_size"));
						rowSize=Integer.parseInt(parser
                                .getAttributeValue(null, "row_size"));
                        total = columnSize * rowSize;
						itemW=Integer.parseInt(parser
								.getAttributeValue(null, "item_width"));
						itemH=Integer.parseInt(parser
								.getAttributeValue(null, "item_height"));
						typePage=Integer.parseInt(parser
								.getAttributeValue(null, "type"));
					} else if (parser.getName().equals("emotion")) {
						try {
                            String id = parser.getAttributeValue(null, "id");
                            String name = parser.getAttributeValue(null, "name");
                            String icon = "R.drawable." + parser.getAttributeValue(null, "icon");
                            int typ = Integer.parseInt(parser.getAttributeValue(null, "type"));
                            EmotionInfo emotionInfo = generateEmotionInfo(id, name, icon, typ);
                            mapPngEmotionTxtInfo.put(emotionInfo.getName(), emotionInfo.getText());

                            if (parser.getAttributeValue(null, "icon").equals("")) {
                                break;
                            }
                            if (index == 0) {
                                EmotionPageInfo emotionPageInfo = generatePageInfo(columnSize, typePage, itemH, itemW);
                                vecE = emotionPageInfo.getVecEmotionInfo();
                                vecEmotionPageInfo.add(emotionPageInfo);

                                vecE.add(emotionInfo);
                                index++;
                            } else if (index < total -1 && index > 0) {
                                if (parser.getAttributeValue(null, "icon").equals("")) {
                                    continue;
                                }
                                vecE.add(emotionInfo);
                                index++;
                            } else if (index == total - 1) {
                                if (typePage == PAGE_TYPE_PNG_SMALL) {
                                    EmotionInfo emotionInfoDel = generateEmotionInfo(EMOTION_PNG_DEL_ID, "", "", EMOTION_TYPE_PNG_DEL);
                                    vecE.add(emotionInfoDel);

                                    EmotionPageInfo emotionPageInfo = generatePageInfo(columnSize, typePage, itemH, itemW);
                                    vecE = emotionPageInfo.getVecEmotionInfo();
                                    vecEmotionPageInfo.add(emotionPageInfo);

                                    vecE.add(emotionInfo);
                                    index = 1;
                                } else {
                                    vecE.add(emotionInfo);
                                    index = 0;
                                }
                            }

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
                    break;
				case XmlPullParser.END_TAG:
                    try {
                        if (parser.getName().equals("emotion_list")) {
                            if (typePage == PAGE_TYPE_PNG_SMALL) {
                                EmotionInfo emotionInfo = generateEmotionInfo("#2D", "", "", EMOTION_TYPE_PNG_DEL);
                                if (vecE != null) {
                                    vecE.add(emotionInfo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
					break;
				default:
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// return res;
		}
		return pageIconRes;
	}

    private static EmotionInfo generateEmotionInfo(String id, String name, String icon, int typ) {
        EmotionInfo emotionInfo = new EmotionInfo();
        emotionInfo.setName(id);
        emotionInfo.setText(name);
        emotionInfo.setIcon(icon);
        emotionInfo.setType(typ);
        return emotionInfo;
    }

    private static EmotionPageInfo generatePageInfo(int columnSize, int type1, int itemH, int itemW) {
        Vector<EmotionInfo> vecE =new Vector<EmotionInfo>();
        EmotionPageInfo emotionPageInfo = new EmotionPageInfo();
        emotionPageInfo.setColumnSize(columnSize);
        emotionPageInfo.setType(type1);
        emotionPageInfo.setVecEmotionInfo(vecE);
        emotionPageInfo.setItemHeight(itemH);
        emotionPageInfo.setItemWidth(itemW);
        return emotionPageInfo;
    }


    /** Menu tag name in XML. */
    private static final String XML_MENU = "menu";

    /** Group tag name in XML. */
    private static final String XML_GROUP = "group";

    /** Item tag name in XML. */
    private static final String XML_ITEM = "item";

    public static List<McTitleBarExtMenuItem> parseTitleBarExtMenu(Context context, int menuResId) {
        if (menuResId == 0) return null;
        ArrayList<McTitleBarExtMenuItem> list = new ArrayList<>();
        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getLayout(menuResId);
            AttributeSet attrs = Xml.asAttributeSet(parser);

            int eventType = parser.getEventType();
            String tagName;
            boolean lookingForEndOfUnknownTag = false;
            String unknownTagName = null;

            // This loop will skip to the menu start tag
            do {
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = parser.getName();
                    if (tagName.equals(XML_MENU)) {
                        // Go to next tag
                        eventType = parser.next();
                        break;
                    }

                    throw new RuntimeException("Expecting menu, got " + tagName);
                }
                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);

            boolean reachedEndOfMenu = false;
            while (!reachedEndOfMenu) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (lookingForEndOfUnknownTag) {
                            break;
                        }

                        tagName = parser.getName();
                        if (tagName.equals(XML_GROUP)) {
                            // TODO: 3/2/16 之后可添加支持带分组的menu项
                        } else if (tagName.equals(XML_ITEM)) {
                            list.add(generateMenuItem(context, attrs));
                        } else {
                            lookingForEndOfUnknownTag = true;
                            unknownTagName = tagName;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if (lookingForEndOfUnknownTag && tagName.equals(unknownTagName)) {
                            lookingForEndOfUnknownTag = false;
                            unknownTagName = null;
                        } else if (tagName.equals(XML_GROUP)) {
                            // TODO: 3/2/16 之后可添加支持带分组的menu项
                        } else if (tagName.equals(XML_MENU)) {
                            reachedEndOfMenu = true;
                        }
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        throw new RuntimeException("Unexpected end of document");
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (parser != null) parser.close();
        }

        return list;
    }

    private static McTitleBarExtMenuItem generateMenuItem(Context context, AttributeSet attrs) {
        McTitleBarExtMenuItem item = new McTitleBarExtMenuItem();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.McTitleBarExt);
        item.id = ta.getResourceId(R.styleable.McTitleBarExt_android_id, 0);
        item.title = ta.getString(R.styleable.McTitleBarExt_android_title);
        item.iconLargeResId = ta.getResourceId(R.styleable.McTitleBarExt_mc_title_action_menu_item_icon_large, 0);
        item.iconSmallResId = ta.getResourceId(R.styleable.McTitleBarExt_android_icon, 0);
        ta.recycle();
        return item;
    }
}