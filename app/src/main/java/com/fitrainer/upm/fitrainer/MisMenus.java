package com.fitrainer.upm.fitrainer;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

import android.widget.AbsListView.MultiChoiceModeListener;

import com.fitrainer.upm.fitrainer.ListadoDietas.ListViewAdapterMenu;
import com.fitrainer.upm.fitrainer.ListadoDietas.Menu;

public class MisMenus extends Activity {

    // Declare Variables
    ListView list;
    ListViewAdapterMenu listviewadapter;
    List<Menu> arrayMenus = new ArrayList<Menu>();
    String[] id;
    String[] nombre;
    String[] descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.activity_listado_menus);

        // Generate sample data into string arrays
        id = new String[] { "1","2","3" };

        nombre = new String[] { "Menu1", "Menu2", "Menu3"};

        descripcion = new String[] { "Para adelgazar", "Para adelgazar en 1 mes",
                "Para perder 5KG en una semana"};
/*
        flag = new int[] { R.drawable.china, R.drawable.india,
                R.drawable.unitedstates, R.drawable.indonesia,
                R.drawable.brazil, R.drawable.pakistan, R.drawable.nigeria,
                R.drawable.bangladesh, R.drawable.russia, R.drawable.japan };*/

        for (int i = 0; i < id.length; i++) {
            Menu listadoMenu = new Menu(id[i], nombre[i], descripcion[i]);
            arrayMenus.add(listadoMenu);
        }


        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.ListView_listadoMenu);

        // Pass results to ListViewAdapterMenu Class
        listviewadapter = new ListViewAdapterMenu(this, R.layout.entrada,
                arrayMenus);

        // Binds the Adapter to the ListView
        list.setAdapter(listviewadapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        // Capture ListView item click
        list.setMultiChoiceModeListener(new MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = list.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapterMenu Class
                listviewadapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Calls getSelectedIds method from ListViewAdapterMenu Class
                        SparseBooleanArray selected = listviewadapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Menu selecteditem = listviewadapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                listviewadapter.remove(selecteditem);
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
                mode.getMenuInflater().inflate(R.menu.listado_menus, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                listviewadapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }
}