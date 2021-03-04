package not.forgot.again.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import not.forgot.again.R;
import not.forgot.again.model.entities.BaseEntity;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;

public class CustomSpinnerAdapter<T extends BaseEntity> extends ArrayAdapter<BaseEntity> {
    private List<T> data = new ArrayList<>();
    private Context context;
    private String hint;

    public CustomSpinnerAdapter(@NonNull Context context, String hint) {
        super(context, R.layout.item_custom_spinner, R.id.tvItemSpinner);
        this.context = context;
        this.hint = hint;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    public void updateData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public T getSelectedItem(Spinner spinner) {
        int position = (int) spinner.getSelectedItemId();
        if (position == -1) return null;
        if (data.size() <= position) return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position - 1;
    }

    @Override
    public int getCount() {
        return data.size() + 1; //1 for hint
    }

    @NonNull
    @SuppressLint({"ViewHolder", "UseCompatLoadingForDrawables"})
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.item_custom_spinner, parent, false);
        row.setBackground(context.getDrawable(R.drawable.custom_edit_text_back));

        TextView textView = (TextView) row.findViewById(R.id.tvItemSpinner);
//        Typeface myTypeFace = Typeface.createFromAsset(context.getAssets(),
//                "fonts/gilsanslight.otf");
//        v.setTypeface(myTypeFace);

        if (position == 0) {
            textView.setText(hint);
            textView.setTypeface(null, Typeface.ITALIC);
            textView.setTextColor(Color.GRAY);
            return row;
        }

        position = position - 1; //offset by one for hint

        BaseEntity d = data.get(position);

        if (d instanceof Priority) {
            textView.setText(((Priority) d).getName());
        } else if (d instanceof Category) {
            textView.setText(((Category) d).getName());
        } else {
            throw new RuntimeException("You not handled case for for type: " + d.getClass()
                    .getName());
        }
        return row;
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.item_custom_spinner, parent, false);
        TextView textView = (TextView) row.findViewById(R.id.tvItemSpinner);
//        Typeface myTypeFace = Typeface.createFromAsset(context.getAssets(),
//                "fonts/gilsanslight.otf");
//        v.setTypeface(myTypeFace);

        if (position == 0) {
            textView.setVisibility(View.GONE);
            return row;
        }

        position = position - 1; //offset by one for hint

        BaseEntity d = data.get(position);

        if (d instanceof Priority) {

            textView.setText(((Priority) d).getName());
        } else if (d instanceof Category) {
            textView.setText(((Category) d).getName());
        } else {
            throw new RuntimeException("You not handled case for for type: " + d.getClass()
                    .getName());
        }
        return row;
    }
}
