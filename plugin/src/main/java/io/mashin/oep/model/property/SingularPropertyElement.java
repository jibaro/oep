package io.mashin.oep.model.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;


public abstract class SingularPropertyElement extends PropertyElement {

  protected IPropertyDescriptor[] descriptor;
  
  public SingularPropertyElement(String id, String name) {
    super(id, name);
  }

  @Override
  public void setValue(String id, Object value) {
    setValue(value);
  }

  @Override
  public void resetValue(String id) {
    resetValue();
  }

  @Override
  public boolean isSet(String id) {
    return isSet();
  }

  @Override
  public Object getValue(String id) {
    return getValue();
  }

  @Override
  public Object getEditableValue(String id) {
    return getEditableValue();
  }

  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    if (descriptor == null) {
      IPropertyDescriptor desc = getPropertyDescriptor();
      if (desc != null && isEditable()) {
        if (category != null && !category.isEmpty()) {
          ((PropertyDescriptor) desc).setCategory(category);
        }
        descriptor = new IPropertyDescriptor[] {
            getPropertyDescriptor()
        };
      } else {
        descriptor = new IPropertyDescriptor[0];
      }
    }
    return descriptor;
  }
  
  public abstract IPropertyDescriptor getPropertyDescriptor();
  
}