package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Address;
import asabre.com.buyout.view.callback.AccountAddressCallback;

public class MyAccountAddressAdapter extends RecyclerView.Adapter<MyAccountAddressAdapter.ViewHolder> {

    private Context mContext;
    private List<Address> mAddressList;
    private AccountAddressCallback mAccountAddressCallback;

    public MyAccountAddressAdapter(Context context,
                                   List<Address> addressList,
                                   AccountAddressCallback accountAddressCallback) {
        mContext = context;
        mAddressList = addressList;
        mAccountAddressCallback = accountAddressCallback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_my_account_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mAddressList == null || mAddressList.size() == 0){
            // when no data is available
        } else {
            final Address address = mAddressList.get(position);
            holder.myAccountAddressName.setText(address.getNickName());

            holder.myAccountEditAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAccountAddressCallback.editAddress(address);
                }
            });
            holder.myAccountViewAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAccountAddressCallback.viewAddress(address);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mAddressList != null && mAddressList.size() != 0 ? mAddressList.size() : 0;
    }

    public void loadNewData(List<Address> list){
        mAddressList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView myAccountAddressName;
        TextView myAccountEditAddress;
        TextView myAccountViewAddress;

        ViewHolder(View itemView){
            super(itemView);
            this.myAccountAddressName = itemView.findViewById(R.id.myAccountAddressName);
            this.myAccountEditAddress = itemView.findViewById(R.id.myAccountEditAddress);
            this.myAccountViewAddress = itemView.findViewById(R.id.myAccountViewAddress);
        }
    }

}
