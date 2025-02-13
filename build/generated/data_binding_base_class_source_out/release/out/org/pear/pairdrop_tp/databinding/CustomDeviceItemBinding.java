// Generated by view binder compiler. Do not edit!
package org.pear.pairdrop_tp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.pear.pairdrop_tp.R;

public final class CustomDeviceItemBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final TextView deviceNameOrIP;

  @NonNull
  public final TextView deviceNameOrIPBackdrop;

  @NonNull
  public final FrameLayout swipeableView;

  private CustomDeviceItemBinding(@NonNull FrameLayout rootView, @NonNull TextView deviceNameOrIP,
      @NonNull TextView deviceNameOrIPBackdrop, @NonNull FrameLayout swipeableView) {
    this.rootView = rootView;
    this.deviceNameOrIP = deviceNameOrIP;
    this.deviceNameOrIPBackdrop = deviceNameOrIPBackdrop;
    this.swipeableView = swipeableView;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CustomDeviceItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CustomDeviceItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.custom_device_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CustomDeviceItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.deviceNameOrIP;
      TextView deviceNameOrIP = rootView.findViewById(id);
      if (deviceNameOrIP == null) {
        break missingId;
      }

      id = R.id.deviceNameOrIPBackdrop;
      TextView deviceNameOrIPBackdrop = rootView.findViewById(id);
      if (deviceNameOrIPBackdrop == null) {
        break missingId;
      }

      id = R.id.swipeableView;
      FrameLayout swipeableView = rootView.findViewById(id);
      if (swipeableView == null) {
        break missingId;
      }

      return new CustomDeviceItemBinding((FrameLayout) rootView, deviceNameOrIP,
          deviceNameOrIPBackdrop, swipeableView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
