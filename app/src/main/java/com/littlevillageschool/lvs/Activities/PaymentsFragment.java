package com.littlevillageschool.lvs.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.littlevillageschool.lvs.Adapters.PaymentAdapter;
import com.littlevillageschool.lvs.Adapters.PaymentsItemsAdapter;
import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Parent;
import com.littlevillageschool.lvs.Model.User;
import com.littlevillageschool.lvs.R;

/**
 * Created by Raafat Alhoumaidy on 11/4/2016.
 */

public class PaymentsFragment extends Fragment {

    private RecyclerView paymentsRecyclerView;
    private Spinner childsSpinner;
    private ProgressBar loadingPB;
    private int prevChildSelected = 0;
    private PaymentAdapter paymentAdapter;
    private PaymentsItemsAdapter paymentsItemsAdapter;
    private TextView totalTxtv;
    private TextView paymentTotalTxtV;
    private TextView netTotalTxtV;

    private Parent.Payment selectedPayment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentAdapter = new PaymentAdapter(getActivity()
                , android.R.layout.simple_list_item_1
                , ((Parent) LvsApplication.currUser).getPayments());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentsRecyclerView = (RecyclerView) view.findViewById(R.id.paymentRecyclerView);
        childsSpinner = (Spinner) view.findViewById(R.id.studentSpinner);
        loadingPB = (ProgressBar) view.findViewById(R.id.loadingPB);
        totalTxtv = (TextView) view.findViewById(R.id.totalTxtV);
        netTotalTxtV = (TextView) view.findViewById(R.id.netTotalTxtV);
        paymentTotalTxtV = (TextView) view.findViewById(R.id.paymentsSumTxtV);

        paymentsRecyclerView.setHasFixedSize(true);
        GridLayoutManager lnLayoutMgr;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE)
            lnLayoutMgr = new GridLayoutManager(getActivity(), 2);
        else
            lnLayoutMgr = new GridLayoutManager(getActivity(), 1);

        paymentsRecyclerView.setLayoutManager(lnLayoutMgr);

        childsSpinner.setAdapter(paymentAdapter);

        loadingPB.setVisibility(View.VISIBLE);

        if (((Parent) LvsApplication.currUser).getPayments().size() == 0) {
            LvsApplication.currUser.setCallBack(new User.MyCallBack() {
                @Override
                public void onSucc(User.UserService service) {
                    loadingPB.setVisibility(View.GONE);
                    paymentAdapter.notifyDataSetChanged();

                    childsSpinner.setSelection(0);
                    if (((Parent) LvsApplication.currUser).getPayments().size() > 0) {

                        selectedPayment = ((Parent) LvsApplication.currUser).getPayments().get(0);
                        totalTxtv.setText(String.valueOf(selectedPayment.getTotal()));
                        netTotalTxtV.setText(String.valueOf(selectedPayment.getNetTotal()));
                        paymentTotalTxtV.setText(String.valueOf(selectedPayment.getPaymentSum()));

                        paymentsItemsAdapter = new PaymentsItemsAdapter(getActivity()
                                , R.layout.payment_item_layout
                                , selectedPayment.getPaymentItems());
                        paymentsRecyclerView.setAdapter(paymentsItemsAdapter);

                    }

                }

                @Override
                public void onFail(User.UserService service, String errorMsg) {
                    LvsApplication.currUser.callService(service);
                }
            });

            LvsApplication.currUser.callService(User.UserService.PAYMENT);

        } else {
            selectedPayment = ((Parent) LvsApplication.currUser).getPayments().get(0);

            loadingPB.setVisibility(View.GONE);
            childsSpinner.setSelection(0);
            paymentsItemsAdapter = new PaymentsItemsAdapter(getActivity()
                    , R.layout.payment_item_layout
                    , selectedPayment.getPaymentItems());
            paymentsRecyclerView.setAdapter(paymentsItemsAdapter);

            totalTxtv.setText(String.valueOf(selectedPayment.getTotal()));
            netTotalTxtV.setText(String.valueOf(selectedPayment.getNetTotal()));
            paymentTotalTxtV.setText(String.valueOf(selectedPayment.getPaymentSum()));
        }

        childsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == prevChildSelected)
                    return;
                selectedPayment = ((Parent) LvsApplication.currUser).getPayments().get(i);
                totalTxtv.setText(String.valueOf(selectedPayment.getTotal()));
                netTotalTxtV.setText(String.valueOf(selectedPayment.getNetTotal()));
                paymentTotalTxtV.setText(String.valueOf(selectedPayment.getPaymentSum()));

                paymentsItemsAdapter = new PaymentsItemsAdapter(getActivity()
                        , R.layout.payment_item_layout
                        , selectedPayment.getPaymentItems());
                paymentsRecyclerView.setAdapter(paymentsItemsAdapter);

                prevChildSelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
