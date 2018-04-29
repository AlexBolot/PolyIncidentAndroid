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

public class IncidentAdapter extends ArrayAdapter<Incident> {
    private List<Incident> incidents;
    private Context context;

    public IncidentAdapter(@NonNull Context context, @NonNull List<Incident> incidents) {
        super(context, 0, incidents);
        Log.d("IncidentAdapter", "Create incident adapter with " + incidents.toString());
        this.incidents = incidents;
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
        Incident incident = this.getItem(indexOfInc);
        if (incident == null) return;
        TextView meta = view.findViewById(R.id.inc_categorie);
        StringBuilder metas = new StringBuilder(incident.getCategoryLabel());
        meta.append(" ");
        metas.append(incident.getDate().toString());
        meta.setText(metas);

        TextView title = view.findViewById(R.id.inc_title);
        title.setText(incident.getTitle());

        TextView description = view.findViewById(R.id.inc_description);
        description.setText(incident.getDescription());

        ImageView imageView = view.findViewById(R.id.inc_thumbnail);

        if (imageView.getDrawable() == null)
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder));

        if (incident.getLinkToPreview() != null)
            new ThumbnailLoader(imageView).execute(incident.getLinkToPreview());
    }

    @Override
    public Incident getItem(int index) {
        return incidents.get(index);
    }
}
