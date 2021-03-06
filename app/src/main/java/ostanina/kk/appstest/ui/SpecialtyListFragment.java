package ostanina.kk.appstest.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.DaggerFragment;
import ostanina.kk.appstest.R;
import ostanina.kk.appstest.ui.adapters.SpecialtyRecyclerViewAdapter;
import ostanina.kk.appstest.factory.ViewModelFactory;
import ostanina.kk.appstest.data.model.Specialty;
import ostanina.kk.appstest.ui.viewmodels.SpecialtyListViewModel;

public class SpecialtyListFragment extends DaggerFragment {
    /**
     *Required interface for hosting activities
     */
    public interface Callbacks {
        void onSpecialtySelected(Specialty specialty);
    }

    @Inject
    ViewModelFactory viewModelFactory;

    private SpecialtyListViewModel specialtyListViewModel;
    private RecyclerView recyclerView;
    private SpecialtyRecyclerViewAdapter adapter;
    private Callbacks listener;

    private RelativeLayout errorLayout;
    private TextView errorMessageLayout;
    private Button retryButton;

    public static SpecialtyListFragment newInstance() {
        return new SpecialtyListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            listener = (Callbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Callbacks interface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        initRecyclerView();

        errorLayout = view.findViewById(R.id.error_layout);
        errorMessageLayout = view.findViewById(R.id.text_view_error_message);
        retryButton = view.findViewById(R.id.button_retry);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        specialtyListViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SpecialtyListViewModel.class);
        subscribeObservers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void subscribeObservers() {
        specialtyListViewModel.getSpecialties().observe(this, new Observer<List<Specialty>>() {
            @Override
            public void onChanged(List<Specialty> specialties) {
                adapter.setSpecialties(specialties);
            }
        });


        specialtyListViewModel.checkError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b) {
                    showErrorMessage(getString(R.string.error_retrieving_data));
                }
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SpecialtyRecyclerViewAdapter(getContext(), listener);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void showErrorMessage(String message) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
        }
        errorMessageLayout.setText(message);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorLayout.setVisibility(View.GONE);
                specialtyListViewModel.loadData();
            }
        });
    }
}
