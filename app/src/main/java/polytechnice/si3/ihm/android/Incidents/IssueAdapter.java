package polytechnice.si3.ihm.android.Incidents;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.database.model.Issue;

public class IssueAdapter extends ArrayAdapter<Issue> {
    private List<Issue> issues;
    private Context context;

    public IssueAdapter(@NonNull Context context, @NonNull List<Issue> issues) {
        super(context, 0, issues);
        Log.d("IncidentAdapter", "Create incident adapter with " + issues.toString());
        this.issues = issues;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int index, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.incident_item, null);

        setUpView(view, index);

        return view;
    }

    private void setUpView(View view, int indexOfInc) {
        Issue issue = this.getItem(indexOfInc);
        if (issue == null) return;
        TextView meta = view.findViewById(R.id.inc_categorie);
        StringBuilder metas = new StringBuilder(issue.getCategoryID());
        meta.append(" ");
        metas.append(issue.getDate());
        meta.setText(metas);

        TextView title = view.findViewById(R.id.inc_title);
        title.setText(issue.getTitle());

        TextView description = view.findViewById(R.id.inc_description);
        description.setText(issue.getDescription());

        ImageView imageView = view.findViewById(R.id.inc_thumbnail);

        if (imageView.getDrawable() == null)
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder));

        if (issue.getLinkToPreview() != null)
            new ThumbnailLoader(imageView).execute(issue.getLinkToPreview());
    }

    @Override
    public Issue getItem(int index) {
        return issues.get(index);
    }
}
