/*
 * SPDX-FileCopyrightText: 2018 Nicolas Fella <nicolas.fella@gmx.de>
 *
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.pear.pairdrop.Plugins.SystemVolumePlugin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.pear.pairdrop.BackgroundService;
import org.pear.pairdrop.Plugins.MprisPlugin.MprisPlugin;
import org.pear.pairdrop.Plugins.MprisPlugin.VolumeKeyListener;
import org.pear.pairdrop_tp.R;
import org.pear.pairdrop_tp.databinding.ListItemSystemvolumeBinding;
import org.pear.pairdrop_tp.databinding.SystemVolumeFragmentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SystemVolumeFragment
        extends Fragment
        implements Sink.UpdateListener, SystemVolumePlugin.SinkListener, VolumeKeyListener {

    private SystemVolumePlugin plugin;
    private RecyclerSinkAdapter recyclerAdapter;
    private boolean tracking;
    private final Consumer<Boolean> trackingConsumer = aBoolean -> tracking = aBoolean;
    private SystemVolumeFragmentBinding systemVolumeFragmentBinding;

    public static SystemVolumeFragment newInstance(String deviceId) {
        SystemVolumeFragment systemvolumeFragment = new SystemVolumeFragment();

        Bundle arguments = new Bundle();
        arguments.putString(MprisPlugin.DEVICE_ID_KEY, deviceId);

        systemvolumeFragment.setArguments(arguments);

        return systemvolumeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        if (systemVolumeFragmentBinding == null) {
            systemVolumeFragmentBinding = SystemVolumeFragmentBinding.inflate(inflater);

            RecyclerView recyclerView = systemVolumeFragmentBinding.audioDevicesRecycler;

            int gap = requireContext().getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            recyclerView.addItemDecoration(new ItemGapDecoration(gap));
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            recyclerAdapter = new RecyclerSinkAdapter();
            recyclerView.setAdapter(recyclerAdapter);
        }

        return systemVolumeFragmentBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        connectToPlugin(getDeviceId());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disconnectFromPlugin(getDeviceId());
    }

    @Override
    public void updateSink(final Sink sink) {

        // Don't set progress while the slider is moved
        if (!tracking) {

            requireActivity().runOnUiThread(() -> recyclerAdapter.notifyDataSetChanged());
        }
    }

    private void connectToPlugin(final String deviceId) {
        BackgroundService.RunWithPlugin(requireActivity(), deviceId, SystemVolumePlugin.class, plugin -> {
            this.plugin = plugin;
            plugin.addSinkListener(SystemVolumeFragment.this);
            plugin.requestSinkList();
        });
    }

    private void disconnectFromPlugin(final String deviceId) {
        BackgroundService.RunWithPlugin(requireActivity(), deviceId, SystemVolumePlugin.class, plugin ->
                plugin.removeSinkListener(SystemVolumeFragment.this));
    }

    @Override
    public void sinksChanged() {

        for (Sink sink : plugin.getSinks()) {
            sink.addListener(SystemVolumeFragment.this);
        }

        requireActivity().runOnUiThread(() -> {
            List<Sink> newSinks = new ArrayList<>(plugin.getSinks());
            recyclerAdapter.submitList(newSinks);
        });
    }

    @Override
    public void onVolumeUp() {
        updateDefaultSinkVolume(5);
    }

    @Override
    public void onVolumeDown() {
        updateDefaultSinkVolume(-5);
    }

    private void updateDefaultSinkVolume(int percent) {

        if (percent < -100) percent = -100;
        if (percent > 100) percent = 100;

        Optional<Sink> foundSink = plugin.getSinks().stream().filter(Sink::isDefault).findFirst();
        if (foundSink.isPresent()) {
            Sink defaultSink = foundSink.get();

            int step = defaultSink.getMaxVolume() * percent / 100;

            int newVolume = defaultSink.getVolume() + step;

            if (newVolume > defaultSink.getMaxVolume()) {
                newVolume = defaultSink.getMaxVolume();
            } else if (newVolume < 0) {
                newVolume = 0;
            }

            if (defaultSink.getVolume() == newVolume) return;

            plugin.sendVolume(defaultSink.getName(), newVolume);
        }
    }

    private String getDeviceId() {
        return requireArguments().getString(MprisPlugin.DEVICE_ID_KEY);
    }

    private class RecyclerSinkAdapter extends ListAdapter<Sink, SinkItemHolder> {

        public RecyclerSinkAdapter() {
            super(new SinkItemCallback());
        }

        @NonNull
        @Override
        public SinkItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = getLayoutInflater();
            ListItemSystemvolumeBinding viewBinding = ListItemSystemvolumeBinding.inflate(inflater, parent, false);

            return new SinkItemHolder(viewBinding, plugin, trackingConsumer);
        }

        @Override
        public void onBindViewHolder(@NonNull SinkItemHolder holder, int position) {
            holder.bind(getItem(position));
        }
    }
}
