// Generated by view binder compiler. Do not edit!
package tk.zwander.seekbarpreference.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import tk.zwander.seekbarpreference.R;
import tk.zwander.seekbarpreference.slider.Slider;

public final class SeekbarGutsBinding implements ViewBinding {
  @NonNull
  private final View rootView;

  @NonNull
  public final FrameLayout bottomLine;

  @NonNull
  public final LinearLayout buttonHolder;

  @NonNull
  public final ImageView down;

  @NonNull
  public final TextView measurementUnit;

  @NonNull
  public final ImageView reset;

  @NonNull
  public final Slider seekbar;

  @NonNull
  public final TextView seekbarValue;

  @NonNull
  public final ImageView up;

  @NonNull
  public final LinearLayout valueHolder;

  private SeekbarGutsBinding(@NonNull View rootView, @NonNull FrameLayout bottomLine,
      @NonNull LinearLayout buttonHolder, @NonNull ImageView down,
      @NonNull TextView measurementUnit, @NonNull ImageView reset, @NonNull Slider seekbar,
      @NonNull TextView seekbarValue, @NonNull ImageView up, @NonNull LinearLayout valueHolder) {
    this.rootView = rootView;
    this.bottomLine = bottomLine;
    this.buttonHolder = buttonHolder;
    this.down = down;
    this.measurementUnit = measurementUnit;
    this.reset = reset;
    this.seekbar = seekbar;
    this.seekbarValue = seekbarValue;
    this.up = up;
    this.valueHolder = valueHolder;
  }

  @Override
  @NonNull
  public View getRoot() {
    return rootView;
  }

  @NonNull
  public static SeekbarGutsBinding inflate(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    if (parent == null) {
      throw new NullPointerException("parent");
    }
    inflater.inflate(R.layout.seekbar_guts, parent);
    return bind(parent);
  }

  @NonNull
  public static SeekbarGutsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bottom_line;
      FrameLayout bottomLine = ViewBindings.findChildViewById(rootView, id);
      if (bottomLine == null) {
        break missingId;
      }

      id = R.id.button_holder;
      LinearLayout buttonHolder = ViewBindings.findChildViewById(rootView, id);
      if (buttonHolder == null) {
        break missingId;
      }

      id = R.id.down;
      ImageView down = ViewBindings.findChildViewById(rootView, id);
      if (down == null) {
        break missingId;
      }

      id = R.id.measurement_unit;
      TextView measurementUnit = ViewBindings.findChildViewById(rootView, id);
      if (measurementUnit == null) {
        break missingId;
      }

      id = R.id.reset;
      ImageView reset = ViewBindings.findChildViewById(rootView, id);
      if (reset == null) {
        break missingId;
      }

      id = R.id.seekbar;
      Slider seekbar = ViewBindings.findChildViewById(rootView, id);
      if (seekbar == null) {
        break missingId;
      }

      id = R.id.seekbar_value;
      TextView seekbarValue = ViewBindings.findChildViewById(rootView, id);
      if (seekbarValue == null) {
        break missingId;
      }

      id = R.id.up;
      ImageView up = ViewBindings.findChildViewById(rootView, id);
      if (up == null) {
        break missingId;
      }

      id = R.id.value_holder;
      LinearLayout valueHolder = ViewBindings.findChildViewById(rootView, id);
      if (valueHolder == null) {
        break missingId;
      }

      return new SeekbarGutsBinding(rootView, bottomLine, buttonHolder, down, measurementUnit,
          reset, seekbar, seekbarValue, up, valueHolder);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
