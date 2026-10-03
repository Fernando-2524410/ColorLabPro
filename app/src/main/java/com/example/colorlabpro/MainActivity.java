package com.example.colorlabpro;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private View root;
    private TextView tvHex;
    private Button btnFav;
    private Button btnTheme;
    private GridLayout gridPalette;
    private ListView listFav;

    private String selectedHex = "#2196F3";

    private SharedPreferences prefs;

    private static final String PREFS = "colorlab_prefs";
    private static final String KEY_THEME = "theme_color";
    private static final String KEY_FAVS = "favorites";

    private ArrayList<String> favList = new ArrayList<>();
    private ArrayAdapter<String> favAdapter;

    // 25 colores de la paleta
    private final String[] palette = {
            "#F44336",
            "#E91E63",
            "#9C27B0",
            "#673AB7",
            "#3F51B5",
            "#2196F3",
            "#03A9F4",
            "#00BCD4",
            "#009688",
            "#4CAF50",
            "#FFC107",
            "#FF5722",
            "#9B59B6",
            "#1ABC9C",
            "#E67E22",
            "#2ECC71",
            "#3498DB",
            "#8E44AD",
            "#16A085",
            "#D35400",
            "#27AE60",
            "#2980B9",
            "#C0392B",
            "#7F8C8D",
            "#34495E"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // Referencias a los elementos del XML
        root = findViewById(R.id.root);
        tvHex = findViewById(R.id.tvHex);
        btnFav = findViewById(R.id.btnFav);
        btnTheme = findViewById(R.id.btnTheme);
        gridPalette = findViewById(R.id.gridPalette);
        listFav = findViewById(R.id.listFav);

        // Ajustar el contenido para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        // Inicializar SharedPreferences
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Cargar el color guardado
        selectedHex = prefs.getString(KEY_THEME, selectedHex);

        // Aplicar el color guardado
        aplicarColorAUi(selectedHex);

        // Cargar favoritos
        cargarFavoritos();

        // Crear adaptador de favoritos
        favAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                favList
        );

        listFav.setAdapter(favAdapter);

        // Crear la paleta de 25 colores
        crearPaleta();

        // Botón para agregar a favoritos
        btnFav.setOnClickListener(v -> {
            agregarAFavoritos(selectedHex);
        });

        // Botón para guardar el tema
        btnTheme.setOnClickListener(v -> {

            prefs.edit()
                    .putString(KEY_THEME, selectedHex)
                    .apply();

            aplicarColorAUi(selectedHex);
        });

        // Seleccionar un color desde favoritos
        listFav.setOnItemClickListener((parent, view, position, id) -> {

            selectedHex = favList.get(position);

            aplicarColorAUi(selectedHex);
        });
    }

    // Crear los cuadros de colores
    private void crearPaleta() {

        gridPalette.removeAllViews();

        int size = (int) (
                getResources()
                        .getDisplayMetrics()
                        .density * 64
        );

        int margin = (int) (
                getResources()
                        .getDisplayMetrics()
                        .density * 8
        );

        for (String hex : palette) {

            View swatch = new View(this);

            // Asignar color al cuadro
            swatch.setBackgroundColor(
                    Color.parseColor(hex)
            );

            // Tamaño y márgenes
            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = size;
            params.height = size;

            params.setMargins(
                    margin,
                    margin,
                    margin,
                    margin
            );

            swatch.setLayoutParams(params);

            // Cuando se toca un color
            swatch.setOnClickListener(v -> {

                selectedHex = hex;

                aplicarColorAUi(selectedHex);
            });

            // Agregar el color al GridLayout
            gridPalette.addView(swatch);
        }
    }

    // Aplicar el color seleccionado a la interfaz
    private void aplicarColorAUi(String hex) {

        tvHex.setText(hex);

        root.setBackgroundColor(
                Color.parseColor(hex)
        );

        // Determinar si el texto debe ser blanco o negro
        int textColor;

        if (esColorOscuro(hex)) {
            textColor = Color.WHITE;
        } else {
            textColor = Color.BLACK;
        }

        tvHex.setTextColor(textColor);
    }

    // Determinar si un color es oscuro
    private boolean esColorOscuro(String hex) {

        int color = Color.parseColor(hex);

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        double luminancia =
                (0.299 * r)
                        + (0.587 * g)
                        + (0.114 * b);

        return luminancia < 140;
    }

    // Cargar favoritos guardados
    private void cargarFavoritos() {

        Set<String> set = prefs.getStringSet(
                KEY_FAVS,
                new LinkedHashSet<>()
        );

        favList.clear();

        favList.addAll(set);
    }

    // Guardar favoritos
    private void guardarFavoritos() {

        Set<String> set =
                new LinkedHashSet<>(favList);

        prefs.edit()
                .putStringSet(KEY_FAVS, set)
                .apply();
    }

    // Agregar color a favoritos
    private void agregarAFavoritos(String hex) {

        if (!favList.contains(hex)) {

            // Agregar al principio de la lista
            favList.add(0, hex);

            // Máximo 10 favoritos
            if (favList.size() > 10) {
                favList.remove(favList.size() - 1);
            }

            // Guardar
            guardarFavoritos();

            // Actualizar la lista
            favAdapter.notifyDataSetChanged();
        }
    }
}