package org.tessell.model.dsl;

import static org.tessell.util.ObjectUtils.eq;
import static org.tessell.util.StringUtils.sanitizeIfString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tessell.gwt.user.client.ui.HasCss;
import org.tessell.gwt.user.client.ui.IsListBox;
import org.tessell.gwt.user.client.ui.IsTextBox;
import org.tessell.model.events.PropertyChangedEvent;
import org.tessell.model.events.PropertyChangedHandler;
import org.tessell.model.properties.HasMaxLength;
import org.tessell.model.properties.ListProperty;
import org.tessell.model.properties.Property;
import org.tessell.util.ObjectUtils;
import org.tessell.widgets.IsTextList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;

/** Binds properties to widgets. */
public class PropertyBinder<P> {

  protected final Binder b;
  protected final Property<P> p;

  public PropertyBinder(final Binder b, final Property<P> p) {
    this.b = b;
    this.p = p;
  }

  /** Binds our property to {@code value} (one-way). */
  public void to(final TakesValue<P> value) {
    b.add(p.addPropertyChangedHandler(new PropertyChangedHandler<P>() {
      public void onPropertyChanged(final PropertyChangedEvent<P> event) {
        value.setValue(p.get());
      }
    }));
    // Set initial value. Even though this is one-way, if value is a cookie/etc.,
    // we may want to set the initial value of our property back to the current
    // value of value. Do this only one, and only if the property looks unset
    // (non-touched and null).
    if (b.canSetInitialValue(p) && value.getValue() != null) {
      p.setInitialValue(value.getValue());
    } else {
      value.setValue(p.get());
    }
  }

  /** Binds our property to {@code source} (two-way). */
  public void to(final HasValue<P> source) {
    // set initial value
    if (b.canSetInitialValue(p) && sanitizeIfString(source.getValue()) != null) {
      p.setInitialValue(sanitizeIfString(source.getValue()));
    } else {
      source.setValue(p.get(), true);
    }
    // after we've set the initial value (which fired ValueChangeEvent and
    // would have messed up our 'touched' state), listen for others changes
    if (!p.isReadOnly()) {
      b.add(source.addValueChangeHandler(new ValueChangeHandler<P>() {
        public void onValueChange(final ValueChangeEvent<P> event) {
          p.set(sanitizeIfString(source.getValue()));
        }
      }));
    }
    b.add(p.addPropertyChangedHandler(new PropertyChangedHandler<P>() {
      public void onPropertyChanged(final PropertyChangedEvent<P> event) {
        source.setValue(event.getProperty().get(), true);
      }
    }));
    if (p instanceof HasMaxLength && source instanceof IsTextBox) {
      final Integer length = ((HasMaxLength) p).getMaxLength();
      if (length != null) {
        ((IsTextBox) source).setMaxLength(length.intValue());
      }
    }
  }

  /** Binds our {@code p} to the selection in {@code source}, given the {@code options}. */
  public void to(final IsListBox source, final List<P> options) {
    to(source, options, new ListBoxIdentityAdaptor<P>());
  }

  /** Binds our {@code p} to the selection in {@code source}, given the {@code options}. */
  public <O> void to(final IsListBox source, final List<O> options, final ListBoxAdaptor<P, O> adaptor) {
    addOptionsAndSetIfNull(source, options, adaptor);
    b.add(source.addChangeHandler(new ChangeHandler() {
      public void onChange(final ChangeEvent event) {
        final int i = source.getSelectedIndex();
        // getSelectedIndex within an onchange should never be -1, but check just in case
        if (i != -1) {
          p.set(adaptor.toValue(options.get(i)));
        }
      }
    }));
    b.add(p.addPropertyChangedHandler(new PropertyChangedHandler<P>() {
      public void onPropertyChanged(final PropertyChangedEvent<P> event) {
        source.setSelectedIndex(indexInOptions(adaptor, options));
      }
    }));
  }

  /** Binds our {@code p} to the selection in {@code source}, given the {@code options}. */
  public void to(final IsListBox source, final ListProperty<P> options) {
    to(source, options, new ListBoxIdentityAdaptor<P>());
  }

