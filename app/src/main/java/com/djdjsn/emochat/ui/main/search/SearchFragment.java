package com.djdjsn.emochat.ui.main.search;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.databinding.FragmentSearchBinding;
import com.djdjsn.emochat.utils.UiUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private NavController navController;


    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSearchBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        navController = Navigation.findNavController(view);

        UiUtils.setOnTextChangeListener(binding.editTextQuery, s -> viewModel.onQueryChanged(s));

        binding.recyclerUsers.setHasFixedSize(true);
        UserAdapter adapter = new UserAdapter();
        binding.recyclerUsers.setAdapter(adapter);

        adapter.setOnItemSelectedListener(position -> {
            User user = adapter.getCurrentList().get(position);
            viewModel.onUserClicked(user);
            hideKeyboard(requireView());
        });

        viewModel.getUsersQueried().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                adapter.submitList(users);
                binding.textViewNoUsers.setVisibility(users.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            } else {
                Toast.makeText(requireContext(), "회원 검색에 실패했습니다", Toast.LENGTH_SHORT).show();
            }
            binding.progressBarUsers.setVisibility(View.INVISIBLE);
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SearchViewModel.Event.ShowLoadingUI) {
                binding.progressBarUsers.setVisibility(View.VISIBLE);
                binding.textViewNoUsers.setVisibility(View.INVISIBLE);
            } else if (event instanceof SearchViewModel.Event.NavigateBackWithResult) {
                User user = ((SearchViewModel.Event.NavigateBackWithResult) event).user;
                Bundle result = new Bundle();
                result.putSerializable("user", user);
                getParentFragmentManager().setFragmentResult("search_fragment", result);
                navController.popBackStack();
            } else if (event instanceof SearchViewModel.Event.ShowGeneralMessage) {
                String message = ((SearchViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }  else if (event instanceof SearchViewModel.Event.ShowGeneralMessageAndHideLoadingUI) {
                String message = ((SearchViewModel.Event.ShowGeneralMessageAndHideLoadingUI) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                binding.progressBarUsers.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}