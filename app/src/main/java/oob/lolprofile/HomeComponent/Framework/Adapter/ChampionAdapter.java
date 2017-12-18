package oob.lolprofile.HomeComponent.Framework.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import oob.lolprofile.HomeComponent.Domain.Model.Champion;
import oob.lolprofile.R;

public class ChampionAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Champion> champions, championsFiltered;
    private int layout;

    public ChampionAdapter(Context context, final ArrayList<Champion> champions, int layout) {
        this.context = context;
        this.champions = champions;
        this.championsFiltered = new ArrayList<Champion>() {{ addAll(champions); }};
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return this.championsFiltered.size();
    }

    @Override
    public Object getItem(int i) {
        return this.championsFiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = this.inflateView(view);

        ChampionViewHolder championViewHolder = (ChampionViewHolder) view.getTag();

        Champion champion = this.championsFiltered.get(i);

        Picasso.with(this.context)
                .load(String.format(this.context.getString(R.string.base_url_champion_image), champion.getImage()))
                .placeholder(R.drawable.teemo_goes_high)
                .centerCrop()
                .fit()
                .into(championViewHolder.getChampionAvatar());

        return view;
    }

    private View inflateView(View view) {
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(this.layout, null);
            view.setTag(this.createViewHolder(view));
        }

        return view;
    }

    private ChampionViewHolder createViewHolder(View view) {
        return (new ChampionViewHolder())
                .setChampionAvatar((RoundedImageView) view.findViewById(R.id.roundImageViewChampionAvatar));
    }

    public void filterByName(String champName) {
        this.championsFiltered.clear();

        if (TextUtils.isEmpty(champName)) {
            this.championsFiltered.addAll(this.champions);
        } else {
            for(Champion champion: this.champions) {
                if (champion.getName().toLowerCase().contains(champName.toLowerCase())) {
                    this.championsFiltered.add(champion);
                }
            }
        }

        this.notifyDataSetChanged();
    }
}