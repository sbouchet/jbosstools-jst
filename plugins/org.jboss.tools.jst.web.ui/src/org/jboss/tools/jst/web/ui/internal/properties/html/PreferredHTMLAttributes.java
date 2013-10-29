package org.jboss.tools.jst.web.ui.internal.properties.html;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.jst.web.html.JQueryHTMLConstants;

public class PreferredHTMLAttributes implements JQueryHTMLConstants {

	static String[] HIDDEN_INPUT_ATTRS = {ATTR_TYPE, ATTR_NAME, ATTR_VALUE, ATTR_FORM};
	static String[] COMMON_INPUT_ATTRS = {ATTR_TYPE, ATTR_NAME, ATTR_VALUE, ATTR_FORM, ATTR_DISABLED, ATTR_AUTOFOCUS};
	static String[] TEXT_INPUT_ATTRS = {ATTR_PLACEHOLDER, ATTR_REQUIRED, ATTR_READONLY, ATTR_MAXLENGTH};
	static String[] TEXT_AREA_ATTRS = {ATTR_COLS, ATTR_ROWS, ATTR_WRAP};
	static String[] NUMBER_INPUT_ATTRS = {ATTR_PATTERN, ATTR_PLACEHOLDER, ATTR_REQUIRED, ATTR_READONLY, ATTR_MAXLENGTH, ATTR_MIN, ATTR_MAX, ATTR_STEP};
	static String[] RANGE_INPUT_ATTRS = {ATTR_REQUIRED, ATTR_READONLY, ATTR_MIN, ATTR_MAX, ATTR_STEP};

	static Map<String, String[]> attributesByTag = new HashMap<String, String[]>();

	private static void addTag(String tagName, String... attributes) {
		attributesByTag.put(tagName, attributes);
	}
	static {
		addTag(TAG_A, 		ATTR_HREF);
		addTag(TAG_AREA, 	ATTR_HREF, ATTR_ALT, ATTR_REL, ATTR_SHAPE, ATTR_COORDS, ATTR_DOWNLOAD);
		addTag(TAG_AUDIO, 	ATTR_SRC, ATTR_PRELOAD, ATTR_CONTROLS, ATTR_LOOP, ATTR_AUTOPLAY, ATTR_MUTED);
		addTag(TAG_BASE, 	ATTR_HREF);
		addTag(TAG_BDO, 	ATTR_DIR);
		addTag(TAG_BLOCKQUOTE, ATTR_CITE);
		addTag(TAG_BUTTON, 	COMMON_INPUT_ATTRS);
		addTag(TAG_CANVAS, 	ATTR_HEIGHT, ATTR_WIDTH);
		addTag(TAG_COMMAND, ATTR_LABEL, ATTR_ICON, CHECKED, ATTR_DISABLED, ATTR_TYPE, ATTR_RADIOGROUP);
		addTag(TAG_DEL, 	ATTR_CITE, ATTR_DATETIME);
		addTag(TAG_DETAILS, ATTR_OPEN);
		addTag(TAG_DIALOG, 	ATTR_OPEN);
		addTag(TAG_EMBED, 	ATTR_SRC, ATTR_HEIGHT, ATTR_WIDTH, ATTR_TYPE);
		addTag(TAG_FIELDSET, ATTR_NAME, ATTR_FORM, ATTR_DISABLED);
		addTag(TAG_FORM, 	ATTR_NAME, ATTR_ACTION, ATTR_METHOD, ATTR_NOVALIDATE, ATTR_AUTOCOMPLETE);
		addTag(TAG_HTML, 	ATTR_MANIFEST);
		addTag(TAG_IFRAME, 	ATTR_NAME, ATTR_SRC, ATTR_SRCDOC, ATTR_WIDTH, ATTR_HEIGHT, ATTR_SANDBOX, ATTR_SEAMLESS);
		addTag(TAG_IMG, 	ATTR_SRC, ATTR_ALT, ATTR_WIDTH, ATTR_HEIGHT, ATTR_ISMAP, ATTR_USEMAP, ATTR_CROSSORIGIN);
		addTag(TAG_INS, 	ATTR_CITE, ATTR_DATETIME);
		addTag(TAG_KEYGEN,	ATTR_NAME, ATTR_KEYTYPE, ATTR_AUTOFOCUS, ATTR_DISABLED, ATTR_CHALLENGE, ATTR_FORM);
		addTag(TAG_LABEL, 	ATTR_FOR, ATTR_FORM);
		addTag(TAG_LI, 		ATTR_VALUE);
		addTag(TAG_LINK, 	ATTR_REL, ATTR_HREF, ATTR_TYPE);
		addTag(TAG_MAP, 	ATTR_NAME);
		addTag(TAG_MENU, 	ATTR_LABEL, ATTR_TYPE);
		addTag(TAG_META, 	ATTR_NAME, ATTR_HTTP_EQUIV, ATTR_CONTENT, ATTR_CHARSET);
		addTag(TAG_METER, 	ATTR_VALUE, ATTR_MIN, ATTR_LOW, ATTR_OPTIMUM, ATTR_HIGH, ATTR_MAX, ATTR_FORM);
		addTag(TAG_OBJECT, 	ATTR_NAME, ATTR_WIDTH, ATTR_HEIGHT, ATTR_USEMAP, ATTR_TYPE, ATTR_FORM);
		addTag(TAG_OL, 		ATTR_TYPE, ATTR_REVERSED, ATTR_START);
		addTag(TAG_OPTGROUP, ATTR_LABEL, ATTR_DISABLED);
		addTag(TAG_OPTION, 	ATTR_LABEL, ATTR_VALUE, SELECTED, ATTR_DISABLED);
		addTag(TAG_OUTPUT, 	ATTR_NAME, ATTR_FOR, ATTR_FORM);
		addTag(TAG_PARAM, 	ATTR_NAME, ATTR_VALUE);
		addTag(TAG_PROGRESS, ATTR_VALUE, ATTR_MAX);
		addTag(TAG_Q, 		ATTR_CITE);
		addTag(TAG_SELECT, 	ATTR_NAME, ATTR_DISABLED, ATTR_AUTOFOCUS, ATTR_REQUIRED, ATTR_MULTIPLE, ATTR_SIZE, ATTR_FORM);
		addTag(TAG_SOURCE, 	ATTR_SRC, ATTR_TYPE);
		addTag(TAG_SCRIPT, 	ATTR_SRC, ATTR_TYPE, ATTR_ASYNC, ATTR_DEFER, ATTR_CHARSET);
		addTag(TAG_STYLE, 	ATTR_TYPE, ATTR_SCOPED);
		addTag(TAG_TABLE,	ATTR_BORDER);
		addTag(TAG_TD,		ATTR_COLSPAN, ATTR_ROWSPAN, ATTR_HEADERS);
		addTag(TAG_TH,		ATTR_COLSPAN, ATTR_ROWSPAN, ATTR_HEADERS, ATTR_SCOPE);
		addTag(TAG_TIME, 	ATTR_DATETIME);
		addTag(TAG_TRACK, 	ATTR_SRC, ATTR_LABEL, ATTR_DEFAULT, ATTR_KIND);
		addTag(TAG_VIDEO, 	ATTR_SRC, ATTR_POSTER, ATTR_WIDTH, ATTR_HEIGHT, ATTR_PRELOAD, ATTR_CONTROLS, ATTR_LOOP, ATTR_AUTOPLAY, ATTR_MUTED);
	}

	static String[] getAttributesByTag(String tagName) {
		return attributesByTag.get(tagName.toLowerCase());
	}

}
