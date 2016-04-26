package com.waynezhang.mcommon.template.response;


import java.io.Serializable;
import java.util.List;

/**
 * Created by sunxinxin on 10/27/15.
 */
public class TemplateResponse implements Serializable {

    public static final String GROUP_DATA = "data";
    public static final String GROUP_HEAD = "head";
    public static final String GROUP_SHOW = "show";
    public static final String GROUP_HIDE = "hide";
    public static final String GROUP_OTHER = "other";


    public static final String FIELD_NO_DRIFT = "no_drift";


    public static final String TYPE_SELECT = "selector";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_INPUT = "input";
    public static final String TYPE_MULTI_INPUT = "multiline";
    public static final String TYPE_SECRET = "secret";
    public static final String TYPE_IMAGE = "images";

    public Content content;

    public static class Content implements  Serializable {
        public List<GroupItem> groups;
    }

    public static class GroupItem implements Serializable {
        public String id;
        public String text;
        public List<TemplateField> fields;

        public GroupItem(String mid, String mtext, List<TemplateField> mfields){
            this.id = mid;
            this.text = mtext;
            this.fields = mfields;
        }
    }

    public static class TemplateField implements Serializable {
        public String id;
        public String text;
        public String type = TYPE_TEXT;
        public int allowEmpty = 0;
        public int allowModify = 1;
        public String tip;
        public String inputType = "text";
        public int minValue = -1;
        public int maxValue = -1;
        public String hint;
        public int maxLength = 128;
        public String validate;
        public String validateFailedMsg;
        public int miniCount;
        public int maxCount;
        public List<TemplateField> values;
        public String unitName;
        public int integrity = 0;
        public String desc;

        public TemplateField(){

        }

        public TemplateField(String mid, String mtext, String mtype, List<TemplateField> mvalue){
            this.id = mid;
            this.text = mtext;
            this.type = mtype;
            this.values = mvalue;
        }
    }


}
