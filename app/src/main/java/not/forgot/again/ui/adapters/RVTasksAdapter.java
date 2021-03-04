package not.forgot.again.ui.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import not.forgot.again.R;
import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Task;
import not.forgot.again.presenters.MainPresenter;

public class RVTasksAdapter extends RecyclerView.Adapter<RVTasksAdapter.CategoryViewHolder> {

    private List<Pair<Category, List<Task>>> data = new LinkedList<>(); //empty
    private MainPresenter presenter;

    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    public RVTasksAdapter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == data.size()) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_DATA;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty, parent, false);
            view.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, 250));
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rv_category_tasks, parent, false);
        }
        return new CategoryViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (position == data.size()) //VIEW_TYPE_EMPTY
            return;

        Pair<Category, List<Task>> pair = data.get(position);
        Category category = pair.first;
        List<Task> tasks = pair.second;

        holder.tvCategory.setText(category.getName());
        holder.setData(tasks);
    }

    public void setData(List<Pair<Category, List<Task>>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        LinearLayout lvTasks;

        CategoryViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_EMPTY) return;
            tvCategory = itemView.findViewById(R.id.tvCategory);
            lvTasks = itemView.findViewById(R.id.llTasks);
        }

        void setData(List<Task> tasks) {
            lvTasks.removeAllViews();
            for (Task task : tasks) {
                lvTasks.addView(getChild(task));
            }
        }

        View getChild(Task task) {
            View view = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_lv_task, lvTasks, false);
            CardView cvTask = view.findViewById(R.id.cvTask);
            TextView tvTaskName = view.findViewById(R.id.tvTaskName);
            TextView tvTaskDescription = view.findViewById(R.id.tvTaskDescription);
            CheckBox checkBox = view.findViewById(R.id.cbTaskDone);

            cvTask.setCardBackgroundColor(Color.parseColor(task.getPriority().getColor()));
            tvTaskName.setText(task.getTitle() + " " + task.getLocalId());
            tvTaskDescription.setText(task.getDescription());

            checkBox.setChecked(task.isDone());
            if (task.isDone()) {
                tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskDescription.setPaintFlags(tvTaskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                checkBox.setEnabled(false);
            }

            cvTask.setOnClickListener(v -> presenter.onCvTaskClicked(task));
            checkBox.setOnClickListener((buttonView) -> {
                checkBox.setChecked(false);
                presenter.onCheckedForTask(task);
            });

            return view;
        }
    }
}


