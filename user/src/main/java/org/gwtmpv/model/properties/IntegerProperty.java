package org.gwtmpv.model.properties;

import org.gwtmpv.model.values.Value;

public class IntegerProperty extends AbstractProperty<Integer, IntegerProperty> {

  public IntegerProperty(final Value<Integer> value) {
    super(value);
  }

  @Override
  protected IntegerProperty getThis() {
    return this;
  }

  public Property<String> asString() {
    return new FormattedProperty<String, Integer>(this, new PropertyFormatter<Integer, String>() {
      public String format(Integer a) {
        return Integer.toString(a);
      }

      @Override
      public Integer parse(String b) {
        return Integer.parseInt(b);
      }
    });
  }
}
