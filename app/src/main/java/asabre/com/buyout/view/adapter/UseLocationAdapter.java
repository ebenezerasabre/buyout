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
import asabre.com.buyout.view.callback.LocationDialogCallback;

public class UseLocationAdapter extends RecyclerView.Adapter<UseLocationAdapter.ViewHolder> {
    private static final String TAG = UseLocationAdapter.class.getSimpleName();

    private Context mContext;
    private List<Address> mAllUserAddress;
    private LocationDialogCallback mLocationDialogCallback;

    public UseLocationAdapter(Context context, List<Address> allUserAddress, LocationDialogCallback locationDialogCallback) {
        mContext = context;
        mAllUserAddress = allUserAddress;
        mLocationDialogCallback = locationDialogCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_add_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mAllUserAddress == null || mAllUserAddress.size() == 0){
            // when no address is available
        } else {
            final Address address = mAllUserAddress.get(position);
            holder.addressName.setText(address.getNickName());

            holder.addressEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLocationDialogCallback.updateLocation(address);
                }
            });
            holder.addressSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLocationDialogCallback.useLocation(address.get_id());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mAllUserAddress != null && mAllUserAddress.size() != 0 ? mAllUserAddress.size() : 0;
    }

    public void loadNewData(List<Address> list){
        mAllUserAddress = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView addressName;
        TextView addressEdit;
        TextView addressSelect;

        ViewHolder(View itemView){
            super(itemView);
            this.addressName = itemView.findViewById(R.id.addressName);
            this.addressEdit = itemView.findViewById(R.id.addressEdit);
            this.addressSelect = itemView.findViewById(R.id.addressSelect);
        }
    }

}
