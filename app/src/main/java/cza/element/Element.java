package cza.element;

import cza.MyFE.FEReader;
import android.view.View;

public abstract class Element {
	public static FEReader mReader;
	public final static String
	TAG_ELEMENTS = "Elements",
	TAG_CHILDREN = "Children",
	TAG_PARENT_SELECT = "SelectParent",
	TAG_PARENT_GROUP = "GroupParent",
	TAG_CHILDREN_GROUP = "ChildGroup",
	TAG_CHILDREN_TEXT_ITEM = "TextItem",
	TAG_CHILDREN_INPUT_ITEM = "InputItem",
	TAG_CHILDREN_BYTES_INPUT = "BytesInput",
	TAG_CHILDREN_FLAG_SELECT = "FlagSelect",
	TAG_CHILDREN_SIMPLE_SELECT = "SimpleSelect",
	ATTR_COUNT = "count",
	ATTR_TITLE = "title",
	ATTR_ITEM_TITLE = "itemTitle",
	ATTR_ENTRIES = "entries",
	ATTR_ENTRIES_SKIP = "entriesSkip",
	ATTR_OFFSET = "offset",
	ATTR_SIZE = "size",
	ATTR_ADDR = "addr",
	ATTR_CHILDREN_LIST = "childrenList",
	ATTR_INCREMENT = "increment",
	ATTR_READ_ONLY = "readOnly";
	
	public abstract View getView();
}