  /** Binds our {@code p} to the selection in {@code source}, given the {@code options}. */
  public <O> void to(final IsListBox source, final ListProperty<O> options, final ListBoxAdaptor<P, O> adaptor) {
    if (options.get() != null) {
      addOptionsAndSetIfNull(source, options.get(), adaptor);
    }
    b.add(source.addChangeHandler(new ChangeHandler() {
      public void onChange(final ChangeEvent event) {
        final int i = source.getSelectedIndex();
        // getSelectedIndex within an onchange should never be -1, but check just in case
        if (i != -1) {
          p.set(adaptor.toValue(options.get().get(i)));
        }
      }
    }));
    b.add(p.addPropertyChangedHandler(new PropertyChangedHandler<P>() {
      public void onPropertyChanged(final PropertyChangedEvent<P> event) {
        source.setSelectedIndex(indexInOptions(adaptor, options.get()));
      }
    }));
    options.addPropertyChangedHandler(new PropertyChangedHandler<List<O>>() {
      public void onPropertyChanged(PropertyChangedEvent<List<O>> event) {
        // it looks like this does not cause an onchange in the browser
        source.clear();
        if (options.get() != null) {
          addOptionsAndSetIfNull(source, options.get(), adaptor);
          // reselect the 1st value if p's current value is not available.
          if (p.get() != null && indexInOptions(adaptor, options.get()) == -1 && !options.get().isEmpty()) {
            p.setInitialValue(adaptor.toValue(options.get().get(0)));
          }
        }
      }
    });
  }

  /** Binds errors for our property to {@code errors}. */
  public void errorsTo(final IsTextList errors) {
    final TextListOnError i = new TextListOnError(errors);
    b.add(p.addRuleTriggeredHandler(i));
    b.add(p.addRuleUntriggeredHandler(i));
    i.addExisting(p);
  }

  /** Binds our property to {@code source} and its errors to {@code errors}. */
  public void to(final HasValue<P> source, final IsTextList errors) {
    to(source);
    errorsTo(errors);
  }

  /** Binds our property to a list of radio buttons. */
  public MoreRadioButtons to(HasValue<Boolean> button, P value) {
    return new MoreRadioButtons().and(button, value);
  }

  /** Binds our property to a list of radio buttons, and a view. */
  public MoreRadioButtons to(HasValue<Boolean> button, P value, HasCss view) {
    return new MoreRadioButtons().and(button, value, view);
  }

  public class MoreRadioButtons {
    private final Map<HasValue<Boolean>, P> buttons = new HashMap<HasValue<Boolean>, P>();

    private MoreRadioButtons() {
      // any time p changes, update all of the buttons. Granted, the browser
      // does this implicitly for radio buttons, but implementing the logic
      // like this means it will work for the stubs too.
      b.add(p.addPropertyChangedHandler(new PropertyChangedHandler<P>() {
        public void onPropertyChanged(PropertyChangedEvent<P> event) {
          for (Map.Entry<HasValue<Boolean>, P> e : buttons.entrySet()) {
            boolean isForThisValue = eq(e.getValue(), event.getNewValue());
            e.getKey().setValue(isForThisValue, true);
          }
        }
      }));
    }

    public MoreRadioButtons and(final HasValue<Boolean> button, final P value) {
      return and(button, value, null);
    }

    public MoreRadioButtons and(final HasValue<Boolean> button, final P value, final HasCss view) {
      buttons.put(button, value);
      b.add(button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
        public void onValueChange(ValueChangeEvent<Boolean> event) {
          if (event.getValue()) {
            p.set(value);
          }
        }
      }));
      button.setValue(eq(p.get(), value), true); // set initial
      if (view != null) {
        b.when(p).is(value).show(view);
      }
      return this;
    }
  }

  // can't use indexOf because we can't map value -> option, only option -> value
  private <O> int indexInOptions(ListBoxAdaptor<P, O> adaptor, List<O> options) {
    int i = 0;
    for (final O option : options) {
      if (ObjectUtils.eq(adaptor.toValue(option), p.get())) {
        return i;
      }
      i++;
    }
    return -1;
  }

  private <O> void addOptionsAndSetIfNull(final IsListBox source, final List<O> options, final ListBoxAdaptor<P, O> adaptor) {
    int i = 0;
    for (final O option : options) {
      source.addItem(adaptor.toDisplay(option), Integer.toString(i++));
    }
    if (p.get() == null) {
      if (!options.contains(null) && !options.isEmpty()) {
        p.setInitialValue(adaptor.toValue(options.get(0)));
      }
    }
    source.setSelectedIndex(indexInOptions(adaptor, options));
  }

}
