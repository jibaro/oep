package io.mashin.oep.model.property;

import org.eclipse.draw2d.geometry.Point;


public class PointPropertyElement extends PropertyElementGroup {

  public PointCoordinatePropertyElement x;
  public PointCoordinatePropertyElement y;
  
  public PointPropertyElement(String id, String name) {
    super(id, name);
    x = new PointCoordinatePropertyElement(id + ".x", "X");
    y = new PointCoordinatePropertyElement(id + ".y", "Y");
    propertyElements.add(x);
    propertyElements.add(y);
  }
  
  @Override
  public void setComplexValue(Object value) {
    if(value instanceof Point) {
      setFromPoint((Point) value);
    }
  }
  
  public Point getAsPoint() {
    return new Point(x.getAsInteger(), y.getAsInteger());
  }
  
  public void setFromPoint(Point point) {
    x.setFromInteger(point.x);
    y.setFromInteger(point.y);
  }
  
}