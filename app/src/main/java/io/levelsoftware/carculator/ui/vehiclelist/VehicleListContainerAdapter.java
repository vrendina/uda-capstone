/*
 * Copyright (C) 2015-2017 Level Software LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.levelsoftware.carculator.ui.vehiclelist;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;
import timber.log.Timber;


public class VehicleListContainerAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Make> data;
    private LinkedHashMap<Integer, Make> filtered = new LinkedHashMap<>();

    private LinkedHashMap<String, Pair<Make, Model>> searchable = new LinkedHashMap<>();

    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;
        if(viewType == VIEW_TYPE_DATA) {
            view = inflater.inflate(R.layout.list_item_vehicle_container, parent, false);
            return new VehicleListContainerViewHolder(view);
        }
        if(viewType == VIEW_TYPE_FOOTER) {
            view = inflater.inflate(R.layout.list_item_vehicle_footer, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VehicleListContainerViewHolder) {
            Integer[] keys = filtered.keySet().toArray(new Integer[filtered.keySet().size()]);
            ((VehicleListContainerViewHolder) holder).setMake(filtered.get(keys[position]));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == filtered.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return (filtered.size() == 0) ? 0 : filtered.size() + 1;
    }

    public void setData(ArrayList<Make> data) {
        this.data = data;

        if(data != null) {
            for (Make make : data) {
                for (Model model : make.models) {
                    String readableVehicleString = make.name + " " + model.name;
                    String searchableString = readableVehicleString
                            .replaceAll("[^A-Za-z0-9\\s]", "").toLowerCase(Locale.ENGLISH);

                    searchable.put(searchableString, new Pair<>(make, model));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter(String query) {
        Timber.d("Query string: '" + query + "'");

        if(query == null || TextUtils.isEmpty(query.replaceAll("[^A-Za-z0-9]", ""))) {
            updateFilteredData(null);
        } else {
            // Split the query string based on whitespace and use an AND operator on each element
            String[] queries = query.split("\\s");

            ArrayList<String> matches = new ArrayList<>();

            for(String s: searchable.keySet()) {
                // Match all parts of the query string
                for (int i = 0; i < queries.length; i++) {
                    // Remove special characters and convert to lowercase
                    String q = queries[i].replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ENGLISH);

                    if(!s.contains(q)) {
                        break;
                    } else {
                        // If we have matched all of the components add to list
                        if(i == queries.length - 1) {
                            matches.add(s);
                        }
                    }
                }
            }

            updateFilteredData(matches);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private void updateFilteredData(@Nullable ArrayList<String> matches) {
        filtered = new LinkedHashMap<>();

        if(data != null) {
            if (matches == null) {
                for (Make make : data) {
                    filtered.put(make.niceName.hashCode(), make);
                }
            } else {
                for (String key : matches) {

                    Pair match = searchable.get(key);

                    Make make = (Make) match.first;
                    Model model = (Model) match.second;

                    if (!filtered.containsKey(make.niceName.hashCode())) {
                        Make filteredMake = new Make(make.name, make.niceName, new ArrayList<Model>());
                        filtered.put(filteredMake.niceName.hashCode(), filteredMake);
                    }
                    filtered.get(make.niceName.hashCode()).models.add(model);
                }
            }
        }

        notifyDataSetChanged();
    }

}
