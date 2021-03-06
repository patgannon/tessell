package org.tessell.model.properties;

import java.util.List;

import org.tessell.util.ListDiff;
import org.tessell.util.ListDiff.Location;

public class UpstreamState {

  private final Property<?> owner;
  private final boolean touch;
  private List<Property<?>> lastUpstream;

  public UpstreamState(Property<?> owner, boolean touch) {
    this.owner = owner;
    this.touch = touch;
  }

  public void update(List<Property<?>> newUpstream) {
    // Only update our upstream properties if they've changed
    if (lastUpstream == null || !lastUpstream.equals(newUpstream)) {
      ListDiff<Property<?>> diff = ListDiff.of(lastUpstream, newUpstream);
      for (Location<Property<?>> removedLocation : diff.removed) {
        Property<?> removed = removedLocation.element;
        removed.removeDerived(owner, this);
      }
      for (Location<Property<?>> addedLocation : diff.added) {
        Property<?> added = addedLocation.element;
        if (added != owner) {
          added.addDerived(owner, this, touch);
        }
      }
      // Remember for change tracking next time
      lastUpstream = newUpstream;
    }
  }
}
