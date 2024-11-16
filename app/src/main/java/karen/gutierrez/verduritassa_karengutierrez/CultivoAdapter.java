package karen.gutierrez.verduritassa_karengutierrez;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CultivoAdapter extends ArrayAdapter<Cultivo> {
    public CultivoAdapter(Context context, List<Cultivo> cultivos) {
        super(context, 0, cultivos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cultivo, parent, false);
        }

        Cultivo cultivo = getItem(position);
        TextView textViewCultivo = convertView.findViewById(R.id.textViewCultivo);
        ImageButton btnOptions = convertView.findViewById(R.id.btnOptions); // Cambiado a ImageButton

        if (cultivo != null) {
            // Mostrar nombre del cultivo, fecha de cultivo y fecha de cosecha
            String displayText = cultivo.getNombre() + "  |  " +
                    cultivo.getFechaCosecha();
            textViewCultivo.setText(displayText);
        }

        // Configurar el listener del botón de opciones
        btnOptions.setOnClickListener(v -> {
            if (getContext() instanceof ListaCultivoActivity) {
                ((ListaCultivoActivity) getContext()).showBottomSheetDialog(cultivo);
            }
        });

        return convertView;
    }
}


