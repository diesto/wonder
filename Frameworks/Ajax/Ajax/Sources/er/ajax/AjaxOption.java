package er.ajax;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class AjaxOption {
  public static final AjaxOption.Type DEFAULT = new AjaxOption.Type(0);
  public static final AjaxOption.Type STRING = new AjaxOption.Type(1);
  public static final AjaxOption.Type SCRIPT = new AjaxOption.Type(2);
  public static final AjaxOption.Type NUMBER = new AjaxOption.Type(3);
  public static final AjaxOption.Type ARRAY = new AjaxOption.Type(4);
  public static final AjaxOption.Type STRING_ARRAY = new AjaxOption.Type(5);
  public static final AjaxOption.Type BOOLEAN = new AjaxOption.Type(6);
  public static final AjaxOption.Type STRING_OR_ARRAY = new AjaxOption.Type(7);
  public static final AjaxOption.Type DICTIONARY = new AjaxOption.Type(8);
  public static final AjaxOption.Type FUNCTION = new AjaxOption.Type(9);	// Function with no args
  public static final AjaxOption.Type FUNCTION_1 = new AjaxOption.Type(9);// Function with one arg
  public static final AjaxOption.Type FUNCTION_2 = new AjaxOption.Type(9);// Function with two args
  
  public static class Type {
    private int _number;

    public Type(int number) {
      _number = number;
    }
  }

  private String _name;
  private String _bindingName;
  private Object _defaultValue;
  private AjaxOption.Type _type;

  public AjaxOption(String name) {
    this(name, AjaxOption.DEFAULT);
  }

  public AjaxOption(String name, AjaxOption.Type type) {
	this(name, name, null, type);
  }

  public AjaxOption(String name, Object defaultValue, AjaxOption.Type type) {
	this(name, name, defaultValue, type);
  }
  
  public AjaxOption(String name, String bindingName, Object defaultValue, AjaxOption.Type type) {
    _name = name;
    _bindingName = bindingName;
    _type = type;
    _defaultValue = defaultValue;
  }

  public String name() {
    return _name;
  }

  public AjaxOption.Type type() {
    return _type;
  }

  public AjaxValue valueForObject(Object obj) {
	  return new AjaxValue(_type, obj);
  }

  public Object defaultValue() {
	  return _defaultValue;
  }
  
  protected Object valueInComponent(WOComponent component) {
	Object value = component.valueForBinding(_bindingName);
    if (value instanceof WOAssociation) {
      WOAssociation association = (WOAssociation) value;
      value = association.valueInComponent(component);
    }
	if (value == null) {
		value = _defaultValue;
	}
    return value;
  }
  
  protected Object valueInComponent(WOComponent component, NSDictionary<String, ? extends WOAssociation> associations) {
	Object value = null;
	if (associations != null) {
		value = associations.objectForKey(_bindingName);
		if (value instanceof WOAssociation) {
			WOAssociation association = (WOAssociation) value;
			value = association.valueInComponent(component);
		}
	}
	if (value == null) {
		value = _defaultValue;
	}
    return value;
  }
  
  public void addToDictionary(WOComponent component, NSMutableDictionary<String, String> dictionary) {
    Object value = valueInComponent(component);
    String strValue = valueForObject(value).javascriptValue();
    if (strValue != null) {
      dictionary.setObjectForKey(strValue, _name);
    }
  }

  protected void addToDictionary(WOComponent component, NSDictionary<String, ? extends WOAssociation> associations, NSMutableDictionary<String, String> dictionary) {
	Object value = valueInComponent(component, associations);
    String strValue = valueForObject(value).javascriptValue();
    if (strValue != null) {
      dictionary.setObjectForKey(strValue, _name);
    }
  }

  public static NSMutableDictionary<String, String> createAjaxOptionsDictionary(NSArray<AjaxOption> ajaxOptions, WOComponent component) {
	NSMutableDictionary<String, String> optionsDictionary = new NSMutableDictionary<String, String>();
    for (AjaxOption ajaxOption : ajaxOptions) {
      ajaxOption.addToDictionary(component, optionsDictionary);
    }
    return optionsDictionary;
  }

  public static NSMutableDictionary<String, String> createAjaxOptionsDictionary(NSArray<AjaxOption> ajaxOptions, WOComponent component, NSDictionary<String, ? extends WOAssociation> associations) {
    NSMutableDictionary<String, String> optionsDictionary = new NSMutableDictionary<String, String>();
    for (AjaxOption ajaxOption : ajaxOptions) {
      ajaxOption.addToDictionary(component, associations, optionsDictionary);
    }
    return optionsDictionary;
  }
}
