package com.example.homework1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homework1.contact.ContactContent;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * A fragment representing a list of Items.
 */
public class ContactFragment extends Fragment implements MyContactRecyclerViewAdapter.InputEventsListener, DeleteDialog.OnDeleteDialogInteractionListener {
    private int mColumnCount = 1;
    private int currentItemPosition = -1;
    private MyContactRecyclerViewAdapter myContactRecyclerViewAdapter;

    public ContactFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));


            myContactRecyclerViewAdapter = new MyContactRecyclerViewAdapter(ContactContent.ITEMS);
            myContactRecyclerViewAdapter.setInputEventsListener(this);
            recyclerView.setAdapter(myContactRecyclerViewAdapter);
        }

        view.findViewById(R.id.add_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ContactFragment.this).navigate(R.id.action_contactFragment_to_addContactFragment);
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Bundle arguments = new Bundle();
        arguments.putInt(getString(R.string.contact_position_key), position);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentManager childFragmentManager = getChildFragmentManager();
            DisplayContactFragment displayContactFragment = (DisplayContactFragment) childFragmentManager.findFragmentById(R.id.dispFragment);
            displayContactFragment.displayContact(position);
        }
        else{
            NavHostFragment.findNavController(ContactFragment.this).navigate(R.id.action_contactFragment_to_displayContactFragment, arguments);
        }

    }

    @Override
    public void onItemLongClick(int position) {
        //go to second activity
        Intent intent = new Intent (getActivity(), CallActivity.class);
        intent.putExtra(getString(R.string.contact_position_key), position);
        startActivity(intent);
    }

    @Override
    public void onItemDeleteClick(int position) {
        currentItemPosition = position;
        showDeleteDialog();
    }
    public void showDeleteDialog(){
        DeleteDialog.newInstance(this).show(getParentFragmentManager(), "DeleteFragmentTag");
    }
    @Override
    public void onDialogPositiveClick() {
        if(currentItemPosition != -1 && currentItemPosition < ContactContent.ITEMS.size()){
            ContactContent.removeItem(currentItemPosition);
            notifyDataChange();
        }
    }

    private void notifyDataChange() {
        myContactRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick() {
        Snackbar.make(getActivity().findViewById(R.id.fragment), R.string.snackbar_cancel_delete_msg,
                BaseTransientBottomBar.LENGTH_LONG).setAction(getString(R.string.snackbar_retry_msg), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        }).show();
    }
}